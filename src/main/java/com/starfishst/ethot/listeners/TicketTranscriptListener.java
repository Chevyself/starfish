package com.starfishst.ethot.listeners;

import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.tickets.transcript.TicketTranscript;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class TicketTranscriptListener {

  @SubscribeEvent
  public void onMessageReceivedEvent(GuildMessageReceivedEvent event) {
    Ticket ticket =
        Main.getManager().getLoader().getTicketByChannel(event.getChannel().getIdLong());
    if (ticket != null) {
      try {
        TicketTranscript transcript = ticket.getTranscript();
        HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
        placeholders.put("sender", event.getAuthor().getAsTag());
        placeholders.put("senderId", String.valueOf(event.getAuthor().getIdLong()));
        placeholders.put("message", event.getMessage().getContentRaw());
        transcript.getLogger().info(Lang.get("LOG_FORMAT_TRANSCRIPT_MESSAGE", placeholders));
      } catch (IOException e) {
        Errors.addError(e.getMessage());
        Messages.error("There's been an error while trying to load the ticket transcript")
            .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
      }
    }
  }
}
