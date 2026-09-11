package ru.sogaz.site.bff.service.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import ru.sogaz.site.bff.service.controller.v1.api.OrderingServiceApi
import ru.sogaz.site.bff.service.dto.request.PayQueryParams
import ru.sogaz.site.bff.service.dto.response.BffResponseInvoiceMetaInfo
import ru.sogaz.site.bff.service.service.InformationalMessageService
import ru.sogaz.site.bff.service.service.impl.InvoiceStandardisationServiceImpl
import ru.sogaz.site.exceptionStarter.starter.dto.exceptions.BusinessException
import ru.sogaz.site.ordering.client.api.InvoicePayPageInfoControllerApi
import ru.sogaz.site.ordering.client.api.InvoicePaymentQrControllerApi
import ru.sogaz.site.ordering.client.model.InvoicePaymentQrRequest
import ru.sogaz.site.ordering.client.model.ResponseInvoicePayPageInfo
import ru.sogaz.siter.models.resonses.Response
import java.net.URI
import java.util.UUID

/**
 * Реализация API сервиса заказов.
 */
@RestController
class OrderingServiceController(
    private val invoicePaymentQrApi: InvoicePaymentQrControllerApi,
    private val invoicePayPageApi: InvoicePayPageInfoControllerApi,
    private val invoiceStandardisationServiceImpl: InvoiceStandardisationServiceImpl,
    private val objectMapper: ObjectMapper,
    private val informationalMessageService: InformationalMessageService,
    @param:Value("\${api.ordering.paySuffix}")
    private val paySuffix: String,
) : OrderingServiceApi {
    companion object {
        const val DMZ = "gateway-site-dmz"
    }

    override fun getQrPaymentRequisite(request: InvoicePaymentQrRequest): Any? =
        try {
            invoicePaymentQrApi
                .getQrPaymentRicvisit(request)
        } catch (ex: HttpClientErrorException) {
            ResponseEntity.status(ex.statusCode).body(ex.toResponse())
        }

    private fun HttpClientErrorException.toResponse(): Response<Any> = objectMapper.readValue(responseBodyAsString)

    override fun getInfoPage(
        originalForwardedForIp: String?,
        xRealIp: String?,
        invoiceId: UUID,
        payQueryParams: PayQueryParams?,
        channelSale: String?,
        saveCard: Boolean?,
        unifiedId: String?,
    ): Any? =
        try {
            invoicePayPageApi
                .getInvoicePayPage(
                    invoiceId,
                    xRealIp,
                    payQueryParams?.urlToReturn,
                    payQueryParams?.urlToReturnS,
                    payQueryParams?.urlToReturnF,
                    payQueryParams?.depersonalization,
                    channelSale,
                    buildPayerIp(originalForwardedForIp, xRealIp),
                    saveCard,
                    unifiedId,
                ).run(invoiceStandardisationServiceImpl::standardize)
                .apply {
                    data?.urlPayBank =
                        data
                            ?.urlPayBank
                            ?.toString()
                            ?.replacePayBankHost()
                            ?.let { URI.create(it) }
                }.withInformationalMessages()
        } catch (ex: HttpClientErrorException.Conflict) {
            throw BusinessException(ex.getResponse().code)
        }

    override fun getStatusInfoPage(
        invoiceId: UUID,
        payment: Boolean,
    ): BffResponseInvoiceMetaInfo =
        invoicePayPageApi
            .getInvoiceMetaInfo(invoiceId, payment)
            .run(invoiceStandardisationServiceImpl::standardize)

    private fun ResponseInvoicePayPageInfo.withInformationalMessages(): Map<String, Any?> {
        val response = objectMapper.convertValue<MutableMap<String, Any?>>(this)
        val messages = informationalMessageService.getMessages(true)

        if (messages.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            val data = response["data"] as? MutableMap<String, Any?>
            data?.put("message", messages)
        }

        return response
    }

    private fun String.replacePayBankHost(): String = replace(DMZ, paySuffix)

    private fun buildPayerIp(
        originalForwardedForIp: String?,
        xRealIp: String?,
    ): String? {
        fun extractIps(raw: String?): List<String> =
            raw
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

        val forwardedIps = extractIps(originalForwardedForIp)
        val realIps = extractIps(xRealIp)

        val uniqueIps = (forwardedIps + realIps).distinct()

        return uniqueIps.takeIf { it.isNotEmpty() }?.joinToString(",")
    }

    private fun HttpClientErrorException.Conflict.getResponse(): Response<Any> = objectMapper.readValue(responseBodyAsString)
}
