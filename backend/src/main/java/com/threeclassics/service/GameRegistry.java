package com.threeclassics.service;

import com.threeclassics.core.GameModule;
import com.threeclassics.core.GameType;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/** Looks up the module for a game type. Each game registers itself as a {@link GameModule} bean. */
@Component
public class GameRegistry {

    private final Map<GameType, GameModule<?, ?>> modules = new EnumMap<>(GameType.class);

    public GameRegistry(ObjectProvider<GameModule<?, ?>> modules) {
        modules.orderedStream().forEach(module -> this.modules.put(module.type(), module));
    }

    public GameModule<?, ?> get(GameType type) {
        GameModule<?, ?> module = modules.get(type);
        if (module == null) {
            throw new IllegalArgumentException("Game type " + type + " is not available.");
        }
        return module;
    }
}
