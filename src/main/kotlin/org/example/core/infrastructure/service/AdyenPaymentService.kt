package org.example.core.infrastructure.service

import com.adyen.model.checkout.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.example.core.domain.exception.PaymentProviderException
import org.example.core.domain.infrastructure.service.PaymentService
import org.example.modules.ConfigurationProvider.config
import org.slf4j.LoggerFactory
import java.util.*

class AdyenPaymentService(
    private val httpClient: HttpClient
) : PaymentService {

    private val logger = LoggerFactory.getLogger(AdyenPaymentService::class.java)

    override suspend fun getPaymentMethods(): String {
        val paymentMethodsRequest = PaymentMethodsRequest()
            .merchantAccount(config.adyen.merchantAccount)

        val httpResponse = httpClient.post(config.adyen.apiVersion + "/paymentMethods") {
            header("X-API-Key", config.adyen.apiKey)
            contentType(ContentType.Application.Json)
            setBody(paymentMethodsRequest.toJson())
        }

        when (httpResponse.status.isSuccess()) {
            true -> return httpResponse.body<String>()
            else -> {
                logger.error("Invalid Adyen Response = {}", httpResponse)
                throw PaymentProviderException("Invalid status response: " + httpResponse.status.toString())
            }
        }
    }

    override suspend fun creditCardPayment(cardDetails: CardDetails, amount: Amount): String {
        val paymentRequest = PaymentRequest()
            .merchantAccount(config.adyen.merchantAccount)
            .returnUrl("")
            .reference(UUID.randomUUID().toString())
            .paymentMethod(CheckoutPaymentMethod(cardDetails))
            .amount(amount)

        val httpResponse = httpClient.post(config.adyen.apiVersion + "/payments") {
            header("X-API-Key", config.adyen.apiKey)
            contentType(ContentType.Application.Json)
            setBody(paymentRequest.toJson())
        }

        when (httpResponse.status.isSuccess()) {
            true -> return httpResponse.body<String>()
            else -> {
                logger.error("Invalid Adyen Response = {}", httpResponse)
                throw PaymentProviderException("Invalid status response: " + httpResponse.status.toString())
            }
        }
    }
}