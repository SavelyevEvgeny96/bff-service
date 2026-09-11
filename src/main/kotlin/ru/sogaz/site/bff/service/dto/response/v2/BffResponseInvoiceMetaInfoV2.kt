package ru.sogaz.site.bff.service.dto.response.v2

import ru.sogaz.site.bff.service.dto.response.BffInvoiceMetaPayment
import java.math.BigDecimal

data class BffResponseInvoiceMetaInfoV2(
    val status: String?,
    val code: Int?,
    val traceId: String?,
    val innerError: Any?,
    val messagesError: Any?,
    val responseUuid: String?,
    val errorsValidate: Any?,
    val data: BffInvoiceMetaInfoV2?,
)

data class BffInvoiceMetaInfoV2(
    val invoiceStatus: String?,
    val premiumAmount: BigDecimal?,
    val accounts: List<BffInvoiceMetaAccountV2>?,
    val email: String?,
    val payment: BffInvoiceMetaPayment? = null,
)

data class BffInvoiceMetaAccountV2(
    val agreementNumber: String?,
    val insuranceKind: String?,
)
