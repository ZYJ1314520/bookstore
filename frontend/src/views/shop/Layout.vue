<template>
  <div class="shop-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="logo"><span class="logo-mark">B</span> 商家后台</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#6f6f67"
        active-text-color="#111111"
      >
        <el-menu-item index="/shop">
          <el-icon><DataBoard /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/shop/books">
          <el-icon><Book /></el-icon>
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/shop/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/shop/reviews">
          <el-icon><ChatLineSquare /></el-icon>
          <span>评价管理</span>
        </el-menu-item>
        <el-menu-item index="/shop/setting">
          <el-icon><Setting /></el-icon>
          <span>店铺设置</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <header class="header">
        <span class="shop-name">{{ shopStore.shopInfo.shopName || '我的店铺' }}</span>
        <div class="header-right">
          <span class="nav-link" @click="openFrontend">访问前台</span>
          <el-dropdown>
            <span class="nav-link">
              <el-icon><User /></el-icon>
              {{ shopStore.userInfo.nickname || '商家' }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useShopStore } from '@/store/shop'

const router = useRouter()
const shopStore = useShopStore()

const handleLogout = () => {
  shopStore.logout()
  router.push('/shop/login')
}

const openFrontend = () => {
  // 用 Vue Router SPA 导航跳转前台，不会重载页面，shop 状态不丢失
  router.push('/')
}
</script>

<style scoped>
.shop-layout {
  display: flex;
  min-height: 100vh;
  background: var(--app-bg);
}
.sidebar {
  width: 236px;
  background: var(--app-surface);
  border-right: 1px solid var(--app-border);
  padding: 18px 12px;
}
.logo {
  height: 54px;
  padding: 0 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--app-text);
  font-size: 17px;
  font-weight: 700;
}
.logo-mark {
  width: 30px;
  height: 30px;
  border: 1px solid var(--app-text);
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}
.el-menu {
  border-right: none;
  background: transparent;
}
:deep(.el-menu-item) {
  border-radius: var(--app-radius);
  height: 44px;
  margin: 4px 0;
}
:deep(.el-menu-item.is-active) {
  background: var(--app-surface-muted);
  font-weight: 700;
}
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.header {
  min-height: 68px;
  background: rgba(247, 247, 244, 0.9);
  border-bottom: 1px solid var(--app-border);
  backdrop-filter: blur(18px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
}
.shop-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-text);
}
.header-right {
  display: flex;
  gap: 20px;
  align-items: center;
}
.nav-link {
  color: var(--app-text-muted);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}
.nav-link:hover {
  color: var(--app-text);
}
.content {
  flex: 1;
  padding: 28px;
  background: var(--app-bg);
}

@media (max-width: 820px) {
  .shop-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--app-border);
  }
}
</style>
