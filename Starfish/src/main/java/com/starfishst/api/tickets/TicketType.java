package com.starfishst.api.tickets;

import com.starfishst.bot.config.DiscordConfiguration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/** There's many different type of tickets and the way to differentiate them is this enum */
public enum TicketType {
  /**
   * An order is a generic ticket. A freelancer and a customer {@link
   * com.starfishst.bot.tickets.type.Order}
   */
  ORDER(Lang.get("CATEGORY_NAME_ORDERS"), Lang.get("CHANNEL_NAME_ORDERS")),
  /**
   * The ticket that people can create to become a freelancer {@link
   * com.starfishst.bot.tickets.type.Apply}
   */
  APPLY(Lang.get("CATEGORY_NAME_APPLIES"), "none"),
  /**
   * The ticket that people can use in case having issues {@link
   * com.starfishst.bot.tickets.type.Support}
   */
  SUPPORT(Lang.get("CATEGORY_NAME_SUPPORT"), "none"),
  /**
   * This is the parent ticket for the rest of them, it is not really going to be saved in the
   * database {@link com.starfishst.bot.tickets.type.Ticket}
   */
  TICKET("ticket", "none"),
  /**
   * This ticket is used as a creator for 'sub-tickets' liker orders, apply and support {@link
   * com.starfishst.bot.tickets.type.TicketCreator}
   */
  TICKET_CREATOR(Lang.get("CATEGORY_NAME_TICKET_CREATOR"), "none"),
  /**
   * A quote is kinda like an order but the freelancer cannot just join the ticket it needs to send
   * a quote that the customer can accept {@link com.starfishst.bot.tickets.type.Quote}
   */
  QUOTE(Lang.get("CATEGORY_NAME_QUOTES"), Lang.get("CHANNEL_NAME_QUOTES")),
  /**
   * This ticket is created by a freelancer offering a product. Later the customers can open a check
   * out to buy it {@link com.starfishst.bot.tickets.type.Product}
   */
  PRODUCT(Lang.get("CATEGORY_NAME_PRODUCTS"), Lang.get("CHANNEL_NAME_PRODUCTS")),
  /** The ticket that is created when a customer wants to buy a product from a freelancer */
  CHECK_OUT(Lang.get("CATEGORY_NAME_CHECK_OUT"), "none"),
  /** Creates a suggestion that later the user can either send it or cancel it */
  SUGGESTION(Lang.get("CATEGORY_NAME_SUGGESTIONS"), Lang.get("CHANNEL_NAME_SUGGESTION")),
  /** Create a report ticket */
  REPORT(Lang.get("CATEGORY_NAME_REPORTS"), "none");

  /** The name of the category where this tickets can be stored */
  @NotNull private final String categoryName;
  /** The name of the channel where this tickets can be announced */
  @NotNull private final String channelName;

  /**
   * The type of ticket
   *
   * @param categoryName the name of the category where the ticket can be stored
   * @param channelName the name of the channel where the ticket can be stored
   */
  TicketType(@NotNull String categoryName, @NotNull String channelName) {
    this.categoryName = categoryName;
    this.channelName = channelName;
  }

  /**
   * Gets a ticket type from a document. This method is replaced by the ticket type codec {@link
   * com.starfishst.bot.tickets.loader.mongo.codec.TicketTypeCodec}
   *
   * @param document the document to get the type from
   * @return the ticket type
   */
  @Deprecated
  @NotNull
  public static TicketType fromDocument(@NotNull Document document) {
    try {
      return TicketType.valueOf(document.getString("type"));
    } catch (NullPointerException | IllegalArgumentException e) {
      throw e;
    }
  }

  /**
   * Get the category where the ticket can be created.
   *
   * @return he category where the ticket can be created
   * @throws DiscordManipulationException in case something related to discord goes wrong
   */
  @NotNull
  public Category getCategory() throws DiscordManipulationException {
    return DiscordConfiguration.getInstance().getCategory(this);
  }

  /**
   * Get the channel that this type of ticket can have
   *
   * @return the channel
   * @throws DiscordManipulationException in case of having to create it goes wrong
   */
  @NotNull
  public TextChannel getChannel() throws DiscordManipulationException {
    return DiscordConfiguration.getInstance().getChannel(this);
  }

  /**
   * Get the key of the category of the ticket type
   *
   * @return the key of the category
   */
  @NotNull
  public String getCategoryName() {
    return categoryName;
  }

  /**
   * Get the key of the channel of the ticket type
   *
   * @return the key of the channel
   */
  @NotNull
  public String getChannelName() {
    return channelName;
  }
}
