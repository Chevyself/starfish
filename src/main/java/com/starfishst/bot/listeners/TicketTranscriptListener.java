package com.starfishst.bot.listeners;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.transcript.TicketTranscript;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.core.fallback.Fallback;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.starfishst.core.utils.Strings;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/** The listener of ticket transcripts. This adds the messages to the transcripts */
public class TicketTranscriptListener {

  /**
   * Listen to the message receive event
   *
   * @param event the message receive event
   */
  @SubscribeEvent
  public void onMessageReceivedEvent(GuildMessageReceivedEvent event) {
    Ticket ticket =
        TicketManager.getInstance().getLoader().getTicketByChannel(event.getChannel().getIdLong());
    if (ticket != null) {
      try {
        TicketTranscript transcript = ticket.getTranscript();
        HashMap<String, String> placeholders = getPlaceholders(event, ticket);
        transcript.getLogger().info(Lang.get("LOG_FORMAT_TRANSCRIPT_MESSAGE", placeholders));
      } catch (IOException e) {
        Fallback.addError(e.getMessage());
        Messages.error("There's been an error while trying to load the ticket transcript")
            .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
      }
    }
  }

  /**
   * Get the placeholders for the transcript message
   *
   * @param event the message received event
   * @param ticket the ticket owner of the transcript
   * @return the placeholders
   */
  @NotNull
  private HashMap<String, String> getPlaceholders(GuildMessageReceivedEvent event, Ticket ticket) {
    HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
    placeholders.put("sender", event.getAuthor().getAsTag());
    placeholders.put("senderId", String.valueOf(event.getAuthor().getIdLong()));
    StringBuilder builder = Strings.getBuilder();
    Message message = event.getMessage();
    builder.append(message.getContentRaw()).append(" ");
    event.getMessage().getEmbeds().forEach(embed -> {
      builder.append(embed.getTitle()).append(" ");
      builder.append(embed.getDescription()).append(" ");
    });
    placeholders.put("message", builder.toString());
    return placeholders;
  }
}
