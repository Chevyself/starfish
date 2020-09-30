package com.starfishst.bot.data;

import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.api.events.messages.BotMessageLoadedEvent;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.utils.responsive.ReactionResponse;
import com.starfishst.commands.utils.responsive.SimpleResponsiveMessage;
import com.starfishst.core.utils.time.Time;
import com.starfishst.core.utils.time.Unit;
import java.util.Set;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** An implementation of {@link BotResponsiveMessage} */
public class StarfishResponsiveMessage extends SimpleResponsiveMessage
    implements BotResponsiveMessage {

  /** The time to remove the message from cache */
  @NotNull private final Time start;
  /** The type of the responsive message */
  @NotNull private final String type;
  /** The data of the message */
  @NotNull private final StarfishValuesMap data;
  /** The seconds left */
  private long secondsLeft;

  /**
   * Create the responsive message. This constructor should be used when creating the responsive
   * message from a configuration
   *
   * @param id the id of the responsive message
   * @param reactions the reactions to add in the message
   * @param type the type of the message
   * @param data the data of the message
   */
  public StarfishResponsiveMessage(
      long id,
      @NotNull Set<ReactionResponse> reactions,
      @NotNull String type,
      @NotNull StarfishValuesMap data) {
    super(id, reactions);
    this.type = type;
    this.data = data;
    this.start = Starfish.getConfiguration().toUnloadMessages();
    this.addToCache();
    new BotMessageLoadedEvent(this).call();
  }

  /**
   * Create the responsive message. This constructor should be used in a message that was already
   * sent
   *
   * @param message the message to make the responsive message
   * @param reactions the reactions to use
   * @param type the type of the message
   * @param data the data of the message
   */
  public StarfishResponsiveMessage(
      @NotNull Message message,
      @NotNull Set<ReactionResponse> reactions,
      @NotNull String type,
      @NotNull StarfishValuesMap data) {
    super(message, reactions);
    this.type = type;
    this.data = data;
    this.start = Starfish.getConfiguration().toUnloadMessages();
    this.addToCache();
    new BotMessageLoadedEvent(this).call();
  }

  /**
   * Get the type of the responsive message
   *
   * @return the type of responsive message
   */
  @Override
  @NotNull
  public String getType() {
    return type;
  }

  @Override
  public @NotNull StarfishResponsiveMessage refresh() {
    this.secondsLeft = this.start.getValue(Unit.SECONDS);
    return this;
  }

  @Override
  public void reduceTime(long l) {
    this.secondsLeft -= l;
  }

  @Override
  public void onSecondsPassed() {
  }

  @Override
  public void onRemove() {
    new BotMessageUnloadedEvent(this).call();
  }

  @Override
  public long getSecondsLeft() {
    return this.secondsLeft;
  }

  @NotNull
  @Override
  public StarfishValuesMap getData() {
    return this.data;
  }

  @Override
  public void delete() {
    Starfish.getLoader().deleteMessage(this);
  }
}
