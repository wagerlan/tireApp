package com.example.aibutler.platform

import com.example.aibutler.domain.WatchTask

/**
 * 开售提醒调度接入点。
 * 实现侧建议：
 * - 精确闹钟：AlarmManager.setAlarmClock / setExactAndAllowWhileIdle
 * - 持久化：Room + BootCompleted 恢复
 * - 开售窗口：短时前台服务 + 高优先级通知
 *
 * 此处保持接口，避免在文档仓库里绑定完整 Android Gradle 工程。
 */
interface AlertScheduler {
    fun scheduleSaleAlert(task: WatchTask)
    fun cancel(taskId: String)
}

class InMemoryAlertScheduler(
    private val onFire: (taskId: String) -> Unit
) : AlertScheduler {

    private val scheduled = mutableMapOf<String, Long>()

    override fun scheduleSaleAlert(task: WatchTask) {
        scheduled[task.id] = task.saleAtEpochMs
        // 真实实现里注册系统闹钟；这里仅记录
    }

    override fun cancel(taskId: String) {
        scheduled.remove(taskId)
    }

    /** 测试/演示：手动触发到点 */
    fun debugFire(taskId: String) {
        if (scheduled.containsKey(taskId)) onFire(taskId)
    }
}
