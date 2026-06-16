# Redisson 分布式锁与库存超卖解决方案

## 一、库存超卖问题分析

### 1.1 什么是超卖？

```
场景：商品库存 = 1，用户A和用户B同时下单

时间线：
T1: 用户A查询库存 → 库存=1
T2: 用户B查询库存 → 库存=1（A还没扣减）
T3: 用户A扣减库存 → 库存=0，创建订单
T4: 用户B扣减库存 → 库存=-1，创建订单 ❌ 超卖！
```

### 1.2 超卖的根本原因

| 问题 | 说明 |
|------|------|
| **并发竞争** | 多个线程同时读取相同库存数据 |
| **非原子操作** | 查询库存和扣减库存是两步操作，中间可被插入 |
| **无锁保护** | 没有对共享资源加锁 |

---

## 二、Redisson 分布式锁原理

### 2.1 为什么不用原生 Redis SETNX？

```java
// 原生 Redis 锁的问题
String result = redis.setnx("lock:stock:123", "value", 30, TimeUnit.SECONDS);
if ("OK".equals(result)) {
    // 1. 如果业务执行超过30秒，锁自动过期
    // 2. 但业务还在执行，此时其他线程获取到锁
    // 3. 业务执行完后删除锁 → 可能删掉别人的锁！
    // 4. 造成锁失效，并发安全问题
}
```

**原生锁的问题：**
- 锁过期时间难以精确设置
- 业务未完成但锁已过期
- 删除锁时可能误删别人的锁
- 不支持可重入

### 2.2 Redisson 锁的核心设计

```
┌─────────────────────────────────────────────────────────────┐
│                     Redisson 锁结构                          │
├─────────────────────────────────────────────────────────────┤
│  Key: redisson_lock:{lockName}                              │
│  Value: {UUID}:{threadId}  (标识锁持有者)                    │
│  Type: Hash结构                                              │
│  ┌──────────────────────────────────────────────────┐       │
│  │  Field: "{UUID}:{threadId}"                       │       │
│  │  Value: 1  (重入次数)                              │       │
│  └──────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### 2.3 Redisson 加锁流程（Lua 脚本）

```lua
-- KEYS[1]: 锁的key，如 redisson_lock:{order:stock:123}
-- ARGV[1]: 锁过期时间（毫秒）
-- ARGV[2]: 锁唯一标识，格式为 UUID:threadId

-- 1. 如果锁不存在，直接加锁
if (redis.call('exists', KEYS[1]) == 0) then
    redis.call('hincrby', KEYS[1], ARGV[2], 1);
    redis.call('pexpire', KEYS[1], ARGV[1]);
    return nil;
end;

-- 2. 如果锁已存在，判断是否是当前线程持有（可重入）
if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then
    redis.call('hincrby', KEYS[1], ARGV[2], 1);
    redis.call('pexpire', KEYS[1], ARGV[1]);
    return nil;
end;

-- 3. 返回锁的剩余存活时间（加锁失败）
return redis.call('pttl', KEYS[1]);
```

**流程图：**

```
                    ┌─────────────┐
                    │  请求加锁   │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 锁是否存在？│
                    └──────┬──────┘
                           │
            ┌──────────────┼──────────────┐
            │ 否           │              │ 是
            ┌──────────────▼──┐    ┌──────▼──────────────┐
            │   直接加锁      │    │ 是否为当前线程持有？│
            │   设置过期时间  │    └──────┬──────────────┘
            └──────────────┬──┘           │
                           │       ┌──────┼──────┐
                           │       │ 是   │      │ 否
                           │  ┌────▼────┐ │ ┌────▼────┐
                           │  │ 重入+1  │ │ │返回TTL  │
                           │  │ 续期    │ │ │等待重试 │
                           │  └────┬────┘ │ └────┬────┘
                           │       │      │      │
                    ┌──────▼───────▼──────▼──────▼──────┐
                    │         加锁成功/失败              │
                    └───────────────────────────────────┘
