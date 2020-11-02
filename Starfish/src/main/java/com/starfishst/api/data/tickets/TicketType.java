package com.starfishst.api.data.tickets;

import com.starfishst.bot.Starfish;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** There's many different type of tickets and the way to differentiate them is this enum */
public enum TicketType {
  /** An order is a generic ticket. A freelancer and a customer */
  ORDER("orders", "orders"),
  /** The ticket that people can create to become a freelancer */
  APPLY("applies", "applies"),
  /** The ticket that people can use in case having issues */
  SUPPORT("support", "support"),
  /**
   * This is the parent ticket for the rest of them, it is not really going to be saved in the
   * database
   */
  TICKET("tickets", "tickets"),
  /** This ticket is used as a creator for 'sub-tickets' liker orders, apply and support */
  TICKET_CREATOR("creators", "creators"),
  /**
   * A quote is kinda like an order but the freelancer cannot just join the ticket it needs to send
   * a quote that the customer can accept
   */
  QUOTE("quotes", "quotes"),
  /**
   * This ticket is created by a freelancer offering a product. Later the customers can open a check
   * out to buy it
   */
  PRODUCT("products", "products"),
  /** The ticket that is created when a customer wants to buy a product from a freelancer */
  CHECK_OUT("checkout", "checkout"),
  /** Creates a suggestion that later the user can either send it or cancel it */
  SUGGESTION("suggestions", "suggestions"),
  /** Create a report ticket */
  REPORT("reports", "reports");

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
   * Get the category where the ticket can be created. This returns null if the guild has not been
   * set
   *
   * @return he category where the ticket can be created
   */
  @Nullable
  public Category getCategory() {
    return Starfish.getDiscordConfiguration().requireCategory(this.getCategoryName());
  }

  /**
   * Get the category where the ticket can be created. This returns null if the guild has not been
   * set
   *
   * @return he text channel where the ticket can be announced
   */
  @Nullable
  public TextChannel getChannel() {
    return Starfish.getDiscordConfiguration().requireChannel(this.getChannelName());
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
