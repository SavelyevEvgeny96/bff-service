package ru.sogaz.site.bff.service.controller

import org.springframework.web.bind.annotation.RestController
import ru.sogaz.site.bff.service.controller.v2.api.OrderingServiceV2Api
import ru.sogaz.site.bff.service.dto.response.v2.BffResponseInvoiceMetaInfoV2
import ru.sogaz.site.bff.service.mapper.InvoiceMetaInfoV2Mapper
import ru.sogaz.site.bff.service.service.impl.InvoiceStandardisationServiceImpl
import ru.sogaz.site.ordering.client.api.InvoicePayPageInfoControllerApi
import java.util.UUID

@RestController
class OrderingServiceV2Controller(
    private val invoicePayPageApi: InvoicePayPageInfoControllerApi,
    private val invoiceStandardisationService: InvoiceStandardisationServiceImpl,
    private val invoiceMetaInfoV2Mapper: InvoiceMetaInfoV2Mapper,
) : OrderingServiceV2Api {
    override fun getStatusInfoPageV2(
        invoiceId: UUID,
        payment: Boolean,
    ): BffResponseInvoiceMetaInfoV2 =
        invoicePayPageApi
            .getInvoiceMetaInfo(invoiceId, payment)
            .run(invoiceStandardisationService::standardize)
            .run(invoiceMetaInfoV2Mapper::toV2Response)
}
