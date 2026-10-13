<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/goods/my-list" class="back-btn">← 返回</router-link>
      <span class="page-title">添加商品</span>
      <span></span>
    </div>

    <div class="card">
      <div class="form-group">
        <label>商品名称 *</label>
        <input v-model="form.goodsName" class="form-input" placeholder="请输入商品名称" />
      </div>

      <!-- 商品图片上传 -->
      <div class="form-group">
        <label>商品图片 *</label>
        <div class="image-upload" @click="triggerFileInput">
          <img v-if="previewUrl || form.goodsImg" :src="previewUrl || form.goodsImg" class="preview-img" />
          <div v-else class="upload-placeholder">
            <span class="upload-icon">📷</span>
            <span class="upload-text">点击上传商品图片</span>
          </div>
          <div class="upload-overlay" v-if="previewUrl || form.goodsImg">
            <span>更换图片</span>
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

      <div class="form-group">
        <label>商品描述</label>
        <textarea v-model="form.content" class="form-input" rows="3" placeholder="请输入商品描述"></textarea>
      </div>
      <div class="form-group">
        <label>价格（分）*</label>
        <input v-model.number="form.price" class="form-input" type="number" placeholder="单位为分" />
      </div>
      <div class="form-group">
        <label>单位 *</label>
        <input v-model="form.unit" class="form-input" placeholder="如：斤、个、瓶" />
      </div>
      <div class="form-group">
        <label>库存 *</label>
        <input v-model.number="form.stock" class="form-input" type="number" placeholder="初始库存" />
      </div>

      <button class="btn btn-primary btn-large" style="width:100%;margin-top:12px" @click="handleSubmit" :disabled="saving || uploading">
        {{ saving ? '添加中...' : (uploading ? '上传中...' : '添加商品') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { addGoods } from '../api/goods'
import { uploadFile } from '../api/common'

const router = useRouter()
const fileInput = ref(null)

const saving = ref(false)
const uploading = ref(false)
const previewUrl = ref('')

const form = ref({
  goodsName: '',
  goodsImg: '',
  content: '',
  price: 0,
  unit: '',
  stock: 0
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
    form.value.goodsImg = res.data
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
  if (!form.value.goodsName || !form.value.goodsImg || !form.value.price || !form.value.unit) {
    alert('请填写必填项')
    return
  }

  saving.value = true
  try {
    await addGoods(form.value)
    alert('添加成功')
    router.push('/goods/my-list')
  } catch (e) {
    alert(e.message)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.image-upload {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 2px dashed #ddd;
  transition: all 0.2s;
}

.image-upload:hover {
  border-color: #fe2c55;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f9f9f9;
}

.upload-icon {
  font-size: 40px;
}

.upload-text {
  font-size: 14px;
  color: #999;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-upload:hover .upload-overlay {
  opacity: 1;
}
</style>