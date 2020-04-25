package com.starfishst.ethot.tickets.type;

import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.Validate;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.questions.Answer;
import com.starfishst.ethot.config.objects.responsive.type.product.ProductShopResponsiveMessage;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.loader.TicketLoader;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A product ticket is something that the freelancer is selling. It will have a message where
 * clients can react with an emoji to create a {@link CheckOut}
 */
public class Product extends QuestionsTicket {

  /** The message for the customer to react */
  @Nullable private ProductShopResponsiveMessage message;

  public Product(long id, @Nullable User customer, @Nullable TextChannel channel) {
    this(id, customer, TicketStatus.CREATING, channel, new HashMap<>(), null);
  }

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

  @Override
  public void onDone() {
    try {
      Messages.announce(this)
          .send(
              getType().getChannel(),
              msg -> {
                message = new ProductShopResponsiveMessage(msg.getIdLong());
                msg.addReaction(Unicode.WHITE_CHECK_MARK).queue();
              });
    } catch (DiscordManipulationException e) {
      Messages.error("This ticket could not be announced");
      Errors.addError(e.getMessage());
    }
    super.onDone();
    setStatus(TicketStatus.SELLING);
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

  /**
   * Gets the freelancer that created the product
   *
   * @return the freelancer that created the product
   */
  @NotNull
  public Freelancer getFreelancer() {
    TicketLoader loader = Main.getManager().getLoader();
    return Validate.notNull(
        loader.getFreelancer(Validate.notNull(user, "User cannot be null").getIdLong()),
        "Freelancer cannot be null!");
  }
}
