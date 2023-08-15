package com.marketplace.model.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable.Deserializable

@Deserializable
@Introspected
data class ProductRequest(
    val name: String,
    val description: String,
    val price: Int
)
