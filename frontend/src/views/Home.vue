<template>
  <div class="home-page">
    <!-- 顶部导航栏（从 App.vue 迁移，固定在根级别，不随视频滑动消失） -->
    <nav class="app-nav">
      <div class="app-nav-inner">
        <router-link to="/home" class="nav-item" :class="{ active: route.path === '/home' }">
          <span class="nav-label">首页</span>
        </router-link>
        <router-link to="/goods" class="nav-item" :class="{ active: route.path.startsWith('/goods') }">
          <span class="nav-label">商城</span>
        </router-link>
        <router-link to="/user" class="nav-item" :class="{ active: route.path === '/user' }">
          <span class="nav-label">我的</span>
        </router-link>
      </div>
    </nav>

    <!-- 视频容器（支持滑动切换） -->
    <div class="video-feed" v-if="video" ref="videoFeed">
      <div class="video-wrapper" ref="videoWrapper">
        <div class="video-container">
          <video
              v-if="video.videoUrl"
              ref="videoPlayer"
              :src="video.videoUrl"
              class="video-player"
              controls
              autoplay
              loop
              playsinline
              @timeupdate="onTimeUpdate"
              @pause="onPause"
              @play="onPlay"
              @seeking="onSeeking"
              @seeked="onSeeked"
          ></video>
          <div class="video-placeholder" v-else>
            <span>暂无视频</span>
          </div>
        </div>

        <!-- 弹幕层 -->
        <div class="barrage-layer" ref="barrageLayer"></div>

        <!-- 右侧操作栏 -->
        <div class="video-sidebar">
          <div class="sidebar-avatar">
            {{ (video.userId || '?').toString().slice(0, 2) }}
          </div>

          <div class="sidebar-btn" @click="handleLike">
            <div class="sidebar-icon" :class="{ active: video.isLike }">♥</div>
            <div class="sidebar-count">{{ video.likeCount || 0 }}</div>
          </div>

          <div class="sidebar-btn" @click="handleCollect">
            <div class="sidebar-icon" :class="{ active: video.isCollect }">★</div>
            <div class="sidebar-count">{{ video.collectCount || 0 }}</div>
          </div>

          <div class="sidebar-btn" @click="openComment">
            <div class="sidebar-icon">💬</div>
            <div class="sidebar-count">{{ video.commentCount || 0 }}</div>
          </div>
        </div>

        <!-- 商品卡片 - 电商带货 -->
        <div class="product-card" v-if="video && video.product">
          <div class="product-inner" @click="goToProduct">
            <img class="product-img" :src="video.product.image || 'https://via.placeholder.com/44'" alt="商品">
            <div class="product-info">
              <div class="product-name">{{ video.product.name || '超值好物推荐' }}</div>
              <div class="product-price-row">
                <span class="product-price">¥{{ video.product.price || '9.9' }}</span>
                <span class="product-original" v-if="video.product.originalPrice">¥{{ video.product.originalPrice }}</span>
              </div>
            </div>
            <div class="product-tag">去抢购</div>
          </div>
        </div>

        <!-- 底部信息栏 -->
        <div class="video-info" v-if="video">
          <div class="info-user">
            <span class="info-username">@{{ video.userName || ('用户' + (video.userId || '?')) }}</span>
            <button class="info-follow-btn">关注</button>
          </div>
          <div class="info-title">{{ video.title || '精彩内容，不容错过！' }}</div>
          <div class="info-music">
            <span class="music-icon">🎵</span>
            <span class="music-text">{{ video.musicName || '原创音乐 - 热门推荐' }}</span>
          </div>
        </div>

        <!-- 音乐唱片 -->
        <div class="music-disc" v-if="video">
          <img :src="video.avatar || 'https://via.placeholder.com/44'" alt="">
        </div>
      </div>
    </div>

    <div v-else class="loading-spinner">
      <div class="spinner"></div>
    </div>

    <!-- 滑动提示 -->
    <div class="swipe-hint" v-if="video && !isLoading">
      <div class="hint-icon">{{ historyList.length > 0 ? '↑↓' : '↑' }}</div>
      <div class="hint-text">{{ historyList.length > 0 ? '上下滑动切换' : '上滑切换' }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getLatestVideo, likeVideo, collectVideo, getBarrages } from '../api/video'

const route = useRoute()

const video = ref(null)
const videoPlayer = ref(null)
const videoFeed = ref(null)
const videoWrapper = ref(null)
const barrageLayer = ref(null)

// 滑动相关
const isLoading = ref(false)
const isAnimating = ref(false)
const historyList = ref([])
const MAX_HISTORY = 15
let touchStartY = 0
let touchStartTime = 0

