package com.euromoby.api;

import com.euromoby.api.customer.CustomerRepository;
import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantEnv;
import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.payment.PaymentRepository;
import com.euromoby.api.user.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TestDataLoader {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MerchantRepository merchantRepository;

    @Autowired
    protected UserMerchantRepository userMerchantRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected PaymentRepository paymentRepository;

    public void cleanupDatabase() {
        Arrays.<ReactiveCrudRepository>asList(
                paymentRepository,
                customerRepository,
                userMerchantRepository,
                merchantRepository,
                userRepository
        ).forEach(repo -> repo.deleteAll().block());
    }

    public void loadData() {
        createAdmin();

        User user = createUser();
        Merchant merchant = createMerchant();
        createUserMerchant(user, merchant);
    }

    private void createAdmin() {
        User adminUser = new User();
        adminUser.setEmail("admin@euromoby.com");
        adminUser.setPasswordHash(BCrypt.hashpw("admin", BCrypt.gensalt()));
        adminUser.setAdmin(true);
        adminUser.setActive(true);
        userRepository.save(adminUser).block();
    }

    private User createUser() {
        User user = new User();
        user.setEmail("user@euromoby.com");
        user.setPasswordHash(BCrypt.hashpw("user", BCrypt.gensalt()));
        user.setAdmin(false);
        user.setActive(true);
        return userRepository.save(user).block();
    }

    private Merchant createMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName("junit");
        merchant.setApiKey("junit-api-key");
        merchant.setDescription("Merchant for junit");
        merchant.setEnv(MerchantEnv.TEST);
        merchant.setActive(true);
        return merchantRepository.save(merchant).block();
    }

    private void createUserMerchant(User user, Merchant merchant) {
        UserMerchant userMerchant = new UserMerchant();
        userMerchant.setUserId(user.getId());
        userMerchant.setMerchantId(merchant.getId());
        userMerchant.setRole(MerchantRole.ROLE_OWNER);
        userMerchantRepository.save(userMerchant).block();
    }
}

