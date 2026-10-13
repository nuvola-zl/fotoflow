/**
 * Baidu Map (百度地图) JS API Loader
 * 使用前请将 YOUR_BAIDU_MAP_KEY 替换为你的百度地图 AK
 * 申请地址: https://lbsyun.baidu.com/apiconsole/key
 */

const BMAP_KEY = 'vhmrvoyI3kkO7xjtmQB0yDR6miPKLNJF'
const BMAP_VERSION = '3.0'
let loadPromise = null

export function loadBMap() {
  if (window.BMap) return Promise.resolve(window.BMap)
  if (loadPromise) return loadPromise

  loadPromise = new Promise((resolve, reject) => {
    const callbackName = 'onBMapLoaded_' + Date.now()
    const script = document.createElement('script')
    script.src = `https://api.map.baidu.com/api?v=${BMAP_VERSION}&ak=${BMAP_KEY}&callback=${callbackName}`
    script.onerror = () => {
      loadPromise = null
      reject(new Error('百度地图加载失败'))
    }
    window[callbackName] = () => {
      delete window[callbackName]
      resolve(window.BMap)
    }
    document.head.appendChild(script)
  })
  return loadPromise
}

export function getBMapKey() {
  return BMAP_KEY
}