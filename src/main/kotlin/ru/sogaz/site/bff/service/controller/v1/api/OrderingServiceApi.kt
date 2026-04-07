package ru.sogaz.site.bff.service.controller.v1.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.sogaz.site.bff.service.constraint.ValidUUID
import ru.sogaz.site.bff.service.dto.request.PayQueryParams
import ru.sogaz.site.ordering.client.model.ResponseDataOrderPaymentPageInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo
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
    /**
     * Получение информации для отображения на платежной странице.
     *
     * @param orderId id заказа
     */
    @Operation(
        summary = "Информация о способах оплаты заказа",
        description = "Возвращает ссылку для оплаты картой и, если возможно оплатить по СБП, QR-code для оплаты по СБП",
    )
    @Parameters(
        Parameter(
            name = "orderId",
            description = "UUID заказа для оплаты",
            required = true,
            schema = Schema(type = "string"),
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
        @RequestParam(required = false) @ValidUUID orderId: String,
        payQueryParams: PayQueryParams?,
        channelSale: String?,
        payerIP: String?,
        saveCard: Boolean?,
        unifiedId: String?,
    ): ResponseDataOrderPaymentPageInfo?

    /**
     * Получение информации для отображения на платежной странице.
     *
     * @param invoiceId id заказа
     */
    @GetMapping("/pagepayinfo/info/{invoiceId}")
    fun getStatusInfoPage(@PathVariable invoiceId: UUID): ResponseInvoiceMetaInfo?
}
