package com.example.aibutler.domain

/**
 * 任务状态机：保证在敏感节点停住，只产出 OwnerPrompt。
 */
class TaskStateMachine(
    private val onOwnerPrompt: (OwnerPrompt) -> Unit,
    private val onLaunchOfficial: (WatchTask) -> Unit,
    private val onNotify: (String) -> Unit
) {

    private val tasks = linkedMapOf<String, WatchTask>()

    fun upsert(task: WatchTask) {
        tasks[task.id] = task
        if (task.state == TaskState.CREATED) {
            task.state = TaskState.WATCHING
        }
    }

    fun get(taskId: String): WatchTask? = tasks[taskId]

    fun all(): List<WatchTask> = tasks.values.toList()

    fun onEvent(event: AssistantEvent) {
        when (event) {
            is AssistantEvent.SaleTimeReached -> handleSaleTime(event.taskId)
            is AssistantEvent.OfficialAppLaunched -> handleLaunched(event.taskId)
            is AssistantEvent.RequestOwner -> handleRequestOwner(event)
            is AssistantEvent.OwnerConfirmed -> handleOwnerConfirmed(event.taskId)
            is AssistantEvent.OwnerCanceled -> {
                tasks[event.taskId]?.state = TaskState.CANCELED
            }
            is AssistantEvent.UserUtterance -> {
                // LLM / NLU 解析后应调用 upsert + 调度闹钟；此处仅占位
                onNotify("已收到指令，请创建盯梢任务（由上层 NLU 填充）")
            }
        }
    }

    private fun handleSaleTime(taskId: String) {
        val task = tasks[taskId] ?: return
        task.state = TaskState.NOTIFY_OPEN
        onNotify("主人，${task.title} 开售了，正在打开官方购票应用")
        task.state = TaskState.LAUNCH_OFFICIAL
        onLaunchOfficial(task)
    }

    private fun handleLaunched(taskId: String) {
        val task = tasks[taskId] ?: return
        // 拉起官方 App 后，默认进入「需主人操作」——选票/支付由人完成
        requestOwner(
            task,
            OwnerActionType.MANUAL_SELECT_SEAT,
            "主人，官方页面已打开，请你完成选票；若进入支付，请亲手支付"
        )
    }

    private fun handleRequestOwner(event: AssistantEvent.RequestOwner) {
        val task = tasks[event.taskId] ?: return
        requestOwner(task, event.action, event.message)
    }

    private fun requestOwner(task: WatchTask, action: OwnerActionType, message: String) {
        task.state = TaskState.NEED_USER_ACTION
        task.pendingOwnerAction = action
        task.noteForOwner = message
        onOwnerPrompt(OwnerPrompt(task.id, action, message))
    }

    private fun handleOwnerConfirmed(taskId: String) {
        val task = tasks[taskId] ?: return
        task.state = TaskState.VERIFY_RESULT
        task.pendingOwnerAction = null
        // 简化：主人确认「我付完了」后记为完成；后续可接订单查询
        task.state = TaskState.DONE
        onNotify("主人，任务「${task.title}」已标记完成")
    }
}
