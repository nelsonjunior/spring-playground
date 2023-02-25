package br.com.nrsjnet.grpcconsumerclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcConsumerClientApplication

fun main(args: Array<String>) {
    runApplication<GrpcConsumerClientApplication>(*args)
}
