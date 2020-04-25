package com.starfishst.ethot.config.objects.responsive.type.panel;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import com.starfishst.ethot.util.Unicode;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public class TicketPanel extends ResponsiveMessage {

  public TicketPanel(long id) {
    super(
        ResponsiveMessageType.TICKET_PANEL,
        id,
        true,
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.TICKET;
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            try {
              Ticket ticket =
                  Main.getManager()
                      .createTicket(TicketType.TICKET_CREATOR, event.getMember(), null);
              HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
              Messages.create(
                      "TICKET_CREATED_TITLE",
                      "TICKET_CREATED_DESCRIPTION",
                      placeholders,
                      placeholders)
                  .send(
                      event.getChannel(),
                      msg -> {
                        msg.delete().queueAfter(10, TimeUnit.SECONDS);
                      });
            } catch (TicketCreationException | DiscordManipulationException e) {
              e.printStackTrace();
            }
          }
        });
  }
}
