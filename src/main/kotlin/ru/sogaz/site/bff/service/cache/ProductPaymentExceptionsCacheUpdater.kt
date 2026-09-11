package ru.sogaz.site.bff.service.cache

import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import ru.sogaz.site.bff.service.config.CacheConfig
import ru.sogaz.site.bff.service.dto.request.ProductPaymentExceptionsChangedEvent
import ru.sogaz.site.bff.service.loggerFor

/**
 * Точечная инвалидация/перепрогрев кэша исключений после успешного коммита.
 */
@Component
class ProductPaymentExceptionsCacheUpdater(
    private val cacheManager: CacheManager,
    private val catalogCacheService: CatalogCacheService,
) {
    private companion object {
        val log = loggerFor(ProductPaymentExceptionsCacheUpdater::class.java)
    }

    /**
     * После коммита транзакции: evict ключа productId и перепрогрев excludedPaymentTypes(productId).
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onChanged(event: ProductPaymentExceptionsChangedEvent) {
        cacheManager
            .getCache(CacheConfig.Names.EXCLUDED_BY_PRODUCT)
            ?.evict(event.productId)

        val excluded = catalogCacheService.excludedPaymentTypes(event.productId)

        log.info(
            "Cache updated: {} key={} size={}",
            CacheConfig.Names.EXCLUDED_BY_PRODUCT,
            event.productId,
            excluded.size,
        )
    }
}
