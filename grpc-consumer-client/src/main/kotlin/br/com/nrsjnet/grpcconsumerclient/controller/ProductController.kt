package br.com.nrsjnet.grpcconsumerclient.controller

import br.com.nrsjnet.grpcconsumerclient.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    val LOG = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable(name = "productId", required = true) productId: String
    ) = LOG.info("ProductController.getProduct(productId: $productId)").let {
        productService.getProduct(productId).let {
            ResponseEntity.ok(it)
        }
    }
}