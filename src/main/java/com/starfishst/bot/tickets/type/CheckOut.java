package com.starfishst.bot.tickets.type;

import com.starfishst.bot.config.DiscordConfiguration;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.util.Messages;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A checkout is created when a client wants product it will have both the freelancer and the client
 */
public class CheckOut extends Ticket {

  /**
   * The freelancer owner of the {@link Product} it can be null because the freelancer can get
   * demoted
   */
  @Nullable private final Freelancer freelancer;
  /** The id of the parent ticket */
  private final long parentId;

  /**
   * The constructor for when the ticket is just being created
   *
   * @param id the id of the ticket
   * @param user the user that wants to buy the product
   * @param channel the channel where the ticket was created
   * @param parent the product that's being purchased
   */
  public CheckOut(
      long id, @Nullable User user, @Nullable TextChannel channel, @NotNull Product parent) {
    super(id, user, TicketStatus.OPEN, channel, new ArrayList<>());
    this.freelancer = parent.getFreelancer();
    this.parentId = parent.getId();
  }

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param user the user that created the ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param freelancer the freelancer that sells the product
   * @param parentId the id of the product
   * @param payments the payments of the ticket
   */
  public CheckOut(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @Nullable Freelancer freelancer,
      long parentId,
      List<String> payments) {
    super(id, user, status, channel, payments);
    this.freelancer = freelancer;
    this.parentId = parentId;
  }

  @Override
  public void onCreation() {
    try {
      Ticket ticket = TicketManager.getInstance().getLoader().getTicket(parentId);
      if (this.channel != null && ticket instanceof Product) {
        this.channel
            .sendMessage(
                Messages.announce((Product) ticket)
                    .getBuilder()
                    .append(DiscordConfiguration.getInstance().getGuild().getPublicRole())
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
