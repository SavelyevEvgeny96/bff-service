package ru.sogaz.site.bff.service.service.impl

import org.springframework.stereotype.Service
import ru.sogaz.site.bff.service.enums.InsuranceKind
import ru.sogaz.site.bff.service.service.InvoiceStandardisationService
import ru.sogaz.site.ordering.client.model.InvoiceMetaAccount
import ru.sogaz.site.ordering.client.model.InvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo

@Service
class InvoiceStandardisationServiceImpl: InvoiceStandardisationService {
    override fun standardize(response: ResponseInvoiceMetaInfo): ResponseInvoiceMetaInfo =
        response.apply {
            data?.standardize()
        }

    private fun InvoiceMetaInfo.standardize(): InvoiceMetaInfo =
        apply {
            accounts?.forEach { it.standardize()}
        }

    private fun InvoiceMetaAccount.standardize(): InvoiceMetaAccount =
        apply {
            insuranceKind = getInsuranceName(insuranceKind)
        }

    private fun getInsuranceName(insuranceKind: String?): String = InsuranceKind.from(insuranceKind).desc
}