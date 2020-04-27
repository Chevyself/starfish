package com.starfishst.ethot.tickets.type;

import com.starfishst.core.utils.Atomic;
import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.freelancers.Offer;
import com.starfishst.ethot.objects.questions.Answer;
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
 */
public class Quote extends FreelancingTicket {

  /** The list of offers send to this quote */
  @NotNull private final List<Offer> offers;

  /**
   * The constructor for when the ticket is just being created
   *
   * @param parent the parent that created this ticket
   * @param channel the channel of the ticket
   */
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

  /**
   * The constructor for when the ticket is being created from the database
   *
   * @param id the id of the ticket
   * @param user the user that created the ticket
   * @param status the status of the ticket
   * @param channel the channel where the ticket has been created
   * @param details the details provided for the freelancers
   * @param freelancer the freelancer that created the ticket
   * @param offers the offers that send the ticket
   */
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

  /**
   * Get an offer from the message sent
   *
   * @param id the id of the message
   * @return the offer if found
   */
  @Nullable
  public Offer getOfferByMessageId(long id) {
    return offers.stream()
        .filter(offer -> offer.getMessage() != null && offer.getMessage().getId() == id)
        .findFirst()
        .orElse(null);
  }

  /**
   * Counts how many offers has a freelancer sent to a quote
   *
   * @param freelancer the freelancer to check
   * @return the number of offers send or zero if none
   */
  public int countOffers(@NotNull Freelancer freelancer) {
    Atomic<Integer> atomic = new Atomic<>(0);
    offers.forEach(
        offer -> {
          if (offer.getFreelancer() == freelancer) {
            atomic.set(atomic.get() + 1);
          }
        });
    return atomic.get();
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
