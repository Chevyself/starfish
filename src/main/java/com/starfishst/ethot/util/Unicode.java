package com.starfishst.ethot.util;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Whats <a href="https://home.unicode.org/">unicode</>?
 *
 * <p>These are used mainly for reactions
 */
public class Unicode {

  @NotNull public static final String WHITE_CHECK_MARK = "U+2705";
  @NotNull public static final String HAMMER = "U+1F528";
  @NotNull public static final String MEMO = "U+1F4DD";
  @NotNull public static final String TICKET = "U+1F3AB";

  /*
  Numeric emojis
   */
  @NotNull public static final String ONE = "U+31U+20E3";
  @NotNull public static final String TWO = "U+32U+20E3";
  @NotNull public static final String THREE = "U+33U+20E3";
  @NotNull public static final String FOUR = "U+34U+20E3";
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
