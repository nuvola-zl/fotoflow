/**
 * 判断 JWT Token 是否过期
 * @param {string} token JWT 字符串
 * @returns {boolean} true=已过期/无效
 */
export function isTokenExpired(token) {
    if (!token || token.split('.').length !== 3) return true
    try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        // exp 是秒级时间戳，留 60 秒缓冲防止边界问题
        return payload.exp * 1000 < Date.now() + 60000
    } catch (e) {
        return true
    }
}

/**
 * 解析 Token 获取用户ID（调试用）
 */
export function getUserIdFromToken(token) {
    if (!token) return null
    try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        return payload.userId || payload.sub || null
    } catch {
        return null
    }
}