<<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/home" class="back-btn">← 返回</router-link>
      <span class="page-title">个人中心</span>
      <router-link to="/user/edit" class="header-btn">编辑</router-link>
    </div>

    <div v-if="info" class="user-info-card">
      <!-- 头像：优先用 Pinia 里的，再用接口返回的 -->
      <div class="avatar">
        <img
            v-if="displayAvatar"
            :src="displayAvatar"
            class="avatar-img"
            alt="头像"
        />
        <span v-else>{{ info.username?.charAt(0) || '?' }}</span>
      </div>

      <div class="user-details">
        <div class="username">{{ info.username }}</div>
        <div class="user-meta">
          <span class="tag" :class="info.isRealName ? 'tag-success' : 'tag-warning'">
            {{ info.isRealName ? '已实名' : '未实名' }}
          </span>
          <span class="tag tag-primary">{{ info.role || 'user' }}</span>
        </div>
        <div class="user-phone" v-if="info.phone">{{ info.phone }}</div>
      </div>
    </div>

    <!-- 功能按钮网格 -->
    <div class="menu-grid">
      <router-link to="/orders" class="menu-card">
        <span class="menu-card-icon">📋</span>
        <span class="menu-card-label">我的订单</span>
      </router-link>
      <router-link to="/address" class="menu-card">
        <span class="menu-card-icon">📍</span>
        <span class="menu-card-label">收货地址</span>
      </router-link>
      <router-link to="/shop/config" class="menu-card">
        <span class="menu-card-icon">🏪</span>
        <span class="menu-card-label">店铺配置</span>
      </router-link>
      <router-link to="/goods/add" class="menu-card">
        <span class="menu-card-icon">➕</span>
        <span class="menu-card-label">添加商品</span>
      </router-link>
      <router-link to="/orders/shop" class="menu-card">
        <span class="menu-card-icon">📦</span>
        <span class="menu-card-label">商品订单</span>
      </router-link>
      <router-link to="/goods/my-list" class="menu-card">
        <span class="menu-card-icon">🏷️</span>
        <span class="menu-card-label">我的商品</span>
      </router-link>
    </div>

    <button class="btn btn-outline" style="width:100%;margin-top:20px;color:var(--danger);border-color:var(--danger)" @click="handleLogout">
      退出登录
    </button>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import {useRouter} from 'vue-router'
import {useAuthStore} from '../stores/auth'
import {getUserInfo} from '../api/user'

const router = useRouter()
const authStore = useAuthStore()
const info = ref(null)

// 计算属性：优先用 Pinia 里的头像，再用接口返回的
const displayAvatar = computed(() => {
  return authStore.user?.avatarUrl || info.value?.avatarUrl || ''
})

onMounted(async () => {
  try {
    const res = await getUserInfo()
    info.value = res.data
    // 同步更新 Pinia（如果接口返回了更新的头像）
    if (res.data?.avatarUrl && res.data.avatarUrl !== authStore.user?.avatarUrl) {
      authStore.updateUser({avatarUrl: res.data.avatarUrl})
    }
  } catch (e) {
    console.error(e)
  }
})

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
/* ========== 页面级 ========== */
.page-container {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 32px;
}

/* ========== 顶部导航 ========== */
.page-header {
  background: #fff;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.back-btn {
  color: #666;
  font-size: 14px;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
  z-index: 1;
}

.back-btn:hover {
  color: #ff5000;
}

.page-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a1a1a;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  letter-spacing: 0.5px;
}

.header-btn {
  color: #ff5000;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  padding: 6px 14px;
  border-radius: 20px;
  background: rgba(255, 80, 0, 0.06);
  transition: all 0.2s;
  z-index: 1;
}

.header-btn:hover {
  background: rgba(255, 80, 0, 0.12);
}

/* ========== 用户信息卡片 ========== */
.user-info-card {
  display: flex;
  align-items: center;
  gap: 18px;
  background: linear-gradient(135deg, #ffffff 0%, #fffaf7 100%);
  border-radius: 20px;
  padding: 24px;
  margin: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 80, 0, 0.06);
}

