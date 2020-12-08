package com.starfishst.bot.tickets;

import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.tickets.Offer;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.data.user.FreelancerRating;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.api.events.role.BotRoleUnloadedEvent;
import com.starfishst.api.events.tickets.TicketStatusUpdatedEvent;
import com.starfishst.api.events.tickets.TicketUnloadedEvent;
import com.starfishst.api.events.user.BotUserUnloadedEvent;
import com.starfishst.api.events.user.FreelancerRatingUnloadedEvent;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import java.util.Collection;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

/** A fallback for the starfish ticket loader */
public class StarfishTicketLoaderFallback implements StarfishLoader {

  @Override
  public boolean acceptBots() {
    return false;
  }

  @Override
  public @NonNull Collection<ResponsiveMessage> getResponsiveMessages(Guild guild) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "fallback-loader";
  }

  @Override
  public Ticket getTicket(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public Ticket getTicketByChannel(long channelId) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public @NonNull BotUser getStarfishUser(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public @NonNull BotRole getStarfishRole(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  /**
   * Get all the offers sent to a ticket
   *
   * @param ticket the ticket querying offers
   * @return the offers found
   */
  @Override
  public @NonNull Collection<Offer> getOffers(@NonNull Ticket ticket) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public void deleteMessage(@NonNull StarfishResponsiveMessage message) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  /**
   * Get the rating of a freelancer by its id
   *
   * @param id the id of the freelancer
   * @return the rating of the freelancer
   */
  @Override
  public @NonNull FreelancerRating getRating(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public @NonNull Collection<Ticket> getTickets(
      @NonNull BotUser user,
      @NonNull String role,
      @NonNull Collection<TicketType> types,
      TicketStatus... statuses) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public void onRoleUnloadedEvent(@NonNull BotRoleUnloadedEvent event) {}

  @Override
  public void onTicketUnloadedEvent(@NonNull TicketUnloadedEvent event) {}

  @Override
  public void onFreelancerRatingUnloaded(@NonNull FreelancerRatingUnloadedEvent event) {}

  @Override
  public void onTicketStatusUpdated(@NonNull TicketStatusUpdatedEvent event) {}

  @Override
  public void onBotUserUnloaded(@NonNull BotUserUnloadedEvent event) {}

  @Override
  public void onBotMessageUnloaded(@NonNull BotMessageUnloadedEvent event) {}
}
