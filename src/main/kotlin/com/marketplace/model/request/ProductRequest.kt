package com.marketplace.model.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable.Deserializable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

@Deserializable
@Introspected
data class ProductRequest(
    @field:NotBlank(message = "name can not be empty!")
    val name: String,
    @field:NotBlank(message = "description can not be empty!")
    val description: String,
    @field:Positive(message = "price must be greater than 0!")
    val price: Int
)
