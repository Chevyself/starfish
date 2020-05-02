package com.starfishst.bot.util;

import com.starfishst.bot.config.language.Lang;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** Methods for unicode usage */
public class Unicode {

  /** The unicode of :white_check_mark: */
  @Deprecated @NotNull public static final String WHITE_CHECK_MARK = "U+2705";
  /** The unicode of :hammer: */
  @Deprecated @NotNull public static final String HAMMER = "U+1F528";
  /** The unicode of :pencil: */
  @Deprecated @NotNull public static final String MEMO = "U+1F4DD";
  /** The unicode of :ticket: */
  @Deprecated @NotNull public static final String TICKET = "U+1F3AB";

  /** Unicode of :1: */
  @Deprecated @NotNull public static final String ONE = "U+31U+20E3";
  /** Unicode of :2: */
  @Deprecated @NotNull public static final String TWO = "U+32U+20E3";
  /** Unicode of :3: */
  @Deprecated @NotNull public static final String THREE = "U+33U+20E3";
  /** Unicode of :4: */
  @Deprecated @NotNull public static final String FOUR = "U+34U+20E3";
  /** Unicode of :5: */
  @Deprecated @NotNull public static final String FIVE = "U+35U+20E3";

  /**
   * From a reaction event get the unicode of the emoji
   *
   * @param event the reaction event
   * @return the unicode of the emoji
   */
  public static String fromReaction(final GuildMessageReactionAddEvent event) {
    return event.getReactionEmote().toString().replace("RE:", "");
  }

  /**
   * Get an specific emoji. It must have the unicode format provided by services like <a
   * href="https://unicode.org/emoji/charts/full-emoji-list.html">unicode.org</a>
   *
   * @param key the key to find the emoji
   * @return the emoji
   */
  public static String getEmoji(@NotNull String key) {
    return Lang.get(key);
  }
}
