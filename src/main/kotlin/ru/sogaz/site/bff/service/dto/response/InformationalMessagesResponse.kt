package ru.sogaz.site.bff.service.dto.response

/**
 * Бизнес-данные ответа со списком информационных сообщений.
 */
data class InformationalMessagesResponse(
    /** Список информационных сообщений. */
    val message: List<InformationalMessageResponse>,
)

/**
 * Элемент списка информационных сообщений.
 */
data class InformationalMessageResponse(
    /** UUID сообщения. */
    val messageId: String,
    /** Тип уведомления. */
    val type: String,
    /** Описание уведомления. */
    val description: String,
    /** Признак доступности для отображения. */
    val checkDisplay: Boolean,
)
