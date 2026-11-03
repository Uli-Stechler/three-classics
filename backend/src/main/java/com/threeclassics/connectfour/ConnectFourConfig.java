package com.threeclassics.connectfour;

import com.threeclassics.core.GameModule;
import com.threeclassics.core.GameType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ConnectFourConfig {

    @Bean
    GameModule<ConnectFourState, ConnectFourMove> connectFourModule() {
        return new GameModule<>(
                GameType.CONNECT_FOUR,
                new ConnectFourEngine(),
                new ConnectFourAi(),
                ConnectFourState.class,
                ConnectFourMove.class);
    }
}
