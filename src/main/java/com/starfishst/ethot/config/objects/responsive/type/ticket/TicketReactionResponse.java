package com.starfishst.ethot.config.objects.responsive.type.ticket;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.TicketCreator;
import com.starfishst.ethot.util.Messages;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public abstract class TicketReactionResponse implements ReactionResponse {

  @NotNull private final TicketCreator creator;

  public TicketReactionResponse(@NotNull TicketCreator creator) {
    this.creator = creator;
  }

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    try {
      Main.getManager().createTicket(getType(), event.getMember(), creator);
    } catch (DiscordManipulationException | TicketCreationException e) {
      Messages.error(e.getMessage()).send(event.getChannel());
    }
    ResponsiveMessage responsiveMessage =
        Main.getConfiguration().getResponsiveMessage(event.getMessageIdLong());
    if (responsiveMessage != null) {
      Main.getConfiguration().removeResponsiveMessage(responsiveMessage);
    }
  }

  @NotNull
  protected abstract TicketType getType();
}
