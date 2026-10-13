<<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/user" class="back-btn">
        <span class="back-icon">←</span>
        <span>返回个人中心</span>
      </router-link>
      <span class="page-title">编辑资料</span>
      <span class="header-spacer"></span>
    </div>

    <div class="content-wrapper" v-if="info">
      <!-- 左侧：头像卡片 -->
      <div class="side-card avatar-card">
        <div class="card-header">
          <span class="card-header-icon">🖼️</span>
          <span>头像设置</span>
        </div>
        <div class="avatar-upload-section">
          <div class="avatar-preview" @click="triggerFileInput">
            <img v-if="previewUrl || form.avatarUrl" :src="previewUrl || form.avatarUrl" class="avatar-img" />
            <div v-else class="avatar-placeholder">
              <span class="avatar-icon">📷</span>
              <span class="avatar-text">点击上传</span>
            </div>
            <div class="avatar-overlay">
              <span class="overlay-icon">📷</span>
              <span>更换头像</span>
            </div>
          </div>
          <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleFileChange"
          />
        </div>
        <p class="avatar-hint">支持 JPG、PNG 格式，建议尺寸 300×300</p>
      </div>

      <!-- 右侧：表单区域 -->
      <div class="main-card">
        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-header">
            <div class="section-title">
              <span class="section-icon">👤</span>
              <span>基本信息</span>
            </div>
            <div class="section-line"></div>
          </div>

          <div class="form-group">
            <label class="form-label">
              <span class="label-text">用户名</span>
              <span class="label-required">*</span>
            </label>
            <div class="input-wrapper">
              <span class="input-prefix">@</span>
              <input
                  v-model="form.username"
                  class="form-input with-prefix"
                  placeholder="请输入用户名"
                  maxlength="20"
              />
            </div>
            <span class="input-hint">用户名用于登录和展示，2-20个字符</span>
          </div>
        </div>

        <!-- 密码修改 -->
        <div class="form-section">
          <div class="section-header">
            <div class="section-title">
              <span class="section-icon">🔐</span>
              <span>修改密码</span>
            </div>
            <span class="section-subtitle">不修改请留空</span>
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label class="form-label">当前密码</label>
              <div class="input-wrapper">
                <input
                    v-model="form.oldPassword"
                    class="form-input"
                    type="password"
                    placeholder="请输入当前密码"
                />
              </div>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label class="form-label">新密码</label>
              <div class="input-wrapper">
                <input
                    v-model="form.password"
                    class="form-input"
                    type="password"
                    placeholder="请输入新密码"
                />
              </div>
            </div>
            <div class="form-group half">
              <label class="form-label">确认新密码</label>
              <div class="input-wrapper">
                <input
                    v-model="form.confirmPassword"
                    class="form-input"
                    type="password"
                    placeholder="请再次输入新密码"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <button
              class="btn btn-secondary"
              @click="router.push('/user')"
          >
            取消
          </button>
          <button
              class="btn btn-primary"
              @click="handleSubmit"
              :disabled="saving || uploading"
          >
            <span v-if="saving" class="btn-loading">
              <span class="loading-spinner"></span>
              保存中...
            </span>
            <span v-else-if="uploading" class="btn-loading">
              <span class="loading-spinner"></span>
              上传中...
            </span>
            <span v-else>
              <span class="btn-icon">💾</span>
              保存修改
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showInfo, updateUserInfo } from '../api/user'
import { uploadFile } from '../api/common'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const fileInput = ref(null)

const info = ref(null)
const saving = ref(false)
const uploading = ref(false)
const previewUrl = ref('')

const form = ref({
  username: '',
  avatarUrl: '',
  oldPassword: '',
  password: '',
  confirmPassword: ''
})

onMounted(async () => {
  try {
    const res = await showInfo()
    info.value = res.data
    form.value.username = res.data.username || ''
    form.value.avatarUrl = res.data.avatarUrl || ''
  } catch (e) {
    console.error(e)
  }
})

function triggerFileInput() {
  fileInput.value?.click()
}

