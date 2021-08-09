package com.starfishst.bot.utility;

import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Offer;
import java.lang.ref.SoftReference;
import java.util.Objects;
import lombok.NonNull;
import lombok.experimental.Delegate;

public class OfferMessage implements Offer {

  @NonNull private final SoftReference<BotResponsiveMessage> message;

  public OfferMessage(@NonNull BotResponsiveMessage message) {
    this.message = new SoftReference<>(message);
  }

  @NonNull
  @Delegate
  public BotResponsiveMessage getMessage() {
    return Objects.requireNonNull(this.message.get(), "Reference to message has expired");
  }

  @Override
  public long getFreelancer() {
    return this.getData().getOr("freelancer", Long.class, -1L);
  }

  @Override
  public @NonNull String getOffer() {
    return this.getData().getOr("offer", String.class, "No offer to be shown");
  }

  @Override
  public long getTicket() {
    return this.getData().getOr("ticket", Long.class, -1L);
  }
}
