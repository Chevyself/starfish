package com.starfishst.ethot.tickets.type;

import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.freelancers.Offer;
import com.starfishst.ethot.config.objects.questions.Answer;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.util.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Quotes are like Order but they have a different system when it comes to adding Freelancers. The
 * customer must accept the freelancer's offer in order to get in the ticket
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Quote extends FreelancingTicket {

  @NotNull private final List<Offer> offers;

  public Quote(@NotNull Order parent, @NotNull TextChannel channel) {
    this(
        parent.getId(),
        parent.getUser(),
        TicketStatus.CREATING,
        channel,
        parent.getAnswers(),
        null,
        new ArrayList<>());
    setCurrent(parent.getCurrent());
  }

  public Quote(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> details,
      @Nullable Freelancer freelancer,
      @NotNull List<Offer> offers) {
    super(id, user, status, channel, details, freelancer);
    this.offers = offers;
  }

  /**
   * Adds an offer to the quote
   *
   * @param offer the offer to add
   */
  public void addOffer(@NotNull Offer offer) {
    offers.add(offer);
    refresh();
  }

  /**
   * Get the list of offers
   *
   * @return the list of offers
   */
  @NotNull
  public List<Offer> getOffers() {
    return offers;
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.QUOTE;
  }

  @Override
  public void onCreation() {}

  @Override
  public @NotNull Document getDocument() {
    Document document = super.getDocument();
    document.append("offers", offers);
    return document;
  }

  @Nullable
  public Offer getOfferByMessageId(long id) {
    return offers.stream()
        .filter(offer -> offer.getMessage().getId() == id)
        .findFirst()
        .orElse(null);
  }

  @Nullable
  public Offer getOffer(long id) {
    return offers.stream()
        .filter(offer -> offer != null && offer.getMessage().getId() == id)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void onDone() {
    try {
      Messages.announce(this).send(getType().getChannel());
    } catch (DiscordManipulationException e) {
      Messages.error("This ticket could not be announced");
      Errors.addError(e.getMessage());
    }
    super.onDone();
  }
}
