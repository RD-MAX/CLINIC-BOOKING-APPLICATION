package com.payment_service.controller;

import com.payment_service.dto.ProductRequestDto;
import com.payment_service.entity.StripeResponse;
import com.payment_service.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class ProductCheckoutController {

    private final StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    // 1️⃣ Create Stripe Checkout Session
    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequestDto productRequestDto) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(stripeResponse);
    }

    // 2️⃣ Stripe success redirect
    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("session_id") String sessionId) {
        try {
            stripeService.handlePaymentSuccess(sessionId);
            return ResponseEntity.ok("Payment successful & booking confirmed");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe error");
        }
    }

    // 3️⃣ Stripe cancel redirect
    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {
        return ResponseEntity.ok("Payment cancelled");
    }
}