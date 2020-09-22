package com.starfishst.bot.users;

import com.starfishst.api.Preferences;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class StarfishPreferences implements Preferences {

    @NotNull
    private final HashMap<String, Object> preferences;

    public StarfishPreferences(@NotNull HashMap<String, Object> preferences) {
        this.preferences = preferences;
    }

    @Override
    public @NotNull HashMap<String, Object> getPreferences() {
        return this.preferences;
    }
}
