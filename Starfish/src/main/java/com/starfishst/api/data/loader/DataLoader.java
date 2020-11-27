package com.starfishst.api.data.loader;

import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.tickets.Offer;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Loads and saves data */
public interface DataLoader {

  /**
   * Get a ticket by its id
   *
   * @param id the id of the ticket
   * @return the returned ticket
   */
  @Nullable
  Ticket getTicket(long id);

  /**
   * Get a ticket by its channel
   *
   * @param channelId the id of the channel
   * @return the returned ticket
   */
  @Nullable
  Ticket getTicketByChannel(long channelId);

  /**
   * Get an starfish user by its id
   *
   * @param id the id of the user
   * @return the starfish user
   */
  @NotNull
  BotUser getStarfishUser(long id);

  /**
   * Get a starfish role by its id
   *
   * @param id the id of the role
   * @return the role
   */
  @NotNull
  BotRole getStarfishRole(long id);

  /**
   * Get all the offers sent to a ticket
   *
   * @param ticket the ticket querying offers
   * @return the offers found
   */
  @NotNull
  Collection<Offer> getOffers(@NotNull Ticket ticket);

  /**
   * Deletes the responsive message
   *
   * @param message the message to delete
   */
  void deleteMessage(@NotNull ResponsiveMessage message);

  /**
   * Get the rating of a freelancer by its id
   *
   * @param id the id of the freelancer
   * @return the rating of the freelancer
   */
  @NotNull
  FreelancerRating getRating(long id);
}
