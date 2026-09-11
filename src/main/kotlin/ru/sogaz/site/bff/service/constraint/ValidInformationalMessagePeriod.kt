package ru.sogaz.site.bff.service.constraint

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import ru.sogaz.site.bff.service.dto.request.InformationalMessagePeriodAware
import kotlin.reflect.KClass

/**
 * Проверяет, что displayTo больше displayWith.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [InformationalMessagePeriodValidator::class])
annotation class ValidInformationalMessagePeriod(
    val message: String = "{informational.message.display-to.after-display-with}",
    val payload: Array<KClass<out Payload>> = [],
    val groups: Array<KClass<*>> = [],
)

class InformationalMessagePeriodValidator : ConstraintValidator<ValidInformationalMessagePeriod, InformationalMessagePeriodAware> {
    override fun isValid(
        value: InformationalMessagePeriodAware?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value?.displayWith == null || value.displayTo == null) return true
        val valid = value.displayTo!!.isAfter(value.displayWith)
        if (!valid) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate("{informational.message.display-to.after-display-with}")
                .addPropertyNode("displayTo")
                .addConstraintViolation()
        }
        return valid
    }
}
