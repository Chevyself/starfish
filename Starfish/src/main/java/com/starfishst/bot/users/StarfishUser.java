package com.starfishst.bot.users;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.user.BotUserLoadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation for {@link com.starfishst.api.data.user.BotUser}
 */
public class StarfishUser extends Catchable implements BotUser {

    /**
     * The discord id of the user
     */
    private final long id;

    /**
     * The preferences of the user
     */
    @NotNull
    private final StarfishPreferences preferences;

    /**
     * Create the starfish user
     *
     * @param id the id of the user
     * @param preferences the preferences of the user
     */
    public StarfishUser(long id, @NotNull StarfishPreferences preferences) {
        // TODO change this to config
        super(Time.fromString("30m"));
        this.id = id;
        this.preferences = preferences;
        new BotUserLoadedEvent(this).call();
    }

    @Override
    public void onSecondsPassed() {

    }

    @Override
    public void onRemove() {
        new BotUserUnloadedEvent(this).call();
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public @NotNull StarfishPreferences getPreferences() {
        return this.preferences;
    }
}
