package ru.sogaz.site.bff.service.constraint

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * Проверяет, что значение строки соответствует одному из значений enum.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnumValidator::class])
annotation class ValidEnumPaymentType(
    val message: String = "Invalid value. Allowed values: {enumClass}",
    val enumClass: KClass<out Enum<*>>,
    val payload: Array<KClass<out Payload>> = [],
    val groups: Array<KClass<*>> = [],
)

class EnumValidator : ConstraintValidator<ValidEnumPaymentType, String> {
    private lateinit var acceptedValues: Set<String>

    override fun initialize(annotation: ValidEnumPaymentType) {
        acceptedValues =
            annotation.enumClass.java
                .enumConstants
                .map { it.name }
                .toSet()
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return false
        return acceptedValues.contains(value)
    }
}
