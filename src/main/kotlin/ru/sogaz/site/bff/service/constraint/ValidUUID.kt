package ru.sogaz.site.bff.service.constraint

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.util.UUID
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UUIDValidator::class])
annotation class ValidUUID(
    val message: String = "Не заполнено обязательное значение",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class UUIDValidator : ConstraintValidator<ValidUUID, String> {
    override fun isValid(
        value: String?,
        p1: ConstraintValidatorContext?,
    ): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }
        return try {
            UUID.fromString(value)
            true
        } catch (ex: IllegalArgumentException) {
            false
        }
    }
}
