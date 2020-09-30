package com.starfishst.bot.data.messages.panel;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedFactory;
import com.starfishst.commands.utils.responsive.ReactionResponse;
import com.starfishst.core.utils.Lots;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * The message to create tickets
 */
public class TicketPanelMessage extends StarfishResponsiveMessage {

    /**
     * Create the ticket panel
     *
     * @param id the id of the message
     */
    public TicketPanelMessage(long id) {
        super(id, Lots.set(new TicketPanelReactionResponse()), "ticket-panel", new StarfishValuesMap());
    }

    /**
     * Create the ticket panel
     *
     * @param message the message to become the ticket panel
     */
    public TicketPanelMessage(@NotNull Message message) {
        super(message, Lots.set(new TicketPanelReactionResponse()), "ticket-panel", new StarfishValuesMap());
    }
}