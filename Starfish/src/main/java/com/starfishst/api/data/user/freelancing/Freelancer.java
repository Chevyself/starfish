package com.starfishst.api.data.user.freelancing;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.core.utils.time.Time;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * A freelancer is an starfish user with certain attributes
 */
public class Freelancer extends BotUser {

    /**
     * The rating of the freelancer. The starfish user that did the rating and the rating that
     * it gave.
     */
    @NotNull
    private final HashMap<BotUser, Integer> rating;

    /** The portfolio of the freelancer */
    @NotNull private List<String> portfolio;

    /**
     * Create the freelancer
     *
     * @param toRemove the time to unload the freelancer
     * @param id the id of the freelancer
     * @param guildId the id where the user is a freelancer
     * @param rating the rating of the freelancer
     * @param portfolio the portfolio of the freelancer
     */
    public Freelancer(@NotNull Time toRemove, long id, long guildId, @NotNull HashMap<BotUser, Integer> rating, @NotNull List<String> portfolio) {
        super(toRemove, id);
        this.rating = rating;
        this.portfolio = portfolio;
    }

}
