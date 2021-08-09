package com.starfishst.bot.handlers.ticket.transcript;

import com.starfishst.api.Starfish;
import com.starfishst.api.StarfishFiles;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.bot.CustomFormatter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.starbox.builders.LoggerBuilder;
import me.googas.starbox.time.Time;

public class TicketTranscript implements StarfishCatchable {

  /** The id of the ticket */
  @Getter private final long id;

  /** The logger that is transcription of the messages */
  @NonNull @Getter private final Logger logger;

  /** The file where the transcript is saved */
  @NonNull @Getter private final StarboxFile file;

  public TicketTranscript(@NonNull Ticket ticket) throws IOException {
    this.file = StarfishFiles.Transcripts.getTicketFile(ticket.getId());
    if (!this.file.exists()) {
      this.file.getParentFile().mkdirs();
      if (!this.file.createNewFile()) throw new IOException(this.file + " could not be created");
    }
    CustomFormatter formatter =
        new CustomFormatter(Starfish.getLanguageHandler().getDefault().get("transcript-log"));
    FileHandler fileHandler = new FileHandler(this.file.getPath(), true);
    fileHandler.setFormatter(formatter);
    this.id = ticket.getId();
    this.logger =
        LoggerBuilder.of("Ticket-" + ticket.getId())
            .saveAt(
                this.file,
                e -> {
                  Starfish.getFallback()
                      .process(e, "Could not save transcript of Ticket-" + ticket.getId());
                })
            .build();
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
