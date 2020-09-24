package com.starfishst.bot.tickets;

import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A fallback for the starfish ticket loader */
public class StarfishTicketLoaderFallback implements StarfishLoader {

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "fallback-loader";
  }

  @Override
  public @Nullable Ticket getTicket(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public @NotNull BotUser getStarfishUser(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }

  @Override
  public @NotNull BotRole getStarfishRole(long id) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }
}
