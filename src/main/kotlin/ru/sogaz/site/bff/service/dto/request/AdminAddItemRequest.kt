package ru.sogaz.site.bff.service.dto.request

import jakarta.validation.constraints.NotBlank
import ru.sogaz.site.bff.service.constraint.ValidEnumPaymentType
import ru.sogaz.site.bff.service.enums.AdminDictionaryElementType

/**
 * Элемент запроса на добавление.
 *
 * @property type тип добавляемого элемента.
 * @property name наименование элемента.
 * @property description описание элемента.
 */
data class AdminAddItemRequest(
    /**
     * Тип элемента.
     */
    @field:ValidEnumPaymentType(
        enumClass = AdminDictionaryElementType::class,
        message = "Значение содержит не допустимый тип элемента",
    )
    val type: String?,
    /**
     * Наименование элемента.
     */
    @field:NotBlank(message = "Параметр name обязателен")
    val name: String?,
    /**
     * Описание элемента.
     */
    @field:NotBlank(message = "Параметр description обязателен")
    val description: String?,
)
