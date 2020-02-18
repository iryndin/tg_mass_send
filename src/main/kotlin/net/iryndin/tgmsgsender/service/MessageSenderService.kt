package net.iryndin.tgmsgsender.service

import mu.KLogging
import net.iryndin.tgmsgsender.db.entity.TgMassMsgEntity
import net.iryndin.tgmsgsender.db.entity.TgMassMsgErrorEntity
import net.iryndin.tgmsgsender.db.entity.TgMsgDeliveryStatus
import net.iryndin.tgmsgsender.db.repository.TgMassMsgErrorRepository
import net.iryndin.tgmsgsender.db.repository.TgMassMsgRepository
import net.iryndin.tgmsgsender.telegram.TelegramHighLevelClient
import net.iryndin.tgmsgsender.telegram.TelegramResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

interface MessageSenderService {
    fun getAndSendAllMessages(limit: Int)
    fun clearInProgressStatus()
}

@Service
class MessageSenderServiceImpl(
    private val tgMassMsgRepository: TgMassMsgRepository,
    private val tgMassMsgErrorRepository: TgMassMsgErrorRepository,
    @Value("\${app.tg.api.token}") private val tgToken: String,
    @Value("\${app.sendMessage.max.retry}") private val maxRetries: Int
): MessageSenderService {

    private val telegramHighLevelClient = TelegramHighLevelClient(tgToken)

    @Transactional
    override fun clearInProgressStatus() {
        tgMassMsgRepository.clearInProgressStatus(
            TgMsgDeliveryStatus.RETRY.name,
            TgMsgDeliveryStatus.IN_PROGRESS.name,
            LocalDateTime.now().minusSeconds(30)
        )
    }

    @Transactional
    override fun getAndSendAllMessages(limit: Int) {
        logger.info { "Going to send all messages, limit=$limit" }
        getMessagesToSend(limit).forEach { sendMessage(it) }
    }

    private fun sendMessage(msg: TgMassMsgEntity) {
        val oldStatus = msg.status

        msg.status = TgMsgDeliveryStatus.IN_PROGRESS
        tgMassMsgRepository.saveAndFlush(msg)

        val text = msg.sendJob!!.text!!

        if (oldStatus == TgMsgDeliveryStatus.TODO) {
            sendMessageAndSaveResult(text, msg)
        } else if (oldStatus == TgMsgDeliveryStatus.RETRY) {
            sendMessageInRetryStatus(text, msg)
        }
    }

    private fun sendMessageInRetryStatus(text: String, msg: TgMassMsgEntity) {
        // 1. check if we reached max retries
        val lastRetryRecord = getLastRetryRecord(msg.id!!)
        if (lastRetryRecord.isPresent) {
            if (lastRetryRecord.get().tryNumber < maxRetries) {
                sendMessageAndSaveResult(text, msg, lastRetryRecord.get().tryNumber)
            } else {
                msg.status = TgMsgDeliveryStatus.ERROR
                tgMassMsgRepository.save(msg)
            }
        } else {
            sendMessageAndSaveResult(text, msg)
        }
    }

    private fun getLastRetryRecord(messageId: Long): Optional<TgMassMsgErrorEntity> =
        tgMassMsgErrorRepository.getLatestRetry(messageId)

    private fun sendMessageAndSaveResult(text: String, msg: TgMassMsgEntity, latestTryNumber: Int = 0) {
        val tgResult = telegramHighLevelClient.sendMessage(msg.userId!!, text)
        if (tgResult.isOk()) {
            msg.status = TgMsgDeliveryStatus.OK
            tgMassMsgRepository.save(msg)
        } else {
            createAndSaveRetryRecord(msg, latestTryNumber, tgResult)
            msg.status = TgMsgDeliveryStatus.RETRY
            tgMassMsgRepository.save(msg)
        }
    }

    private fun createAndSaveRetryRecord(msg: TgMassMsgEntity, latestTryNumber: Int, telegramResponse: TelegramResponse) {
        val msgErr = TgMassMsgErrorEntity().apply {
            this.message = msg
            this.tryNumber = latestTryNumber + 1
            this.httpStatus = telegramResponse.statusCode
            this.response = telegramResponse.body
        }
        tgMassMsgErrorRepository.saveAndFlush(msgErr)
    }

    fun getMessagesToSend(limit: Int): List<TgMassMsgEntity> =
        tgMassMsgRepository.getMessagesToBeSent(
            listOf(TgMsgDeliveryStatus.TODO, TgMsgDeliveryStatus.RETRY).map { it.name },
                limit)

    companion object: KLogging()
}