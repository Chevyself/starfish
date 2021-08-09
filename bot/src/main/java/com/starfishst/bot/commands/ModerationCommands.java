package com.starfishst.bot.commands;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.tickets.Ticket;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import net.dv8tion.jda.api.entities.TextChannel;

/** General moderation commands */
public class ModerationCommands {

  @Command(aliases = "clear", description = "clear.desc", node = "starfish.clear")
  public void clear(
      LocaleFile locale,
      TextChannel channel,
      @Required(name = "clear.amount", description = "clear.amount.desc") int amount) {
    if (amount < 0) {
      amount = 1;
    } else if (amount > 100) {
      amount = 100;
    }
    Ticket ticket = Starfish.getLoader().getTicketByChannel(channel.getIdLong());
    if (ticket != null) return;
    channel
        .getHistory()
        .retrievePast(amount)
        .queue(messages -> messages.forEach(message -> message.delete().queue()));
  }
}
