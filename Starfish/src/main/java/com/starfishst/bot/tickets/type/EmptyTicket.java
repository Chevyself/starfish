package com.starfishst.bot.tickets.type;

import com.starfishst.bot.tickets.TicketStatus;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This is a ticket that was not created/completed */
public class EmptyTicket extends Ticket {

  /**
   * Create a ticket instance
   *
   * @param id the id of the ticket
   * @param user the user owner of the ticket
   * @param status the status of the ticket
   * @param channel the channel where the ticket is operating
   * @param payments the payments of the ticket
   */
  public EmptyTicket(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull List<String> payments) {
    super(id, user, status, channel, payments);
  }

  @Override
  public void onCreation() {
    // empty
  }

  @Override
  public void onDone() {
    // empty
  }
}
