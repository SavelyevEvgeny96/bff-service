package ru.sogaz.site.bff.service.service.impl
import org.springframework.stereotype.Service
import ru.sogaz.site.bff.service.enums.InsuranceKind
import ru.sogaz.site.bff.service.service.InvoiceStandardisationService
import ru.sogaz.site.ordering.client.model.InvoiceAccountData
import ru.sogaz.site.ordering.client.model.InvoiceMetaAccount
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoicePayPageInfo
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class InvoiceStandardisationServiceImpl : InvoiceStandardisationService {
    override fun standardize(response: ResponseInvoicePayPageInfo): ResponseInvoicePayPageInfo =
        response.apply {
            data?.accounts?.forEach(::standardize)
            data?.urlPayBank =
                data
                    ?.urlPayBank
                    ?.toString()
                    ?.let(::standardizeUrlPayBank)
                    ?.let(URI::create)!!
        }

    override fun standardize(response: ResponseInvoiceMetaInfo): ResponseInvoiceMetaInfo =
        response.apply {
            data?.accounts?.forEach(::standardize)
        }

    private fun standardize(accountData: InvoiceMetaAccount): InvoiceMetaAccount =
        accountData.apply {
            insuranceKind = getInsuranceName(insuranceKind)
        }

    private fun standardize(accountData: InvoiceAccountData): InvoiceAccountData =
        accountData.apply {
            insuranceKind = getInsuranceName(insuranceKind)
        }

    private fun getInsuranceName(insuranceKind: String?): String = InsuranceKind.from(insuranceKind).desc

    private fun standardizeUrlPayBank(url: String): String {
        val queryStartIndex = url.indexOf('?')

        if (queryStartIndex == -1) {
            return url
        }

        val baseUrl = url.take(queryStartIndex)
        val query = url.substring(queryStartIndex + 1)

        val encodedQuery =
            query
                .split("&")
                .joinToString("&") { param ->
                    val name = param.substringBefore("=")
                    val value = param.substringAfter("=", "")

                    if (value.isEmpty()) {
                        name
                    } else {
                        "$name=${encodeQueryParam(value)}"
                    }
                }

        return "$baseUrl?$encodedQuery"
    }

    private fun encodeQueryParam(value: String): String {
        val decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8)

        return URLEncoder
            .encode(decodedValue, StandardCharsets.UTF_8)
            .replace("+", "%20")
    }
}
