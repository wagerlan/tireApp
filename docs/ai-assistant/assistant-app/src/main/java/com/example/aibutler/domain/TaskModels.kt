package com.example.aibutler.domain

/**
 * 半智能体任务模型：AI 推进到「需主人确认」即暂停。
 * 支付 / 人脸 / 验证码绝不由 AI 代劳。
 */

enum class TaskState {
    CREATED,
    WATCHING,
    NOTIFY_OPEN,
    LAUNCH_OFFICIAL,
    NEED_USER_ACTION,
    VERIFY_RESULT,
    DONE,
    FAILED,
    CANCELED
}

enum class OwnerActionType {
    PAYMENT,
    FACE_OR_BIOMETRIC,
    CAPTCHA,
    MANUAL_SELECT_SEAT
}

data class WatchTask(
    val id: String,
    val title: String,
    /** 例如：大麦 / 12306 */
    val channel: String,
    val saleAtEpochMs: Long,
    /** 官方 Deep Link，可空则退化为仅打开 App */
    val deepLink: String? = null,
    /** 官方包名 */
    val packageName: String? = null,
    var state: TaskState = TaskState.CREATED,
    var pendingOwnerAction: OwnerActionType? = null,
    var noteForOwner: String? = null
)

sealed class AssistantEvent {
    data class UserUtterance(val text: String) : AssistantEvent()
    data class SaleTimeReached(val taskId: String) : AssistantEvent()
    data class OfficialAppLaunched(val taskId: String) : AssistantEvent()
    data class RequestOwner(val taskId: String, val action: OwnerActionType, val message: String) : AssistantEvent()
    data class OwnerConfirmed(val taskId: String) : AssistantEvent()
    data class OwnerCanceled(val taskId: String) : AssistantEvent()
}

data class OwnerPrompt(
    val taskId: String,
    val action: OwnerActionType,
    /** 直接可播报/展示：「主人，请支付」 */
    val spokenMessage: String
)
