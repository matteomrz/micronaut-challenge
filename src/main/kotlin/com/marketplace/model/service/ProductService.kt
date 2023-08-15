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

    fun save(product: Product): Product {

        return productRepository.save(product)
    }

    fun getAll(): List<Product> =
        productRepository.findAll()
            .toList()

    fun getById(id: String): Product =
        productRepository.findById(id)
            .orElseThrow{HttpStatusException(HttpStatus.NOT_FOUND, "Product with ID {id} was not found.")}

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