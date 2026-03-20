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
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

/**
 * Справочник способов оплаты.
 *
 * <p>Сущность хранит тип способа оплаты (например, {@code CARD}, {@code SBP}) и его описание.
 * Используется для построения доступных способов оплаты по продуктам с учетом исключений,
 * заданных в {@link ProductPaymentException}.</p>
 *
 * <p>Таблица: {@code payment_methods}.</p>
 *
 * <p>Жизненный цикл полей аудита:</p>
 * <ul>
 *   <li>{@link #createDate} заполняется автоматически при вставке записи (Hibernate {@link CreationTimestamp}).</li>
 *   <li>{@link #updateDate} обновляется автоматически при изменении записи (Hibernate {@link UpdateTimestamp}).</li>
 * </ul>
 */
@Entity
@Table(name = "payment_methods")
class PaymentMethod(
    /**
     * Уникальный идентификатор способа оплаты.
     *
     * <p>Генерируется на стороне ORM (UUID), используется как первичный ключ.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,
    /**
     * Тип способа оплаты.
     *
     * <p>Семантический код (например {@code CARD}, {@code SBP}). В БД поле не nullable.</p>
     */
    @Column(name = "type", nullable = false)
    var type: String?,
    /**
     * Человекочитаемое описание способа оплаты.
     *
     * <p>Например: "Банковская карта", "СБП". Поле необязательное.</p>
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
    var createDate: Instant? = null,
    /**
     * Дата/время последнего обновления записи (UTC-таймлайн).
     *
     * <p>Обновляется автоматически ORM при {@code UPDATE}.</p>
     */
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    var updateDate: Instant? = null,
    /**
     * Набор исключений (запрещенных комбинаций) "продукт ↔ способ оплаты", где данный способ оплаты запрещён.
     *
     * <p>Это обратная сторона связи {@link ProductPaymentException#paymentMethod}.
     * Используется для навигации от способа оплаты к продуктам, для которых он исключён.</p>
     *
     * <p>Каскадирование и orphanRemoval включены — удаление элемента из коллекции приводит к удалению
     * соответствующей строки {@code product_payment_exception}.</p>
     */
    @OneToMany(
        mappedBy = "paymentMethod",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    @Fetch(FetchMode.SUBSELECT)
    var productExceptions: MutableSet<ProductPaymentException> = mutableSetOf(),
)