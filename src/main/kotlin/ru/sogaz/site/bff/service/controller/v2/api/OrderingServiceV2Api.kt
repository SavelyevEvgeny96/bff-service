package ru.sogaz.site.bff.service.controller.v2.api

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import ru.sogaz.site.bff.service.dto.response.v2.BffResponseInvoiceMetaInfoV2
import java.util.UUID

@Tag(
    name = "Ordering Service API V2",
    description = "API для получения информации по заказу без служебных платежных данных",
)
@Validated
@RequestMapping("/v2")
interface OrderingServiceV2Api {
    @GetMapping("/pagepayinfo/info/{invoiceId}")
    fun getStatusInfoPageV2(
        @PathVariable invoiceId: UUID,
        @Parameter(
            name = "payment",
            description = "Признак необходимости вернуть информацию с учетом оплаты. По умолчанию false",
            required = false,
            `in` = ParameterIn.QUERY,
            schema = Schema(type = "boolean", defaultValue = "false"),
        ) payment: Boolean,
    ): BffResponseInvoiceMetaInfoV2
}
