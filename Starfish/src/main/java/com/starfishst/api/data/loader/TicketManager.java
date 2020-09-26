package com.starfishst.api.data.loader;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.tickets.exception.TicketCreationException;
import com.starfishst.api.data.user.BotUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Managers {@link com.starfishst.api.data.tickets.Ticket} */
public interface TicketManager {

  /**
   * Creates a ticket. While this is happening {@link
   * com.starfishst.api.events.tickets.TicketPreCreationEvent} has to be called
   *
   * @param type the type of ticket to create
   * @param user the user that wants to create the ticket
   * @param parent the ticket parent
   * @return the ticket created
   * @throws TicketCreationException in case the ticket cannot be created
   */
  @NotNull
  Ticket createTicket(@NotNull TicketType type, @NotNull BotUser user, @Nullable Ticket parent)
      throws TicketCreationException;

  /**
   * Get the id for a new ticket
   *
   * @param parent the parent of the ticket being created
   * @return the new id
   */
  default long getNewId(@Nullable Ticket parent) {
    if (parent != null && parent.getTicketType() != TicketType.PRODUCT) {
      return parent.getId();
    } else {
      long total = this.getTotal();
      Ticket ticket = this.getDataLoader().getTicket(total);
      while (ticket != null) {
        total++;
        ticket = this.getDataLoader().getTicket(total);
      }
      this.setTotal(total);
      return total;
    }
  }

  /**
   * Set the total of tickets created
   *
   * @param total the new total
   */
  void setTotal(long total);

  /**
   * Get the data loader that the manager is using
   *
   * @return the data loader
   */
  @NotNull
  DataLoader getDataLoader();

  /**
   * Get the total of tickets loaded
   *
   * @return the total of tickets loaded
   */
  long getTotal();

  /**
   * Sets the data loader that the manager must
   *
   * @param loader the new loader to set
   */
  void setDataLoader(@NotNull DataLoader loader);

  /**
   * Sets the configuration that the manager must use
   *
   * @param configuration the configuration
   */
  void setConfiguration(@NotNull Configuration configuration);
}
