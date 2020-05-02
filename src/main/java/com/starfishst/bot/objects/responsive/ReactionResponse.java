package com.starfishst.bot.objects.responsive;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class is the one that executes the action when the unicode matches
 *
 * @author Chevy
 * @version 1.0.0
 */
public interface ReactionResponse {

  /**
   * The unicode to match and run the action
   *
   * @return the unicode
   */
  @NotNull
  String getUnicode();

  /**
   * If unicode matches this is the action to run
   *
   * @param event the reaction event
   */
  void onReaction(@NotNull GuildMessageReactionAddEvent event);
}
