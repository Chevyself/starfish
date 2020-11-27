package com.starfishst.api.utility.console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import me.googas.commons.Strings;
import me.googas.commons.time.TimeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Make a formatter for you handlers easily.
 *
 * <p>Your string to be formatted can have the next place holders (all of them must look like
 * %placeholder name%): - day: Gives the day of the month - month: Gives the number of the month -
 * year: Gives the year - hour: Gives the hour - minute: Gives the minute - second: Gives the second
 * - level: Gives the level of the log - message: Gives the message logging - stack: Gives the stack
 * trace in case of a thrown error
 */
public class CustomFormatter extends Formatter {

  /** The format for the formatter */
  @NotNull private final String format;

  /**
   * Create an instance
   *
   * @param format the format to use
   */
  public CustomFormatter(@NotNull String format) {
    this.format = format;
  }

  /**
   * Gets the placeholders to use in the process
   *
   * @param record the record to format
   * @return a pack of placeholders
   */
  private HashMap<String, String> getPlaceholders(@NotNull LogRecord record) {
    HashMap<String, String> placeHolders = new HashMap<>();
    this.addTimePlaceholders(record, placeHolders);
    placeHolders.put("level", record.getLevel().getName());
    placeHolders.put("message", record.getMessage());
    placeHolders.put("stack", "");
    return placeHolders;
  }

  /**
   * Get the time placeholders of a record
   *
   * @param record the record to get the placeholders from
   * @param placeHolders the brand new place holders
   */
  private void addTimePlaceholders(
      @NotNull LogRecord record, @NotNull HashMap<String, String> placeHolders) {
    LocalDateTime date = TimeUtils.getLocalDateFromMillis(record.getMillis());
    placeHolders.put("day", String.valueOf(date.getDayOfMonth()));
    placeHolders.put("month", String.valueOf(date.getMonthValue()));
    placeHolders.put("year", String.valueOf(date.getYear()));
    placeHolders.put("hour", String.valueOf(date.getHour()));
    placeHolders.put("minute", String.valueOf(date.getMinute()));
    placeHolders.put("second", String.valueOf(date.getSecond()));
  }

  @NotNull
  private String getStackTrace(@NotNull Throwable throwable) {
    Writer stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  @Override
  public String format(@NotNull LogRecord record) {
    HashMap<String, String> placeholders = this.getPlaceholders(record);
    if (record.getThrown() != null) {
      String message = record.getThrown().getMessage();
      placeholders.put("message", message == null ? "No information provided" : message);
      placeholders.put("stack", this.getStackTrace(record.getThrown()));
    }
    return Strings.buildMessage(this.format, placeholders);
  }
}
