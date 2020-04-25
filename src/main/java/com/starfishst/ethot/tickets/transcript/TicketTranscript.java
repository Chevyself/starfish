package com.starfishst.ethot.tickets.transcript;

import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.core.utils.time.Unit;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.simple.files.FileUtils;
import com.starfishst.simple.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class TicketTranscript extends Catchable {

  @NotNull private final Logger logger;
  @NotNull private final Ticket ticket;
  @NotNull private String url;

  public TicketTranscript(@NotNull Ticket ticket) throws IOException {
    super(new Time(30, Unit.MINUTES));
    this.ticket = ticket;
    this.url = FileUtils.getCurrentDirectory() + "\\transcripts\\";
    File logsDirectory = new File(url);
    if (!logsDirectory.exists()) {
      if (!logsDirectory.mkdir()) {
        throw new IOException("The logs directory could not be created");
      }
    }
    this.url += "ticket-" + ticket.getId() + "-transcript.txt";
    logger =
        LoggerFactory.getLogger("ticket-" + ticket.getId(), Lang.get("LOG_FORMAT_TRANSCRIPT"), url);
  }

  @NotNull
  public Logger getLogger() {
    return logger;
  }

  @NotNull
  public Ticket getTicket() {
    return ticket;
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    for (Handler handler : getLogger().getHandlers()) {
      handler.close();
    }
  }

  @NotNull
  public File getFile() throws IOException {
    return FileUtils.getFile(url);
  }
}
