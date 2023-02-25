package br.com.nrsjnet.grpcconsumerclient.clients

import br.com.nrsjnet.ProductServiceGrpc.ProductServiceBlockingStub
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductClientConfig {

    @GrpcClient("product-grpc-service")
    lateinit var productServiceGrpc: ProductServiceBlockingStub

    @Bean
    fun productClient() = productServiceGrpc

}