```

### 2.4 看门狗（Watchdog）机制

**问题：** 锁的过期时间设多长？

- 太短：业务没执行完锁就过期了
- 太长：如果持有者宕机，锁长时间无法释放

**Redisson 的解决方案：看门狗自动续期**

```java
// Redisson 默认配置
public class Config {
    // 锁的过期时间：30秒
    private long lockWatchdogTimeout = 30 * 1000;
}

// 看门狗工作流程：
// 1. 加锁成功后启动看门狗（后台线程）
// 2. 每隔 lockWatchdogTimeout / 3 = 10秒 检查一次
// 3. 如果锁还被持有，自动续期到30秒
// 4. 如果业务提前完成，手动释放锁，看门狗停止
```

**看门狗时序图：**

```
时间轴：
0s        10s        20s        30s        40s
│          │          │          │          │
├──── 加锁 ──────────────────────────────────┤
          │          │
          ├── 续期 ──┤
                     ├── 续期 ──┤
                                ├── 续期 ──┤
                                           │
                                    业务完成，手动释放锁
```

### 2.5 可重入锁实现

```java
// 同一线程可以多次获取同一把锁
RLock lock = redisson.getLock("order:lock");
lock.lock();

// 再次获取（可重入）
RLock lock2 = redisson.getLock("order:lock");
lock2.lock();  // 成功，重入计数+1

// 释放锁（需要释放多次）
lock2.unlock();  // 重入计数-1
lock.unlock();   // 重入计数=0，真正释放锁
```

**可重入原理：**

```
Hash结构存储：
Key: redisson_lock:{order:lock}
┌─────────────────────────────────────┐
│ Field: "uuid:thread-123"            │
│ Value: 3  (重入次数)                │
└─────────────────────────────────────┘

每次 lock() → hincrby +1
每次 unlock() → hincrby -1
当 value = 0 时 → 删除 key，真正释放锁
```

---

## 三、库存超卖解决方案（Redisson实现）

### 3.1 方案架构

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端请求                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Gateway (JWT鉴权)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 OrderService.createOrder()                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  1. 获取分布式锁 (Redisson)                         │   │
│  │     lock = redisson.getLock("order:stock:{bookId}") │   │
│  │     lock.lock(10, SECONDS)                          │   │
│  │                                                     │   │
│  │  2. 查询库存 (Redis/DB)                             │   │
│  │     stock = bookMapper.getStock(bookId)             │   │
│  │                                                     │   │
│  │  3. 校验库存                                        │   │
│  │     if (stock < quantity) throw Exception           │   │
│  │                                                     │   │
│  │  4. 扣减库存 (Lua脚本保证原子性)                     │   │
│  │     bookMapper.decreaseStock(bookId, quantity)      │   │
│  │                                                     │   │
│  │  5. 创建订单                                        │   │
│  │     orderMapper.insert(order)                       │   │
│  │                                                     │   │
│  │  6. 释放锁                                          │   │
│  │     lock.unlock()                                   │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      MySQL 数据库                           │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 核心代码实现

```java
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, Long bookId, Integer quantity) {
        // 1. 获取分布式锁
        String lockKey = "order:stock:" + bookId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待5秒，锁自动过期10秒
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("系统繁忙，请稍后重试");
            }

            log.info("用户{}获取锁成功，准备创建订单", userId);

            // 2. 查询库存（双重检查）
            Book book = bookMapper.selectById(bookId);
            if (book == null) {
                throw new RuntimeException("图书不存在");
            }

            if (book.getStock() < quantity) {
                throw new RuntimeException("库存不足，当前库存：" + book.getStock());
            }

            // 3. 扣减库存（使用乐观锁或UPDATE语句）
            int affected = bookMapper.decreaseStock(bookId, quantity);
            if (affected == 0) {
                throw new RuntimeException("库存扣减失败，可能已被其他用户购买");
            }

            // 4. 创建订单
            Order order = buildOrder(userId, bookId, quantity, book.getPrice());
            orderMapper.insert(order);

            log.info("用户{}订单创建成功，订单号：{}", userId, order.getOrderNo());

            return order;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("系统异常", e);
        } finally {
            // 5. 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("用户{}释放锁成功", userId);
            }
        }
    }
}
```

### 3.3 库存扣减的两种实现

#### 方案A：数据库乐观锁（推荐）

```sql
-- 表结构：增加 version 字段
ALTER TABLE book ADD COLUMN version INT DEFAULT 0;

