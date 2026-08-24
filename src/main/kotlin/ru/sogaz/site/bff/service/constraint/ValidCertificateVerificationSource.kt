package ru.sogaz.site.bff.service.constraint

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import ru.sogaz.site.bff.service.dto.request.CertificateVerificationRequest
import java.util.UUID
import kotlin.reflect.KClass

/** Проверяет наличие shortLink или invoiceId и формат переданного invoiceId. */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CertificateVerificationSourceValidator::class])
annotation class ValidCertificateVerificationSource(
    val message: String = "Не заполнено обязательное значение",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class CertificateVerificationSourceValidator :
    ConstraintValidator<ValidCertificateVerificationSource, CertificateVerificationRequest> {
    override fun isValid(
        value: CertificateVerificationRequest?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return true

        val hasShortLink = !value.shortLink.isNullOrBlank()
        val hasInvoiceId = !value.invoiceId.isNullOrBlank()
        if (!hasShortLink && !hasInvoiceId) {
            context.disableDefaultConstraintViolation()
            addViolation(context, "shortLink", "Не заполнено обязательное значение")
            addViolation(context, "invoiceId", "Не заполнено обязательное значение")
            return false
        }

        if (hasInvoiceId && !value.invoiceId.isUuid()) {
            context.disableDefaultConstraintViolation()
            addViolation(context, "invoiceId", "Некорректный формат идентификатора счета")
            return false
        }
        return true
    }

    private fun String?.isUuid(): Boolean = runCatching { UUID.fromString(this) }.isSuccess

    private fun addViolation(
        context: ConstraintValidatorContext,
        property: String,
        message: String,
    ) {
        context
            .buildConstraintViolationWithTemplate(message)
            .addPropertyNode(property)
            .addConstraintViolation()
    }
}
