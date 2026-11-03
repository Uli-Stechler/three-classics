package com.threeclassics.checkers;

import com.threeclassics.core.GameModule;
import com.threeclassics.core.GameType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CheckersConfig {

    @Bean
    GameModule<CheckersState, CheckersMove> checkersModule() {
        return new GameModule<>(
                GameType.CHECKERS,
                new CheckersEngine(),
                new CheckersAi(),
                CheckersState.class,
                CheckersMove.class);
    }
}
