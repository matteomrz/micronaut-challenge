package com.marketplace.model.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable.Deserializable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

@Deserializable
@Introspected
data class OrderRequest(
    @field:NotBlank(message = "productId can not be empty!")
    val productId: String,
    @field:Positive(message = "quantity must be greater than 0!")
    val quantity: Int
)
