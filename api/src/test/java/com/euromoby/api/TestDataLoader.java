package com.euromoby.api;

import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantEnv;
import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.user.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MerchantRepository merchantRepository;

    @Autowired
    protected UserMerchantRepository userMerchantRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        cleanupDatabase();

        createAdmin();

        User user = createUser();
        Merchant merchant = createMerchant();
        createUserMerchant(user, merchant, MerchantRole.ROLE_OWNER);
    }

    private void cleanupDatabase() {
        userMerchantRepository.deleteAll().block();
        merchantRepository.deleteAll().block();
        userRepository.deleteAll().block();
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

    private void createUserMerchant(User user, Merchant merchant, MerchantRole role) {
        UserMerchant userMerchant = new UserMerchant();
        userMerchant.setUserId(user.getId());
        userMerchant.setMerchantId(merchant.getId());
        userMerchant.setRole(role);
        userMerchantRepository.save(userMerchant).block();
    }
}

