# AOSP 中度配合清单（阶段 3）

前置：阶段 1 App 已能完成「盯梢 → 提醒 → 打开官方 App → 请主人支付」。

## 源码获取（完整一套）

```bash
mkdir -p ~/aosp && cd ~/aosp
repo init --partial-clone --no-use-superproject \
  -b android-latest-release \
  -u https://android.googlesource.com/platform/manifest
repo sync -c -j8
```

详见：https://source.android.com/docs/setup/download

## 建议放入 AOSP 的内容

```
packages/apps/AiButler/     # 将阶段 1 App 迁为系统应用
device/.../overlay/         # 默认 Assistant、通知相关 overlay
privapp-permissions-*.xml   # 特权权限白名单（最小必要）
```

## 中度配合要做的 4 件事

1. **预装**：`PRODUCT_PACKAGES += AiButler`
2. **默认助理**：Role / config 指向 AiButler
3. **待确认中心**（可选独立 Activity）：聚合「请支付 / 请人脸」
4. **Launcher 入口**：桌面主入口改为对话（可选）

## 明确不做

- Kernel 伪造触摸  
- 绕过官方支付 / 生物识别  
- 高频自动刷票点击  

## 验证顺序

1. `lunch` 模拟器目标编译通过  
2. 模拟器内 AiButler 可收通知、拉起浏览器或演示 App  
3. 再考虑 Pixel 真机 + 官方 proprietary binaries  
