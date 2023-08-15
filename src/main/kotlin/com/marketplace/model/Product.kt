package com.marketplace.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity
data class Product(
    @field:Id
    @field:GeneratedValue
    val id: String? = null,
    var name: String,
    var description: String,
    var price: Int
)
