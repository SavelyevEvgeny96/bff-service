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
        frontErrorText = "Платёжная система временно недоступна",
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

    CARD_EXPIRED(
        sourceErrorTexts =
            setOf(
                "Истек срок действия карты",
            ),
        frontErrorText = "Истек срок действия карты",
        frontErrorDescription = "Пожалуйста, повторите запрос с использованием другой карты",
    ),

    CARD_INVALID(
        sourceErrorTexts =
            setOf(
                "Карта недействительна",
            ),
        frontErrorText = "Карта недействительна",
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
            "Пожалуйста, повторите запрос. " +
                    "Убедитесь, что все данные вашей карты введены правильно и отсутствуют ошибки",
    ),

    CARD_NOT_SUPPORTED(
        sourceErrorTexts =
            setOf(
                "Карта не поддерживается",
            ),
        frontErrorText = "Карта не поддерживается",
        frontErrorDescription = "Пожалуйста, повторите запрос с использованием другой карты",
    ),

    CARD_AUTHENTICATION_FAILED(
        sourceErrorTexts =
            setOf(
                "Таймаут при выполнении аутентификации 3-D Secure",
                "Ошибка при выполнении аутентификации 3-D Secure",
                "Истекло время завершения авторизации",
            ),
        frontErrorText = "Не пройдена проверка данных карты",
        frontErrorDescription =
            "Пожалуйста, повторите запрос. " +
                    "Убедитесь, что в банке указан Ваш актуальный номер телефона и коды подтверждения " +
                    "введены правильно и отсутствуют ошибки",
    ),

    CARD_AUTHENTICATION_REFUSED(
        sourceErrorTexts =
            setOf(
                "Клиент отказался от аутентификации",
            ),
        frontErrorText = "Не пройдена проверка данных карты",
        frontErrorDescription =
            "Пожалуйста, повторите запрос. " +
                    "Введите код подтверждения проведения платежной операции",
    ),

    CARD_PAYMENT_RESTRICTED(
        sourceErrorTexts =
            setOf(
                "Транзакция не разрешена банком эмитентом или есть ограничения по карте",
            ),
        frontErrorText = "Наличие ограничений на проведение платежей по карте",
        frontErrorDescription = "Пожалуйста, повторите запрос с использованием другой карты",
    ),

    CARD_OR_ACCOUNT_RESTRICTED_BY_ISSUER(
        sourceErrorTexts =
            setOf(
                "Есть ограничения банка эмитента на карту или счет",
            ),
        frontErrorText = "Наличие ограничений на проведение платежей по карте",
        frontErrorDescription = "Пожалуйста, повторите запрос",
    ),

    CARD_AMOUNT_LIMIT_EXCEEDED(
        sourceErrorTexts =
            setOf(
                "Превышен лимит карты по общей сумме операций данного типа за период",
            ),
        frontErrorText = "Превышен лимит карты по общей сумме операций данного типа за период",
        frontErrorDescription = "Пожалуйста, повторите запрос с использованием другой карты",
    ),

    CARD_OPERATION_COUNT_LIMIT_EXCEEDED(
        sourceErrorTexts =
            setOf(
                "Превышен лимит карты по общему количеству операций данного типа за период",
            ),
        frontErrorText = "Превышен лимит карты по общему количеству операций данного типа за период",
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

    OFFER_DECLINED_BY_CLIENT(
        sourceErrorTexts =
            setOf(
                "Оферта отклонена клиентом",
            ),
        frontErrorText = "Оферта отклонена клиентом",
        frontErrorDescription = "Пожалуйста, повторите запрос",
    ),

    CARD_DATA_INPUT_DECLINED_BY_CLIENT(
        sourceErrorTexts =
            setOf(
                "Клиент отказался вводить данные платежной карты на CardPage",
            ),
        frontErrorText = "Клиент отказался вводить данные платежной карты",
        frontErrorDescription = "Пожалуйста, повторите запрос",
    ),

    WALLET_PAYMENT_DECLINED_BY_CLIENT(
        sourceErrorTexts =
            setOf(
                "Клиент отказался от оплаты с использованием кошелька MasterPass",
            ),
        frontErrorText = "Клиент отказался от оплаты с использованием электронного кошелька",
        frontErrorDescription = "Пожалуйста, повторите запрос",
    ),

    BANK_ERROR(
        sourceErrorTexts =
            setOf(
                "Ошибка авторизации",
                "Запрет на выполнение операций данного типа",
                "Карта с указанным токеном не найдена",
                "Транзакция не открыта",
                "Системная ошибка. Платеж завершился неуспешно из-за внутренней ошибки Сервиса",
                "Ошибка при взаимодействии с внешней системой",
                "Системная ошибка на стороне банка",
                "Превышен установленный лимит на общую сумму операций за отведенный период",
                "Превышен установленный лимит на количество операций за отведенный период",
                "Превышен установленный лимит на количество или общую сумму операций за отведенный период",
                "Транзакция отменена инициатором платежа",
                "Сумма операции не соответствует условиям ее проведения",
                "Отклонение запроса из-за неуспешной проверки криптовеличин",
                "Ошибка формата запроса на стороне Эквайрера",
                "Не найден оригинальный запрос или запрос задублирован",
                "Расхождение в расчёте комиссии",
                "Транзакция отклонена системой фрод-мониторинга (Сервиса или Банка)",
                "Оценка риска транзакции системой фрод-мониторинга превышает установленный порог",
                "Транзакция отклонена системой фрод-мониторинга Банка",
                "Ошибка при взаимодействии с системой фрод-мониторинга",
                "Оценка риска транзакции системой ReCaptcha превышает установленный порог",
                "Ошибка при взаимодействии с системой ReCaptcha",
                "Неверный формат авторизационного сообщения",
                "Невозможно продолжить транзакцию без перенаправления",
                "Невозможно продолжить транзакцию без отображения оферты",
                "Транзакция отклонена эквайером",
                "Транзакция отклонена эмитентом",
                "Аутентификация отклонена клиентским устройством",
                "Не выполнен запрос статуса платежа или не выполнено перенаправление",
                "Истёк таймаут на подготовку к 3-D Secure",
                "Запрет банка эмитента на выполнение операций для категории ТСП",
                "Операция не разрешена законодательством",
                "Отсутствие Эмитента в БИН-таблице, или невозможность маршрутизации запроса",
                "Банк эмитент отклонил операцию (Прочие)",
                "Ошибка при инициализации PaymentPage",
                "Таймаут при взаимодействии с клиентом на CardPage",
                "Ошибка при взаимодействии с клиентом на CardPage",
                "Магазин ответил отказом на первой фазе взаимодействия",
                "Ошибка при взаимодействии с магазином на первой фазе",
                "Ошибка при взаимодействии с магазином на завершающей фазе",
                "Ошибка при взаимодействии с магазином",
                "Ошибка при взаимодействии с кошельком MasterPass",
                "Таймаут при оплате с использованием кошелька MasterPass",
                "Не удалось выполнить подписку на SMS-информирование по операциям с виртуальной картой",
                "Пул виртуальных карт пуст",
                "Ошибка при создании автоплатежа у эквайера",
                "Ошибка при создании автоплатежа у мерчанта",
                "Ошибка при проведении автоплатежа",
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
        private val ignoredSourceErrorTexts: Set<String> =
            setOf(
                "Транзакция автоматически закрылась по таймауту",
                "Таймаут при подтверждении оферты",
            ).mapTo(mutableSetOf(), ::normalize)

        private val rulesByNormalizedSourceText: Map<String, PaymentErrorRule> =
            buildRulesMap()

        fun from(errorText: String?): PaymentErrorFrontData? {
            if (errorText.isNullOrBlank()) {
                return null
            }

            val normalizedErrorText = normalize(errorText)

            if (normalizedErrorText in ignoredSourceErrorTexts) {
                return null
            }

            return rulesByNormalizedSourceText[normalizedErrorText]
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