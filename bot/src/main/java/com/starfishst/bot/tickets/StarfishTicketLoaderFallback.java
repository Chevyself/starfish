package com.starfishst.bot.tickets;

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
import me.googas.commands.jda.utils.responsive.ResponsiveMessage;
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
  public Optional<Ticket> getTicket(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public Optional<Ticket> getTicketByChannel(long channelId) {
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

  @Override
  public @NonNull Collection<Offer> getOffers(@NonNull Ticket ticket) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public void deleteMessage(@NonNull BotResponsiveMessage message) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

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
