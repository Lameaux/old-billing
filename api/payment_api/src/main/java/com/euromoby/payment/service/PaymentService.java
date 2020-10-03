package com.euromoby.payment.service;

import com.euromoby.payment.dto.PaymentDto;
import com.euromoby.payment.entity.Payment;
import com.euromoby.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class PaymentService {
    private static final Function<Payment, PaymentDto> TO_DTO = p -> {
        var dto = new PaymentDto();
        dto.setId(p.getId());
        dto.setMerchantId(p.getMerchantId());
        return dto;
    };
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Flux<PaymentDto> getAllPayments(String merchantId) {
        return paymentRepository.findAllByMerchantId(merchantId).map(TO_DTO);
    }

    public Mono<PaymentDto> getPayment(String id, String merchantId) {
        return paymentRepository.findByIdAndMerchantId(id, merchantId).map(TO_DTO);
    }

    public Mono<PaymentDto> createPayment(String merchantId) {
        Payment p = new Payment();
        p.setMerchantId(merchantId);
        return paymentRepository.save(p).map(TO_DTO);
    }
}
