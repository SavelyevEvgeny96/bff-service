package ru.sogaz.site.bff.service.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy
import ru.sogaz.site.bff.service.model.CertificateVerification
import java.util.UUID

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
)
interface CertificateVerificationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateVerification", ignore = true)
    @Mapping(target = "invoiceId", source = "invoiceId")
    @Mapping(target = "certMin", source = "certMin")
    fun toEntity(invoiceId: UUID, certMin: Boolean): CertificateVerification
}
