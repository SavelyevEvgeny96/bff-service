package ru.sogaz.site.bff.service.constraint

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import ru.sogaz.site.bff.service.enums.InformationalMessageType
import kotlin.reflect.KClass

/**
 * Проверяет, что тип информационного сообщения входит в список допустимых значений.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [InformationalMessageTypeValidator::class])
annotation class ValidInformationalMessageType(
    val message: String = "{informational.message.type.invalid}",
    val payload: Array<KClass<out Payload>> = [],
    val groups: Array<KClass<*>> = [],
)

class InformationalMessageTypeValidator : ConstraintValidator<ValidInformationalMessageType, String> {
    private val acceptedValues = InformationalMessageType.entries.map { it.name }.toSet()

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.isNullOrBlank()) return true
        return acceptedValues.contains(value)
    }
}
