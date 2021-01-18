package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import me.googas.commons.maps.Maps;

/** Provides the registry with {@link Ticket}! */
public class TicketProvider implements JdaArgumentProvider<Ticket> {

  @Override
  public @NonNull Class<Ticket> getClazz() {
    return Ticket.class;
  }

  @NonNull
  @Override
  public Ticket fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    Loader loader = Starfish.getLoader();
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
