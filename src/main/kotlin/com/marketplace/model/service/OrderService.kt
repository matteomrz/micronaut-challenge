package com.marketplace.model.service

import com.marketplace.model.Order
import com.marketplace.repository.OrderRepository
import jakarta.inject.Singleton

@Singleton
class OrderService(
   private val orderRepository: OrderRepository
) {

    fun save(order: Order): Order =
        orderRepository.save(order)

    fun getAll(): List<Order> =     //Not required for the assignment, but for testing purposes
        orderRepository.findAll()
            .toList()
}