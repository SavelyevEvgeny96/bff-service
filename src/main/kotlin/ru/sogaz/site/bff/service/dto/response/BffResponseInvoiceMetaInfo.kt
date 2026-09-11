package ru.sogaz.site.bff.service.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BffResponseInvoiceMetaInfo(
    val status: String?,
    val code: Int?,
    val traceId: String?,
    val innerError: Any?,
    val messagesError: Any?,
    val responseUuid: String?,
    val errorsValidate: Any?,
    val data: BffInvoiceMetaInfo?,
)

data class BffInvoiceMetaInfo(
    val invoiceId: UUID?,
    val invoiceStatus: String?,
    val premiumAmount: BigDecimal?,
    val accounts: List<BffInvoiceMetaAccount>?,
    val invoiceEndDate: Instant?,
    val externalId: String?,
    val typePaymentOperation: String?,
    val email: String?,
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val payment: BffInvoiceMetaPayment? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BffInvoiceMetaPayment(
    val status: String?,
    val errorText: String? = null,
    val errorDescription: String? = null,
)

data class BffInvoiceMetaAccount(
    val policyNumber: String?,
    val policyDate: Instant?,
    val agreementPrice: String?,
    val agreementNumber: String?,
    val agreementDate: Instant?,
    val typeOperation: String?,
    val insuranceKind: String?,
    val program: String?,
    val channel: String?,
)
