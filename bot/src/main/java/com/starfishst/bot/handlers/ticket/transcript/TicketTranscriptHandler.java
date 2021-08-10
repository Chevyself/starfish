package com.starfishst.bot.handlers.ticket.transcript;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.tickets.Ticket;
import java.io.IOException;
import java.util.Map;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class TicketTranscriptHandler implements StarfishHandler {

  /**
   * Listen to the message receive event
   *
   * @param event the message receive event
   */
  @SubscribeEvent
  public void onMessageReceivedEvent(GuildMessageReceivedEvent event) {
    Starfish.getLoader()
        .getTicketByChannel(event.getChannel().getIdLong())
        .ifPresent(
            ticket -> {
              TicketTranscript transcript = this.getTranscript(ticket);
              Map<String, String> placeholders = this.getPlaceholders(event, ticket);
              LocaleFile lang = Starfish.getLanguageHandler().getDefault();
              transcript.getLogger().info(lang.get("transcript-log.message", placeholders));
            });
  }

  /**
   * Get the placeholders for the transcript message
   *
   * @param event the message received event
   * @param ticket the ticket owner of the transcript
   * @return the placeholders
   */
  @NonNull
  private Map<String, String> getPlaceholders(GuildMessageReceivedEvent event, Ticket ticket) {
    Map<String, String> placeholders = ticket.getPlaceholders();
    placeholders.put("sender", event.getAuthor().getAsTag());
    placeholders.put("senderId", String.valueOf(event.getAuthor().getIdLong()));
    StringBuilder builder = new StringBuilder();
    Message message = event.getMessage();
    builder.append(message.getContentRaw()).append(" ");
    event
        .getMessage()
        .getEmbeds()
        .forEach(
            embed -> {
              builder.append(embed.getTitle()).append(" ");
              builder.append(embed.getDescription()).append(" ");
            });
    placeholders.put("message", builder.toString());
    return placeholders;
  }

  private TicketTranscript getTranscript(@NonNull Ticket ticket) {
    return Starfish.getCache()
        .getOrSupply(
            TicketTranscript.class,
            transcript -> transcript.getId() == ticket.getId(),
            () -> {
              try {
                return new TicketTranscript(ticket).cache();
              } catch (IOException e) {
                Starfish.getFallback()
                    .process(
                        e, "Transcript for Ticket-" + ticket.getId() + " could not be initialized");
                return null;
              }
            });
  }

  @Override
  public String getName() {
    return "transcript";
  }
}
