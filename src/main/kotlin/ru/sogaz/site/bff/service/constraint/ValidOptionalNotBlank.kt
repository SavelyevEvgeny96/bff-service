package ru.sogaz.site.bff.service.constraint

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * Проверяет, что необязательное строковое поле не пустое, если оно передано в запросе.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [OptionalNotBlankValidator::class])
annotation class ValidOptionalNotBlank(
    val message: String = "{informational.message.optional-not-blank}",
    val payload: Array<KClass<out Payload>> = [],
    val groups: Array<KClass<*>> = [],
)

class OptionalNotBlankValidator : ConstraintValidator<ValidOptionalNotBlank, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean = value == null || value.isNotBlank()
}
