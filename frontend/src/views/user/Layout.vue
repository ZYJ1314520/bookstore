<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left">
        <router-link to="/" class="logo">📚 网上书店</router-link>
      </div>
      <div class="header-center">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索书名、作者、ISBN"
          @keyup.enter="handleSearch"
          style="width: 400px"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      <div class="header-right">
        <router-link v-if="isShopLoggedIn" to="/shop" class="nav-link back-shop">返回商家后台</router-link>
        <router-link to="/cart" class="nav-link">
          <el-badge :value="cartCount" :hidden="cartCount === 0">
            <el-icon><ShoppingCart /></el-icon>
          </el-badge>
          购物车
        </router-link>
        <router-link to="/my/orders" class="nav-link">我的订单</router-link>
        <el-dropdown>
          <span class="nav-link">
            <el-icon><User /></el-icon>
            {{ displayNickname }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/my/orders')">我的订单</el-dropdown-item>
              <el-dropdown-item @click="$router.push('/my/address')">收货地址</el-dropdown-item>
              <el-dropdown-item @click="$router.push('/my/reviews')">我的评价</el-dropdown-item>
              <el-dropdown-item @click="$router.push('/my/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 分类导航 -->
    <div class="category-nav">
      <router-link to="/" class="category-item">首页</router-link>
      <router-link v-for="cat in categories" :key="cat.id" :to="'/books?categoryId=' + cat.id" class="category-item">
        {{ cat.name }}
      </router-link>
    </div>

    <!-- 主内容区 -->
    <main class="main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <p>© 2026 网上书店 版权所有</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/api'

const router = useRouter()
const userStore = useUserStore()

const isShopLoggedIn = computed(() => !!localStorage.getItem('shopToken'))

// 当没有用户token但有shopToken时，显示商家信息
const displayNickname = computed(() => {
  if (userStore.userInfo.nickname) return userStore.userInfo.nickname
  if (localStorage.getItem('shopToken')) {
    try {
      const shopUser = JSON.parse(localStorage.getItem('shopUserInfo') || '{}')
      return shopUser.nickname || '商家'
    } catch { return '商家' }
  }
  return '个人中心'
})

const searchKeyword = ref('')
const categories = ref([])
const cartCount = ref(0)

onMounted(async () => {
  // 获取分类
  const res = await request.get('/api/public/categories')
  if (res.data.code === 200) {
    categories.value = res.data.data
  }
})

const handleSearch = () => {
  router.push({ path: '/books', query: { keyword: searchKeyword.value } })
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header {
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-left .logo {
  font-size: 20px;
  font-weight: bold;
  color: #667eea;
  text-decoration: none;
}
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}
.header-right {
  display: flex;
  gap: 20px;
  align-items: center;
}
.nav-link {
  color: #333;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}
.nav-link:hover {
  color: #667eea;
}
.back-shop {
  color: #e6a23c;
  font-weight: bold;
}
.back-shop:hover {
  color: #f56c6c;
}
.category-nav {
  background: #667eea;
  padding: 10px 20px;
  display: flex;
  gap: 20px;
  overflow-x: auto;
}
.category-item {
  color: white;
  text-decoration: none;
  white-space: nowrap;
  padding: 5px 10px;
  border-radius: 4px;
}
.category-item:hover {
  background: rgba(255,255,255,0.2);
}
.main {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
.footer {
  background: #333;
  color: #999;
  text-align: center;
  padding: 20px;
}
</style>
