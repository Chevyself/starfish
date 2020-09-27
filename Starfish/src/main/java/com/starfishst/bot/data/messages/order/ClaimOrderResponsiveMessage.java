package com.starfishst.bot.data.messages.order;

import com.starfishst.bot.data.StarfishPreferences;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import java.util.HashSet;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** The message to claim an order */
public class ClaimOrderResponsiveMessage extends StarfishResponsiveMessage {

  /**
   * Create the responsive message. This constructor should be used when creating the responsive
   * message from a configuration
   *
   * @param id the id of the responsive message
   * @param data the data of the message
   */
  public ClaimOrderResponsiveMessage(long id, @NotNull StarfishPreferences data) {
    super(id, new HashSet<>(), "claim-order", data);
    this.addReactionResponse(new ClaimOrderReactionResponse(this));
  }

  /**
   * Create the responsive message. This constructor should be used in a message that was already
   * sent
   *
   * @param message the message to make the responsive message
   * @param data the data of the message
   */
  public ClaimOrderResponsiveMessage(@NotNull Message message, @NotNull StarfishPreferences data) {
    super(message, new HashSet<>(), "claim-order", data);
    this.addReactionResponse(new ClaimOrderReactionResponse(this), message);
  }
}
