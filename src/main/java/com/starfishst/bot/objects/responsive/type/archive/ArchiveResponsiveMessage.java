package com.starfishst.bot.objects.responsive.type.archive;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.objects.management.AllowedTicketCloserChecker;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.objects.responsive.ResponsiveMessageType;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.bot.util.Unicode;
import com.starfishst.simple.Lots;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The message to make sure that the user archiving the ticket really wants to do it */
public class ArchiveResponsiveMessage extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   */
  public ArchiveResponsiveMessage(@NotNull Message message) {
    super(ResponsiveMessageType.ARCHIVE, message, getReactions(), true);
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   */
  public ArchiveResponsiveMessage(long id) {
    super(ResponsiveMessageType.ARCHIVE, id, getReactions(), true);
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
            return Unicode.getEmoji("UNICODE_ARCHIVE");
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            if (AllowedTicketCloserChecker.getInstance().isAllowed(event.getMember())) {
              Ticket ticket =
                  TicketManager.getInstance()
                      .getLoader()
                      .getTicketByChannel(event.getChannel().getIdLong());
              if (ticket != null && ticket.getChannel() != null) {
                if (ticket.getStatus() != TicketStatus.ARCHIVED) {
                  ticket.setStatus(TicketStatus.ARCHIVED);
                  ResponsiveMessage responsiveMessage =
                      Configuration.getInstance().getResponsiveMessage(event.getMessageIdLong());
                  if (responsiveMessage != null) {
                    Configuration.getInstance().removeResponsiveMessage(responsiveMessage);
                  }
                } else {
                  Messages.error(Lang.get("ALREADY_ARCHIVED", Tickets.getPlaceholders(ticket)))
                      .send(
                          ticket.getChannel(),
                          msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                }
              }
            } else {
              Messages.error(Lang.get("NO_PERMISSION"))
                  .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            }
          }
        });
  }
}
