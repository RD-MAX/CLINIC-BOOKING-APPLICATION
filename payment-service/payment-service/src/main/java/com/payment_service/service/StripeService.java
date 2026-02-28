package com.payment_service.service;

import com.payment_service.client.BookingClient;
import com.payment_service.dto.BookingConfirmationDto;
import com.payment_service.dto.ProductRequestDto;
import com.payment_service.entity.StripeResponse;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
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




    public StripeResponse checkoutByBookingId(Long bookingId) {

        // 1️⃣ Fetch booking details from booking-service
        BookingConfirmationDto booking = bookingClient.getBookingById(bookingId);

        if (booking == null) {
            throw new RuntimeException("Booking not found for id: " + bookingId);
        }

        if (booking.getStatus() == null) {
            throw new RuntimeException("Booking status is missing for bookingId: " + bookingId);
        }

        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Booking is not in payable state: " + booking.getStatus());
        }

        if (booking.getBookingAmount() == null) {
            throw new RuntimeException("Booking amount is missing for bookingId: " + bookingId);
        }

        // 2️⃣ Init Stripe
        Stripe.apiKey = secretkey;

        // 3️⃣ Build Stripe product dynamically
        String productName = "Doctor Consultation - " +
                (booking.getClinicName() != null ? booking.getClinicName() : "Clinic");

        long amountInPaise = Math.round(booking.getBookingAmount() * 100); // ₹ -> paise

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productName)
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setUnitAmount(amountInPaise)
                        .setCurrency("inr")
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        // 4️⃣ Create Stripe session with bookingId metadata
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:7075/api/v1/payments/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:7075/api/v1/payments/cancel")
                        .addLineItem(lineItem)
                        .putMetadata("bookingId", String.valueOf(bookingId))  // 🔥 source of truth
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
            throw new RuntimeException("Stripe session creation failed: " + e.getMessage());
        }
    }
//
//    public StripeResponse checkoutProducts(ProductRequestDto productRequestDto) {
//
//        Stripe.apiKey = secretkey;
//
//        SessionCreateParams.LineItem.PriceData.ProductData productData =
//                SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                        .setName(productRequestDto.getName())
//                        .build();
//
//        SessionCreateParams.LineItem.PriceData priceData =
//                SessionCreateParams.LineItem.PriceData.builder()
//                        .setUnitAmount(productRequestDto.getAmount())
//                        .setCurrency(productRequestDto.getCurrency())
//                        .setProductData(productData)
//                        .build();
//
//        SessionCreateParams.LineItem lineItem =
//                SessionCreateParams.LineItem.builder()
//                        .setQuantity(productRequestDto.getQuantity())
//                        .setPriceData(priceData)
//                        .build();
//
//        SessionCreateParams params =
//                SessionCreateParams.builder()
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .setSuccessUrl("http://localhost:7075/api/v1/payments/success?session_id={CHECKOUT_SESSION_ID}")
//                        .setCancelUrl("http://localhost:7075/api/v1/payments/cancel")
//                        .addLineItem(lineItem)
//
//                        .putMetadata("doctorId", String.valueOf(productRequestDto.getDoctorId()))
//                        .putMetadata("patientId", String.valueOf(productRequestDto.getPatientId()))
//                        .putMetadata("date", productRequestDto.getDate())
//                        .putMetadata("time", productRequestDto.getTime())
////booking id
//                        .putMetadata("bookingId",String.valueOf(productRequestDto.getBookingId()))
//                        .build();
//
//        try {
//            Session session = Session.create(params);
//
//            return StripeResponse.builder()
//                    .status("success")
//                    .message("Payment session created")
//                    .sessionId(session.getId())
//                    .sessionUrl(session.getUrl())
//                    .build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Stripe session creation failed");
//        }
//    }
//


//

public void handlePaymentSuccess(String sessionId) throws Exception {

    Stripe.apiKey = secretkey;

    Session session = Session.retrieve(sessionId);

    if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
        throw new RuntimeException("Payment not completed");
    }

    // 🔍 Debug log (keep for now)
    System.out.println("Stripe metadata = " + session.getMetadata());

    String bookingIdStr = session.getMetadata().get("bookingId");

    if (bookingIdStr == null || bookingIdStr.isBlank()) {
        throw new RuntimeException("bookingId missing in Stripe metadata");
    }

    Long bookingId;
    try {
        bookingId = Long.parseLong(bookingIdStr);
    } catch (NumberFormatException e) {
        throw new RuntimeException("Invalid bookingId in Stripe metadata: " + bookingIdStr);
    }

    bookingClient.confirmBookingById(bookingId);
}



    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public void handleWebhook(String payload, String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    webhookSecret
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid Stripe signature");
        }

        System.out.println("🔔 Webhook event received: " + event.getType());

        // ✅ Only handle checkout success
        if ("checkout.session.completed".equals(event.getType())) {

            // 🔥 THIS IS THE KEY LINE
            StripeObject stripeObject = event.getData().getObject();

            if (!(stripeObject instanceof Session session)) {
                System.out.println("❌ Event data is NOT a Session");
                return;
            }

            System.out.println("✅ Session ID: " + session.getId());
            System.out.println("📦 Metadata: " + session.getMetadata());

            String bookingIdStr = session.getMetadata().get("bookingId");

            if (bookingIdStr == null || bookingIdStr.isBlank()) {
                System.out.println("⚠ bookingId missing in metadata");
                return;
            }

            Long bookingId = Long.valueOf(bookingIdStr);

            bookingClient.confirmBookingById(bookingId);

            System.out.println("✅ Booking confirmed via webhook for ID: " + bookingId);
        }
    }

}