async function handleFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return

  previewUrl.value = URL.createObjectURL(file)

  uploading.value = true
  try {
    const res = await uploadFile(file)
    form.value.avatarUrl = res.data
    previewUrl.value = ''
  } catch (e) {
    alert(e.message || '上传失败')
    previewUrl.value = ''
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

async function handleSubmit() {
  if (form.value.password && form.value.password !== form.value.confirmPassword) {
    alert('两次新密码不一致')
    return
  }
  saving.value = true
  try {
    await updateUserInfo({
      ...form.value,
      id: authStore.user?.id
    })
    if (form.value.avatarUrl) {
      authStore.updateUser({ avatarUrl: form.value.avatarUrl })
    }
    alert('修改成功')
    router.push('/user')
  } catch (e) {
    alert(e.message || '修改失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  padding: 24px;
  box-sizing: border-box;
}

.page-header {
  max-width: 960px;
  margin: 0 auto 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
  background: white;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.back-btn:hover {
  color: #fe2c55;
  transform: translateX(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.back-icon {
  font-size: 16px;
  transition: transform 0.2s ease;
}

.back-btn:hover .back-icon {
  transform: translateX(-3px);
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.header-spacer {
  width: 100px;
}

/* 主内容布局 */
.content-wrapper {
  max-width: 960px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  align-items: start;
}

/* 卡片基础样式 */
.side-card,
.main-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06), 0 4px 12px rgba(0,0,0,0.04);
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.side-card:hover,
.main-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08), 0 12px 32px rgba(0,0,0,0.06);
}

/* 头像卡片 */
.avatar-card {
  padding: 24px;
  text-align: center;
  position: sticky;
  top: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 20px;
}

.card-header-icon {
  font-size: 18px;
}

.avatar-upload-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-preview {
  position: relative;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 4px solid #f3f4f6;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.avatar-preview:hover {
  border-color: #fe2c55;
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(254, 44, 85, 0.2);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.avatar-preview:hover .avatar-img {
  transform: scale(1.1);
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.avatar-icon {
  font-size: 40px;
  opacity: 0.6;
}

.avatar-text {
  font-size: 13px;
  color: #9ca3af;
  font-weight: 500;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  backdrop-filter: blur(4px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  opacity: 0;
  transition: all 0.3s ease;
}

.overlay-icon {
  font-size: 24px;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.avatar-hint {
  margin-top: 16px;
  font-size: 12px;
  color: #9ca3af;
  line-height: 1.5;
}

/* 主卡片 */
.main-card {
  padding: 32px;
}

/* 表单分区 */
.form-section {
  margin-bottom: 32px;
}

.form-section:last-of-type {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
}

.section-icon {
  font-size: 18px;
}

.section-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, #e5e7eb, transparent);
}

.section-subtitle {
  font-size: 13px;
  color: #9ca3af;
  font-weight: 400;
  margin-left: auto;
  background: #f9fafb;
  padding: 4px 12px;
  border-radius: 20px;
}

/* 表单组 */
.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.label-required {
  color: #fe2c55;
  font-size: 14px;
}

.input-hint {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #9ca3af;
}

/* 输入框 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.form-input {
  width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #fafafa;
  transition: all 0.2s ease;
  outline: none;
  box-sizing: border-box;
}

.form-input:hover {
  border-color: #d1d5db;
  background: white;
}

.form-input:focus {
  border-color: #fe2c55;
  background: white;
  box-shadow: 0 0 0 4px rgba(254, 44, 85, 0.08);
}

.form-input::placeholder {
  color: #9ca3af;
}

.input-prefix {
  position: absolute;
  left: 14px;
  color: #9ca3af;
  font-size: 14px;
  font-weight: 500;
  z-index: 1;
}

.form-input.with-prefix {
  padding-left: 36px;
}

/* 按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

.btn {
  height: 44px;
  padding: 0 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-primary {
  background: linear-gradient(135deg, #fe2c55 0%, #ff6b6b 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(254, 44, 85, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(254, 44, 85, 0.4);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #6b7280;
}

.btn-secondary:hover {
  background: #e5e7eb;
  color: #374151;
}

.btn-icon {
  font-size: 16px;
}

/* 加载动画 */
.btn-loading {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .avatar-card {
    position: static;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .page-header {
    flex-wrap: wrap;
    gap: 12px;
  }

  .header-spacer {
    display: none;
  }

  .main-card {
    padding: 24px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn {
    width: 100%;
  }
}
</style>