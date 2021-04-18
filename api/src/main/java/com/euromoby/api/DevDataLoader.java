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
        loadData();
    }

    private void loadData() {
        createUsers();
        createMerchants();
    }

    private void createUsers() {
        userRepository.deleteAll().block();

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

        User adminUser = new User();
        adminUser.setEmail("admin@euromoby.com");
        adminUser.setPasswordHash(BCrypt.hashpw("admin", BCrypt.gensalt()));
        user.setName("Admin");
        user.setMsisdn("+420777000001");
        user.setActive(true);
        user.setAdmin(true);
        user.setEmailVerified(true);
        user.setMsisdnVerified(false);
        userRepository.save(adminUser).block();
    }

    private void createMerchants() {
        userMerchantRepository.deleteAll().block();
        merchantRepository.deleteAll().block();

        Merchant merchant = new Merchant();
        merchant.setName("dev.euromoby.com");
        merchant.setApiKey("api-key");
        merchant.setDescription("Merchant for development");
        merchant.setEnv(MerchantEnv.TEST);
        merchant.setActive(true);
        merchantRepository.save(merchant).block();

        User user = userRepository.findByEmail("user@euromoby.com").block();

        UserMerchant userMerchant = new UserMerchant();
        userMerchant.setUserId(user.getId());
        userMerchant.setMerchantId(merchant.getId());
        userMerchant.setRole(MerchantRole.ROLE_OWNER);
        userMerchantRepository.save(userMerchant).block();
    }
}
