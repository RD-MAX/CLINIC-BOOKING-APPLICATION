package com.payment_service.service;

import com.payment_service.dto.ProductRequestDto;
import com.payment_service.entity.StripeResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secretkey}")
    private String secretkey;


    public StripeResponse checkoutProducts(ProductRequestDto productRequestDto) {

        Stripe.apiKey=secretkey;

        SessionCreateParams.LineItem.PriceData.ProductData productData= SessionCreateParams.LineItem.PriceData.ProductData
                .builder()
                .setName(productRequestDto.getName())
                .build();

        SessionCreateParams.LineItem.PriceData priceData= SessionCreateParams.LineItem.PriceData
                .builder()
                .setUnitAmount(productRequestDto.getAmount())
                .setCurrency(productRequestDto.getCurrency())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem= SessionCreateParams.LineItem
                .builder()
                .setQuantity(productRequestDto.getQuantity())
                .setPriceData(priceData)
                .build();

//--------------------------------------------------stripe checkout configuration----
        SessionCreateParams params = SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:7075/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:7075/cancel")
                .addLineItem(lineItem)
                .build();


        //-----------new session----------

        Session s= null;
        try{
            s= Session.create(params);
        }catch(Exception e){
            e.printStackTrace();
        }

       //------------------------------

       return StripeResponse.builder()
               .status("success")
               .message("payment session created")
               .sessionId(s.getId())
               .sessionUrl(s.getUrl())
               .build();

    }
}
