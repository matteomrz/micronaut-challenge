package com.marketplace.repository

import com.marketplace.model.Product
import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.CrudRepository

@MongoRepository
interface ProductRepository : CrudRepository<Product, String>