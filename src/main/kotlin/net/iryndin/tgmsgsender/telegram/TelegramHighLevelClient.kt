package net.iryndin.tgmsgsender.telegram

class TelegramHighLevelClient(
    private val token: String
) {
    private val lowLevelClient = TelegramLowLevelClient(token)

    fun sendMessage(userId: Long, text: String): TelegramResponse =
        lowLevelClient.get("sendMessage", mapOf(
            "chat_id" to userId.toString(),
            "text" to text
        ))
}