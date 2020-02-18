package net.iryndin.tgmsgsender.task

import net.iryndin.tgmsgsender.service.MessageSenderService
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SendMsgTask(
    private val messageSenderService: MessageSenderService,
    @Value("\${app.tasks.sendMsg.users.limit}") private val usersLimit: Int
) {
    @Scheduled(fixedRate = 2000)
    fun sendMessages() {
        messageSenderService.getAndSendAllMessages(usersLimit)
    }
}

@Component
class ClearInProgressStatusTask(
    private val messageSenderService: MessageSenderService
) {
    @Scheduled(fixedRate = 20_000)
    fun clearStatuses() {
        messageSenderService.clearInProgressStatus()
    }
}