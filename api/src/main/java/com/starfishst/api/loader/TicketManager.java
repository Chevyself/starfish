package com.starfishst.api.loader;

import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.exception.TicketCreationException;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import lombok.NonNull;

import java.util.Optional;

/** Managers {@link Ticket} */
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
  @NonNull
  Ticket createTicket(@NonNull TicketType type, @NonNull BotUser user, Ticket parent)
      throws TicketCreationException;

  /**
   * Get the id for a new ticket
   *
   * @param parent the parent of the ticket being created
   * @return the new id
   */
  default long getNewId(Ticket parent) {
    if (parent != null && parent.getType() != TicketType.PRODUCT) {
      return parent.getId();
    } else {
      long total = this.getTotal();
      while (this.getDataLoader().getTicket(total).isPresent()) {
        total++;
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
   * Sets the data loader that the manager must
   *
   * @param loader the new loader to set
   */
  void setDataLoader(@NonNull Loader loader);

  /**
   * Get the total of tickets loaded
   *
   * @return the total of tickets loaded
   */
  long getTotal();

  /**
   * Sets the configuration that the manager must use
   *
   * @param configuration the configuration
   */
  void setConfiguration(@NonNull Configuration configuration);

  /**
   * Get the data loader that the manager is using
   *
   * @return the data loader
   */
  @NonNull
  Loader getDataLoader();
}
