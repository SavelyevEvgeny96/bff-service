package ru.sogaz.site.bff.service.service.impl

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.sogaz.site.bff.service.dto.request.ProductPaymentExceptionsChangedEvent
import ru.sogaz.site.bff.service.exceptions.PaymentExceptionAlreadyExistsException
import ru.sogaz.site.bff.service.model.ProductPaymentException
import ru.sogaz.site.bff.service.repository.PaymentMethodRepository
import ru.sogaz.site.bff.service.repository.ProductPaymentExceptionRepository
import ru.sogaz.site.bff.service.repository.ProductRepository
import ru.sogaz.site.exceptionStarter.starter.dto.exceptions.InnerException
import ru.sogaz.site.filterStarter.services.RequestInfo
import java.util.UUID

/**
 * Сервис администрирования исключений “продукт ↔ способ оплаты”.
 */
@Service
class AdminPaymentExceptionServiceImpl(
    private val productRepo: ProductRepository,
    private val paymentRepo: PaymentMethodRepository,
    private val exceptionRepo: ProductPaymentExceptionRepository,
    private val publisher: ApplicationEventPublisher,
) {
    companion object {
        const val PRODUCT_PAYMENT_EXCEPTION_IS_EMPTY = "Исключение по этому данному productId: %s, уже заведено"
        const val ERROR_SAVE_PRODUCT_PAYMENT_EXCEPTION = "Ошибка при сохранении исключения"
        const val PAYMENT_TYPE_NOT_FOUND =
            "Ошибка при поиске вида типа оплаты paymentType: %s, " +
                "нет в списке допустимых значений "
        const val PRODUCT_NOT_FOUND = "Ошибка при поиске продукта по productId: %s , нет в списке допустимых значений "
    }

    /**
     * Создает исключение (productId + paymentType).
     *
     * @throws ru.sogaz.site.exceptionStarter.starter.dto.exceptions.InnerException если paymentType не найден и если продукт не найден
     * @throws ru.sogaz.site.bff.service.exceptions.PaymentExceptionAlreadyExistsException если исключение уже существует
     */
    @Transactional
    fun createException(
        productId: UUID,
        paymentType: String,
    ): UUID {
        val product =
            productRepo
                .findById(productId)
                .orElseThrow { InnerException(RequestInfo.getTraceId(), PRODUCT_NOT_FOUND.format(productId)) }

        val method =
            paymentRepo.findByType(paymentType)
                ?: throw InnerException(RequestInfo.getTraceId(), PAYMENT_TYPE_NOT_FOUND.format(paymentType))

        if (method.id?.let { exceptionRepo.existsByProductIdAndPaymentMethodId(productId, it) } == true) {
            throw InnerException(RequestInfo.getTraceId(), PRODUCT_PAYMENT_EXCEPTION_IS_EMPTY.format(productId))
        }

        // Сохраняем и форсим flush, чтобы поймать UNIQUE(product_id, payment_id) в рамках этого метода
        try {
            val saved =
                exceptionRepo.saveAndFlush(
                    ProductPaymentException(
                        product = product,
                        paymentMethod = method,
                    ),
                )

            // Обновление кэша делаем ПОСЛЕ коммита (слушатель AFTER_COMMIT)
            publisher.publishEvent(ProductPaymentExceptionsChangedEvent(productId))

            return saved.id ?: throw InnerException(RequestInfo.getTraceId(), ERROR_SAVE_PRODUCT_PAYMENT_EXCEPTION)
        } catch (e: Exception) {
            // На случай гонки: два запроса одновременно -> UNIQUE сработал
            throw PaymentExceptionAlreadyExistsException(productId, paymentType, e)
        }
    }
}
