package ru.sogaz.site.bff.service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff.service.model.PaymentMethod
import java.util.UUID

interface PaymentMethodRepository : JpaRepository<PaymentMethod, UUID> {
    fun findByType(type: String): PaymentMethod?
}
