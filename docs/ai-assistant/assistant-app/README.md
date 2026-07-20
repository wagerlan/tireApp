# AiButler — 阶段 1 半智能体 App 骨架

对应文档：`../半智能体技术方案.md`

## 模块说明

- `TaskState`：任务状态机（盯梢 → 提醒 → 拉起官方 App → 等主人支付/人脸）
- `OwnerAction`：需要主人亲手完成的动作（支付 / 人脸 / 验证码）
- `AlertScheduler`：开售提醒（Alarm / WorkManager 接入点）
- `OfficialAppLauncher`：用包名 / Deep Link 拉起官方购票 App
- **不包含**：模拟点击刷票、绕过人脸/支付

## 本地打开

用 Android Studio 打开本目录（需自行补齐 Android SDK）。  
当前以「可阅读的 Kotlin 骨架」为主，便于在 Cursor 中继续实现 UI 与 LLM 对接。

## 建议实现顺序

1. 先跑通：创建任务 → 到点通知 → 打开大麦 → 显示「请支付」**  
2. 再接 LLM 意图解析  
3. 最后再谈预装进 AOSP
