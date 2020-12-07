package com.starfishst.bot.data.messages.order;

import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import java.util.HashSet;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;

/** The message to claim an order */
public class ClaimOrderResponsiveMessage extends StarfishResponsiveMessage {
  /**
   * Create the responsive message. This constructor should be used in a message that was already
   * sent
   *
   * @param message the message to make the responsive message
   * @param data the data of the message
   */
  public ClaimOrderResponsiveMessage(@NonNull Message message, @NonNull StarfishValuesMap data) {
    super(message, new HashSet<>(), "claim-order", data);
    this.addReactionResponse(new ClaimOrderReactionResponse(this), message);
  }
}