// 弹幕系统
const barrageList = ref([])
const activeBarrages = []
let animationFrameId = null
let lastFrameTime = 0
const LANE_COUNT = 8
const laneNextAvailable = new Array(LANE_COUNT).fill(0)
const BARRAGE_SPEED = 140
const BARRAGE_DURATION = 12
const shownBarrageIds = new Set()
let isSeeking = false
let barrageRequestId = 0
let isBarrageLocked = false
let isPlaying = ref(false)

onMounted(async () => {
  await loadVideo()
  bindSwipeEvents()
})

onUnmounted(() => {
  cancelAnimationFrame(animationFrameId)
  removeSwipeEvents()
})

// ==================== 视频加载 ====================
async function loadVideo() {
  try {
    const res = await getLatestVideo()
    video.value = res.data
    resetBarrageSystem()
    if (video.value?.id) {
      await loadBarrages()
    }
  } catch (e) {
    console.error('加载视频失败', e)
  }
}

// ==================== 滑动切换 ====================
function bindSwipeEvents() {
  const el = videoFeed.value
  if (!el) return
  el.addEventListener('touchstart', handleTouchStart, { passive: true })
  el.addEventListener('touchend', handleTouchEnd, { passive: true })
  el.addEventListener('wheel', handleWheel, { passive: true })
}

function removeSwipeEvents() {
  const el = videoFeed.value
  if (!el) return
  el.removeEventListener('touchstart', handleTouchStart)
  el.removeEventListener('touchend', handleTouchEnd)
  el.removeEventListener('wheel', handleWheel)
}

function handleTouchStart(e) {
  touchStartY = e.touches[0].clientY
  touchStartTime = Date.now()
}

function handleTouchEnd(e) {
  const diff = touchStartY - e.changedTouches[0].clientY
  const timeDiff = Date.now() - touchStartTime

  if (Math.abs(diff) > 80 || (Math.abs(diff) > 30 && timeDiff < 300)) {
    if (diff > 0) {
      toNextVideo()
    } else {
      toPrevVideo()
    }
  }
}

function handleWheel(e) {
  e.preventDefault()
  if (e.deltaY > 0) {
    toNextVideo()
  } else {
    toPrevVideo()
  }
}

async function toNextVideo() {
  if (isAnimating.value || !video.value) return

  isBarrageLocked = true
  isPlaying.value = false
  if (videoPlayer.value) videoPlayer.value.pause()

  historyList.value.push({ ...video.value })
  if (historyList.value.length > MAX_HISTORY) {
    historyList.value.shift()
  }

  await loadNextWithAnimation()
}

async function toPrevVideo() {
  if (historyList.value.length === 0 || isAnimating.value) return

  isBarrageLocked = true
  isPlaying.value = false
  isAnimating.value = true
  isLoading.value = true

  videoWrapper.value.style.transform = 'translateY(100px)'
  videoWrapper.value.style.opacity = '0'
  await new Promise(r => setTimeout(r, 300))

  resetBarrageSystem()
  video.value = historyList.value.pop()
  await loadBarrages()

  videoWrapper.value.style.transform = 'translateY(0)'
  videoWrapper.value.style.opacity = '1'
  setTimeout(() => videoPlayer.value?.play(), 100)

  isLoading.value = false
  setTimeout(() => isAnimating.value = false, 300)
}

async function loadNextWithAnimation() {
  isAnimating.value = true
  isLoading.value = true

  videoWrapper.value.style.transform = 'translateY(-100px)'
  videoWrapper.value.style.opacity = '0'
  await new Promise(r => setTimeout(r, 300))

  await loadVideo()

  videoWrapper.value.style.transform = 'translateY(0)'
  videoWrapper.value.style.opacity = '1'
  setTimeout(() => videoPlayer.value?.play(), 100)

  isLoading.value = false
  setTimeout(() => isAnimating.value = false, 300)
}

// ==================== 弹幕系统 ====================
async function loadBarrages() {
  if (!video.value) return
  const requestId = ++barrageRequestId
  const targetVideoId = video.value.id

  try {
    const res = await getBarrages(targetVideoId)
    if (!video.value || video.value.id !== targetVideoId || requestId !== barrageRequestId) return
    barrageList.value = res.data || []
    isBarrageLocked = false
  } catch (e) {
    console.error('加载弹幕失败', e)
  }
}

function resetBarrageSystem() {
  if (barrageLayer.value) barrageLayer.value.innerHTML = ''
  isBarrageLocked = true
  barrageRequestId++
  if (animationFrameId) cancelAnimationFrame(animationFrameId)
  activeBarrages.length = 0
  laneNextAvailable.fill(0)
  shownBarrageIds.clear()
  lastFrameTime = 0
  isSeeking = false
}

