package com.starfishst.ethot.objects.responsive.type.suggestions;

import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.type.Suggestion;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Unicode;
import com.starfishst.simple.Lots;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The message that is send to cancel or send the suggestion */
public class SuggestionsResponsiveMessage extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   * @param suggestionId the id of the suggestion ticket
   */
  public SuggestionsResponsiveMessage(@NotNull Message message, long suggestionId) {
    super(ResponsiveMessageType.SUGGESTION_CONFIRM, message, getReactions(suggestionId), true);
  }

  /**
   * Get the reactions that will be used in this message
   *
   * @param suggestionId the id of the suggestion
   * @return the list of reactions
   */
  public static List<ReactionResponse> getReactions(long suggestionId) {
    return Lots.list(
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.getEmoji("UNICODE_SUGGESTION_CONFIRM");
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            Ticket ticket = TicketManager.getInstance().getLoader().getTicket(suggestionId);
            if (ticket instanceof Suggestion) {
              ((Suggestion) ticket).superOnDone();
              ticket.setStatus(TicketStatus.CLOSED);
              event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
            }
          }
        },
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.getEmoji("UNICODE_SUGGESTION_DELETE");
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            Ticket ticket = TicketManager.getInstance().getLoader().getTicket(suggestionId);
            if (ticket != null) {
              ticket.setStatus(TicketStatus.CLOSED);
              event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
            }
          }
        });
  }
}
