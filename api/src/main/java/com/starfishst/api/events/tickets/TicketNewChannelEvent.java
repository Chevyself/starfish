package com.starfishst.api.events.tickets;

import com.starfishst.api.events.StarfishCancellable;
import com.starfishst.api.tickets.Ticket;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;

/** Called when a {@link Ticket} has a new channel */
public class TicketNewChannelEvent extends TicketEvent implements StarfishCancellable {

  /** The new channel of the ticket */
  @Getter private final TextChannel channel;

  /** Whether the event is cancelled */
  @Getter @Setter private boolean cancelled;

  /**
   * Create the event
   *
   * @param ticket the ticket involved in the event
   * @param channel the new channel of the ticket
   */
  public TicketNewChannelEvent(@NonNull Ticket ticket, TextChannel channel) {
    super(ticket);
    this.channel = channel;
  }
}
