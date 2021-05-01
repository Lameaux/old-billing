package com.euromoby.api.customer;

import com.euromoby.api.common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

class CustomerRepositoryTest extends BaseTest {
    private static final UUID MERCHANT_ID = UUID.randomUUID();
    @Autowired
    CustomerRepository customerRepository;
    private Customer alice = newCustomer("alice", "alice@euromoby.com", "+123000001", "Alice");
    private Customer bob = newCustomer("bob", "bob@euromoby.com", "+123000002", "Bob");
    private Customer john = newCustomer("john", "john@euromoby.com", "+123000003", "John");

    private List<Customer> customers = Arrays.asList(alice, bob, john);

    private Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
    private Pageable pageOfSizeOne = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "name"));

    @BeforeEach
    public void setUp() {
        super.setUp();

        customerRepository.deleteAll()
                .thenMany(Flux.fromIterable(customers))
                .flatMap(customerRepository::save)
                .blockLast();
    }

    @Test
    void all() {
        StepVerifier.create(customerRepository.findAllByMerchantIdAndIdNotNull(MERCHANT_ID, pageable))
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void allPageSizeOne() {
        StepVerifier.create(customerRepository.findAllByMerchantIdAndIdNotNull(MERCHANT_ID, pageOfSizeOne))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllByMerchantReference() {
        StepVerifier.create(
                customerRepository.findAllByMerchantIdAndFilter(
                        MERCHANT_ID,
                        alice.getMerchantReference(),
                        null,
                        null,
                        null,
                        10,
                        0
                )
        )
                .expectSubscription()
                .expectNextMatches(user -> Objects.equals(user.getId(), alice.getId()))
                .verifyComplete();
    }

    @Test
    void findAllByEmailDomain() {
        StepVerifier.create(customerRepository.findAllByMerchantIdAndFilter(
                MERCHANT_ID,
                null,
                "%@euromoby.com",
                null,
                null,
                10,
                0
        ))
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findAllByMsisdnPrefix() {
        StepVerifier.create(customerRepository.findAllByMerchantIdAndFilter(
                MERCHANT_ID,
                null,
                null,
                "+123%",
                null,
                10,
                0
        ))
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findAllByEmailDomainWithPagination() {
        StepVerifier.create(customerRepository.findAllByMerchantIdAndFilter(
                MERCHANT_ID,
                null,
                "%@euromoby.com",
                null,
                null,
                1,
                1
        ))
                .expectSubscription()
                .expectNextMatches(user -> Objects.equals(user.getId(), bob.getId()))
                .verifyComplete();
    }

    @Test
    void findAllByName() {
        StepVerifier.create(customerRepository.findAllByMerchantIdAndFilter(
                MERCHANT_ID,
                null,
                null,
                null,
                "John",
                10,
                0
        ))
                .expectSubscription()
                .expectNextMatches(user -> Objects.equals(user.getId(), john.getId()))
                .verifyComplete();
    }

    @Test
    void findNothing() {
        StepVerifier.create(customerRepository.findAllByMerchantIdAndFilter(
                MERCHANT_ID,
                null,
                "unknown",
                "unknown",
                "unknown",
                10,
                0
        ))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void duplicateMerchantReference() {
        var alice2 = newCustomer("alice", "alice2@euromoby.com", "+123000001", "Alice 2");

        StepVerifier.create(customerRepository.save(alice2))
                .expectSubscription()
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    private Customer newCustomer(String merchantReference, String email, String msisdn, String name) {
        var customer = new Customer();
        customer.setMerchantId(MERCHANT_ID);
        customer.setMerchantReference(merchantReference);
        customer.setEmail(email);
        customer.setMsisdn(msisdn);
        customer.setName(name);
        return customer;
    }
}
