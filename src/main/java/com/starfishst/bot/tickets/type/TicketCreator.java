package com.starfishst.bot.tickets.type;

import com.starfishst.bot.listeners.questions.QuestionTicketListener;
import com.starfishst.bot.objects.responsive.type.ticket.TicketCreatorResponsiveMessage;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.core.fallback.Fallback;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A ticket creator is the one that is made only to select another ticket type to create */
public class TicketCreator extends Ticket {

  /** The message listening to the reaction to create the next message */
  @Nullable private TicketCreatorResponsiveMessage message;

  /**
   * The simplest constructor
   *
   * @param id the id of the ticket
   * @param customer the customer creating the ticket
   * @param channel the channel to use for the ticket
   */
  public TicketCreator(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, TicketStatus.CREATING, channel, new ArrayList<>());
  }

  /**
   * This ticket is only for creating
   *
   * @return the creating status
   */
  @NotNull
  @Override
  public TicketStatus getStatus() {
    return TicketStatus.CREATING;
  }

  @Override
  public void onCreation() {
    if (this.message == null) {
      if (this.channel != null && this.user != null) {
        HashMap<String, String> placeholders = Tickets.getPlaceholders(this);
        QuestionTicketListener.sendNextMessage(
            this.id,
            channel,
            Messages.create("CREATOR_TITLE", "CREATOR_DESCRIPTION", placeholders, placeholders)
                .getAsMessageQuery()
                .getMessage(),
            msg -> message = new TicketCreatorResponsiveMessage(msg, this));
      } else {
        Fallback.addError(
            "Ticket "
                + this.id
                + " is missing customer: "
                + (this.user == null)
                + " or channel: "
                + (this.channel == null));
      }
    }
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.TICKET_CREATOR;
  }

  @Override
  public void onDone() {}
}
