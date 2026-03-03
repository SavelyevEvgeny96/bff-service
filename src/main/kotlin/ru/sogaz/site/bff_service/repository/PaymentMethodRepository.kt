package ru.sogaz.site.bff_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff_service.model.PaymentMethod
import java.util.UUID

interface PaymentMethodRepository : JpaRepository<PaymentMethod, UUID> {
    fun findByType(type: String): PaymentMethod?
}