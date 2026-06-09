import { createRouter, createWebHistory } from 'vue-router'

// 用户端页面
import UserLayout from '@/views/user/Layout.vue'
import Home from '@/views/user/Home.vue'
import BookList from '@/views/user/BookList.vue'
import BookDetail from '@/views/user/BookDetail.vue'
import Cart from '@/views/user/Cart.vue'
import OrderConfirm from '@/views/user/OrderConfirm.vue'
import MyOrders from '@/views/user/MyOrders.vue'
import OrderDetail from '@/views/user/OrderDetail.vue'
import Address from '@/views/user/Address.vue'
import MyReviews from '@/views/user/MyReviews.vue'
import Profile from '@/views/user/Profile.vue'
import ShopDetail from '@/views/user/ShopDetail.vue'
import MyFavorites from '@/views/user/MyFavorites.vue'
import UserLogin from '@/views/user/Login.vue'
import UserRegister from '@/views/user/Register.vue'

// 商家端页面
import ShopLayout from '@/views/shop/Layout.vue'
import ShopDashboard from '@/views/shop/Dashboard.vue'
import ShopBooks from '@/views/shop/Books.vue'
import ShopBookEdit from '@/views/shop/BookEdit.vue'
import ShopOrders from '@/views/shop/Orders.vue'
import ShopReviews from '@/views/shop/Reviews.vue'
import ShopProfile from '@/views/shop/ShopSetting.vue'
import ShopLogin from '@/views/shop/Login.vue'
import ShopRegister from '@/views/shop/Register.vue'

// 管理员端页面
import AdminLayout from '@/views/admin/Layout.vue'
import AdminDashboard from '@/views/admin/Dashboard.vue'
import AdminShops from '@/views/admin/Shops.vue'
import AdminUsers from '@/views/admin/Users.vue'
import AdminBooks from '@/views/admin/Books.vue'
import AdminCategories from '@/views/admin/Categories.vue'
import AdminOrders from '@/views/admin/Orders.vue'
import AdminLogin from '@/views/admin/Login.vue'

const routes = [
  // 用户端 - 登录注册（公开）
  {
    path: '/login',
    name: 'UserLogin',
    component: UserLogin,
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'UserRegister',
    component: UserRegister,
    meta: { public: true }
  },
  // 用户端 - 需要登录的页面
  {
    path: '/',
    component: UserLayout,
    children: [
      { path: '', name: 'Home', component: Home, meta: { public: true } },
      { path: 'books', name: 'BookList', component: BookList, meta: { public: true } },
      { path: 'book/:id', name: 'BookDetail', component: BookDetail, meta: { public: true } },
      { path: 'shop/:id', name: 'ShopDetail', component: ShopDetail, meta: { public: true } },
      { path: 'cart', name: 'Cart', component: Cart, meta: { auth: 'user' } },
      { path: 'order/confirm', name: 'OrderConfirm', component: OrderConfirm, meta: { auth: 'user' } },
      { path: 'my/orders', name: 'MyOrders', component: MyOrders, meta: { auth: 'user' } },
      { path: 'my/order/:id', name: 'OrderDetail', component: OrderDetail, meta: { auth: 'user' } },
      { path: 'my/address', name: 'Address', component: Address, meta: { auth: 'user' } },
      { path: 'my/reviews', name: 'MyReviews', component: MyReviews, meta: { auth: 'user' } },
      { path: 'my/favorites', name: 'MyFavorites', component: MyFavorites, meta: { auth: 'user' } },
      { path: 'my/profile', name: 'Profile', component: Profile, meta: { auth: 'user' } }
    ]
  },

  // 商家端 - 登录注册（公开）
  {
    path: '/shop/login',
    name: 'ShopLogin',
    component: ShopLogin,
    meta: { public: true }
  },
  {
    path: '/shop/register',
    name: 'ShopRegister',
    component: ShopRegister,
    meta: { public: true }
  },
  // 商家端 - 需要登录的页面
  {
    path: '/shop',
    component: ShopLayout,
    children: [
      { path: '', name: 'ShopDashboard', component: ShopDashboard, meta: { auth: 'shop' } },
      { path: 'books', name: 'ShopBooks', component: ShopBooks, meta: { auth: 'shop' } },
      { path: 'book/add', name: 'ShopBookAdd', component: ShopBookEdit, meta: { auth: 'shop' } },
      { path: 'book/edit/:id', name: 'ShopBookEdit', component: ShopBookEdit, meta: { auth: 'shop' } },
      { path: 'orders', name: 'ShopOrders', component: ShopOrders, meta: { auth: 'shop' } },
      { path: 'reviews', name: 'ShopReviews', component: ShopReviews, meta: { auth: 'shop' } },
      { path: 'setting', name: 'ShopProfile', component: ShopProfile, meta: { auth: 'shop' } }
    ]
  },

  // 管理员端 - 登录（公开）
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin,
    meta: { public: true }
  },
  // 管理员端 - 需要登录的页面
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: '', name: 'AdminDashboard', component: AdminDashboard, meta: { auth: 'admin' } },
      { path: 'shops', name: 'AdminShops', component: AdminShops, meta: { auth: 'admin' } },
      { path: 'users', name: 'AdminUsers', component: AdminUsers, meta: { auth: 'admin' } },
      { path: 'books', name: 'AdminBooks', component: AdminBooks, meta: { auth: 'admin' } },
      { path: 'categories', name: 'AdminCategories', component: AdminCategories, meta: { auth: 'admin' } },
      { path: 'orders', name: 'AdminOrders', component: AdminOrders, meta: { auth: 'admin' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 角色token映射
const tokenMap = {
  user: 'token',
  shop: 'shopToken',
  admin: 'adminToken'
}

// 路由守卫
router.beforeEach((to, from, next) => {
  // 公开页面直接放行
  if (to.meta.public) {
    next()
    return
  }

  // 需要特定角色登录的页面
  const requiredRole = to.meta.auth
  if (requiredRole) {
    const tokenKey = tokenMap[requiredRole]
    const token = localStorage.getItem(tokenKey)

    // 用户端页面：如果没有 user token，但有 shopToken，也放行（商家可访问前台）
    if (!token && requiredRole === 'user' && localStorage.getItem('shopToken')) {
      next()
      return
    }

    if (!token) {
      // 未登录，跳转对应登录页
      if (requiredRole === 'shop') {
        next('/shop/login')
      } else if (requiredRole === 'admin') {
        next('/admin/login')
      } else {
        next('/login')
      }
      return
    }
  }

  next()
})

export default router
