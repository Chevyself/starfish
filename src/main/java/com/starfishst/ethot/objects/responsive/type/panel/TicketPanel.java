package com.starfishst.ethot.objects.responsive.type.panel;

import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import com.starfishst.ethot.util.Unicode;
import com.starfishst.simple.Lots;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** This panel helps customer to easily create tickets */
public class TicketPanel extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   */
  public TicketPanel(@NotNull Message message) {
    super(ResponsiveMessageType.TICKET_PANEL, message, getReactions(), true);
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   */
  public TicketPanel(long id) {
    super(ResponsiveMessageType.TICKET_PANEL, id, getReactions(), true);
  }

  /**
   * Get the reactions that will be used in this message
   *
   * @return the list of reactions
   */
  @NotNull
  private static List<ReactionResponse> getReactions() {
    return Lots.list(
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.getEmoji("UNICODE_TICKET_PANEL");
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            try {
              Ticket ticket =
                  TicketManager.getInstance()
                      .createTicket(TicketType.TICKET_CREATOR, event.getMember(), null);
              HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
              Messages.create(
                      "TICKET_CREATED_TITLE",
                      "TICKET_CREATED_DESCRIPTION",
                      placeholders,
                      placeholders)
                  .send(event.getChannel(), msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            } catch (TicketCreationException | DiscordManipulationException e) {
              e.printStackTrace();
            }
          }
        });
  }
}
