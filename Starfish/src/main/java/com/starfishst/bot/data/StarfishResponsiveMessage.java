package com.starfishst.bot.data;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.messages.BotResponsiveMessage;
import com.starfishst.api.events.messages.BotMessageUnloadedEvent;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import com.starfishst.jda.utils.responsive.SimpleResponsiveMessage;
import java.util.Set;
import lombok.NonNull;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.entities.Message;

/** An implementation of {@link BotResponsiveMessage} */
public class StarfishResponsiveMessage extends SimpleResponsiveMessage
    implements BotResponsiveMessage {

  /** The type of the responsive message */
  @NonNull private final String type;
  /** The data of the message */
  @NonNull private final StarfishValuesMap data;

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
      @NonNull Set<ReactionResponse> reactions,
      @NonNull String type,
      @NonNull StarfishValuesMap data) {
    super(id, reactions);
    this.type = type;
    this.data = data;
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
      @NonNull Message message,
      @NonNull Set<ReactionResponse> reactions,
      @NonNull String type,
      @NonNull StarfishValuesMap data) {
    super(message, reactions);
    this.type = type;
    this.data = data;
  }

  /**
   * Get the type of the responsive message
   *
   * @return the type of responsive message
   */
  @Override
  @NonNull
  public String getType() {
    return type;
  }

  @Override
  public void onRemove() {
    new BotMessageUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return Starfish.getConfiguration().toUnloadMessages();
  }

  @NonNull
  @Override
  public StarfishValuesMap getData() {
    return this.data;
  }

  @Override
  public void delete() {
    Starfish.getLoader().deleteMessage(this);
  }
}