function onTimeUpdate() {
  if (!video.value || !barrageLayer.value || isSeeking) return
  const current = videoPlayer.value.currentTime

  barrageList.value.forEach(b => {
    const bid = getBarrageId(b)
    if (shownBarrageIds.has(bid)) return
    if (current >= b.videoTime - 1 && current <= b.videoTime + BARRAGE_DURATION) {
      shownBarrageIds.add(bid)
      showBarrage(b.content, b.color, bid)
    }
  })
}

function getBarrageId(b) {
  const vid = video.value?.id ?? '0'
  return b.id ? `${vid}_${b.id}` : `${vid}_local_${Date.now()}`
}

function showBarrage(text, color, id) {
  if (isBarrageLocked || !barrageLayer.value) return

  const now = performance.now()
  const boxWidth = barrageLayer.value.clientWidth
  const boxHeight = barrageLayer.value.clientHeight
  const laneHeight = boxHeight / LANE_COUNT

  let bestLane = 0
  let earliest = laneNextAvailable[0]
  for (let i = 1; i < LANE_COUNT; i++) {
    if (laneNextAvailable[i] < earliest) {
      earliest = laneNextAvailable[i]
      bestLane = i
    }
  }

  const el = document.createElement('div')
  el.className = 'barrage-item'
  el.innerText = text
  el.style.color = color || '#fff'
  el.style.textShadow = '0 0 4px #000'
  el.style.position = 'absolute'
  el.style.whiteSpace = 'nowrap'
  el.style.fontSize = '18px'
  el.style.fontWeight = 'bold'
  el.style.left = boxWidth + 'px'
  el.style.top = (bestLane * laneHeight + 5) + 'px'
  el.style.zIndex = '10'
  barrageLayer.value.appendChild(el)

  const obj = {
    id,
    el,
    lane: bestLane,
    currentX: boxWidth,
    width: 0,
    speed: BARRAGE_SPEED
  }
  activeBarrages.push(obj)

  const textWidth = text.length * 18
  const dur = ((boxWidth + textWidth) / BARRAGE_SPEED) * 1000
  laneNextAvailable[bestLane] = now + dur * 0.5
}

function gameLoop(timestamp) {
  if (!isPlaying.value) return
  if (!lastFrameTime) lastFrameTime = timestamp
  const dt = (timestamp - lastFrameTime) / 1000
  lastFrameTime = timestamp

  for (let i = activeBarrages.length - 1; i >= 0; i--) {
    const b = activeBarrages[i]
    if (!b.width) b.width = b.el.offsetWidth
    b.currentX -= b.speed * dt
    b.el.style.left = b.currentX + 'px'
    if (b.currentX < -b.width) {
      b.el.remove()
      activeBarrages.splice(i, 1)
    }
  }

  animationFrameId = requestAnimationFrame(gameLoop)
}

function onPause() {
  isPlaying.value = false
  cancelAnimationFrame(animationFrameId)
}

function onPlay() {
  isPlaying.value = true
  lastFrameTime = 0
  animationFrameId = requestAnimationFrame(gameLoop)
}

function onSeeking() {
  isSeeking = true
  if (barrageLayer.value) barrageLayer.value.innerHTML = ''
  activeBarrages.length = 0
  shownBarrageIds.clear()
}

function onSeeked() {
  isSeeking = false
  onTimeUpdate()
}

// ==================== 点赞/收藏 ====================
async function handleLike() {
  if (!video.value) return

  try {
    const res = await likeVideo(video.value.id)
    video.value.isLike = res.data
    video.value.likeCount += res.data ? 1 : -1
  } catch (e) {
    alert(e.message)
  }
}

async function handleCollect() {
  if (!video.value) return

  try {
    const res = await collectVideo(video.value.id)
    video.value.isCollect = res.data
    video.value.collectCount += res.data ? 1 : -1
  } catch (e) {
    alert(e.message)
  }
}

function openComment() {
  console.log('打开评论')
}

function goToProduct() {
  console.log('跳转商品详情')
}
</script>

<style scoped>
/* ========== 全局重置 & 变量 ========== */
.home-page {
  max-width: 480px;
  margin: 0 auto;
  min-height: 100vh;
  background: #000;
  position: relative;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* ========== 顶部黑色导航栏（从 App.vue 迁移，固定在根级别） ========== */
.app-nav {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  padding-top: env(safe-area-inset-top, 0);
  background: #000;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-nav-inner {
  display: flex;
  align-items: center;
  gap: 48px;
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 8px;
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
  border-radius: 4px;
}

.nav-item:hover,
.nav-item.active {
  color: #fff;
}

.nav-label {
  line-height: 1;
}

/* ========== 视频区域 - 全屏沉浸式 ========== */
.video-feed {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: #000;
}

.video-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.35s cubic-bezier(0.25, 0.46, 0.45, 0.94), opacity 0.35s ease;
}

