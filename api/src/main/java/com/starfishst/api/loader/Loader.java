package com.starfishst.api.loader;

import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.events.user.FreelancerRatingUnloadedEvent;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.role.BotRole;
import com.starfishst.api.tickets.Offer;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.tickets.TicketStatus;
import com.starfishst.api.tickets.TicketType;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.user.FreelancerRating;
import java.util.Collection;
import java.util.Optional;

import lombok.NonNull;
import me.googas.commands.jda.utils.responsive.controller.ResponsiveMessageController;
import me.googas.starbox.events.Listener;

/** Loads and saves data */
// TODO create modules for each data
public interface Loader extends StarfishHandler, ResponsiveMessageController {

  /**
   * Get a ticket by its id
   *
   * @param id the id of the ticket
   * @return a {@link java.util.Optional} holding the nullable ticket
   */
  @NonNull
  Optional<Ticket> getTicket(long id);

  /**
   * Get a ticket by its channel
   *
   * @param channelId the id of the channel
   * @return a {@link java.util.Optional} holding the nullable ticket
   */
  @NonNull
  Optional<Ticket> getTicketByChannel(long channelId);

  /**
   * Get an starfish user by its id
   *
   * @param id the id of the user
   * @return the starfish user
   */
  @NonNull
  BotUser getStarfishUser(long id);

  /**
   * Get a starfish role by its id
   *
   * @param id the id of the role
   * @return the role
   */
  @NonNull
  BotRole getStarfishRole(long id);

  /**
   * Get all the offers sent to a ticket
   *
   * @param ticket the ticket querying offers
   * @return the offers found
   */
  @NonNull
  Collection<Offer> getOffers(@NonNull Ticket ticket);

  /**
   * Deletes the responsive message
   *
   * @param message the message to delete
   */
  void deleteMessage(@NonNull BotResponsiveMessage message);

  /**
   * Get the rating of a freelancer by its id
   *
   * @param id the id of the freelancer
   * @return the rating of the freelancer
   */
  @NonNull
  FreelancerRating getRating(long id);

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

  /**
   * Listen to a role being unloaded to save it to the database
   *
   * @param event the event of a role being unloaded
   */
  @Listener
  void onRoleUnloadedEvent(@NonNull BotRoleUnloadedEvent event);

  /**
   * Listens to when a ticket is unloaded
   *
   * @param event the event of a ticket being unloaded
   */
  @Listener
  void onTicketUnloadedEvent(@NonNull TicketUnloadedEvent event);

  /**
   * Listens to when the rating of a freelancer is unloaded
   *
   * @param event the event of the rating of a freelancer being unloaded
   */
  @Listener
  void onFreelancerRatingUnloaded(@NonNull FreelancerRatingUnloadedEvent event);
  /**
   * Listens to when a ticket is done to save it in the database
   *
   * @param event the event of a ticket being done
   */
  @Listener
  void onTicketStatusUpdated(@NonNull TicketStatusUpdatedEvent event);

  /**
   * Listen to when a user is unloaded to save it to the database
   *
   * @param event the event of a user being unloaded
   */
  @Listener
  void onBotUserUnloaded(@NonNull BotUserUnloadedEvent event);

  /**
   * Listens for messages being unloaded to upload them to the database
   *
   * @param event the event of a message being unloaded
   */
  @Listener
  void onBotMessageUnloaded(@NonNull BotMessageUnloadedEvent event);
}
