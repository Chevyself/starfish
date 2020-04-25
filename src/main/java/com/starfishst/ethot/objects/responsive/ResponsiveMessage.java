package com.starfishst.ethot.objects.responsive;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.Configuration;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Responsive messages listen to reactions inside of discord and execute a desired action when the
 * unicode matches
 *
 * @author Chevy
 * @version 1.0.0
 */
public class ResponsiveMessage {

  /** The type of responsive message */
  @NotNull private final ResponsiveMessageType type;
  /** The id of the message that this will be listening to */
  private final long id;
  /** The list of reactions to listen to */
  @NotNull private final List<ReactionResponse> reactions;

  /**
   * The primary constructor
   *
   * @param type the type of the responsive message
   * @param message the message that this will be listening to
   * @param reactions the reactions that the message will be listening to
   * @param addToConfig if the message should be listened from the config
   */
  public ResponsiveMessage(
      @NotNull ResponsiveMessageType type,
      @NotNull Message message,
      @NotNull List<ReactionResponse> reactions,
      boolean addToConfig) {
    this.type = type;
    this.id = message.getIdLong();
    this.reactions = reactions;
    if (addToConfig) {
      addToConfig();
    }
    this.reactions.forEach(
        reactionResponse -> message.addReaction(reactionResponse.getUnicode()).queue());
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param type the type of responsive message
   * @param id the id of the message
   * @param reactions the reactions to listen to
   * @param addToConfig if the message should be listened from the config
   */
  public ResponsiveMessage(
      @NotNull ResponsiveMessageType type,
      long id,
      @NotNull List<ReactionResponse> reactions,
      boolean addToConfig) {
    this.type = type;
    this.id = id;
    this.reactions = reactions;
    if (addToConfig) {
      addToConfig();
    }
  }

  /** Adds the responsive message to the 'config.json' file */
  private void addToConfig() {
    if (Main.hasConfiguration()) {
      Configuration.getInstance().addResponsiveMessage(this);
    }
  }

  /**
   * Get the response (action) of a reaction to the discord message
   *
   * @param unicode the unicode of the reaction
   * @return if there's an action to the reaction else null
   */
  @Nullable
  public ReactionResponse getResponse(@NotNull String unicode) {
    return reactions.stream()
        .filter(reaction -> reaction.getUnicode().equalsIgnoreCase(unicode))
        .findFirst()
        .orElse(null);
  }

  /**
   * To identify multiple types of responsive messages
   *
   * @return the type of the message
   */
  @NotNull
  public ResponsiveMessageType getType() {
    return type;
  }

  /**
   * The id of the message to listen
   *
   * @return the id of the message
   */
  public long getId() {
    return id;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    ResponsiveMessage that = (ResponsiveMessage) object;

    if (id != that.id) return false;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (int) (id ^ (id >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "ResponsiveMessage{" + "type=" + type + ", id=" + id + '}';
  }
}
