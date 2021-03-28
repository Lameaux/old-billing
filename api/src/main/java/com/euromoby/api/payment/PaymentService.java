package com.euromoby.api.payment;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
public class PaymentService {
    private static final Function<Payment, PaymentResponse> TO_DTO = p -> {
        var dto = new PaymentResponse();
        dto.setId(p.getId().toString());
        dto.setMerchantReference(p.getMerchantReference());
        dto.setCustomerId(p.getCustomerId());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    };
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Flux<PaymentResponse> getAllPayments(UUID merchantId) {
        return paymentRepository.findAllByMerchantId(merchantId).map(TO_DTO);
    }

    public Mono<PaymentResponse> getPayment(UUID id, UUID merchantId) {
        return paymentRepository.findByIdAndMerchantId(id, merchantId).map(TO_DTO);
    }

    public Mono<PaymentResponse> createPayment(UUID merchantId, Mono<PaymentRequest> paymentRequestMono) {
        return paymentRequestMono.flatMap(paymentRequest -> {
            Payment p = new Payment();
            p.setState(Payment.STATE_NEW);
            p.setMerchantId(merchantId);
            p.setMerchantReference(paymentRequest.getMerchantReference());
            p.setCustomerId(paymentRequest.getCustomerId());
            return paymentRepository.save(p).map(TO_DTO);
        });
    }
}
