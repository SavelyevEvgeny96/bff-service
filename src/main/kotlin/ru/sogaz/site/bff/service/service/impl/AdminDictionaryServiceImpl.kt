package ru.sogaz.site.bff.service.service.impl

import org.springframework.stereotype.Service
import ru.sogaz.site.bff.service.dto.request.AdminAddRequest
import ru.sogaz.site.bff.service.dto.response.ItemResponse
import ru.sogaz.site.bff.service.enums.AdminDictionaryElementType
import ru.sogaz.site.bff.service.model.PaymentMethod
import ru.sogaz.site.bff.service.model.Product
import ru.sogaz.site.bff.service.repository.PaymentMethodRepository
import ru.sogaz.site.bff.service.repository.ProductRepository
import ru.sogaz.site.bff.service.service.AdminDictionaryService

/**
 * Реализация сервиса для работы со справочными элементами,
 * добавляемыми через административную ручку.
 *
 * Сервис обрабатывает список элементов из запроса и в зависимости
 * от их типа выполняет:
 * - поиск существующей записи в базе данных;
 * - создание новой записи, если существующая не найдена;
 * - преобразование результата в DTO ответа.
 *
 * Поддерживаемые типы элементов:
 * - [AdminDictionaryElementType.PRODUCT] — продукт;
 * - [AdminDictionaryElementType.PAYMENT_METHOD] — способ оплаты.
 *
 * Если запись уже существует:
 * - для продукта поиск выполняется по полю name;
 * - для способа оплаты поиск выполняется по полю type.
 *
 * Если запись не найдена, она создаётся и возвращается в ответе.
 *
 * @property productRepository репозиторий для работы с продуктами
 * @property paymentMethodRepository репозиторий для работы со способами оплаты
 */
@Service
class AdminDictionaryServiceImpl(
    private val productRepository: ProductRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : AdminDictionaryService {
    /**
     * Добавляет элементы справочника из входного запроса.
     *
     * Для каждого элемента из списка:
     * - если тип = PRODUCT, выполняется поиск продукта по имени;
     * - если тип = PAYMENT_METHOD, выполняется поиск способа оплаты по типу;
     * - если запись уже существует, возвращается она;
     * - если запись отсутствует, создаётся новая.
     *
     * @param request запрос на добавление элементов справочника
     * @return список элементов, найденных или созданных в базе данных
     */
    override fun add(request: AdminAddRequest): List<ItemResponse?> =
        request.list.map { item ->
            when (item.type) {
                AdminDictionaryElementType.PRODUCT.name -> {
                    val product =
                        productRepository.findByName(item.name)
                            ?: productRepository.save(
                                Product(
                                    name = item.name,
                                    description = item.description,
                                ),
                            )

                    ItemResponse(
                        id = product.id.toString(),
                        type = AdminDictionaryElementType.PRODUCT.name,
                        name = product.name,
                    )
                }

                AdminDictionaryElementType.PAYMENT_METHOD.name -> {
                    val payment =
                        paymentMethodRepository.findByType(item.name)
                            ?: paymentMethodRepository.save(
                                PaymentMethod(
                                    type = item.name,
                                    description = item.description,
                                ),
                            )

                    ItemResponse(
                        id = payment.id.toString(),
                        type = AdminDictionaryElementType.PAYMENT_METHOD.name,
                        name = payment.type,
                    )
                }
                else -> null
            }
        }
}
