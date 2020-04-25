package com.starfishst.ethot.tickets.loader;

import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.tickets.type.Order;
import com.starfishst.ethot.tickets.type.Product;
import com.starfishst.ethot.tickets.type.Quote;
import com.starfishst.ethot.tickets.type.Ticket;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** The ticket loader is used to get ticket from the database */
public interface TicketLoader {

  /**
   * Get a ticket using it's id
   *
   * @param id the id of the ticket
   * @return the ticket if found
   */
  @Nullable
  Ticket getTicket(long id);

  /**
   * Save a ticket into the database
   *
   * @param ticket the ticket to be saved
   */
  void saveTicket(Ticket ticket);

  /**
   * Get a ticket using the channel id.
   *
   * @param id the id of the channel
   * @return the ticket if found else null
   */
  @Nullable
  Ticket getTicketByChannel(long id);

  /**
   * Saves a Freelancer in the database
   *
   * @param freelancer the freelancer to save
   */
  void saveFreelancer(Freelancer freelancer);

  /**
   * Get the list of created ticket by a user
   *
   * @param user the user to get the tickets from
   * @return the list of tickets created by the user
   */
  @NotNull
  List<Ticket> getTickets(@NotNull User user);

  /**
   * Get an order by its message
   *
   * @param id the id of the message
   * @return the order if found else null
   */
  @Nullable
  Order getOrderByMessage(long id);

  /**
   * Get a freelancer using its id
   *
   * @param id the id of the freelancer
   * @return the freelancer if found else null
   */
  @Nullable
  Freelancer getFreelancer(long id);

  /**
   * Get a quote using an offer message id
   *
   * @param id the offer message id
   * @return the quote if found else null
   */
  @Nullable
  Quote getQuoteByOfferMessage(long id);

  /** Closes the database */
  void close();

  /**
   * Get a product using its message
   *
   * @param id the id of the message
   * @return the product if found else null
   */
  @Nullable
  Product getProductByMessage(long id);

  /**
   * Demotes a freelancer
   *
   * @param freelancer the freelancer to demote
   */
  void demoteFreelancer(Freelancer freelancer);
}
