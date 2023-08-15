package com.marketplace.model.controller

import com.marketplace.model.Product
import com.marketplace.model.request.ProductRequest
import com.marketplace.model.service.ProductService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put

@Controller("/products")
class ProductController(
    private val productService: ProductService
) {
    @Get
    fun getAllProducts(): List<Product> =
        productService.getAll()

    @Get("/{product_id}")
    fun getProductById(@PathVariable("product_id") id : String): Product =
        productService.getById(id)

    @Post
    @Consumes(MediaType.APPLICATION_JSON)
    fun saveProduct(@Body productRequest: ProductRequest): Product {
        println("Success")
        return productService.save(
            product = productRequest.toProduct()
        )
    }

    @Put("/{product_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun updateProduct(@PathVariable("product_id") id: String, @Body productRequest: ProductRequest): Product =
        productService.update(
            id, product = productRequest.toProduct()
        )

    @Delete("/{product_id}")
    fun deleteProduct(@PathVariable("product_id") id: String) =
        productService.remove(id)

    private fun ProductRequest.toProduct() : Product =
        Product(
            name = this.name,
            description = this.description,
            price = this.price
        )
}