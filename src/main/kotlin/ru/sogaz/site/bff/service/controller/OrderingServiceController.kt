package ru.sogaz.site.bff.service.controller

import org.springframework.web.bind.annotation.RestController
import ru.sogaz.site.bff.service.controller.v1.api.OrderingServiceApi
import ru.sogaz.site.bff.service.dto.request.PayQueryParams
import ru.sogaz.site.ordering.client.api.PaymentPageInfoControllerApi
import ru.sogaz.site.ordering.client.model.ResponseDataOrderPaymentPageInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo
import java.util.UUID

/**
 * Реализация API сервиса заказов.
 */
@RestController
class OrderingServiceController(
    private val paymentPageInfoApi: PaymentPageInfoControllerApi,
) : OrderingServiceApi {
    override fun getInfoPage(
        orderId: String,
        payQueryParams: PayQueryParams?,
        channelSale: String?,
        payerIP: String?,
        saveCard: Boolean?,
        unifiedId: String?,
    ): ResponseDataOrderPaymentPageInfo? =
        paymentPageInfoApi.getInfoPage(
            UUID.fromString(orderId),
            payQueryParams?.urlToReturn,
            payQueryParams?.urlToReturnS,
            payQueryParams?.urlToReturnF,
            payQueryParams?.depersonalization,
            channelSale,
            payerIP,
            saveCard,
            unifiedId,
        )

    override fun getStatusInfoPage(invoiceId: UUID): ResponseInvoiceMetaInfo? = paymentPageInfoApi.getMetaInfo(invoiceId)
}
