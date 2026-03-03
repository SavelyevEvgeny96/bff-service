package ru.sogaz.site.bff_service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff_service.model.Product
import java.util.UUID

interface ProductRepository : JpaRepository<Product, UUID> {
    fun findByName(name: String): Product?
}