package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;

/** Provides tickets as an extra argument in commands */
public class TicketSenderProvider implements JdaExtraArgumentProvider<Ticket> {
  @Override
  public @NonNull Class<Ticket> getClazz() {
    return Ticket.class;
  }

  @NonNull
  @Override
  public Ticket getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    Ticket ticket = Starfish.getLoader().getTicketByChannel(context.getChannel().getIdLong());
    if (ticket != null) {
      return ticket;
    }
    throw new ArgumentProviderException(
        Starfish.getLanguageHandler().getFile(context).get("ticket.invalid-channel"));
  }
}
