package ru.sogaz.site.bff.service.service.impl

import org.springframework.stereotype.Service
import ru.sogaz.site.bff.service.enums.InsuranceKind
import ru.sogaz.site.bff.service.service.InvoiceStandardisationService
import ru.sogaz.site.ordering.client.model.InvoiceAccountData
import ru.sogaz.site.ordering.client.model.InvoiceMetaAccount
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoicePayPageInfo

@Service
class InvoiceStandardisationServiceImpl : InvoiceStandardisationService {
    override fun standardize(response: ResponseInvoicePayPageInfo): ResponseInvoicePayPageInfo =
        response.apply {
            data?.accounts?.forEach(::standardize)
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
}