/* 右上角装饰光晕 */
.user-info-card::before {
  content: '';
  position: absolute;
  top: -40px;
  right: -40px;
  width: 140px;
  height: 140px;
  background: radial-gradient(circle, rgba(255, 80, 0, 0.08) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.avatar {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff8f00 0%, #ff5000 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: 700;
  flex-shrink: 0;
  overflow: hidden;
  border: 3px solid #fff;
  box-shadow: 0 4px 15px rgba(255, 80, 0, 0.25);
  position: relative;
  z-index: 1;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details {
  flex: 1;
  position: relative;
  z-index: 1;
}

.username {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: 0.3px;
}

.user-meta {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  align-items: center;
}

.tag {
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  line-height: 1.4;
  display: inline-flex;
  align-items: center;
}

.tag-success {
  background: #e6f7e6;
  color: #52c41a;
}

.tag-warning {
  background: #fff7e6;
  color: #fa8c16;
}

.tag-primary {
  background: linear-gradient(135deg, #ff8f00, #ff5000);
  color: #fff;
  box-shadow: 0 2px 6px rgba(255, 80, 0, 0.2);
}

.user-phone {
  font-size: 13px;
  color: #999;
  margin-top: 6px;
}

/* ========== 菜单网格 ========== */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 0 16px;
}

.menu-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 24px 12px;
  background: #fff;
  border-radius: 16px;
  text-decoration: none;
  color: inherit;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.02);
  position: relative;
  overflow: hidden;
}

/* 右侧箭头 */
.menu-card::before {
  content: '›';
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #ddd;
  transition: all 0.3s;
  font-weight: 300;
}

/* 顶部彩色悬停条 */
.menu-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  opacity: 0;
  transition: opacity 0.3s;
}

.menu-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
}

.menu-card:hover::before {
  right: 8px;
  color: #bbb;
}

.menu-card:hover::after {
  opacity: 1;
}

.menu-card:active {
  transform: scale(0.97);
}

.menu-card-icon {
  font-size: 28px;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  transition: transform 0.3s;
}

.menu-card:hover .menu-card-icon {
  transform: scale(1.1);
}

.menu-card-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  letter-spacing: 0.3px;
}

/* 各菜单卡片主题色（顶部条 + 图标背景） */
.menu-card:nth-child(1)::after { background: linear-gradient(90deg, #ff5000, #ff8f00); }
.menu-card:nth-child(2)::after { background: linear-gradient(90deg, #1677ff, #69b1ff); }
.menu-card:nth-child(3)::after { background: linear-gradient(90deg, #52c41a, #95de64); }
.menu-card:nth-child(4)::after { background: linear-gradient(90deg, #fa8c16, #ffc53d); }
.menu-card:nth-child(5)::after { background: linear-gradient(90deg, #722ed1, #b37feb); }
.menu-card:nth-child(6)::after { background: linear-gradient(90deg, #eb2f96, #ffadd2); }

.menu-card:nth-child(1) .menu-card-icon { background: linear-gradient(135deg, #fff5f0, #ffeee6); }
.menu-card:nth-child(2) .menu-card-icon { background: linear-gradient(135deg, #f0f5ff, #e6f0ff); }
.menu-card:nth-child(3) .menu-card-icon { background: linear-gradient(135deg, #f6ffed, #e6ffdb); }
.menu-card:nth-child(4) .menu-card-icon { background: linear-gradient(135deg, #fff7e6, #fff0db); }
.menu-card:nth-child(5) .menu-card-icon { background: linear-gradient(135deg, #f9f0ff, #f0e6ff); }
.menu-card:nth-child(6) .menu-card-icon { background: linear-gradient(135deg, #fff0f6, #ffe6f0); }

/* ========== 退出登录 ========== */
button.btn-outline {
  margin: 24px 16px 0 !important;
  width: calc(100% - 32px) !important;
  padding: 14px !important;
  border-radius: 12px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  border: 1px solid #ffd8bf !important;
  background: #fff !important;
  color: #ff5000 !important;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  position: relative;
  overflow: hidden;
}

button.btn-outline:hover {
  background: #fff5f0 !important;
  border-color: #ff5000 !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 80, 0, 0.12);
}

button.btn-outline:active {
  transform: scale(0.98);
}
</style>