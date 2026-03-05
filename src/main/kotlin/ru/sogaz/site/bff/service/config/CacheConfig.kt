package ru.sogaz.site.bff.service.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация кеширования приложения на базе Spring Cache + Caffeine.
 *
 * <p>Назначение:</p>
 * <ul>
 *   <li>Включить поддержку аннотаций {@code @Cacheable} через {@link EnableCaching}.</li>
 *   <li>Сконфигурировать {@link CacheManager} (провайдер — Caffeine).</li>
 * </ul>
 *
 * <p>Важно:</p>
 * <ul>
 *   <li>Caffeine по умолчанию — in-memory, т.е. кэш не пишется на диск.</li>
 *   <li>При рестарте приложения кэш автоматически пустой (JVM память очищается).</li>
 * </ul>
 */
@EnableCaching
@Configuration
class CacheConfig {
    /**
     * Имена кэшей (строки), которые используются в {@code @Cacheable}.
     */
    object Names {
        /**
         * Кэш для поиска продуктов по имени.
         *
         * <p>Ключ: {@code String name}</p>
         * <p>Значение: {@code Product}</p>
         */
        const val PRODUCTS_BY_NAME = "productsByName"

        /**
         * Кэш для поиска способов оплаты по типу.
         *
         * <p>Ключ: {@code String type}</p>
         * <p>Значение: {@code PaymentMethod}</p>
         */
        const val PAYMENT_BY_TYPE = "paymentByType"

        /**
         * Кэш исключений по продукту: какие типы оплат запрещены для продукта.
         *
         * <p>Ключ: {@code UUID productId}</p>
         * <p>Значение: {@code Set<String>} (набор {@code PaymentMethod.type})</p>
         */
        const val EXCLUDED_BY_PRODUCT = "excludedByProduct"
    }

    /**
     * Создает и настраивает {@link CacheManager} на базе {@link CaffeineCacheManager}.
     *
     * <p>Настройки:</p>
     * <ul>
     *   <li>{@code maximumSize} — ограничение количества элементов в кэше (защита памяти).</li>
     * </ul>
     *
     * @return настроенный менеджер кэша для Spring Cache.
     */
    @Bean
    fun cacheManager(): CacheManager {
        val manager =
            CaffeineCacheManager(
                Names.PRODUCTS_BY_NAME,
                Names.PAYMENT_BY_TYPE,
                Names.EXCLUDED_BY_PRODUCT,
            )

        manager.setCaffeine(
            Caffeine
                .newBuilder()
                .maximumSize(10_000),
        )

        return manager
    }
}
