package com.starfishst.bot.data.messages.offer;

import com.starfishst.api.data.tickets.Offer;
import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import java.util.HashSet;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A message sent to a quote ticket */
public class OfferMessage extends StarfishResponsiveMessage implements Offer {

  /**
   * Create the offer message
   *
   * @param id the id of the message
   * @param data the data of the message
   */
  public OfferMessage(long id, @NotNull StarfishValuesMap data) {
    super(id, new HashSet<>(), "offer", data);
    this.addReactionResponse(new OfferAcceptReactionResponse(this));
  }

  /**
   * Create the offer message
   *
   * @param message the the message
   * @param data the data of the message
   */
  public OfferMessage(@NotNull Message message, @NotNull StarfishValuesMap data) {
    super(message, new HashSet<>(), "offer", data);
    this.addReactionResponse(new OfferAcceptReactionResponse(this), message);
  }

  /**
   * Get the id of the freelancer that made the offer
   *
   * @return the id of the freelancer
   */
  @Override
  public long getFreelancer() {
    return this.getData().getValueOr("freelancer", Long.class, -1L);
  }

  /**
   * Get the offer
   *
   * @return the offer as a string
   */
  @Override
  public @NotNull String getOffer() {
    return this.getData().getValueOr("offer", String.class, "No offer");
  }

  /**
   * Get the ticket id
   *
   * @return the ticket id of the offer
   */
  @Override
  public long getTicket() {
    return this.getData().getValueOr("ticket", Long.class, -1L);
  }

  @Override
  public int hashCode() {
    int result = (int) (this.getFreelancer() ^ (this.getFreelancer() >>> 32));
    result = 31 * result + this.getOffer().hashCode();
    result = 31 * result + (int) (this.getTicket() ^ (this.getTicket() >>> 32));
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Offer) {
      Offer that = (Offer) obj;
      return this.getTicket() == that.getTicket()
          && this.getFreelancer() == that.getFreelancer()
          && this.getOffer().equalsIgnoreCase(that.getOffer());
    }
    return false;
  }
}
