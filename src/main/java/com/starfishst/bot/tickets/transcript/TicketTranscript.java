package com.starfishst.bot.tickets.transcript;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.core.utils.time.Unit;
import com.starfishst.simple.files.FileUtils;
import com.starfishst.simple.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/** Ticket transcripts are the messages send in a ticket channel saved in a log file inside /logs */
public class TicketTranscript extends Catchable {

  /** The logger instance used to save the messages */
  @NotNull private final Logger logger;
  /** The ticket that this is listening to */
  @NotNull private final Ticket ticket;
  /** The path to the file */
  @NotNull private String url;

  /**
   * The generic constructor to get the file
   *
   * @param ticket the ticket to log messages from
   * @throws IOException in case that file manipulation goes wrong
   */
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

  /**
   * Get the logger instance
   *
   * @return the logger instance
   */
  @NotNull
  public Logger getLogger() {
    return logger;
  }

  /**
   * Get the ticket that this is listening to
   *
   * @return the ticket
   */
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

  /**
   * Get this as the file created
   *
   * @return the file
   * @throws IOException in case working with files goes wrong
   */
  @NotNull
  public File getFile() throws IOException {
    return FileUtils.getFile(url);
  }
}