-- 扣减库存 SQL（使用 CAS 机制）
UPDATE book
SET stock = stock - #{quantity},
    sales = sales + #{quantity},
    version = version + 1
WHERE id = #{bookId}
  AND stock >= #{quantity}  -- 防止超卖
  AND version = #{version};  -- 乐观锁
```

```java
// Mapper 方法
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Update("UPDATE book SET stock = stock - #{quantity}, " +
            "sales = sales + #{quantity}, " +
            "version = version + 1 " +
            "WHERE id = #{bookId} AND stock >= #{quantity}")
    int decreaseStock(@Param("bookId") Long bookId,
                      @Param("quantity") Integer quantity);
}
```

#### 方案B：Redis + Lua 原子操作（高性能）

```java
@Service
public class StockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Lua脚本：原子性扣减库存
    private static final String DECREASE_STOCK_LUA =
        "local stock = tonumber(redis.call('get', KEYS[1])) " +
        "if stock == nil then " +
        "  return -1 " +  // 库存不存在
        "end " +
        "if stock < tonumber(ARGV[1]) then " +
        "  return 0 " +   // 库存不足
        "end " +
        "redis.call('decrby', KEYS[1], ARGV[1]) " +
        "return 1";        // 扣减成功

    /**
     * 扣减 Redis 库存
     */
    public boolean decreaseStockRedis(Long bookId, Integer quantity) {
        String key = "stock:" + bookId;
        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(DECREASE_STOCK_LUA, Long.class),
            List.of(key),
            quantity.toString()
        );
        return result != null && result == 1;
    }

    /**
     * 扣减数据库库存（兜底方案）
     */
    @Transactional
    public boolean decreaseStockDb(Long bookId, Integer quantity) {
        int affected = bookMapper.decreaseStock(bookId, quantity);
        return affected > 0;
    }
}
```

### 3.4 完整流程（Redis + 数据库双写）

```
┌──────────────────────────────────────────────────────────────┐
│                      请求：购买1本图书                        │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  获取分布式锁    │
                    │  Redisson.tryLock│
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Redis扣减库存   │  ← 第一层：高性能
                    │  Lua脚本原子操作 │
                    └────────┬────────┘
                             │
                 ┌───────────┼───────────┐
                 │ 成功      │           │ 失败
                 ▼           │           ▼
        ┌────────────┐      │    ┌────────────┐
        │ DB扣减库存  │      │    │ 返回库存不足│
        │ 作为兜底    │      │    └────────────┘
        └─────┬──────┘      │
              │             │
              ▼             │
        ┌────────────┐      │
        │  创建订单   │      │
        └─────┬──────┘      │
              │             │
              ▼             │
        ┌────────────┐      │
        │  释放锁     │      │
        └─────┬──────┘      │
              │             │
              ▼             │
        ┌────────────┐      │
        │ 返回成功    │      │
        └────────────┘      │
```

---

## 四、高级话题

### 4.1 锁的可重入性在订单场景的应用

```java
// 订单服务中调用库存服务（嵌套锁）
@Service
public class OrderServiceImpl {

