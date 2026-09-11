package ru.sogaz.site.bff.service.controller.admin

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.sogaz.site.bff.service.dto.request.AdminAddRequest
import ru.sogaz.site.bff.service.dto.request.CreateInformationalMessageRequest
import ru.sogaz.site.bff.service.dto.request.CreatePaymentExceptionRequest
import ru.sogaz.site.bff.service.dto.request.UpdateInformationalMessageRequest
import ru.sogaz.site.bff.service.dto.response.CreateInformationalMessageResponse
import ru.sogaz.site.bff.service.dto.response.CreatePaymentExceptionResponse
import ru.sogaz.site.bff.service.dto.response.DataResponseAddProductOrType
import ru.sogaz.site.bff.service.dto.response.InformationalMessagesResponse
import ru.sogaz.site.bff.service.service.AdminDictionaryService
import ru.sogaz.site.bff.service.service.InformationalMessageService
import ru.sogaz.site.bff.service.service.impl.AdminPaymentExceptionServiceImpl
import ru.sogaz.site.filterStarter.services.RequestInfo.getTraceId
import ru.sogaz.siter.models.resonses.Response
import ru.sogaz.siter.models.resonses.getSuccessResponse
import java.util.UUID

/**
 * Админские ручки управления исключениями по оплатам.
 */
@Tag(
    name = "Admin API",
    description = "Админские методы управления платежными настройками и информационными сообщениями",
)
@RestController
@RequestMapping("/admin")
class AdminController(
    private val service: AdminPaymentExceptionServiceImpl,
    private val adminDictionaryService: AdminDictionaryService,
    private val informationalMessageService: InformationalMessageService,
) {
    companion object {
        const val CODE_SUCCESS_PAYMENT_EXCEPTION = 1101543201
        const val CODE_SUCCESS_ADD_PRODUCT_OR_PAYMENT_TYPE = 1101542200
        const val CODE_SUCCESS_INFORMATIONAL_MESSAGE = 1101544201
        const val CODE_SUCCESS_UPDATE_INFORMATIONAL_MESSAGE = 1101544200
        const val CODE_SUCCESS_GET_INFORMATIONAL_MESSAGES = 1101545200
    }

    /**
     * Создать исключение: запретить оплату продукту указанным paymentType.
     *
     * <p>Кэш excludedByProduct для productId обновляется без рестарта после успешного коммита.</p>
     */
    @PostMapping("/products/{productId}/payment-exceptions")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable productId: UUID,
        @RequestBody @Valid request: CreatePaymentExceptionRequest,
    ): Response<CreatePaymentExceptionResponse> {
        val id = service.createException(productId, request.paymentType.trim())
        return getSuccessResponse(
            getTraceId(),
            CODE_SUCCESS_PAYMENT_EXCEPTION,
            CreatePaymentExceptionResponse(id.toString()),
        )
    }

    /**
     * Создать информационное сообщение для отображения клиенту на платежной странице.
     */
    @Operation(
        summary = "Создание информационного сообщения",
        description = "Создает информационное сообщение, которое может отображаться клиенту на платежной странице.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Информационное сообщение создано",
                content = [Content(schema = Schema(implementation = CreateInformationalMessageResponse::class))],
            ),
            ApiResponse(responseCode = "422", description = "Ошибка валидации входящих параметров"),
        ],
    )
    @PostMapping("/informational_message")
    @ResponseStatus(HttpStatus.CREATED)
    fun createInformationalMessage(
        @RequestBody @Valid request: CreateInformationalMessageRequest,
    ): Response<CreateInformationalMessageResponse> {
        val id = informationalMessageService.create(request)
        return getSuccessResponse(
            getTraceId(),
            CODE_SUCCESS_INFORMATIONAL_MESSAGE,
            CreateInformationalMessageResponse(id.toString()),
        )
    }

    /**
     * Изменить информационное сообщение для отображения клиенту на платежной странице.
     */
    @Operation(
        summary = "Изменение информационного сообщения",
        description = "Изменяет информационное сообщение по идентификатору записи.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Информационное сообщение изменено",
                content = [Content(schema = Schema(implementation = CreateInformationalMessageResponse::class))],
            ),
            ApiResponse(responseCode = "409", description = "Информационное сообщение не найдено"),
            ApiResponse(responseCode = "422", description = "Ошибка валидации входящих параметров"),
        ],
    )
    @PatchMapping("/informational_message/{messageId}")
    fun updateInformationalMessage(
        @Parameter(
            description = "Идентификатор информационного сообщения",
            required = true,
            schema = Schema(type = "string", format = "uuid"),
        )
        @PathVariable messageId: UUID,
        @RequestBody @Valid request: UpdateInformationalMessageRequest,
    ): Response<CreateInformationalMessageResponse> {
        val id = informationalMessageService.update(messageId, request)
        return getSuccessResponse(
            getTraceId(),
            CODE_SUCCESS_UPDATE_INFORMATIONAL_MESSAGE,
            CreateInformationalMessageResponse(id.toString()),
        )
    }

    /**
     * Получить список информационных сообщений для отображения клиенту на платежной странице.
     */
    @Operation(
        summary = "Получение списка информационных сообщений",
        description = "Возвращает список информационных сообщений с опциональной фильтрацией по признаку отображения.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Список информационных сообщений получен",
                content = [Content(schema = Schema(implementation = InformationalMessagesResponse::class))],
            ),
        ],
    )
    @GetMapping("/informational_message")
    fun getInformationalMessages(
        @Parameter(
            description = "Признак доступности сообщения для отображения",
            required = false,
            schema = Schema(type = "boolean"),
        )
        @RequestParam(required = false) checkDisplay: Boolean?,
    ): Response<InformationalMessagesResponse> {
        val messages = informationalMessageService.getMessages(checkDisplay)
        return getSuccessResponse(
            getTraceId(),
            CODE_SUCCESS_GET_INFORMATIONAL_MESSAGES,
            InformationalMessagesResponse(messages),
        )
    }

    /**
     * Добавляет новые продукты и/или способы оплаты.
     *
     * @param request тело запроса.
     * @return стандартизированный ответ по спецификации.
     */
    @PostMapping("/add")
    fun add(
        @Valid
        @RequestBody
        request: AdminAddRequest,
    ): Response<DataResponseAddProductOrType> {
        val response = adminDictionaryService.add(request)
        return getSuccessResponse(
            getTraceId(),
            CODE_SUCCESS_ADD_PRODUCT_OR_PAYMENT_TYPE,
            DataResponseAddProductOrType(response),
        )
    }
}
