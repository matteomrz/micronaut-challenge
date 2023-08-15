package com.marketplace.model.controller

import com.marketplace.model.Order
import com.marketplace.model.request.OrderRequest
import com.marketplace.model.service.OrderService
import com.marketplace.model.service.ProductService
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import jakarta.validation.Valid

@Validated
@Controller("/orders")
class OrderController(
    private val orderService: OrderService,
    private val productService: ProductService
) {

    @Post
    fun saveOrder(@Body @Valid orderRequest: OrderRequest): Order {
        productService.getById(orderRequest.productId)      //check whether product_id is valid
        return orderService.save(
            orderRequest.toOrder()
        )
    }

    @Get                //Not required for the assignment, but for testing purposes
    fun getAllOrders(): List<Order> =
        orderService.getAll()

    private fun OrderRequest.toOrder(): Order =
        Order (
            productId = this.productId,
            quantity = this.quantity
        )
}