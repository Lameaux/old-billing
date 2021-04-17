package com.euromoby.api;

import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
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
    protected MerchantRepository merchantRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        createMerchant();
    }

    private void createMerchant() {
        merchantRepository.deleteAll().block();

        Merchant merchant = new Merchant();
        merchant.setName("junit");
        merchant.setApiKey(BCrypt.hashpw("junit-api-key", BCrypt.gensalt()));
        merchant.setDescription("Merchant for junit");
        merchant.setEnv("test");
        merchant.setActive(true);
        merchantRepository.save(merchant).block();
    }
}

