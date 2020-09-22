package com.starfishst.bot.objects.responsive.type.unicode;

import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.objects.responsive.ResponsiveMessageType;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Unicode;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Is a message that gives the Unicode of the reaction that was added to it */
public class UnicodeGiverResponsiveMessage extends ResponsiveMessage {

  /**
   * Generic constructor for the message
   *
   * @param message the message that is going to be the unicode giver
   */
  public UnicodeGiverResponsiveMessage(@NotNull Message message) {
    this(message.getIdLong());
  }

  /**
   * Constructor for databases and config
   *
   * @param id the id of the message
   */
  public UnicodeGiverResponsiveMessage(long id) {
    super(ResponsiveMessageType.UNICODE_GIVER, id, new ArrayList<>(), true);
    this.addReactionResponse(new UnicodeGiverReactionResponse());
  }

  @Override
  public @Nullable ReactionResponse getResponse(@NotNull String unicode) {
    return reactions.stream()
        .filter(
            reaction ->
                reaction.getUnicode().equalsIgnoreCase(unicode)
                    | reaction.getUnicode().equalsIgnoreCase("any"))
        .findFirst()
        .orElse(null);
  }

  /** The reaction response to give the unicode */
  public static class UnicodeGiverReactionResponse implements ReactionResponse {

    @Override
    public @NotNull String getUnicode() {
      return "any";
    }

    @Override
    public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
      HashMap<String, String> placeholders = new HashMap<>();
      placeholders.put("unicode", Unicode.fromReaction(event).toUpperCase());
      if (event.getReactionEmote().isEmoji()) {
        placeholders.put("emoji", event.getReactionEmote().getEmoji());
      }
      Messages.create(
              Lang.get("EMOJI_UNICODE_TITLE", placeholders),
              Lang.get("EMOJI_UNICODE_DESCRIPTION", placeholders))
          .send(event.getChannel());
    }
  }
}
