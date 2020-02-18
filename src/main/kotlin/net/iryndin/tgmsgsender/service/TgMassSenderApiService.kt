package net.iryndin.tgmsgsender.service

import net.iryndin.tgmsgsender.db.entity.TgMassMsgEntity
import net.iryndin.tgmsgsender.db.entity.TgMassSendJobEntity
import net.iryndin.tgmsgsender.db.entity.TgMsgDeliveryStatus
import net.iryndin.tgmsgsender.db.repository.TgMassMsgRepository
import net.iryndin.tgmsgsender.db.repository.TgMassSendJobRepository
import net.iryndin.tgmsgsender.util.ldt2millis
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

class EntityNotFoundException: RuntimeException("Entity not found")
class InvalidMassSendRequestException(private val msg: String): RuntimeException(msg)

data class MassSendJob(
    val jobId: Long,
    val createTsMillis: Long
)

enum class MsgStatus {
    WAITING,
    DELIVERED,
    ERROR
}

data class UserMsgStatus(
    val userId: Long,
    val status: MsgStatus
)

fun buildMassSendJob(tgMassSendJobEntity: TgMassSendJobEntity): MassSendJob =
        MassSendJob(
                jobId = tgMassSendJobEntity.id!!,
                createTsMillis = ldt2millis(tgMassSendJobEntity.createTs)
        )

fun mapStatus(s: TgMsgDeliveryStatus): MsgStatus =
    when (s) {
        TgMsgDeliveryStatus.TODO -> MsgStatus.WAITING
        TgMsgDeliveryStatus.IN_PROGRESS -> MsgStatus.WAITING
        TgMsgDeliveryStatus.RETRY -> MsgStatus.WAITING
        TgMsgDeliveryStatus.ERROR -> MsgStatus.ERROR
        TgMsgDeliveryStatus.OK -> MsgStatus.DELIVERED
    }

fun buildUserMsgStatus(msg: TgMassMsgEntity): UserMsgStatus =
    UserMsgStatus(
        userId = msg.userId!!,
        status = mapStatus(msg.status)
    )

interface TgMassSenderApiService {
    fun registerMassSendRequest(userIds: List<Long>, text: String): MassSendJob
    fun status(jobId: Long): List<UserMsgStatus>
}

@Service
class TgMassSenderApiServiceImpl(
    private val tgMassSendJobRepository: TgMassSendJobRepository,
    private val tgMassMsgRepository: TgMassMsgRepository,
    @Value("\${app.sendMessage.max.users}") private val maxUsers: Int
): TgMassSenderApiService {

    @Transactional
    override fun registerMassSendRequest(userIds: List<Long>, text: String): MassSendJob {
        val uniqueUserIds = userIds.toSet().toList()

        if (uniqueUserIds.isEmpty()) {
            throw InvalidMassSendRequestException("No users in request")
        }
        if (uniqueUserIds.size > maxUsers) {
            throw InvalidMassSendRequestException("Too many users in request, max allowed users is $maxUsers")
        }
        if (text.isNullOrEmpty()) {
            throw InvalidMassSendRequestException("Text in request is null or empty")
        }
        return buildMassSendJob(createNewMassSendJob(uniqueUserIds, text))
    }

    @Transactional(readOnly = true)
    override fun status(jobId: Long): List<UserMsgStatus> {
        if (!tgMassSendJobRepository.existsById(jobId)) {
            throw EntityNotFoundException()
        }
        return tgMassMsgRepository.getMessagesByJobId(jobId).map { buildUserMsgStatus(it) }
    }

    private fun createNewMassSendJob(userIds: List<Long>, text: String): TgMassSendJobEntity {
        val job = TgMassSendJobEntity().apply {
            this.text = text
        }
        val savedJob = tgMassSendJobRepository.save(job)
        createMessages(job, userIds)
        return savedJob
    }

    private fun createMessages(job: TgMassSendJobEntity, userIds: List<Long>) {
        userIds.forEach { createAndSaveUserMessage(job, it) }
    }

    private fun createAndSaveUserMessage(job: TgMassSendJobEntity, userId: Long) {
        val msg = TgMassMsgEntity().apply {
            this.sendJob = job
            this.status = TgMsgDeliveryStatus.TODO
            this.userId = userId
        }
        tgMassMsgRepository.save(msg)
    }
}