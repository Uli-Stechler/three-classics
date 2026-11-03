package com.threeclassics.backgammon;

import com.threeclassics.core.GameModule;
import com.threeclassics.core.GameType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BackgammonConfig {

    @Bean
    GameModule<BackgammonState, BackgammonMove> backgammonModule() {
        return new GameModule<>(
                GameType.BACKGAMMON,
                new BackgammonEngine(),
                new BackgammonAi(),
                BackgammonState.class,
                BackgammonMove.class);
    }
}
