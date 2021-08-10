package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.tickets.Ticket;
import java.util.Collections;
import java.util.Optional;
import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaArgumentProvider;
import me.googas.commands.jda.providers.type.JdaExtraArgumentProvider;

/** Provides the registry with {@link Ticket}! */
public class TicketProvider
    implements JdaArgumentProvider<Ticket>, JdaExtraArgumentProvider<Ticket> {

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
      Optional<? extends Ticket> ticket = loader.getTicket((long) id);
      if (ticket.isPresent()) {
        return ticket.get();
      }
      ticket = loader.getTicketByChannel(context.getChannel().getIdLong());
      if (ticket.isPresent()) {
        return ticket.get();
      }
    }
    throw new ArgumentProviderException(
        loader
            .getStarfishUser(context.getSender().getIdLong())
            .getLocaleFile()
            .get("ticket.invalid", Collections.singletonMap("id", id.toString())));
  }

  @NonNull
  @Override
  public Ticket getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    Optional<? extends Ticket> ticket =
        Starfish.getLoader().getTicketByChannel(context.getChannel().getIdLong());
    if (ticket.isPresent()) {
      return ticket.get();
    }
    throw new ArgumentProviderException(
        Starfish.getLanguageHandler().getFile(context).get("ticket.invalid-channel"));
  }
}