    @Transactional
    public void createOrder(Long userId, Long bookId, int qty) {
        RLock orderLock = redissonClient.getLock("order:user:" + userId);
        orderLock.lock();

        try {
            // 调用库存服务（内部也会获取锁，但因为可重入所以不会死锁）
            stockService.decreaseStock(bookId, qty);

            // 创建订单
            orderMapper.insert(buildOrder(userId, bookId, qty));

        } finally {
            orderLock.unlock();
        }
    }
}

@Service
public class StockService {

    public void decreaseStock(Long bookId, int qty) {
        // 即使是同一个线程，也可以再次获取同一把锁
        RLock stockLock = redissonClient.getLock("order:stock:" + bookId);
        stockLock.lock();  // 可重入，不会死锁

        try {
            bookMapper.decreaseStock(bookId, qty);
        } finally {
            stockLock.unlock();
        }
    }
}
```

### 4.2 防止误删别人的锁

```java
// 错误做法 ❌
lock.lock();
try {
    // 执行业务...
} finally {
    lock.unlock();  // 可能删掉别人的锁！
}

// 正确做法 ✅
String lockValue = UUID.randomUUID().toString();  // 唯一标识
String lockKey = "order:stock:" + bookId;

// 加锁时存储唯一标识
redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);

try {
    // 执行业务...
} finally {
    // 释放锁前检查是否是自己的锁
    String currentValue = redisTemplate.opsForValue().get(lockKey);
    if (lockValue.equals(currentValue)) {
        redisTemplate.delete(lockKey);
    }
}

// Redisson 已经处理了这个问题，使用 {UUID}:{threadId} 标识
```

### 4.3 Redis 与 MySQL 数据一致性

```
方案：延迟双删 + 消息队列

1. 更新 Redis 库存
2. 更新 MySQL 库存
3. 延迟 500ms 再次删除 Redis 缓存
4. 后台线程定期校验 Redis 与 MySQL 是否一致

为什么需要双删？
- 场景：线程A更新DB，线程B读取DB旧值写入Redis
- 双删可以清除这种脏数据
```

---

## 五、面试回答模板

### Q1：Redisson 锁的原理是什么？

> Redisson 基于 Redis 实现分布式锁，核心是通过 Lua 脚本保证原子性操作。它使用 Hash 结构存储锁信息，key 是锁名称，field 是 `{UUID}:{threadId}` 标识持有者，value 是重入次数。
>
> 它解决了原生 Redis 锁的三个问题：一是看门狗机制自动续期，避免业务未完成锁就过期；二是支持可重入，同一个线程可以多次获取同一把锁；三是锁持有者标识，防止误删别人的锁。

### Q2：库存超卖怎么解决的？

> 我们采用三层防护：
> 1. **Redisson 分布式锁**：同一时刻只有一个线程能操作库存，从根本上避免竞争
> 2. **Redis Lua 脚本**：库存扣减操作原子化，查询和扣减在一条命令中完成
> 3. **数据库乐观锁**：`WHERE stock >= quantity` 作为最后兜底，即使锁失效也不会超卖

### Q3：为什么用 Redisson 而不用 SETNX？

> SETNX 有三个致命问题：锁过期时间难以精确设置、无法自动续期、删除锁时可能误删别人的锁。Redisson 通过看门狗自动续期、Lua 脚本原子操作、UUID 标识持有者解决了这些问题，而且使用更简单，只需要 `lock()` 和 `unlock()` 即可。

---

## 六、总结对比

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| **原生 Redis SETNX** | 简单 | 无法续期、可能误删 | 低并发、允许偶尔不一致 |
| **Redisson** | 可重入、自动续期、防误删 | 依赖 Redis | 高并发、要求强一致 |
| **数据库悲观锁** | 强一致 | 性能差、容易死锁 | 低并发、金融场景 |
| **数据库乐观锁** | 无锁、性能好 | 高并发下重试多 | 读多写少 |
| **Redis + Lua** | 原子性、高性能 | 需要同步到DB | 高并发读写 |

---

*文档版本：v1.0*
*最后更新：2026-06-10*
