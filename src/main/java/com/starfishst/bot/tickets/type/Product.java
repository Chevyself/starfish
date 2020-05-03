package com.starfishst.bot.tickets.type;

import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.objects.questions.ImageAnswer;
import com.starfishst.bot.objects.responsive.type.product.ProductShopResponsiveMessage;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.tickets.loader.TicketLoader;
import com.starfishst.bot.util.Messages;
import com.starfishst.commands.utils.embeds.EmbedQuery;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Validate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
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

  /**
   * The constructor for when the ticket is just being created
   *
   * @param id the id of the ticket
   * @param customer the customer that created the ticket
   * @param channel the channel where the ticket was created
   */
  public Product(long id, @Nullable User customer, @Nullable TextChannel channel) {
    this(id, customer, TicketStatus.CREATING, channel, new HashMap<>(), null, new ArrayList<>());
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
   * @param payments the payments of the ticket
   */
  public Product(
      long id,
      @Nullable User user,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> details,
      @Nullable ProductShopResponsiveMessage message,
      List<String> payments) {
    super(id, user, status, channel, details, payments);
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
      EmbedBuilder embedBuilder = Messages.announceEmbed(this).getEmbedBuilder();
      getAnswers()
          .forEach(
              (simple, answer) -> {
                if (answer instanceof ImageAnswer) {
                  embedBuilder.setImage(((ImageAnswer) answer).getUrl());
                }
              });
      getType()
          .getChannel()
          .sendMessage(new EmbedQuery(embedBuilder).getAsMessageQuery().getMessage())
          .queue(msg -> message = new ProductShopResponsiveMessage(msg));
    } catch (DiscordManipulationException e) {
      Messages.error("This ticket could not be announced");
      Fallback.addError(e.getMessage());
    }
    super.onDone();
    setStatus(TicketStatus.SELLING);
    if (channel != null) {
      this.close(false);
    }
  }
}
