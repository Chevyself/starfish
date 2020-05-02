package com.starfishst.bot.objects.responsive.type.ticket;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.TicketCreationException;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.tickets.type.TicketCreator;
import com.starfishst.bot.util.Messages;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The reaction to create a ticket */
public abstract class TicketReactionResponse implements ReactionResponse {

  /** The ticket creator waiting for the next step */
  @NotNull private final TicketCreator creator;

  /**
   * Create the reaction response
   *
   * @param creator the ticket creator waiting for the reaction
   */
  public TicketReactionResponse(@NotNull TicketCreator creator) {
    this.creator = creator;
  }

  /**
   * Get the type of ticket to create
   *
   * @return the type of ticket to create
   */
  @NotNull
  protected abstract TicketType getType();

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    try {
      TicketManager.getInstance().createTicket(getType(), event.getMember(), creator);
    } catch (DiscordManipulationException | TicketCreationException e) {
      Messages.error(e.getMessage()).send(event.getChannel());
    }
    ResponsiveMessage responsiveMessage =
        Configuration.getInstance().getResponsiveMessage(event.getMessageIdLong());
    if (responsiveMessage != null) {
      Configuration.getInstance().removeResponsiveMessage(responsiveMessage);
    }
  }
}
