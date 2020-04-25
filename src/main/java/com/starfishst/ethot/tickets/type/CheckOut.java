package com.starfishst.ethot.tickets.type;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.util.Messages;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A checkout is created when a client wants product it will have both the freelancer and the client
 *
 * @author Chevy
 * @version 1.0.0
 */
public class CheckOut extends Ticket {

  /**
   * The freelancer owner of the {@link Product} it can be null because the freelancer can get
   * demoted
   */
  @Nullable private Freelancer freelancer;
  /** The id of the parent ticket */
  private final long parentId;

  public CheckOut(
      long id, @Nullable User user, @Nullable TextChannel channel, @NotNull Product parent) {
    super(id, user, TicketStatus.OPEN, channel);
    this.freelancer = parent.getFreelancer();
    this.parentId = parent.getId();
  }

  public CheckOut(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @Nullable Freelancer freelancer,
      long parentId) {
    super(id, user, status, channel);
    this.freelancer = freelancer;
    this.parentId = parentId;
  }

  @Override
  public void onCreation() {
    try {
      Ticket ticket = Main.getManager().getLoader().getTicket(parentId);
      if (this.channel != null && ticket instanceof Product) {
        this.channel
            .sendMessage(
                Messages.announce((Product) ticket)
                    .getBuilder()
                    .append(Main.getDiscordConfiguration().getGuild().getPublicRole())
                    .build())
            .queue();
      }
    } catch (DiscordManipulationException e) {
      if (this.channel != null) {
        Messages.error(e.getMessage()).send(this.channel);
      }
    }
  }

  @Override
  public void onDone() {}

  @Override
  public @NotNull TicketType getType() {
    return TicketType.CHECK_OUT;
  }

  @Override
  public @NotNull Document getDocument() {
    Document document = super.getDocument();
    if (freelancer != null) document.append("freelancer", freelancer.getId());
    document.append("parent", parentId);
    return document;
  }
}
