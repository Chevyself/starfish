package com.starfishst.bot.data.messages.offer;

import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * A message sent to a quote ticket
 */
public class OfferMessage extends StarfishResponsiveMessage {

    /**
     * Create the offer message
     *
     * @param id the id of the message
     * @param data the data of the message
     */
    public OfferMessage(long id, @NotNull StarfishValuesMap data) {
        super(id, new HashSet<>(), "offer", data);
        this.addReactionResponse(new OfferAcceptReactionResponse(this));
    }

    /**
     * Create the offer message
     *
     * @param message the the message
     * @param data the data of the message
     */
    public OfferMessage(@NotNull Message message, @NotNull StarfishValuesMap data) {
        super(message, new HashSet<>(), "offer", data);
        this.addReactionResponse(new OfferAcceptReactionResponse(this), message);
    }
}
