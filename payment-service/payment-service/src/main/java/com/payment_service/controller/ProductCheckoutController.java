package com.payment_service.controller;

import com.payment_service.dto.ProductRequestDto;
import com.payment_service.entity.StripeResponse;
import com.payment_service.service.StripeService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductCheckoutController {

    private StripeService stripeService;
    public ProductCheckoutController(StripeService stripeService){
        this.stripeService=stripeService;
    }

    @PostMapping("/checkout")
public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequestDto productRequestDto){

        StripeResponse stripeResponse=stripeService.checkoutProducts(productRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body( stripeResponse);
}

@GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("session_id") String sessionId){

    Stripe.apiKey="sk_test_51T3gES4FDa3hpyW3KaNX4egedDi0adS01HKp3POexvLYMVLzfInSUKY1RBZtnRzCTw0eywqo4yJM87fj6OqydTpI00EU2pGT0H";
    try{
        Session s = Session.retrieve(sessionId);
        String paymentStatus= s.getPaymentStatus();

        if(("paid").equalsIgnoreCase(paymentStatus)){
            return ResponseEntity.ok("payment successfull");
        }
        else{
            return ResponseEntity.status(400).body("payment not completed");
        }
    }catch (Exception e){ e.printStackTrace();

        return ResponseEntity.status(500).body( "striper error");
    }

}
@GetMapping("/cancel")
    public ResponseEntity<String> handleCancel(){

    System.out.println("payment cancelled");

    return ResponseEntity.ok("payment cancelled");
}


}
