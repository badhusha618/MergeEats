package com.mergeeats.paymentservice.service;

import com.mergeeats.common.models.Payment;
import com.mergeeats.common.enums.PaymentMethod;
import com.mergeeats.common.enums.PaymentType;
import com.mergeeats.common.enums.PaymentStatus;
import com.mergeeats.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public Payment createPayment(Payment payment) {
        try {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment created successfully: {}", savedPayment.getPaymentId());
            return savedPayment;
        } catch (Exception e) {
            logger.error("Error creating payment", e);
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    public Payment processPayment(String paymentId) {
        try {
            Payment payment = paymentRepository.findById(paymentId).orElse(null);
            if (payment == null) {
                throw new RuntimeException("Payment not found");
            }

            payment.setStatus(PaymentStatus.PROCESSING);
            payment.setProcessedAt(LocalDateTime.now());
            
            // Simulate payment processing
            Thread.sleep(100);
            
            payment.setStatus(PaymentStatus.COMPLETED);
            Payment savedPayment = paymentRepository.save(payment);
            
            logger.info("Payment processed successfully: {}", paymentId);
            return savedPayment;
        } catch (Exception e) {
            logger.error("Error processing payment: {}", paymentId, e);
            throw new RuntimeException("Failed to process payment", e);
        }
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
}
