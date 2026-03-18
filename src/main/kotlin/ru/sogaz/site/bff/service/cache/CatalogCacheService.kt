package ru.sogaz.site.bff.service.cache

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.sogaz.site.bff.service.config.CacheConfig
import ru.sogaz.site.bff.service.model.PaymentMethod
import ru.sogaz.site.bff.service.model.Product
import ru.sogaz.site.bff.service.repository.PaymentMethodRepository
import ru.sogaz.site.bff.service.repository.ProductPaymentExceptionRepository
import ru.sogaz.site.bff.service.repository.ProductRepository
import java.util.UUID

/**
 * Сервис доступа к “справочным” данным через локальный in-memory кэш (Caffeine).
 *
 * <p>Сервис инкапсулирует кэширование и предоставляет методы чтения, которые:</p>
 * <ul>
 *   <li>Сначала пытаются вернуть значение из кэша</li>
 *   <li>Если кэша нет — читают из БД и кладут результат в кэш</li>
 * </ul>
 *
 * <p>Используются аннотации Spring Cache:</p>
 * <ul>
 *   <li>{@link Cacheable} — кэширование результата метода</li>
 * </ul>
 *
 * <p>Важно:</p>
 * <ul>
 *   <li>Кэш in-memory: при рестарте приложения пустой.</li>
 * </ul>
 */
@Service
class CatalogCacheService(
    private val productRepo: ProductRepository,
    private val paymentRepo: PaymentMethodRepository,
    private val exceptionRepo: ProductPaymentExceptionRepository,
) {
    /**
     * Получить продукт по имени (name) с кэшированием.
     *
     * <p>Ключ кэша: {@code name}</p>
     *
     * @param name значение поля {@code products.name}.
     * @return найденный {@link Product}.
     * TODO: Спросить у Вали как поступаем в случае если продукт не нашли
     * @throws IllegalArgumentException если продукт не найден.
     */
    @Cacheable(cacheNames = [CacheConfig.Names.PRODUCTS_BY_NAME], key = "#name")
    fun productByName(name: String?): Product = productRepo.findByName(name) ?: throw IllegalArgumentException("Product not found: $name")

    /**
     * Получить способ оплаты по типу (type) с кэшированием.
     *
     * <p>Ключ кэша: {@code type}</p>
     *
     * @param type значение поля {@code payment_methods.type} (например {@code CARD}, {@code SBP}).
     * @return найденный {@link PaymentMethod}.
     *  TODO: Спросить у Вали как поступаем в случае если продукт не нашли
     * @throws IllegalArgumentException если способ оплаты не найден.
     */
    @Cacheable(cacheNames = [CacheConfig.Names.PAYMENT_BY_TYPE], key = "#type")
    fun paymentByType(type: String?): PaymentMethod =
        paymentRepo.findByType(type) ?: throw IllegalArgumentException("Payment method not found: $type")

    /**
     * Получить список типов способов оплаты, которые запрещены для указанного продукта.
     *
     * <p>Ключ кэша: {@code productId}</p>
     * <p>Значение: {@code Set<String>} — набор {@code PaymentMethod.type}.</p>
     *
     * <p>Типичный use-case: “разрешенные методы” = все методы - запрещенные.</p>
     *
     * @param productId идентификатор продукта.
     * @return множество запрещенных типов оплат для продукта.
     */
    @Cacheable(cacheNames = [CacheConfig.Names.EXCLUDED_BY_PRODUCT], key = "#productId")
    fun excludedPaymentTypes(productId: UUID): Set<String> =
        exceptionRepo
            .findAllByProductId(productId)
            .mapNotNull { it.paymentMethod?.type }
            .toSet()
}
