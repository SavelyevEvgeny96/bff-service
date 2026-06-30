package ru.sogaz.site.bff.service.dto.standardisation

import java.text.Normalizer
import java.util.Locale

data class PaymentErrorFrontData(
    val errorText: String,
    val errorDescription: String,
)

enum class PaymentErrorRule(
    private val sourceErrorTexts: Set<String>,
    private val frontErrorText: String,
    private val frontErrorDescription: String,
) {
    INSUFFICIENT_FUNDS(
        sourceErrorTexts =
            setOf(
                "Недостаточно средств на платежной карте",
            ),
        frontErrorText = "Недостаточно средств на платежной карте",
        frontErrorDescription = "Пожалуйста, повторите запрос используя другую карту",
    ),

    PAYMENT_SYSTEM_UNAVAILABLE(
        sourceErrorTexts =
            setOf(
                "Платежная система недоступна",
                "Эмитент недоступен",
            ),
        frontErrorText = "Платежная система временно недоступна",
        frontErrorDescription = "Пожалуйста, повторите запрос",
    ),

    CARD_TEMPORARILY_BLOCKED(
        sourceErrorTexts =
            setOf(
                "Карта временно заблокирована",
                "Клиент находится в чёрном списке",
            ),
        frontErrorText = "Карта временно заблокирована",
        frontErrorDescription = "Пожалуйста, повторите запрос с использованием другой карты",
    ),

    CARD_BLOCKED(
        sourceErrorTexts =
            setOf(
                "Карта заблокирована",
                "Карта заблокирована без возможности восстановления",
                "Карта утеряна или украдена",
                "Диапазон номеров карт для карты блокирован",
                "Карта утеряна",
                "Карта украдена",
                "Карта ограничена",
            ),
        frontErrorText = "Карта заблокирована",
        frontErrorDescription = "Пожалуйста, повторите запрос с использованием другой карты",
    ),

    WRONG_CARD_DATA(
        sourceErrorTexts =
            setOf(
                "Неверно указаны данные карты (например, CVV2/CVC2)",
                "Некорректный PAN",
            ),
        frontErrorText = "Неверно указаны данные карты",
        frontErrorDescription =
            "Пожалуйста, повторите запрос. " +
                "Убедитесь, что все данные вашей карты введены правильно и отсутствуют ошибки",
    ),

    CARD_DATA_PROBLEM(
        sourceErrorTexts =
            setOf(
                "Проблемы с картой, карточными данными",
            ),
        frontErrorText = "Проблема с данными платежной карты",
        frontErrorDescription =
            "Пожалуйста, повторите запрос." +
                " Убедитесь, что все данные вашей карты введены правильно и отсутствуют ошибки",
    ),

    CARD_NOT_SUPPORTED(
        sourceErrorTexts =
            setOf(
                "Карта не поддерживается",
            ),
        frontErrorText = "Карта не поддерживается",
        frontErrorDescription = "Пожалуйста, повторите запрос с использованием другой карты",
    ),

    PAYMENT_DECLINED_BY_CLIENT(
        sourceErrorTexts =
            setOf(
                "Транзакция отклонена клиентом",
            ),
        frontErrorText = "Платеж отклонен клиентом",
        frontErrorDescription = "Пожалуйста, повторите запрос",
    ),

    BANK_ERROR(
        sourceErrorTexts =
            setOf(
                "Ошибка авторизации",
                "Запрет на выполнение операций данного типа",
                "Транзакция автоматически закрылась по таймауту",
                "Карта с указанным токеном не найдена",
                "Транзакция не открыта",
                "Системная ошибка. Платеж завершился неуспешно из-за внутренней ошибки Сервиса",
                "Ошибка при взаимодействии с внешней системой",
                "Системная ошибка на стороне банка",
                "Транзакция отменена инициатором платежа",
                "Сумма операции не соответствует условиям ее проведения",
                "Транзакция отклонена эквайером",
                "Транзакция отклонена эмитентом",
                "Ошибка при инициализации PaymentPage",
                "Таймаут при взаимодействии с клиентом на CardPage",
                "Ошибка при взаимодействии с клиентом на CardPage",
                "Транзакция отклонена внешней системой",
                "Таймаут при взаимодействии с внешней системой",
            ),
        frontErrorText = "Ошибка со стороны банка при совершении платежа",
        frontErrorDescription = "Пожалуйста, повторите запрос",
    ),
    ;

    fun toFrontData(): PaymentErrorFrontData =
        PaymentErrorFrontData(
            errorText = frontErrorText,
            errorDescription = frontErrorDescription,
        )

    companion object {
        private val rulesByNormalizedSourceText: Map<String, PaymentErrorRule> =
            buildRulesMap()

        fun from(errorText: String?): PaymentErrorFrontData? {
            if (errorText.isNullOrBlank()) {
                return null
            }

            return rulesByNormalizedSourceText[normalize(errorText)]
                ?.toFrontData()
                ?: defaultError()
        }

        private fun defaultError(): PaymentErrorFrontData =
            PaymentErrorFrontData(
                errorText = "Ошибка со стороны банка при совершении платежа",
                errorDescription = "Пожалуйста, повторите запрос",
            )

        private fun buildRulesMap(): Map<String, PaymentErrorRule> {
            val pairs =
                entries.flatMap { rule ->
                    rule.sourceErrorTexts.map { sourceText ->
                        normalize(sourceText) to rule
                    }
                }

            validateDuplicates(pairs)

            return pairs.toMap()
        }

        private fun normalize(value: String): String =
            Normalizer
                .normalize(value, Normalizer.Form.NFKC)
                .replace('ё', 'е')
                .replace('Ё', 'Е')
                .replace('–', '-')
                .replace('—', '-')
                .replace(Regex("[\\s\\u00A0]+"), " ")
                .trim()
                .lowercase(Locale.ROOT)

        private fun validateDuplicates(pairs: List<Pair<String, PaymentErrorRule>>) {
            val duplicates =
                pairs
                    .groupBy { it.first }
                    .filterValues { it.size > 1 }

            require(duplicates.isEmpty()) {
                "Duplicate payment error mappings found: ${duplicates.keys}"
            }
        }
    }
}
