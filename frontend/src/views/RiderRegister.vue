<<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-logo">
        <svg class="logo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
          <line x1="12" y1="18" x2="12" y2="18"/>
        </svg>
        FotoFlow
      </div>
      <h2 class="auth-title">骑手注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>手机号</label>
          <div class="input-wrapper">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
              <line x1="12" y1="18" x2="12" y2="18"/>
            </svg>
            <input v-model="form.phone" class="form-input" placeholder="请输入手机号" required />
          </div>
        </div>
        <div class="form-group">
          <label>真实姓名</label>
          <div class="input-wrapper">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            <input v-model="form.realName" class="form-input" placeholder="请输入真实姓名" />
          </div>
        </div>
        <div class="form-group">
          <label>密码</label>
          <div class="input-wrapper">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            <input v-model="form.password" class="form-input" type="password" placeholder="请输入密码" required />
          </div>
        </div>
        <button type="submit" class="btn btn-primary btn-large" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <div class="auth-links">
        <router-link to="/rider/login">已有账号？立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerRider } from '../api/login'

const router = useRouter()
const loading = ref(false)
const form = ref({ phone: '', realName: '', password: '' })

async function handleRegister() {
  loading.value = true
  try {
    await registerRider(form.value)
    alert('注册成功，请登录')
    router.push('/rider/login')
  } catch (e) {
    alert(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }

.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f7;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow:
      0 0 0 1px rgba(0,0,0,0.03),
      0 2px 8px rgba(0,0,0,0.04),
      0 16px 48px rgba(0,0,0,0.06);
}

.auth-logo {
  text-align: center;
  font-size: 26px;
  font-weight: 800;
  color: #1d1d1f;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.logo-icon {
  width: 30px;
  height: 30px;
  color: #1d1d1f;
  stroke-width: 2.5px;
}

.auth-title {
  text-align: center;
  font-size: 17px;
  font-weight: 500;
  margin-bottom: 36px;
  color: #86868b;
}

form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: 0.2px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  width: 18px;
  height: 18px;
  color: #86868b;
  pointer-events: none;
  transition: color 0.2s ease;
  z-index: 1;
}

.form-input {
  width: 100%;
  height: 48px;
  padding: 0 16px 0 44px;
  border: 1.5px solid #e5e5e7;
  border-radius: 14px;
  font-size: 15px;
  color: #1d1d1f;
  background: #fafafa;
  transition: all 0.2s ease;
  outline: none;
  -webkit-appearance: none;
}

.form-input:hover {
  border-color: #d2d2d7;
  background: #ffffff;
}

.form-input:focus {
  border-color: #1d1d1f;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(29, 29, 31, 0.08);
}

.input-wrapper:focus-within .input-icon {
  color: #1d1d1f;
}

.form-input::placeholder {
  color: #a1a1a6;
}

.btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
  position: relative;
  overflow: hidden;
}

.btn-primary {
  background: #1d1d1f;
  color: #ffffff;
}

.btn-primary:hover:not(:disabled) {
  background: #000000;
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.18);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.btn-primary:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2.5px solid rgba(255,255,255,0.25);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.auth-links {
  margin-top: 28px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
}

.auth-links a {
  color: #0066cc;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
  position: relative;
  padding-bottom: 2px;
}

.auth-links a:hover {
  color: #004499;
}

.auth-links a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background: currentColor;
  transition: width 0.25s ease;
}

.auth-links a:hover::after {
  width: 100%;
}

@media (max-width: 480px) {
  .auth-card {
    padding: 36px 24px;
    border-radius: 20px;
    box-shadow:
        0 0 0 1px rgba(0,0,0,0.03),
        0 1px 4px rgba(0,0,0,0.04),
        0 8px 24px rgba(0,0,0,0.06);
  }
  .auth-logo { font-size: 24px; }
  .auth-title { font-size: 16px; margin-bottom: 28px; }
}
</style>