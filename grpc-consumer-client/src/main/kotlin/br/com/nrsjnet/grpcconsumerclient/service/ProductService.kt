package br.com.nrsjnet.grpcconsumerclient.service

import br.com.nrsjnet.IdRequest
import br.com.nrsjnet.Product
import br.com.nrsjnet.ProductServiceGrpc.ProductServiceBlockingStub
import br.com.nrsjnet.grpcconsumerclient.domain.ProductResponseDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductService(private val productClient: ProductServiceBlockingStub) {

    val LOGGER = LoggerFactory.getLogger(ProductService::class.java)

    fun getProduct(productId: String) =
        LOGGER.info("ProductService.getProduct(productId: $productId)").let {
            productClient
                .findByID(IdRequest.newBuilder()
                    .setId(productId).build()).let {
                    ProductResponseDTO(it.id, it.name, it.price)
                }
        }
}