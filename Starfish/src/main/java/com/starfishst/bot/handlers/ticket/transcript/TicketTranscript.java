package com.starfishst.bot.handlers.ticket.transcript;

import com.starfishst.api.Starfish;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.utility.StarfishCatchable;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.CoreFiles;
import me.googas.commons.log.LoggerFactory;
import me.googas.commons.log.formatters.CustomFormatter;
import me.googas.commons.time.Time;

public class TicketTranscript implements StarfishCatchable {

  /** The id of the ticket */
  @Getter private final long id;

  /** The logger that is transcription of the messages */
  @NonNull @Getter private final Logger logger;

  /** The file where the transcript is saved */
  @NonNull @Getter private final File file;

  public TicketTranscript(@NonNull Ticket ticket) throws IOException {
    String url =
        CoreFiles.validatePath(
            CoreFiles.currentDirectory() + "/transcripts/ticket-" + ticket.getId() + ".txt");
    CustomFormatter formatter =
        new CustomFormatter(Starfish.getLanguageHandler().getDefault().get("transcript-log"));
    this.file = CoreFiles.getOrCreate(url);
    FileHandler fileHandler = new FileHandler(url, true);
    fileHandler.setFormatter(formatter);
    this.id = ticket.getId();
    this.logger =
        LoggerFactory.start(
            "Ticket-" + ticket.getId(), LoggerFactory.getConsoleHandler(formatter), fileHandler);
  }

  @Override
  public void onRemove() {
    for (Handler handler : this.getLogger().getHandlers()) {
      handler.close();
    }
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().getUnloadTickets();
  }

  @Override
  public @NonNull TicketTranscript cache() {
    return (TicketTranscript) StarfishCatchable.super.cache();
  }
}
