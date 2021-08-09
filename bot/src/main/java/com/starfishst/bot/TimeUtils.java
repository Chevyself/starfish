package com.starfishst.bot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.NonNull;

/** Static utilities for time */
@Deprecated
public class TimeUtils {

  /**
   * Get local date from toMillis.
   *
   * @param millis the toMillis to get the date from
   * @return the date
   */
  @NonNull
  public static LocalDateTime getLocalDateFromMillis(long millis) {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  /**
   * Get the offset date from toMillis
   *
   * @param millis the toMillis to get the offset date
   * @return the offset date
   */
  @NonNull
  public static OffsetDateTime getOffsetDateFromMillis(long millis) {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toOffsetDateTime();
  }

  /**
   * Get the toMillis from a local date
   *
   * @param date to get the toMillis from
   * @return the toMillis
   */
  public static long getMillisFromLocalDate(@NonNull LocalDateTime date) {
    return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
  /**
   * Get the toMillis from a local date
   *
   * @param date to get the toMillis from
   * @return the toMillis
   */
  public static long getMillisFromOffsetDate(@NonNull OffsetDateTime date) {
    return date.toInstant().toEpochMilli();
  }
}
