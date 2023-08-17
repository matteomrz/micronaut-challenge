package com.marketplace

import com.marketplace.model.Order
import com.marketplace.model.Product
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest
class ProductAndOrderControllerTest(@Client("/") val client: HttpClient) {

    private lateinit var testProduct: HttpResponse<Product>

    @BeforeEach
    fun setUp() {
        val request = HttpRequest.POST("/products", mapOf("name" to "coffee", "description" to "beans", "price" to 12))
        testProduct = client.toBlocking().exchange(request, Product::class.java)
    }

    @AfterEach
    fun tearDown() {
        val request = HttpRequest.DELETE<Any>("/products/" + testProduct.body().id)
        client.toBlocking().exchange<Any, Any>(request)
    }

    @Test
    fun testPostProduct() {
        val name = "tea"
        val request = HttpRequest.POST("/products", mapOf("name" to name, "description" to "dried leaves", "price" to 15))
        val response = client.toBlocking().exchange(request, Product::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(response.body().name, name)

        tearDown()
        testProduct = response
    }

    @Test
    fun testPostProductInvalidParameters() {
        val request = HttpRequest.POST("/products", mapOf("name" to "", "description" to "", "price" to 0))
        val thrown = assertThrows<HttpClientResponseException> { client.toBlocking().exchange(request, Product::class.java) }

        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)
    }

    @Test
    fun testUpdateProduct() {
        val request = HttpRequest.PUT("products/" + testProduct.body().id, mapOf("name" to "coffee", "description" to "beans", "price" to 9))
        val new = client.toBlocking().exchange(request, Product::class.java)

        assertEquals(HttpStatus.OK, new.status)
        assertEquals(testProduct.body().id, new.body().id)
        assertNotEquals(testProduct.body().price, new.body().price)
    }

    @Test
    fun testGetProduct() {
        val request = HttpRequest.GET<Any>("/products/" + testProduct.body().id)
        val get = client.toBlocking().exchange(request, Product::class.java)

        assertEquals(HttpStatus.OK, get.status)
        assertEquals(testProduct.body(), get.body())
    }

    @Test
    fun testGetProductInvalidID() {
        val request = HttpRequest.GET<Any>("/products/invalidID")
        val thrown = assertThrows<HttpClientResponseException> { client.toBlocking().exchange(request, Product::class.java) }

        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)
    }

    @Test
    fun testGetAllProducts() {
        var request = HttpRequest.POST("/products", mapOf("name" to "tea", "description" to "dried leaves", "price" to 15))
        val secondProduct = client.toBlocking().exchange(request, Product::class.java)

        request = HttpRequest.GET("/products/")
        val get = client.toBlocking().exchange(request, Argument.listOf(Product::class.java))

        assertEquals(HttpStatus.OK, get.status)
        assertEquals(2, get.body().size)
        assertTrue(get.body().contains(secondProduct.body()))
        assertTrue(get.body().contains(testProduct.body()))

        tearDown()
        testProduct = secondProduct
    }

    @Test
    fun testRemoveProduct() {
        var request = HttpRequest.POST("/products", mapOf("name" to "coffee", "description" to "beans", "price" to 12))
        var response = client.toBlocking().exchange(request, Product::class.java)

        val id = response.body().id

        request = HttpRequest.DELETE("/products/$id")
        response = client.toBlocking().exchange(request)

        assertEquals(HttpStatus.OK, response.status)

        request = HttpRequest.GET("/products/$id")
        val thrown = assertThrows<HttpClientResponseException> { response = client.toBlocking().exchange(request, Product::class.java) }

        assertEquals(thrown.status, HttpStatus.NOT_FOUND)
        assertNotNull(thrown.response)
    }

    @Test
    fun testPostOrder() {
        val request = HttpRequest.POST("/orders", mapOf("productId" to testProduct.body().id, "quantity" to 12))
        val response = client.toBlocking().exchange(request, Order::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(response.body().productId, testProduct.body().id)
    }

    @Test
    fun testPostOrderInvalidParameters() {
        var request = HttpRequest.POST("/orders", mapOf("productId" to "invalidID", "quantity" to -1))
        var thrown = assertThrows<HttpClientResponseException> { client.toBlocking().exchange(request, Order::class.java) }
        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)

        request = HttpRequest.POST("/orders", mapOf("productId" to "", "quantity" to 0))
        thrown = assertThrows<HttpClientResponseException> { client.toBlocking().exchange(request, Order::class.java) }
        assertEquals(HttpStatus.BAD_REQUEST, thrown.status)
    }

    @Test
    fun testPostOrderProductDeleted() {
        var request = HttpRequest.POST("/products", mapOf("name" to "coffee", "description" to "beans", "price" to 12))
        val response = client.toBlocking().exchange(request, Product::class.java)

        request = HttpRequest.DELETE("/products/" + response.body().id)
        client.toBlocking().exchange(request, Nothing::class.java)

        val orderRequest = HttpRequest.POST("/orders", mapOf("productId" to response.body().id, "quantity" to 10))
        val thrown = assertThrows<HttpClientResponseException> { client.toBlocking().exchange(orderRequest, Order::class.java) }

        assertEquals(HttpStatus.NOT_FOUND, thrown.status)
    }

}