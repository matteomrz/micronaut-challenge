package com.marketplace

import com.marketplace.model.Product
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest
class GenreControllerTest(@Client("/") val client: HttpClient) {

    @Test
    fun testPostProduct() {
        val name = "coffee"
        val request = HttpRequest.POST("/products", mapOf("name" to name, "description" to "beans", "price" to 12))
        val response = client.toBlocking().exchange(request, Product::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(response.body().name, name)
    }

    @Test
    fun testUpdateProduct() {
        var request = HttpRequest.POST("/products", mapOf("name" to "coffee", "description" to "beans", "price" to 12))
        val original = client.toBlocking().exchange(request, Product::class.java)
        request = HttpRequest.PUT("products/" + original.body().id, mapOf("name" to "coffee", "description" to "beans", "price" to 9))
        val new = client.toBlocking().exchange(request, Product::class.java)

        assertEquals(HttpStatus.OK, new.status)
        assertEquals(original.body().id, new.body().id)
        assertNotEquals(original.body().price, new.body().price)
    }

    @Test
    fun testGetProduct() {
        var request = HttpRequest.POST("/products", mapOf("name" to "coffee", "description" to "beans", "price" to 12))
        val post = client.toBlocking().retrieve(request, Product::class.java)
        request = HttpRequest.GET("/products/" + post.id)
        val get = client.toBlocking().exchange(request, Product::class.java)

        assertEquals(HttpStatus.OK, get.status)
        assertEquals(post, get.body())
    }


    /**         --------------DOES NOT WORK (MANGODB STORES PRODUCTS CREATED DURING TESTING)--------------------
    @Test
    fun testGetAllProducts() {
        var request = HttpRequest.POST("/products", mapOf("name" to "coffee", "description" to "beans", "price" to 12))
        client.toBlocking().exchange(request, Product::class.java)
        request = HttpRequest.POST("/products", mapOf("name" to "tea", "description" to "dried leaves", "price" to 15))
        client.toBlocking().exchange(request, Product::class.java)
        request = HttpRequest.POST("/products", mapOf("name" to "cacao", "description" to "chocolate powder", "price" to 7))
        client.toBlocking().exchange(request, Product::class.java)
        request = HttpRequest.GET("/products/")
        val get = client.toBlocking().exchange(request, Argument.listOf(Product::class.java))

        assertEquals(HttpStatus.OK, get.status)
        assertEquals(3, get.body().size)
    }
    **/

    @Test
    fun testRemoveProduct() {
        var request = HttpRequest.POST("/products", mapOf("name" to "coffee", "description" to "beans", "price" to 12))
        var response = client.toBlocking().exchange(request, Product::class.java)

        val id = response.body().id

        request = HttpRequest.DELETE("/products/" + id)
        response = client.toBlocking().exchange(request)

        assertEquals(HttpStatus.OK, response.status)

        request = HttpRequest.GET("/products/" + id)
        val thrown = assertThrows<HttpClientResponseException> { response = client.toBlocking().exchange(request, Product::class.java) }

        assertEquals(thrown.status, HttpStatus.NOT_FOUND)
        assertNotNull(thrown.response)
    }

}