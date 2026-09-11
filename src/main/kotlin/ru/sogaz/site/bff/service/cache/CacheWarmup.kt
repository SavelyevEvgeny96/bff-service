package ru.sogaz.site.bff.service.cache

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.sogaz.site.bff.service.config.CacheConfig
import ru.sogaz.site.bff.service.loggerFor
import ru.sogaz.site.bff.service.repository.PaymentMethodRepository
import ru.sogaz.site.bff.service.repository.ProductRepository

/**
 * Прогрев и принудительная очистка локального кэша на старте приложения.
 *
 * <p>Почему {@link ApplicationReadyEvent}:</p>
 * <ul>
 *   <li>На этом этапе Spring Boot уже поднял контекст.</li>
 *   <li>Liquibase к этому моменту уже применил актуальные миграции (если включен в проекте).</li>
 * </ul>
 *
 * <p>Алгоритм:</p>
 * <ol>
 *   <li>Очистить кэши через {@link CacheManager} (на всякий случай).</li>
 *   <li>Загрузить справочники из БД и “потрогать” кэшируемые методы, чтобы заполнить кэш.</li>
 * </ol>
 *
 * <p>Важно:</p>
 * <ul>
 *   <li>Даже без явной очистки кэш после рестарта пустой (in-memory), но очистка делает поведение явным.</li>
 * </ul>
 */
@Component
class CacheWarmup(
    private val cacheManager: CacheManager,
    private val productRepo: ProductRepository,
    private val paymentRepo: PaymentMethodRepository,
    private val cacheService: CatalogCacheService,
) {
    /**
     * Обработчик события готовности приложения.
     *
     * <p>Метод очищает кэши и прогревает их актуальным состоянием БД.</p>
     */
    @EventListener(ApplicationReadyEvent::class)
    fun warmup() {
        // 1) явная очистка кэшей
        cacheManager.getCache(CacheConfig.Names.PRODUCTS_BY_NAME)?.clear()
        cacheManager.getCache(CacheConfig.Names.PAYMENT_BY_TYPE)?.clear()
        cacheManager.getCache(CacheConfig.Names.EXCLUDED_BY_PRODUCT)?.clear()

        // 2) прогрев: продукты
        val products = productRepo.findAll()
        products.forEach { p ->
            cacheService.productByName(p.name)
            p.id?.let { cacheService.excludedPaymentTypes(it) }
        }

        // 3) прогрев: способы оплаты
        val methods = paymentRepo.findAll()
        methods.forEach { m ->
            cacheService.paymentByType(m.type)
        }

        // 4) логируем только количества элементов в кэшах
        logCacheSizes()
    }

    /**
     * Логирует количество элементов в каждом кэше (только числа, без ключей/значений).
     *
     * <p>Если кэш не Caffeine или не найден — вернет {@code -1} для него.</p>
     */
    private fun logCacheSizes() {
        fun sizeOf(cacheName: String): Long {
            val springCache = cacheManager.getCache(cacheName) as? CaffeineCache ?: return -1L
            return springCache.nativeCache.estimatedSize()
        }

        log.info(
            "Cache sizes: {}={}, {}={}, {}={}",
            CacheConfig.Names.PRODUCTS_BY_NAME,
            sizeOf(CacheConfig.Names.PRODUCTS_BY_NAME),
            CacheConfig.Names.PAYMENT_BY_TYPE,
            sizeOf(CacheConfig.Names.PAYMENT_BY_TYPE),
            CacheConfig.Names.EXCLUDED_BY_PRODUCT,
            sizeOf(CacheConfig.Names.EXCLUDED_BY_PRODUCT),
        )
    }

    private companion object {
        val log = loggerFor(CacheWarmup::class.java)
    }
}
