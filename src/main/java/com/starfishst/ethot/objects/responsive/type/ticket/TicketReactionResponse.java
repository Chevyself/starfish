package com.starfishst.ethot.objects.responsive.type.ticket;

import com.starfishst.ethot.config.Configuration;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.TicketCreator;
import com.starfishst.ethot.util.Messages;
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
