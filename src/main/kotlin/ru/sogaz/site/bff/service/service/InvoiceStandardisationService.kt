package ru.sogaz.site.bff.service.service

import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo

interface InvoiceStandardisationService {
    fun standardize(response: ResponseInvoiceMetaInfo): ResponseInvoiceMetaInfo
}