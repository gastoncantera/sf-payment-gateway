package org.example.core.domain.action

import com.adyen.model.checkout.Amount
import com.adyen.model.checkout.CardDetails
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.example.core.domain.infrastructure.service.AdyenPaymentService
import kotlin.test.Test

@ExperimentalCoroutinesApi
class DirectCreditCardPaymentTest {

    private val service: AdyenPaymentService = mockk(relaxed = true)
    private val action = DirectCreditCardPayment(service)

    @Test
    fun `should call creditCardPayment from service`() = runTest {
        whenActionIsInvoked()

        thenServiceCreditCardPaymentIsCalled()
    }

    private suspend fun whenActionIsInvoked() {
        action(ACTION_DATA)
    }

    private fun thenServiceCreditCardPaymentIsCalled() {
        coVerify(exactly = 1) { service.creditCardPayment(CARD_DETAILS, AMOUNT) }
    }

    private companion object {
        val CARD_DETAILS: CardDetails = CardDetails()
            .holderName("John Smith")
            .encryptedCardNumber("test_4111111111111111")
            .encryptedExpiryMonth("test_03")
            .encryptedExpiryYear("test_2030")
            .encryptedSecurityCode("test_737")
        val AMOUNT: Amount = Amount()
            .currency("EUR").value(1000)
        var ACTION_DATA = DirectCreditCardPayment.ActionData(CARD_DETAILS, AMOUNT)
    }
}