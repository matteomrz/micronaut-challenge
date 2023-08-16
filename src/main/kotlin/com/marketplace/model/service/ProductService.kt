package com.marketplace.model.service

import com.marketplace.model.Product
import com.marketplace.repository.ProductRepository
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Singleton

@Singleton
class ProductService(
    private val productRepository: ProductRepository
) {

    private val hexRegex = """^[0-9a-fA-F]{24}$""".toRegex() //Needed Format for getById to work properly

    fun save(product: Product): Product =
        productRepository.save(product)

    fun getAll(): List<Product> =
        productRepository.findAll()
            .toList()

    fun getById(id: String): Product {
        if (!hexRegex.matches(id)) {
            throw HttpStatusException(HttpStatus.BAD_REQUEST,
                "Product ID does not have the correct format. A valid ID should consist of exactly 24 characters, each representing a valid hexadecimal digit (0-9, a-f, or A-F).")
        }

        return productRepository.findById(id)
            .orElseThrow { HttpStatusException(HttpStatus.NOT_FOUND, "Product with ID {id} was not found.") }
    }

    fun update(id: String, product: Product): Product {
        val saved = getById(id)
        saved.name = product.name
        saved.description = product.description
        saved.price = product.price
        return saved
    }

    fun remove(id: String) {
        getById(id)                         //Check whether product was in the database before deletion
        productRepository.deleteById(id)
    }
}