package com.euromoby.payment.service;

import com.euromoby.payment.dto.PaymentDto;
import com.euromoby.payment.entity.Payment;
import com.euromoby.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
public class PaymentService {
    private static final Function<Payment, PaymentDto> TO_DTO = p -> {
        var dto = new PaymentDto();
        dto.setId(p.getId().toString());
        dto.setMerchantId(p.getMerchantId().toString());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    };
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Flux<PaymentDto> getAllPayments(UUID merchantId) {
        return paymentRepository.findAllByMerchantId(merchantId).map(TO_DTO);
    }

    public Mono<PaymentDto> getPayment(UUID id, UUID merchantId) {
        return paymentRepository.findByIdAndMerchantId(id, merchantId).map(TO_DTO);
    }

    public Mono<PaymentDto> createPayment(UUID merchantId) {
        Payment p = new Payment();
        p.setState(Payment.STATE_PENDING);
        p.setMerchantId(merchantId);
        return paymentRepository.save(p).map(TO_DTO);
    }
}
