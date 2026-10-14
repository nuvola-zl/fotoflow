# FotoFlow —— 从照片社区到视频平台再到电商（版本演进实录）

> 我的"心头肉"项目。它不是一个项目，而是一段编年史：**照片分享 → 视频平台 → 视频+电商**，
> 三个版本完整保留在本仓库的 Tags 中。

## 版本演进（点仓库右侧 Tags 切换查看任意版本）

| Tag | 版本 | 说明 |
|-----|------|------|
| `v1.0` | 照片 & 视频分享 | 单模块 Spring Boot 后端，照片分享起步，视频功能登场 |
| `v2.0` | 视频平台期 | 多模块重构（foto-common / foto-pojo / foto-server），照片退场，纯视频 |
| `v3.0` | 视频 + 电商（当前 main） | 完整前后端：电商模块、订单、**骑手配送端**、RabbitMQ、Vue 前端 |

main 分支即 v3.0 完整版（后端多模块 + `frontend/` Vue 前端）。

## v3.0 技术栈

- 后端：Spring Boot 多模块 · MyBatis · MySQL · Redis · **RabbitMQ** · 阿里云 OSS（环境变量注入）
- 前端：Vue 3 + Vite（`frontend/`，node_modules 未纳入仓库）
- 鉴权：JWT（管理员/用户双密钥）

## 运行（v3.0）

1. MySQL 创建 `fotoflow` 库；Redis、RabbitMQ 本地可用；
2. 通过环境变量注入：`MYSQL_HOST` / `MYSQL_USER` / `MYSQL_PASSWORD` / `REDIS_*` / `OSS_*`；
3. 后端运行 `foto-server` 模块；前端 `cd frontend && npm install && npm run dev`。

## 考古提示

- 三个版本的数据库表结构互不兼容（演进过程中的真实痕迹），运行哪个版本就用哪个时代的库
- OSS 配置在 v2 时代曾明文存放，v3 已全面改为环境变量——演进史的一部分
- 独立的前端副本（当年实际运行过的版本）与仓库内 frontend 存在少量差异，以仓库为准

## 为什么这个项目值得一个仓库

因为它记录的不是代码，是成长：从单模块写到多模块，从明文密钥写到环境变量，
从"照片社区"写到"带骑手端的电商平台"。每个 commit 都是当时的我留下的路标。
