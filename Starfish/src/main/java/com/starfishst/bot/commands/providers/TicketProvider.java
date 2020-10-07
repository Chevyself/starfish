package com.starfishst.bot.commands.providers;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.tickets.StarfishLoader;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;

/** Provides the registry with {@link Ticket}! */
public class TicketProvider implements JdaArgumentProvider<Ticket> {

  @Override
  public @NotNull Class<Ticket> getClazz() {
    return Ticket.class;
  }

  @NotNull
  @Override
  public Ticket fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    StarfishLoader loader = Starfish.getLoader();
    Object id = context.getRegistry().fromString(s, long.class, context);
    if (id instanceof Long) {
      Ticket ticket = loader.getTicket((long) id);
      if (ticket != null) {
        return ticket;
      }
      ticket = loader.getTicketByChannel(context.getChannel().getIdLong());
      if (ticket != null) {
        return ticket;
      }
    }
    throw new ArgumentProviderException(
        loader
            .getStarfishUser(context.getSender().getIdLong())
            .getLocaleFile()
            .get("ticket.invalid", Maps.singleton("id", id.toString())));
  }
}
