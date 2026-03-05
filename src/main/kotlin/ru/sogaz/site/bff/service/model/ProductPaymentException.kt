package ru.sogaz.site.bff.service.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

/**
 * Исключение по способам оплаты для продукта.
 *
 * <p>Сущность фиксирует запрет оплаты определённым способом для конкретного продукта.
 * Каждая запись представляет пару {@code (product_id, payment_id)}.</p>
 *
 * <p>Таблица: {@code product_payment_exception}.</p>
 *
 * <p>Ограничения:</p>
 * <ul>
 *   <li>Уникальность пары {@code (product_id, payment_id)} обеспечивается уникальным ограничением
 *       {@code uq_ppe_product_payment}.</li>
 *   <li>Связи на продукт и метод оплаты обязательны (NOT NULL).</li>
 * </ul>
 *
 * <p>Жизненный цикл полей аудита:</p>
 * <ul>
 *   <li>{@link #createDate} заполняется автоматически при вставке записи (Hibernate {@link CreationTimestamp}).</li>
 *   <li>{@link #updateDate} обновляется автоматически при изменении записи (Hibernate {@link UpdateTimestamp}).</li>
 * </ul>
 */
@Entity
@Table(
    name = "product_payment_exception",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_ppe_product_payment", columnNames = ["product_id", "payment_id"]),
    ],
)
class ProductPaymentException(
    /**
     * Уникальный идентификатор исключения.
     *
     * <p>Суррогатный первичный ключ (UUID). Генерируется ORM.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,
    /**
     * Продукт, для которого действует исключение.
     *
     * <p>Обязательная связь many-to-one на {@link Product}.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product?,
    /**
     * Способ оплаты, который запрещён для указанного продукта.
     *
     * <p>Обязательная связь many-to-one на {@link PaymentMethod}.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    var paymentMethod: PaymentMethod?,
    /**
     * Дата/время создания записи (UTC-таймлайн).
     *
     * <p>Заполняется автоматически ORM при {@code INSERT}. Не изменяется при {@code UPDATE}.</p>
     */
    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    var createDate: Instant? = null,
    /**
     * Дата/время последнего обновления записи (UTC-таймлайн).
     *
     * <p>Обновляется автоматически ORM при {@code UPDATE}.</p>
     */
    @UpdateTimestamp
    @Column(name = "update_date")
    var updateDate: Instant? = null,
)
