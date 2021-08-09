package com.starfishst.bot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import lombok.NonNull;
import me.googas.starbox.Strings;

/**
 * Make a formatter for you handlers easily.
 *
 * <p>Your string to be formatted can have the next placeholders (all of them must look like
 * %placeholder name%):
 *
 * <ul>
 *   <li><b>day:</b> Gives the day of the month
 *   <li><b>month:</b> Gives the number of the month
 *   <li><b>year:</b> Gives the year
 *   <li><b>hour:</b> Gives the hour
 *   <li><b>minute:</b> Gives the minute
 *   <li><b>second:</b> Gives the second
 *   <li><b>level:</b> Gives the level of log as in {@link Level#toString()}
 *   <li><b>message:</b> Gives the message logging
 *   <li><b>stack:</b>Gives the stacktrace of an exception
 * </ul>
 */
public class CustomFormatter extends Formatter {

  @NonNull private final String format;

  /**
   * Create an instance
   *
   * @param format the format to use which may contain the placeholders that this formatter supports
   */
  public CustomFormatter(@NonNull String format) {
    this.format = format;
  }

  /**
   * Gets the placeholders to use in the process
   *
   * @param record the record to format
   * @return a map of placeholders
   */
  private Map<String, String> getPlaceholders(@NonNull LogRecord record) {
    HashMap<String, String> placeHolders = new HashMap<>();
    this.addTimePlaceholders(record, placeHolders);
    placeHolders.put("level", record.getLevel().getName());
    placeHolders.put("message", record.getMessage());
    placeHolders.put("stack", "");
    return placeHolders;
  }

  /**
   * Get the time placeholders of a record and add them to the map of placeholders
   *
   * @param record the record to get the placeholders from
   * @param placeHolders the map of the placeholders in which the time placeholders will be included
   */
  private void addTimePlaceholders(
      @NonNull LogRecord record, @NonNull Map<String, String> placeHolders) {
    LocalDateTime date = TimeUtils.getLocalDateFromMillis(record.getMillis());
    placeHolders.put("day", String.valueOf(date.getDayOfMonth()));
    placeHolders.put("month", String.valueOf(date.getMonthValue()));
    placeHolders.put("year", String.valueOf(date.getYear()));
    placeHolders.put("hour", String.valueOf(date.getHour()));
    placeHolders.put("minute", String.valueOf(date.getMinute()));
    placeHolders.put("second", String.valueOf(date.getSecond()));
  }

  @NonNull
  private String getStackTrace(@NonNull Throwable throwable) {
    Writer stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  @Override
  public String format(@NonNull LogRecord record) {
    Map<String, String> placeholders = this.getPlaceholders(record);
    if (record.getThrown() != null) {
      String message = record.getThrown().getMessage();
      placeholders.put("message", message == null ? "No information provided" : message);
      placeholders.put("stack", this.getStackTrace(record.getThrown()));
    }
    return Strings.format(this.format, placeholders);
  }
}
