package ru.sogaz.site.bff.service.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy
import ru.sogaz.site.bff.service.dto.response.BffInvoiceMetaAccount
import ru.sogaz.site.bff.service.dto.response.BffInvoiceMetaInfo
import ru.sogaz.site.bff.service.dto.response.BffResponseInvoiceMetaInfo
import ru.sogaz.site.bff.service.dto.response.v2.BffInvoiceMetaAccountV2
import ru.sogaz.site.bff.service.dto.response.v2.BffInvoiceMetaInfoV2
import ru.sogaz.site.bff.service.dto.response.v2.BffResponseInvoiceMetaInfoV2

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
)
interface InvoiceMetaInfoV2Mapper {
    fun toV2Response(response: BffResponseInvoiceMetaInfo): BffResponseInvoiceMetaInfoV2

    fun toV2InvoiceMetaInfo(data: BffInvoiceMetaInfo): BffInvoiceMetaInfoV2

    fun toV2InvoiceMetaAccount(account: BffInvoiceMetaAccount): BffInvoiceMetaAccountV2
}
