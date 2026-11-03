package com.threeclassics.service;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RandomConfig {

    /** Dice and computer choices in production. Tests inject a seeded generator instead. */
    @Bean
    RandomGenerator random() {
        return new SecureRandom();
    }
}
