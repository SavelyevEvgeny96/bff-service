package ru.sogaz.site.bff.service.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.sogaz.site.bff.service.dto.request.CreateInformationalMessageRequest
import ru.sogaz.site.bff.service.dto.request.UpdateInformationalMessageRequest
import ru.sogaz.site.bff.service.dto.response.InformationalMessageResponse
import ru.sogaz.site.bff.service.model.InformationalMessageClient
import ru.sogaz.site.bff.service.repository.InformationalMessageClientRepository
import ru.sogaz.site.bff.service.service.InformationalMessageService
import ru.sogaz.site.exceptionStarter.starter.dto.exceptions.BusinessException
import ru.sogaz.site.exceptionStarter.starter.dto.exceptions.InnerException
import ru.sogaz.site.filterStarter.services.RequestInfo
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

/**
 * Сервис для работы с информационными сообщениями клиента.
 */
@Service
class InformationalMessageServiceImpl(
    private val repository: InformationalMessageClientRepository,
) : InformationalMessageService {
    companion object {
        const val ERROR_SAVE_INFORMATIONAL_MESSAGE = "Ошибка при сохранении информационного сообщения"
        const val CODE_INFORMATIONAL_MESSAGE_NOT_FOUND = 1101544409
    }

    /**
     * Создает информационное сообщение для клиента.
     */
    @Transactional
    override fun create(request: CreateInformationalMessageRequest): UUID {
        val saved =
            repository.saveAndFlush(
                InformationalMessageClient(
                    type = requireNotNull(request.type).trim(),
                    description = requireNotNull(request.description).trim(),
                    displayWith = requireNotNull(request.displayWith).toUtcLocalDateTime(),
                    displayTo = requireNotNull(request.displayTo).toUtcLocalDateTime(),
                    checkDisplay = true,
                ),
            )
        return saved.id ?: throw InnerException(RequestInfo.getTraceId(), ERROR_SAVE_INFORMATIONAL_MESSAGE)
    }

    /**
     * Изменяет информационное сообщение для клиента.
     */
    @Transactional
    override fun update(
        messageId: UUID,
        request: UpdateInformationalMessageRequest,
    ): UUID {
        val message =
            repository.findById(messageId).orElseThrow {
                BusinessException(CODE_INFORMATIONAL_MESSAGE_NOT_FOUND)
            }

        request.type?.trim()?.let { message.type = it }
        request.description?.trim()?.let { message.description = it }
        request.displayWith?.let { message.displayWith = it.toUtcLocalDateTime() }
        request.displayTo?.let { message.displayTo = it.toUtcLocalDateTime() }
        request.checkDisplay?.let { message.checkDisplay = it }

        val saved = repository.saveAndFlush(message)
        return saved.id ?: throw InnerException(RequestInfo.getTraceId(), ERROR_SAVE_INFORMATIONAL_MESSAGE)
    }

    /**
     * Возвращает список информационных сообщений с опциональной фильтрацией по признаку отображения.
     *
     * Перед формированием ответа отключает отображение сообщений, период показа которых не содержит текущую дату.
     */
    @Transactional
    override fun getMessages(checkDisplay: Boolean?): List<InformationalMessageResponse> {
        val messages = repository.findAll()
        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)

        val messagesToDisable = messages.filter { it.checkDisplay && !it.contains(currentDateTime) }
        messagesToDisable.forEach { it.checkDisplay = false }

        if (messagesToDisable.isNotEmpty()) {
            repository.saveAllAndFlush(messagesToDisable)
        }

        return messages
            .filter { message -> checkDisplay?.let { message.checkDisplay == it } ?: true }
            .map { message ->
                InformationalMessageResponse(
                    messageId = requireNotNull(message.id).toString(),
                    type = message.type,
                    description = message.description,
                    checkDisplay = message.checkDisplay,
                )
            }
    }

    private fun InformationalMessageClient.contains(currentDateTime: LocalDateTime): Boolean =
        !currentDateTime.isBefore(displayWith) && !currentDateTime.isAfter(displayTo)

    private fun OffsetDateTime.toUtcLocalDateTime(): LocalDateTime = withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime()
}
