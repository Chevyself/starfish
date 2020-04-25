package com.starfishst.ethot.tickets.type;

import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.Validate;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.objects.responsive.type.product.ProductShopResponsiveMessage;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.loader.TicketLoader;
import com.starfishst.ethot.util.Messages;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * A product ticket is something that the freelancer is selling. It will have a message where
 * clients can react with an emoji to create a {@link CheckOut}
 */
public class Product extends QuestionsTicket {

  /** The message for the customer to react */
  @Nullable private ProductShopResponsiveMessage message;

  /**
   * The constructor for when the ticket is just being created
   *
   * @param id the id of the ticket
   * @param customer the customer that created the ticket
   * @param channel the channel where the ticket was created
   */
  public Product(long id, @Nullable User customer, @Nullable TextChannel channel) {
    this(id, customer, TicketStatus.CREATING, channel, new HashMap<>(), null);
  }

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param user the user that created the ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param details the details given by the customer
   * @param message the message that the product is listening to
   */
  public Product(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> details,
      @Nullable ProductShopResponsiveMessage message) {
    super(id, user, status, channel, details);
    this.message = message;
  }

  /**
   * Gets the freelancer that created the product
   *
   * @return the freelancer that created the product
   */
  @NotNull
  public Freelancer getFreelancer() {
    TicketLoader loader = TicketManager.getInstance().getLoader();
    return Validate.notNull(
        loader.getFreelancer(Validate.notNull(user, "User cannot be null").getIdLong()),
        "Freelancer cannot be null!");
  }

  @Override
  public @NotNull Document getDocument() {
    Document document = super.getDocument();
    if (message != null) document.append("message", message);
    return document;
  }

  @Override
  public @NotNull TicketType getType() {
    return TicketType.PRODUCT;
  }

  /**
   * Get the announce message of the product
   *
   * @return the announce message if it exists
   */
  @Nullable
  public ProductShopResponsiveMessage getMessage() {
    return message;
  }

  @Override
  public void onDone() {
    try {
      Messages.announce(this)
          .send(getType().getChannel(), msg -> message = new ProductShopResponsiveMessage(msg));
    } catch (DiscordManipulationException e) {
      Messages.error("This ticket could not be announced");
      Errors.addError(e.getMessage());
    }
    super.onDone();
    setStatus(TicketStatus.SELLING);
  }
}
