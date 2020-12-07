package com.starfishst.api.events.tickets;

import com.starfishst.api.data.loader.TicketManager;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.events.StarfishEvent;
import lombok.NonNull;

/** Called before a ticket is created by a {@link com.starfishst.api.data.loader.TicketManager} */
public class TicketPreCreationEvent implements StarfishEvent, StarfishCancellable {

  /** The ticket manager creating the ticket */
  @NonNull private final TicketManager manager;

  /** The type of ticket that is being created */
  @NonNull private final TicketType type;

  /** The user creating the ticket */
  @NonNull private final BotUser user;

  /** The parent of the ticket */
  private final Ticket parent;

  /** Whether the event is cancelled */
  private boolean cancelled;

  /** Why is it cancelled */
  @NonNull private String reason = "Ticket creation cancelled: No reason given";

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

  /**
   * Set the reason why it was cancelled
   *
   * @param reason the reason why it was cancelled
   */
  public void setReason(@NonNull String reason) {
    this.reason = reason;
  }

  /**
   * Get the ticket creating the event
   *
   * @return the manager
   */
  @NonNull
  public TicketManager getManager() {
    return manager;
  }

  /**
   * Get the type of ticket that is being created
   *
   * @return the type of ticket that is being created
   */
  @NonNull
  public TicketType getType() {
    return this.type;
  }

  /**
   * Get the user that is creating the ticket
   *
   * @return the user creating the ticket
   */
  @NonNull
  public BotUser getUser() {
    return user;
  }

  /**
   * Get the parent of the ticket being creating
   *
   * @return the parent
   */
  public Ticket getParent() {
    return parent;
  }

  /**
   * Get why the creating was cancelled
   *
   * @return the reason why it was cancelled
   */
  @NonNull
  public String getReason() {
    return reason;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean bol) {
    this.cancelled = bol;
  }
}