.video-container {
  width: 100%;
  height: 100%;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 18px;
}

/* ========== 底部渐变遮罩 - 提升文字可读性 ========== */
.video-wrapper::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 280px;
  background: linear-gradient(to top, rgba(0,0,0,0.75) 0%, rgba(0,0,0,0.35) 50%, transparent 100%);
  pointer-events: none;
  z-index: 3;
}

/* ========== 弹幕层 ========== */
.barrage-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 100px;
  bottom: 120px;
  pointer-events: none;
  overflow: hidden;
  z-index: 5;
}

.barrage-item {
  will-change: left;
}

/* ========== 右侧操作栏 - 抖音风格 ========== */
.video-sidebar {
  position: absolute;
  right: 10px;
  bottom: 110px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 18px;
  z-index: 10;
}

/* 头像 */
.sidebar-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  border: 2.5px solid #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.3);
  margin-bottom: 4px;
  position: relative;
}

.sidebar-avatar::after {
  content: '+';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 20px;
  background: #fe2c55;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  border: 2px solid #000;
}

/* 按钮 */
.sidebar-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  cursor: pointer;
  user-select: none;
  transition: transform 0.15s ease;
}

.sidebar-btn:active {
  transform: scale(0.92);
}

.sidebar-icon {
  font-size: 36px;
  filter: drop-shadow(0 1px 4px rgba(0,0,0,0.5));
  transition: all 0.2s ease;
  color: #fff;
  line-height: 1;
}

.sidebar-icon.active {
  color: #ff4444;
  animation: heartBeat 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.sidebar-btn:nth-child(3) .sidebar-icon.active {
  color: #ffcc00;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  25% { transform: scale(1.35); }
  50% { transform: scale(0.95); }
  75% { transform: scale(1.15); }
  100% { transform: scale(1); }
}

.sidebar-count {
  font-size: 13px;
  color: #fff;
  text-shadow: 0 1px 3px rgba(0,0,0,0.6);
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* ========== 底部信息栏 - 抖音风格 ========== */
.video-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 100px;
  padding: 0 16px 20px;
  z-index: 8;
  color: #fff;
  pointer-events: none;
}

.info-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.info-username {
  font-size: 16px;
  font-weight: 700;
  text-shadow: 0 1px 4px rgba(0,0,0,0.5);
}

.info-follow-btn {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 4px;
  background: #fe2c55;
  color: #fff;
  font-weight: 600;
  pointer-events: auto;
  cursor: pointer;
  border: none;
}

.info-title {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 8px;
  text-shadow: 0 1px 4px rgba(0,0,0,0.5);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.info-music {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  opacity: 0.9;
}

.music-icon {
  font-size: 14px;
  animation: musicRotate 3s linear infinite;
}

@keyframes musicRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.music-text {
  text-shadow: 0 1px 3px rgba(0,0,0,0.5);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

/* ========== 商品卡片 - 电商带货 ========== */
.product-card {
  position: absolute;
  bottom: 100px;
  left: 12px;
  right: 110px;
  z-index: 9;
  pointer-events: auto;
}

.product-inner {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 12px;
  padding: 8px 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.product-inner:hover,
.product-inner:active {
  background: rgba(255,255,255,0.2);
}

.product-img {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  object-fit: cover;
  background: #333;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 13px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 1px 2px rgba(0,0,0,0.5);
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-top: 2px;
}

.product-price {
  font-size: 16px;
  color: #ff4757;
  font-weight: 700;
}

.product-original {
  font-size: 11px;
  color: rgba(255,255,255,0.5);
  text-decoration: line-through;
}

.product-tag {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  background: #fe2c55;
  color: #fff;
  font-weight: 600;
  flex-shrink: 0;
}

/* ========== 滑动提示 ========== */
.swipe-hint {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  text-align: center;
  z-index: 10;
  animation: pulse 2.5s infinite;
  pointer-events: none;
}

.hint-icon {
  font-size: 18px;
  margin-bottom: 2px;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.7; }
}

/* ========== 加载动画 ========== */
.loading-spinner {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(255, 255, 255, 0.15);
  border-top-color: #fe2c55;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ========== 音乐唱片动画 ========== */
.music-disc {
  position: absolute;
  right: 14px;
  bottom: 24px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #222;
  border: 4px solid #111;
  overflow: hidden;
  z-index: 10;
  animation: discSpin 5s linear infinite;
}

.music-disc img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@keyframes discSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>