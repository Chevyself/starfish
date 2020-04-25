package com.starfishst.ethot.util;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Whats <a href="https://home.unicode.org/">unicode</a>?
 *
 * <p>These are used mainly for reactions TODO deprecate this class and allow people to use their
 * own unicodes
 */
public class Unicode {

  /** The unicode of :white_check_mark: */
  @NotNull public static final String WHITE_CHECK_MARK = "U+2705";
  /** The unicode of :hammer: */
  @NotNull public static final String HAMMER = "U+1F528";
  /** The unicode of :pencil: */
  @NotNull public static final String MEMO = "U+1F4DD";
  /** The unicode of :ticket: */
  @NotNull public static final String TICKET = "U+1F3AB";

  /** Unicode of :1: */
  @NotNull public static final String ONE = "U+31U+20E3";
  /** Unicode of :2: */
  @NotNull public static final String TWO = "U+32U+20E3";
  /** Unicode of :3: */
  @NotNull public static final String THREE = "U+33U+20E3";
  /** Unicode of :4: */
  @NotNull public static final String FOUR = "U+34U+20E3";
  /** Unicode of :5: */
  @NotNull public static final String FIVE = "U+35U+20E3";

  /**
   * From a reaction event get the unicode of the emoji
   *
   * @param event the reaction event
   * @return the unicode of the emoji
   */
  public static String fromReaction(final GuildMessageReactionAddEvent event) {
    return event.getReactionEmote().toString().replace("RE:", "");
  }
}
