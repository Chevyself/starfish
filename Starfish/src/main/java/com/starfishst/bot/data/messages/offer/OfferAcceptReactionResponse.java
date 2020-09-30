package com.starfishst.bot.data.messages.offer;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.utils.responsive.ReactionResponse;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The reaction to accept an offer
 */
public class OfferAcceptReactionResponse implements ReactionResponse {

    /**
     * The message of the offer
     */
    @NotNull
    private final OfferMessage message;

    /**
     * Create the reaction response
     *
     * @param message the message of the offer
     */
    public OfferAcceptReactionResponse(@NotNull OfferMessage message) {
        this.message = message;
    }

    @Override
    public boolean removeReaction() {
        return false;
    }

    @Override
    public void onReaction(@NotNull MessageReactionAddEvent event) {
        Ticket ticket = Starfish.getTicketManager().getDataLoader().getTicketByChannel(event.getChannel().getIdLong());
        if (ticket != null) {
            BotUser freelancer = Starfish.getTicketManager().getDataLoader().getStarfishUser(message.getData().getValueOr("freelancer", Long.class, -1L));
            ticket.addUser(freelancer, "freelancer");
        }
        message.delete();
    }

    @Override
    public @NotNull String getUnicode() {
        return Starfish.getLanguageHandler().getFile("en").get("unicode.accept-offer");
    }
}
