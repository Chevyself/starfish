package com.starfishst.bot.utility;

import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Offer;
import java.lang.ref.SoftReference;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.commons.Validate;

public class OfferMessage implements Offer {

  @NonNull private final SoftReference<BotResponsiveMessage> message;

  public OfferMessage(@NonNull BotResponsiveMessage message) {
    this.message = new SoftReference<>(message);
  }

  @NonNull
  @Delegate
  public BotResponsiveMessage getMessage() {
    return Validate.notNull(message.get(), "Reference to message has expired");
  }

  @Override
  public long getFreelancer() {
    return this.getData().getValueOr("freelancer", Long.class, -1L);
  }

  @Override
  public @NonNull String getOffer() {
    return this.getData().getValueOr("offer", String.class, "No offer to be shown");
  }

  @Override
  public long getTicket() {
    return this.getData().getValueOr("ticket", Long.class, -1L);
  }
}
