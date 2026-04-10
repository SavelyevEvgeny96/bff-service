package ru.sogaz.site.bff.service.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import ru.sogaz.site.bff.service.controller.v1.api.OrderingServiceApi
import ru.sogaz.site.bff.service.dto.request.PayQueryParams
import ru.sogaz.site.bff.service.service.impl.InvoiceStandardisationServiceImpl
import ru.sogaz.site.exceptionStarter.starter.dto.exceptions.BusinessException
import ru.sogaz.site.ordering.client.api.InvoicePayPageInfoControllerApi
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoicePayPageInfo
import ru.sogaz.siter.models.resonses.Response
import java.util.UUID

/**
 * Реализация API сервиса заказов.
 */
@RestController
class OrderingServiceController(
    private val invoicePayPageApi: InvoicePayPageInfoControllerApi,
    private val invoiceStandardisationServiceImpl: InvoiceStandardisationServiceImpl,
    private val objectMapper: ObjectMapper,
) : OrderingServiceApi {
    override fun getInfoPage(
        invoiceId: UUID,
        payQueryParams: PayQueryParams?,
        channelSale: String?,
        payerIP: String?,
        saveCard: Boolean?,
        unifiedId: String?,
    ): ResponseInvoicePayPageInfo? =
        try {
            invoicePayPageApi.getInvoicePayPage(
                invoiceId,
                payQueryParams?.urlToReturn,
                payQueryParams?.urlToReturnS,
                payQueryParams?.urlToReturnF,
                payQueryParams?.depersonalization,
                channelSale,
                payerIP,
                saveCard,
                unifiedId,
            )
        } catch (ex: HttpClientErrorException.Conflict) {
            throw BusinessException(ex.getResponse().code)
        }

    override fun getStatusInfoPage(invoiceId: UUID): ResponseInvoiceMetaInfo? =
        invoiceId
            .run(invoicePayPageApi::getInvoiceMetaInfo)
            .run(invoiceStandardisationServiceImpl::standardize)

    private fun HttpClientErrorException.Conflict.getResponse(): Response<Any> =
        objectMapper.readValue(responseBodyAsString)
}
