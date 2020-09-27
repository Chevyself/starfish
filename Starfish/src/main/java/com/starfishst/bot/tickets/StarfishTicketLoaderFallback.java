package com.starfishst.bot.tickets;

import com.starfishst.api.data.role.BotRole;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import java.util.Collection;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A fallback for the starfish ticket loader */
public class StarfishTicketLoaderFallback implements StarfishLoader {

  @Override
  public boolean acceptBots() {
    return false;
  }

  @Override
  public @NotNull Collection<ResponsiveMessage> getResponsiveMessages(@Nullable Guild guild) {
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

  @Override
  public void deleteMessage(@NotNull ResponsiveMessage message) {
    throw new UnsupportedOperationException(
        "Operations are not permitted in fallback ticket loader");
  }
}
