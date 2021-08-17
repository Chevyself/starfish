package com.starfishst.api.loader;

import com.starfishst.api.tickets.Offer;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import me.googas.lazy.Subloader;

public interface TicketSubloader extends Subloader {

  /**
   * Get a ticket by its id
   *
   * @param id the id of the ticket
   * @return a {@link java.util.Optional} holding the nullable ticket
   */
  @NonNull
  Optional<? extends Ticket> getTicket(long id);

  /**
   * Get a ticket by its channel
   *
   * @param channelId the id of the channel
   * @return a {@link java.util.Optional} holding the nullable ticket
   */
  @NonNull
  Optional<? extends Ticket> getTicketByChannel(long channelId);

  /**
   * Get all the offers sent to a ticket
   *
   * @param ticket the ticket querying offers
   * @return the offers found
   */
  @NonNull
  Collection<Offer> getOffers(@NonNull Ticket ticket);

  /**
   * Get the tickets by some user with certain role in the given types that are in the given status
   *
   * @param user the user to match
   * @param role the role to match
   * @param types the types to match
   * @param statuses the statuses to match
   * @return the collection of tickets found else null
   */
  @NonNull
  Collection<Ticket> getTickets(
      @NonNull BotUser user,
      @NonNull String role,
      @NonNull Collection<TicketType> types,
      TicketStatus... statuses);
}
