package ru.sogaz.site.bff.service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff.service.model.ProductPaymentException
import java.util.UUID

interface ProductPaymentExceptionRepository : JpaRepository<ProductPaymentException, UUID> {
    /**
     * Проверка существования исключения по паре (productId, paymentId).
     */
    fun existsByProductIdAndPaymentId(
        productId: UUID,
        paymentId: UUID,
    ): Boolean

    /**
     * Для кэша: получить исключения для продукта.
     */
    fun findAllByProductId(productId: UUID): List<ProductPaymentException>
}
