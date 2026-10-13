import { createRouter, createWebHistory } from 'vue-router'
import { isTokenExpired } from '../utils/jwt'
const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录',guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册',guestOnly: true }
  },
  {
    path: '/rider/login',
    name: 'RiderLogin',
    component: () => import('../views/RiderLogin.vue'),
    meta: { title: '骑手登录',guestOnly: true }
  },
  {
    path: '/rider/register',
    name: 'RiderRegister',
    component: () => import('../views/RiderRegister.vue'),
    meta: { title: '骑手注册',guestOnly: true }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页', requireAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('../views/UserCenter.vue'),
    meta: { title: '个人中心', requireAuth: true }
  },
  {
    path: '/user/edit',
    name: 'UserEdit',
    component: () => import('../views/UserEdit.vue'),
    meta: { title: '编辑资料', requireAuth: true }
  },
  {
    path: '/goods',
    name: 'GoodsList',
    component: () => import('../views/GoodsList.vue'),
    meta: { title: '商品列表' }
  },
  {
    path: '/goods/:id',
    name: 'GoodsDetail',
    component: () => import('../views/GoodsDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/goods/my-list',
    name: 'MyGoods',
    component: () => import('../views/MyGoods.vue'),
    meta: { title: '我的商品', requireAuth: true }
  },
  {
    path: '/goods/add',
    name: 'GoodsAdd',
    component: () => import('../views/GoodsAdd.vue'),
    meta: { title: '添加商品', requireAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/CartList.vue'),
    meta: { title: '购物车', requireAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('../views/OrderList.vue'),
    meta: { title: '我的订单', requireAuth: true }
  },
  {
    path: '/orders/shop',
    name: 'ShopOrders',
    component: () => import('../views/ShopOrders.vue'),
    meta: { title: '店铺订单', requireAuth: true }
  },
  {
    path: '/orders/:orderNo',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue'),
    meta: { title: '订单详情', requireAuth: true }
  },
  {
    path: '/address',
    name: 'AddressList',
    component: () => import('../views/AddressList.vue'),
    meta: { title: '收货地址', requireAuth: true }
  },
  {
    path: '/address/add',
    name: 'AddressAdd',
    component: () => import('../views/AddressEdit.vue'),
    meta: { title: '添加地址', requireAuth: true }
  },
  {
    path: '/address/:id/edit',
    name: 'AddressEdit',
    component: () => import('../views/AddressEdit.vue'),
    meta: { title: '编辑地址', requireAuth: true }
  },
  {
    path: '/shop/config',
    name: 'ShopConfig',
    component: () => import('../views/ShopConfig.vue'),
    meta: { title: '店铺配置', requireAuth: true }
  },
  {
    path: '/rider/dashboard',
    name: 'RiderDashboard',
    component: () => import('../views/RiderDashboard.vue'),
    meta: { title: '骑手中心', requireRiderAuth: true }
  }
]

// ===== 关键：先创建 router =====
const router = createRouter({
  history: createWebHistory(),
  routes
})

// ===== 工具函数 =====
function getStorage(key) {
  try {
    return JSON.parse(localStorage.getItem(key) || 'null')
  } catch {
    return null
  }
}

function clearAuth(type) {
  if (type === 'user') {
    localStorage.removeItem('fotoflow_user')
  } else if (type === 'rider') {
    localStorage.removeItem('fotoflow_rider')
  }
}

// ===== 路由守卫 =====
router.beforeEach((to, from, next) => {
  const authData = getStorage('fotoflow_user')
  const riderData = getStorage('fotoflow_rider')

  const userValid = authData?.token && !isTokenExpired(authData.token)
  const riderValid = riderData?.token && !isTokenExpired(riderData.token)

  if (to.meta.guestOnly && userValid) {
    return next('/home')
  }

  if (to.meta.requireAuth) {
    if (!userValid) {
      clearAuth('user')
      return next('/login')
    }
  }

  if (to.meta.requireRiderAuth) {
    if (!riderValid) {
      clearAuth('rider')
      return next('/rider/login')
    }
  }

  document.title = to.meta.title || 'FotoFlow'
  next()
})

export default router