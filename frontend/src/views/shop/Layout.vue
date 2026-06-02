<template>
  <div class="shop-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="logo">📚 商家后台</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
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
}
.sidebar {
  width: 200px;
  background: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
}
.el-menu {
  border-right: none;
}
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.header {
  height: 60px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.shop-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
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
.content {
  flex: 1;
  padding: 20px;
  background: #f5f5f5;
}
</style>
