package com.payment_service.service;

import com.payment_service.client.BookingClient;
import com.payment_service.dto.BookingConfirmationDto;
import com.payment_service.dto.ProductRequestDto;
import com.payment_service.entity.StripeResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class StripeService {

    @Value("${stripe.secretkey}")
    private String secretkey;
@Autowired
    private BookingClient bookingClient;

//    public StripeService(BookingClient bookingClient) {
//        this.bookingClient = bookingClient;
//    }

    public StripeResponse checkoutProducts(ProductRequestDto productRequestDto) {

        Stripe.apiKey = secretkey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequestDto.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setUnitAmount(productRequestDto.getAmount())
                        .setCurrency(productRequestDto.getCurrency())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productRequestDto.getQuantity())
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:7075/api/v1/payments/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:7075/api/v1/payments/cancel")
                        .addLineItem(lineItem)
                        .putMetadata("doctorId", String.valueOf(productRequestDto.getDoctorId()))
                        .putMetadata("patientId", String.valueOf(productRequestDto.getPatientId()))
                        .putMetadata("date", productRequestDto.getDate())
                        .putMetadata("time", productRequestDto.getTime())
                        .build();

        try {
            Session session = Session.create(params);

            return StripeResponse.builder()
                    .status("success")
                    .message("Payment session created")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Stripe session creation failed");
        }
    }

    public void handlePaymentSuccess(String sessionId) throws Exception {

        Stripe.apiKey = secretkey;

        Session session = Session.retrieve(sessionId);

        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            throw new RuntimeException("Payment not completed");
        }

        String doctorId = session.getMetadata().get("doctorId");
        String patientId = session.getMetadata().get("patientId");
        String date = session.getMetadata().get("date");
        String time = session.getMetadata().get("time");

        BookingConfirmationDto dto = new BookingConfirmationDto();
        dto.setDoctorId(Long.parseLong(doctorId));
        dto.setPatientId(Long.parseLong(patientId));
        dto.setDate(LocalDate.parse(date));
        dto.setTime(LocalTime.parse(time));

        bookingClient.confirmBooking(dto);
    }
}