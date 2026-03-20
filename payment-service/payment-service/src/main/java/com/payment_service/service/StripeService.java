package com.payment_service.service;

import com.payment_service.client.BookingClient;
import com.payment_service.dto.BookingConfirmationDto;
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

@Service
public class StripeService {

    @Value("${stripe.secretkey}")
    private String secretkey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private BookingClient bookingClient;

    // ================== CREATE CHECKOUT ==================
    public StripeResponse checkoutByBookingId(Long bookingId) {

        BookingConfirmationDto booking = bookingClient.getBookingById(bookingId);

        if (booking == null) {
            throw new RuntimeException("Booking not found for id: " + bookingId);
        }

        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Booking not payable: " + booking.getStatus());
        }

        if (booking.getBookingAmount() == null) {
            throw new RuntimeException("Booking amount missing");
        }

        Stripe.apiKey = secretkey;

        String productName = "Doctor Consultation - " +
                (booking.getClinicName() != null ? booking.getClinicName() : "Clinic");

        long amountInPaise = Math.round(booking.getBookingAmount() * 100);

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

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:7075/api/v1/payments/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:7075/api/v1/payments/cancel")
                        .addLineItem(lineItem)
                        .putMetadata("bookingId", String.valueOf(bookingId))
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
            throw new RuntimeException("Stripe session creation failed: " + e.getMessage());
        }
    }

    // ================== WEBHOOK ==================
    public void handleWebhook(String payload, String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Stripe signature");
        }

        System.out.println("🔔 Event: " + event.getType());

        if ("checkout.session.completed".equals(event.getType())) {

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

            StripeObject stripeObject;

            if (deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                System.out.println("❌ Deserialization failed");
                return;
            }

            // ✅ SAFE CAST (THIS FIXES YOUR ERROR)
            if (!(stripeObject instanceof Session session)) {
                System.out.println("❌ Not a session");
                return;
            }

            System.out.println("✅ Session ID: " + session.getId());
            System.out.println("📦 Metadata: " + session.getMetadata());

            if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                System.out.println("❌ Payment not completed");
                return;
            }

            String bookingIdStr = session.getMetadata().get("bookingId");

            if (bookingIdStr == null || bookingIdStr.isBlank()) {
                System.out.println("⚠ bookingId missing");
                return;
            }

            try {
                Long bookingId = Long.valueOf(bookingIdStr);

                System.out.println("➡ Confirming booking: " + bookingId);

                bookingClient.confirmBookingById(bookingId);

                System.out.println("✅ Booking confirmed!");

            } catch (Exception e) {
                System.out.println("❌ Booking confirmation failed: " + e.getMessage());
            }
        }
    }
}