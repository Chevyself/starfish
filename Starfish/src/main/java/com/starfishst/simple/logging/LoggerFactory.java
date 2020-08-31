package com.starfishst.simple.logging;

import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.core.utils.time.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A factory for creating loggers */
public class LoggerFactory {

  /**
   * Gets a logger that will save files in /logs and format strings
   *
   * @param name the name of the logger
   * @param format the format to use
   * @param url the url to a file to use instead
   * @return a brand new logger
   * @throws IOException in case anything goes wrong
   */
  @NotNull
  public static Logger getLogger(@NotNull String name, @NotNull String format, @Nullable String url)
      throws IOException {
    Logger logger = Logger.getLogger(name);
    Formatter formatter = new CustomFormatter(format);
    logger.setUseParentHandlers(false);
    logger.addHandler(getFileHandler(formatter, url));
    logger.addHandler(getConsoleHander(formatter));
    return logger;
  }

  /**
   * Get a file handler which creates files in /logs
   *
   * @param formatter the formatter to use in the handler
   * @param url the path to the file
   * @return the handler created
   * @throws IOException in case the /logs directory could not be created
   */
  @NotNull
  public static FileHandler getFileHandler(@NotNull Formatter formatter, @Nullable String url)
      throws IOException {
    String path = CoreFiles.currentDirectory() + File.separator + "logs" + File.separator;
    File logsDirectory = new File(path);
    if (!logsDirectory.exists()) {
      if (!logsDirectory.mkdir()) {
        throw new IOException("The logs directory could not be created");
      }
    }
    LocalDateTime date = TimeUtils.getLocalDateFromMillis(System.currentTimeMillis());
    FileHandler handler;
    if (url != null) {
      handler = new FileHandler(url, true);
    } else {
      handler =
          new FileHandler(
              path
                  + date.getMonthValue()
                  + "-"
                  + date.getDayOfMonth()
                  + "-"
                  + date.getYear()
                  + " @ "
                  + date.getHour()
                  + "-"
                  + date.getMinute()
                  + ".txt");
    }
    handler.setFormatter(formatter);
    return handler;
  }

  /**
   * Get a new console handler
   *
   * @param formatter the formatter to use in the handler
   * @return the handler formatted
   */
  @NotNull
  public static ConsoleHandler getConsoleHander(@NotNull Formatter formatter) {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(formatter);
    return handler;
  }
}
