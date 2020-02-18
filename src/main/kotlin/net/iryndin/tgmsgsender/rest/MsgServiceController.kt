package net.iryndin.tgmsgsender.rest

import mu.KLogging
import net.iryndin.tgmsgsender.service.EntityNotFoundException
import net.iryndin.tgmsgsender.service.InvalidMassSendRequestException
import net.iryndin.tgmsgsender.service.TgMassSenderApiService
import net.iryndin.tgmsgsender.service.UserMsgStatus
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1")
class MsgServiceController(
    private val tgMassSenderApiService: TgMassSenderApiService
) {

    @PostMapping("/massSend")
    fun massSend(@RequestBody request: MassSendRequest): MassSendRequestRegistration {
        logger.info { "Request: $request" }
        try {
            val jobInfo = tgMassSenderApiService.registerMassSendRequest(request.users, request.msg)
            return MassSendRequestRegistration(taskId = jobInfo.jobId)
        } catch (ex: InvalidMassSendRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message, ex)
        }
    }

    @GetMapping("/massSend/{taskId}")
    fun getMassSendStatus(@PathVariable("taskId") taskId: Long): List<UserMsgStatus> =
        try {
            tgMassSenderApiService.status(taskId)
        } catch (ex: EntityNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ex.message, ex)
        }

    companion object: KLogging()
}

data class MassSendRequestRegistration(
    val taskId: Long
)

data class MassSendRequest(
    val users: List<Long>,
    val msg: String
)