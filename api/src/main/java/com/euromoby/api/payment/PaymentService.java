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
        dto.setId(p.getId());
        dto.setCustomerId(p.getCustomerId());
        dto.setMerchantReference(p.getMerchantReference());
        dto.setState(p.getState());
        dto.setDescription(p.getDescription());
        dto.setCurrency(p.getCurrency());
        dto.setNetAmount(p.getNetAmount());
        dto.setVatAmount(p.getVatAmount());
        dto.setVatRate(p.getVatRate());
        dto.setTotalAmount(p.getTotalAmount());
        dto.setInstrumentId(p.getInstrumentId());
        dto.setProviderReference(p.getProviderReference());
        dto.setCallbackUrl(p.getCallbackUrl());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    };
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    Flux<PaymentResponse> getAllPayments(UUID merchantId) {
        return paymentRepository.findAllByMerchantId(merchantId).map(TO_DTO);
    }

    Mono<PaymentResponse> getPayment(UUID id, UUID merchantId) {
        return paymentRepository.findByIdAndMerchantId(id, merchantId).map(TO_DTO);
    }

    Mono<PaymentResponse> getPaymentByMerchantReference(UUID merchantId, String merchantReference) {
        return paymentRepository.findByMerchantIdAndMerchantReference(merchantId, merchantReference).map(TO_DTO);
    }

    public Mono<PaymentResponse> createPayment(UUID merchantId, Mono<PaymentRequest> paymentRequestMono) {
        return paymentRequestMono.flatMap(paymentRequest -> {
            Payment p = new Payment();
            p.setState(PaymentState.CREATED);
            p.setMerchantId(merchantId);
            p.setCustomerId(paymentRequest.getCustomerId());
            p.setMerchantReference(paymentRequest.getMerchantReference());
            p.setDescription(paymentRequest.getDescription());
            p.setCurrency(paymentRequest.getCurrency());
            p.setNetAmount(paymentRequest.getNetAmount());
            p.setVatAmount(paymentRequest.getVatAmount());
            p.setVatRate(paymentRequest.getVatRate());
            p.setTotalAmount(paymentRequest.getTotalAmount());
            p.setCallbackUrl(paymentRequest.getCallbackUrl());
            return paymentRepository.save(p).map(TO_DTO);
        });
    }
}
