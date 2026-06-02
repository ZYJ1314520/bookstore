<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="logo"><span class="logo-mark">B</span> 管理后台</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#6f6f67"
        active-text-color="#111111"
      >
        <el-menu-item index="/admin">
          <el-icon><DataBoard /></el-icon>
          <span>数据大屏</span>
        </el-menu-item>
        <el-menu-item index="/admin/shops">
          <el-icon><Shop /></el-icon>
          <span>商家管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/books">
          <el-icon><Book /></el-icon>
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <header class="header">
        <span class="title">网上书店管理系统</span>
        <div class="header-right">
          <span class="admin-name">
            <el-icon><User /></el-icon>
            {{ adminStore.userInfo.username || '管理员' }}
          </span>
          <el-button link @click="handleLogout">退出登录</el-button>
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
import { useAdminStore } from '@/store/admin'

const router = useRouter()
const adminStore = useAdminStore()

const handleLogout = () => {
  adminStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
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
.title {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-text);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.admin-name {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--app-text-muted);
}
.content {
  flex: 1;
  padding: 28px;
  background: var(--app-bg);
}

@media (max-width: 820px) {
  .admin-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--app-border);
  }
}
</style>
