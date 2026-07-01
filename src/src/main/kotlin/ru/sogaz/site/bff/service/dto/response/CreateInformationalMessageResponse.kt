package ru.sogaz.site.bff.service.dto.response

/**
 * Ответ о создании информационного сообщения для клиента.
 */
class CreateInformationalMessageResponse(
    /** UUID созданной записи informational_messages_client.id. */
    val messageId: String,
)
