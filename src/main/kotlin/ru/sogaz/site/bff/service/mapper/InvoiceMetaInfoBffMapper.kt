package ru.sogaz.site.bff.service.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.Named
import org.mapstruct.ReportingPolicy
import ru.sogaz.site.bff.service.dto.response.BffInvoiceMetaAccount
import ru.sogaz.site.bff.service.dto.response.BffInvoiceMetaInfo
import ru.sogaz.site.bff.service.dto.response.BffInvoiceMetaPayment
import ru.sogaz.site.bff.service.dto.response.BffResponseInvoiceMetaInfo
import ru.sogaz.site.bff.service.dto.standardisation.PaymentErrorRule
import ru.sogaz.site.bff.service.enums.InsuranceKind
import ru.sogaz.site.ordering.client.model.InvoiceMetaAccount
import ru.sogaz.site.ordering.client.model.InvoiceMetaInfo
import ru.sogaz.site.ordering.client.model.InvoiceMetaPayment
import ru.sogaz.site.ordering.client.model.ResponseInvoiceMetaInfo
import java.time.Instant
import java.time.OffsetDateTime

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
)
abstract class InvoiceMetaInfoBffMapper {
    @Mapping(target = "status", source = "status", qualifiedByName = ["toNullableString"])
    @Mapping(target = "responseUuid", source = "responseUuid", qualifiedByName = ["toNullableString"])
    abstract fun toBffResponse(response: ResponseInvoiceMetaInfo): BffResponseInvoiceMetaInfo

    abstract fun toBffInvoiceMetaInfo(data: InvoiceMetaInfo): BffInvoiceMetaInfo

    @Mapping(target = "insuranceKind", source = "insuranceKind", qualifiedByName = ["mapInsuranceKind"])
    abstract fun toBffInvoiceMetaAccount(account: InvoiceMetaAccount): BffInvoiceMetaAccount

    @Mapping(target = "status", source = "status", qualifiedByName = ["toNullableString"])
    @Mapping(target = "errorText", source = "errorText", qualifiedByName = ["mapPaymentErrorText"])
    @Mapping(target = "errorDescription", source = "errorText", qualifiedByName = ["mapPaymentErrorDescription"])
    abstract fun toBffInvoiceMetaPayment(payment: InvoiceMetaPayment): BffInvoiceMetaPayment

    @Named("mapInsuranceKind")
    protected fun mapInsuranceKind(insuranceKind: String?): String = InsuranceKind.from(insuranceKind).desc

    @Named("mapPaymentErrorText")
    protected fun mapPaymentErrorText(errorText: String?): String? = PaymentErrorRule.from(errorText)?.errorText

    @Named("mapPaymentErrorDescription")
    protected fun mapPaymentErrorDescription(errorText: String?): String? = PaymentErrorRule.from(errorText)?.errorDescription

    @Named("toNullableString")
    protected fun toNullableString(value: Any?): String? = value?.toString()

    protected fun map(value: OffsetDateTime?): Instant? = value?.toInstant()
}
