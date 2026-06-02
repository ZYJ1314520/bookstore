<template>
  <div class="layout">
    <header class="header">
      <div class="header-left">
        <router-link to="/" class="logo">
          <span class="logo-mark">B</span>
          <span>网上书店</span>
        </router-link>
      </div>
      <div class="header-center" ref="searchWrapRef">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索书名、作者、ISBN"
            @keyup.enter="handleSearch"
            @focus="showSearchPanel = true"
            class="search-input"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
          <div class="search-panel" v-if="showSearchPanel">
            <div class="panel-section" v-if="searchHistory.length > 0">
              <div class="panel-title">
                <span>搜索历史</span>
                <el-button link size="small" @click="clearHistory">清空</el-button>
              </div>
              <div class="panel-tags">
                <el-tag v-for="item in searchHistory" :key="item" size="small" @click="pickSearch(item)">{{ item }}</el-tag>
              </div>
            </div>
            <div class="panel-section">
              <div class="panel-title"><span>热门搜索</span></div>
              <div class="panel-tags">
                <el-tag v-for="item in hotKeywords" :key="item" size="small" @click="pickSearch(item)">{{ item }}</el-tag>
              </div>
            </div>
          </div>
        </div>
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

    <div class="category-nav">
      <router-link to="/" class="category-item" :class="{ active: $route.path === '/' }">首页</router-link>
      <router-link
        v-for="cat in categories"
        :key="cat.id"
        :to="'/books?categoryId=' + cat.id"
        class="category-item"
        :class="{ active: $route.query.categoryId == cat.id }"
      >
        {{ cat.name }}
      </router-link>
    </div>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <p>© 2026 网上书店 · 为每一次阅读挑选更好的入口</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
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
const showSearchPanel = ref(false)
const searchWrapRef = ref(null)
const searchHistory = ref(JSON.parse(localStorage.getItem('searchHistory') || '[]'))
const hotKeywords = ref(['三体', '活着', 'JavaScript', '高等数学', '经济学'])

onMounted(async () => {
  const res = await request.get('/api/public/categories')
  if (res.data.code === 200) {
    categories.value = res.data.data
  }
  document.addEventListener('click', handleOutsideClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleOutsideClick)
})

const handleOutsideClick = (e) => {
  if (searchWrapRef.value && !searchWrapRef.value.contains(e.target)) {
    showSearchPanel.value = false
  }
}

const handleSearch = () => {
  showSearchPanel.value = false
  router.push({ path: '/books', query: { keyword: searchKeyword.value } })
}

const pickSearch = (kw) => {
  searchKeyword.value = kw
  handleSearch()
}

const clearHistory = () => {
  localStorage.removeItem('searchHistory')
  searchHistory.value = []
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
  background: var(--app-bg);
}
.header {
  min-height: 72px;
  background: rgba(247, 247, 244, 0.88);
  border-bottom: 1px solid var(--app-border);
  backdrop-filter: blur(18px);
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 clamp(18px, 4vw, 56px);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-left .logo {
  color: var(--app-text);
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 700;
  white-space: nowrap;
}
.logo-mark {
  width: 32px;
  height: 32px;
  border: 1px solid var(--app-text);
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  letter-spacing: 0;
}
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}
.search-input {
  width: min(460px, 100%);
}
.search-box {
  position: relative;
  width: min(460px, 100%);
}
.search-panel {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  box-shadow: var(--app-shadow);
  padding: 14px 16px;
  z-index: 200;
}
.panel-section {
  margin-bottom: 12px;
}
.panel-section:last-child {
  margin-bottom: 0;
}
.panel-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--app-text-muted);
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 8px;
}
.panel-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.panel-tags .el-tag {
  cursor: pointer;
}
.header-right {
  display: flex;
  gap: 18px;
  align-items: center;
  white-space: nowrap;
}
.nav-link {
  color: var(--app-text-muted);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.2s ease;
}
.nav-link:hover {
  color: var(--app-text);
}
.back-shop {
  color: var(--app-text);
  font-weight: 600;
}
.back-shop:hover {
  color: var(--app-text);
}
.category-nav {
  background: var(--app-bg);
  border-bottom: 1px solid var(--app-border);
  padding: 12px clamp(18px, 4vw, 56px);
  display: flex;
  gap: 8px;
  overflow-x: auto;
}
.category-item {
  color: var(--app-text-muted);
  text-decoration: none;
  white-space: nowrap;
  padding: 8px 14px;
  border: 1px solid transparent;
  border-radius: 999px;
  font-size: 14px;
  transition: all 0.2s ease;
}
.category-item:hover,
.category-item.active {
  color: var(--app-text);
  background: var(--app-surface);
  border-color: var(--app-border);
}
.main {
  flex: 1;
  padding: clamp(22px, 4vw, 48px) clamp(18px, 4vw, 56px);
  max-width: 1320px;
  margin: 0 auto;
  width: 100%;
}
.footer {
  background: var(--app-text);
  color: rgba(255,255,255,0.68);
  text-align: center;
  padding: 28px 18px;
  font-size: 14px;
}

:deep(.el-input-group__append) {
  background: var(--app-text);
  border-color: var(--app-text);
  color: white;
}

:deep(.el-input-group__append .el-button) {
  color: white;
}

@media (max-width: 900px) {
  .header {
    align-items: stretch;
    flex-direction: column;
    gap: 14px;
    padding-top: 16px;
    padding-bottom: 16px;
  }

  .header-center,
  .header-right {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .search-input {
    width: 100%;
  }
}
</style>
