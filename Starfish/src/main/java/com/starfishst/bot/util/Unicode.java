package com.starfishst.bot.util;

import com.starfishst.bot.config.language.Lang;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** Methods for unicode usage */
public class Unicode {

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
