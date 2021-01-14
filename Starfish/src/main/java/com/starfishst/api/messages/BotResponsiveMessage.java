package com.starfishst.api.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.bot.messages.StarfishReactionResponse;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An implementation for {@link ResponsiveMessage} */
public class BotResponsiveMessage implements ResponsiveMessage, StarfishCatchable {

  @Getter private final long id;

  @NonNull @Getter private final Set<ReactionResponse> reactions;

  @NonNull @Getter private final ValuesMap data;

  public BotResponsiveMessage(
      long id, @NonNull Set<? extends ReactionResponse> reactions, @NonNull ValuesMap data) {
    this.id = id;
    this.reactions = new HashSet<>(reactions);
    this.data = data;
  }

  public BotResponsiveMessage(long id, @NonNull ValuesMap data) {
    this(id, new HashSet<>(), data);
  }

  public BotResponsiveMessage(long id, @NonNull Set<? extends ReactionResponse> reactions) {
    this(id, new HashSet<>(reactions), new StarfishValuesMap());
  }

  public BotResponsiveMessage(long id) {
    this(id, new StarfishValuesMap());
  }

  /** Deletes the responsive message */
  public void delete() {
    Starfish.getLoader().deleteMessage(this);
  }

  /**
   * Makes all the reactions that are {@link
   * com.starfishst.bot.messages.StarfishReactionResponse#setMessage(BotResponsiveMessage)}
   */
  public void update() {
    for (ReactionResponse reaction : this.reactions) {
      if (reaction instanceof StarfishReactionResponse)
        ((StarfishReactionResponse) reaction).setMessage(this);
    }
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull BotResponsiveMessage cache() {
    return (BotResponsiveMessage) StarfishCatchable.super.cache();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("reactions", reactions)
        .append("data", data)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BotResponsiveMessage that = (BotResponsiveMessage) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
