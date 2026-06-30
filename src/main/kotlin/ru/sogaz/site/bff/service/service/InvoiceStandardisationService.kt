
package ru.sogaz.site.bff.service.service

import ru.sogaz.site.bff.service.dto.response.BffResponseInvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.ResponseInvoicePayPageInfo

interface InvoiceStandardisationService {
    fun standardize(response: ResponseInvoicePayPageInfo): ResponseInvoicePayPageInfo

    fun standardize(response: ResponseInvoiceMetaInfo): BffResponseInvoiceMetaInfo
}
