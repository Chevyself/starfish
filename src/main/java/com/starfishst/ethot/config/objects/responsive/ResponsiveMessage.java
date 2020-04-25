package com.starfishst.ethot.config.objects.responsive;

import com.starfishst.ethot.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Responsive messages listen to reactions inside of discord and execute a desired action when the
 * unicode matches
 *
 * @author Chevy
 * @version 1.0.0
 */
public class ResponsiveMessage {

  @NotNull private final ResponsiveMessageType type;
  private final long id;
  @NotNull private final List<ReactionResponse> reactions;

  public ResponsiveMessage(
      @NotNull ResponsiveMessageType type,
      long id,
      @NotNull List<ReactionResponse> reactions,
      boolean addToConfig) {
    this.type = type;
    this.id = id;
    this.reactions = reactions;
    if (addToConfig) {
      if (Main.hasConfiguration()) {
        Main.getConfiguration().addResponsiveMessage(this);
      }
    }
  }

  public ResponsiveMessage(
      @NotNull ResponsiveMessageType type,
      long id,
      boolean addToConfig,
      ReactionResponse... reactions) {
    this(type, id, Arrays.asList(reactions), addToConfig);
  }

  public ResponsiveMessage(@NotNull ResponsiveMessageType type, long id, boolean addToConfig) {
    this(type, id, new ArrayList<>(), addToConfig);
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
