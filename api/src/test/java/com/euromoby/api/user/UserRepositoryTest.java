package com.euromoby.api.user;

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

class UserRepositoryTest extends BaseTest {
    @Autowired
    UserRepository userRepository;

    private User alice = newUser("alice@euromoby.com", "+123000001", "Alice");
    private User bob = newUser("bob@euromoby.com", "+123000002", "Bob");
    private User john = newUser("john@euromoby.com", "+123000003", "John");

    private List<User> users = Arrays.asList(alice, bob, john);

    private Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "email"));
    private Pageable pageOfSizeOne = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "email"));

    @BeforeEach
    public void setUp() {
        super.setUp();

        userRepository.deleteAll()
                .thenMany(Flux.fromIterable(users))
                .flatMap(userRepository::save)
                .blockLast();
    }

    @Test
    void all() {
        StepVerifier.create(userRepository.findAllByIdNotNull(pageable))
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void allPageSizeOne() {
        StepVerifier.create(userRepository.findAllByIdNotNull(pageOfSizeOne))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getByEmail() {
        StepVerifier.create(userRepository.findByEmail(alice.getEmail()))
                .expectSubscription()
                .expectNextMatches(user -> Objects.equals(user.getId(), alice.getId()))
                .verifyComplete();
    }

    @Test
    void findAllByEmailDomain() {
        StepVerifier.create(userRepository.findAllByFilter(
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
        StepVerifier.create(userRepository.findAllByFilter(
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
        StepVerifier.create(userRepository.findAllByFilter(
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
        StepVerifier.create(userRepository.findAllByFilter(
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
        StepVerifier.create(userRepository.findAllByFilter(
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
    void duplicateEmail() {
        var alice2 = newUser("alice@euromoby.com", "+123000001", "Alice 2");

        StepVerifier.create(userRepository.save(alice2))
                .expectSubscription()
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    private User newUser(String email, String msisdn, String name) {
        var user = new User();
        user.setEmail(email);
        user.setPasswordHash(UUID.randomUUID().toString());
        user.setMsisdn(msisdn);
        user.setName(name);
        return user;
    }
}
