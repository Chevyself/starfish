package com.starfishst.ethot.tickets.type;

import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.config.objects.responsive.type.ticket.TicketCreatorResponsiveMessage;
import com.starfishst.ethot.listeners.questions.QuestionTicketListener;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A ticket creator is the one that is made only to select another ticket type to create
 *
 * @author Chevy
 * @version 1.0.0
 */
public class TicketCreator extends Ticket {

  @Nullable private TicketCreatorResponsiveMessage message;

  public TicketCreator(long id, @Nullable User customer, @Nullable TextChannel channel) {
    super(id, customer, TicketStatus.CREATING, channel);
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

  /** When the ticket is created the message to select the ticket should be sent */
  @Override
  public void onCreation() {
    if (this.message == null) {
      if (this.channel != null && this.user != null) {
        HashMap<String, String> placeHolders = new HashMap<>();
        placeHolders.put("creator", this.user.getName());
        placeHolders.put("id", String.valueOf(this.id));

        QuestionTicketListener.sendNextMessage(
            this.id,
            channel,
            Messages.create("CREATOR_TITLE", "CREATOR_DESCRIPTION", placeHolders, placeHolders)
                .getAsMessageQuery()
                .getMessage(),
            msg -> {
              message = new TicketCreatorResponsiveMessage(msg.getIdLong(), this);
              msg.addReaction(Unicode.WHITE_CHECK_MARK).queue();
              msg.addReaction(Unicode.MEMO).queue();
              msg.addReaction(Unicode.HAMMER).queue();
            });
      } else {
        Errors.addError(
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
