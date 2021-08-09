package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.events.StarfishEvent;
import com.starfishst.api.loader.TicketManager;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Called before a ticket is created by a {@link TicketManager} */
public class TicketPreCreationEvent implements StarfishEvent, StarfishCancellable {

  @NonNull @Getter private final TicketManager manager;
  @NonNull @Getter private final TicketType type;
  @NonNull @Getter private final BotUser user;
  @Getter private final Ticket parent;
  @Getter @Setter private boolean cancelled;
  @Getter @Setter @NonNull private String reason = "Ticket creation cancelled: No reason given";

  /**
   * Create the event
   *
   * @param manager the manager that is creating the ticket
   * @param type the type of the ticket being created
   * @param user the user that is creating the ticket
   * @param parent the parent of the ticket
   */
  public TicketPreCreationEvent(
      @NonNull TicketManager manager,
      @NonNull TicketType type,
      @NonNull BotUser user,
      Ticket parent) {
    this.manager = manager;
    this.type = type;
    this.user = user;
    this.parent = parent;
  }
}
