package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Called when a {@link BotUser} is added to a ticket */
public class TicketAddUserEvent extends TicketEvent implements StarfishCancellable {

  @NonNull @Getter private final BotUser user;

  @NonNull @Getter private final String role;
  @Getter @Setter private boolean cancelled;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param user the user that will be added
   * @param role the role that the user will take in the ticket
   */
  public TicketAddUserEvent(@NonNull Ticket ticket, @NonNull BotUser user, @NonNull String role) {
    super(ticket);
    this.user = user;
    this.role = role;
  }
}
