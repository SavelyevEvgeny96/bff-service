package ru.sogaz.site.bff.service.dto.response

/**
 * Объект бизнес-данных ответа.
 *
 * Содержит список добавленных сущностей (продуктов или способов оплаты),
 * которые были успешно сохранены в базе данных.
 *
 * @property list список добавленных элементов
 */
data class DataResponseAddProductOrType(
    val list: List<ItemResponse?>,
)

/**
 * DTO элемента ответа.
 *
 * Представляет одну созданную запись (продукт или способ оплаты),
 * возвращаемую в ответе метода add.
 *
 * @property id уникальный идентификатор созданной записи (UUID в строковом формате)
 * @property type тип элемента:
 * - PRODUCT — продукт
 * - PAYMENT_METHOD — способ оплаты
 * @property name наименование элемента:
 * - для PRODUCT — название продукта
 * - для PAYMENT_METHOD — значение поля type (например CARD, SBP)
 */
data class ItemResponse(
    val id: String,
    val type: String,
    val name: String?,
)
