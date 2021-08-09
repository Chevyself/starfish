package com.starfishst.api.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import lombok.Getter;
import lombok.NonNull;
import me.googas.commands.jda.utils.responsive.ReactionResponse;
import me.googas.commands.jda.utils.responsive.ResponsiveMessage;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;
import net.dv8tion.jda.api.entities.Message;

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

  public BotResponsiveMessage(
      @NonNull Message message,
      @NonNull Set<? extends ReactionResponse> reactions,
      @NonNull ValuesMap data) {
    this(message.getIdLong(), new HashSet<>(), data);
    for (ReactionResponse reaction : reactions) {
      this.addReactionResponse(reaction, message);
    }
  }

  public BotResponsiveMessage(long id, @NonNull ValuesMap data) {
    this(id, new HashSet<>(), data);
  }

  public BotResponsiveMessage(long id, @NonNull Set<? extends ReactionResponse> reactions) {
    this(id, new HashSet<>(reactions), new ValuesMap());
  }

  public BotResponsiveMessage(long id) {
    this(id, new ValuesMap());
  }

  /** Deletes the responsive message */
  public void delete() {
    Starfish.getLoader().deleteMessage(this);
  }

  /**
   * Makes all the reactions that are {@link
   * StarfishReactionResponse#setMessage(BotResponsiveMessage)}
   */
  public void update() {
    for (ReactionResponse reaction : this.reactions) {
      if (reaction instanceof StarfishReactionResponse)
        ((StarfishReactionResponse) reaction).setMessage(this);
    }
  }

  @Override
  public @NonNull Time getToRemove() {
    return Time.of(3, Unit.MINUTES);
  }

  @Override
  public @NonNull BotResponsiveMessage cache() {
    return (BotResponsiveMessage) StarfishCatchable.super.cache();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", BotResponsiveMessage.class.getSimpleName() + "[", "]")
            .add("id=" + this.id)
            .add("reactions=" + this.reactions)
            .add("data=" + this.data)
            .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    BotResponsiveMessage that = (BotResponsiveMessage) o;
    return this.id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public void onRemove() {
    new BotMessageUnloadedEvent(this).call();
  }
}
