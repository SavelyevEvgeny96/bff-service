package ru.sogaz.site.bff.service.controller.v1.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.sogaz.site.bff.service.dto.request.PayQueryParams
import ru.sogaz.site.bff.service.dto.response.BffResponseInvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.InvoicePaymentQrRequest
import java.util.UUID

/**
 * API для работы с сервисом заказов.
 *
 * Контракт BFF для получения информации заказа  по orderId.
 */
@Tag(
    name = "Ordering Service API",
    description = "API для получения информации по заказу по id  заказа",
)
@Validated
@RequestMapping("/v1")
interface OrderingServiceApi {
    companion object {
        const val ORIGINAL_FORWARDED_FOR_HEADER = "x-original-forwarded-for"
        const val X_REAL_IP = "x-real-ip"
    }

    /**
     * Получение QR-кода с реквизитами для оплаты.
     *
     * @param request данные запроса (invoiceId и bank)
     */
    @Operation(
        summary = "Получение QR с реквизитами",
        description = "Запрос для получения ссылки на оплату QRC. Синхронный",
    )
    @PostMapping("/pagepayinfo/payqr")
    fun getQrPaymentRequisite(
        @RequestBody request: InvoicePaymentQrRequest,
    ): Any?

    /**
     * Получение информации для отображения на платежной странице.
     *
     * @param invoiceId id заказа
     */
    @Operation(
        summary = "Информация о способах оплаты заказа",
        description = "Возвращает ссылку для оплаты картой и, если возможно оплатить по СБП, QR-code для оплаты по СБП",
    )
    @Parameters(
        Parameter(
            name = "invoiceId",
            description = "UUID заказа для оплаты",
            required = true,
            schema = Schema(type = "string", format = "uuid"),
        ),
        Parameter(
            name = "urlToReturn",
            description = "Ссылка для редиректа после успешной оплаты",
            example = "http://www.sogaz.ru",
            schema = Schema(type = "string"),
        ),
        Parameter(
            name = "urlToReturnS",
            description = "Ссылка для редиректа после успешной оплаты",
            schema = Schema(type = "string"),
        ),
        Parameter(
            name = "urlToReturnF",
            description = "Ссылка для редиректа после неуспешной оплаты",
            schema = Schema(type = "string"),
        ),
        Parameter(
            name = "depersonalization",
            description = "Флаг необходимости анонимизированной оплаты",
            example = "true",
            schema = Schema(type = "boolean"),
        ),
        Parameter(
            name = "channelSale",
            description = "Канал продажи",
            example = "true",
            schema = Schema(type = "string"),
        ),
        Parameter(
            name = "payerIP",
            description = "IP плательщика",
            example = "true",
            schema = Schema(type = "string"),
        ),
    )
    @GetMapping("/pagepayinfo")
    fun getInfoPage(
        @Parameter(
            name = ORIGINAL_FORWARDED_FOR_HEADER,
            description = "IP пользователя из заголовка",
            `in` = ParameterIn.HEADER,
            required = false,
        )
        @RequestHeader(name = ORIGINAL_FORWARDED_FOR_HEADER, required = false)
        originalForwardedForIp: String?,
        @RequestHeader(name = X_REAL_IP, required = false)
        xRealIp: String?,
        @Parameter(
            name = X_REAL_IP,
            description = "IP пользователя из заголовка",
            `in` = ParameterIn.HEADER,
            required = false,
        )
        @RequestParam(required = false) invoiceId: UUID,
        payQueryParams: PayQueryParams?,
        channelSale: String?,
        saveCard: Boolean?,
        unifiedId: String?,
    ): Any?

    /**
     * Получение информации для отображения на платежной странице.
     *
     * @param invoiceId id заказа
     */
    @GetMapping("/pagepayinfo/info/{invoiceId}")
    fun getStatusInfoPage(
        @PathVariable invoiceId: UUID,
        @Parameter(
            name = "payment",
            description = "Признак необходимости вернуть информацию с учетом оплаты. По умолчанию false",
            required = false,
            `in` = ParameterIn.QUERY,
            schema = Schema(type = "boolean", defaultValue = "false"),
        ) payment: Boolean,
    ): BffResponseInvoiceMetaInfo?
}
