package ru.sogaz.site.bff.service.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

/**
 * Продукт/страховой продукт.
 *
 * <p>Сущность описывает продукт, для которого могут действовать исключения по способам оплаты.
 * Исключения задаются через сущность {@link ProductPaymentException}, которая фиксирует запрет
 * на оплату конкретным методом для конкретного продукта.</p>
 *
 * <p>Таблица: {@code products}.</p>
 *
 * <p>Жизненный цикл полей аудита:</p>
 * <ul>
 *   <li>{@link #createDate} заполняется автоматически при вставке записи (Hibernate {@link CreationTimestamp}).</li>
 *   <li>{@link #updateDate} обновляется автоматически при изменении записи (Hibernate {@link UpdateTimestamp}).</li>
 * </ul>
 */
@Entity
@Table(name = "products")
class Product(
    /**
     * Уникальный идентификатор продукта.
     *
     * <p>Генерируется на стороне ORM (UUID), используется как первичный ключ.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID?,
    /**
     * Код/название продукта.
     *
     * <p>Семантическое имя (например {@code AccidentInsurance}, {@code SogazFlat}). В БД поле не nullable.</p>
     */
    @Column(name = "name", nullable = false)
    var name: String,
    /**
     * Описание продукта.
     *
     * <p>Человекочитаемое описание. Поле необязательное.</p>
     */
    @Column(name = "description")
    var description: String?,
    /**
     * Дата/время создания записи (UTC-таймлайн).
     *
     * <p>Заполняется автоматически ORM при {@code INSERT}. Не изменяется при {@code UPDATE}.</p>
     */
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    var createDate: Instant?,
    /**
     * Дата/время последнего обновления записи (UTC-таймлайн).
     *
     * <p>Обновляется автоматически ORM при {@code UPDATE}.</p>
     */
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    var updateDate: Instant?,
    /**
     * Набор исключений (запрещенных комбинаций) "продукт ↔ способ оплаты", действующих для данного продукта.
     *
     * <p>Это обратная сторона связи {@link ProductPaymentException#product}.
     * Используется для навигации от продукта к запрещенным способам оплаты.</p>
     *
     * <p>Каскадирование и orphanRemoval включены — удаление элемента из коллекции приводит к удалению
     * соответствующей строки {@code product_payment_exception}.</p>
     */
    @OneToMany(
        mappedBy = "product",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val paymentExceptions: MutableSet<ProductPaymentException> = mutableSetOf(),
)
