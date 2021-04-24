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

@Profile("dev")
@Component
public class DevDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MerchantRepository merchantRepository;

    @Autowired
    protected UserMerchantRepository userMerchantRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        cleanupDatabase();

        createUsers();
        createMerchants();
    }


    private void cleanupDatabase() {
        userMerchantRepository.deleteAll().block();
        merchantRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    private void createUsers() {
        User user = new User();
        user.setEmail("user@euromoby.com");
        user.setPasswordHash(BCrypt.hashpw("user", BCrypt.gensalt()));
        user.setName("User");
        user.setMsisdn("+420777000001");
        user.setActive(true);
        user.setAdmin(false);
        user.setEmailVerified(true);
        user.setMsisdnVerified(true);
        userRepository.save(user).block();

        User admin = new User();
        admin.setEmail("admin@euromoby.com");
        admin.setPasswordHash(BCrypt.hashpw("admin", BCrypt.gensalt()));
        admin.setName("Admin");
        admin.setMsisdn("+420777000001");
        admin.setActive(true);
        admin.setAdmin(true);
        admin.setEmailVerified(true);
        admin.setMsisdnVerified(false);
        userRepository.save(admin).block();
    }

    private void createMerchants() {
        Merchant merchant = new Merchant();
        merchant.setName("dev.euromoby.com");
        merchant.setApiKey("api-key");
        merchant.setDescription("Merchant for development");
        merchant.setEnv(MerchantEnv.TEST);
        merchant.setActive(true);
        merchantRepository.save(merchant).block();

        var userId = userRepository.findByEmail("user@euromoby.com").map(User::getId).block();

        UserMerchant userMerchant = new UserMerchant();
        userMerchant.setUserId(userId);
        userMerchant.setMerchantId(merchant.getId());
        userMerchant.setRole(MerchantRole.ROLE_OWNER);
        userMerchantRepository.save(userMerchant).block();
    }
}
