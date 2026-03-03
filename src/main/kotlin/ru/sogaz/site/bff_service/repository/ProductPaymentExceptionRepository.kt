package ru.sogaz.site.bff_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff_service.model.ProductPaymentException
import java.util.UUID

interface ProductPaymentExceptionRepository : JpaRepository<ProductPaymentException, UUID> {
    fun findAllByProductId(productId: UUID): List<ProductPaymentException>
}