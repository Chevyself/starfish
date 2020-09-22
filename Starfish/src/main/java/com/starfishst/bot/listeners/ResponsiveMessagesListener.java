package com.starfishst.bot.listeners;

import com.starfishst.bot.oldconfig.Configuration;
import com.starfishst.bot.objects.freelancers.Offer;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.oldtickets.TicketManager;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.bot.oldtickets.loader.TicketLoader;
import com.starfishst.bot.oldtickets.type.Order;
import com.starfishst.bot.oldtickets.type.Product;
import com.starfishst.bot.oldtickets.type.Quote;
import com.starfishst.bot.util.Unicode;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/** This listener checks for the reactions to discord messages */
public class ResponsiveMessagesListener {

  /** The configuration is needed to multiple tasks */
  @NotNull private final Configuration configuration;

  /**
   * Create an instance
   *
   * @param configuration the configuration to help the listener complete tasks
   */
  public ResponsiveMessagesListener(@NotNull Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * This checks for the reaction add event
   *
   * @param event the reaction add event
   */
  @SubscribeEvent
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
    TicketLoader ticketLoader = TicketManager.getInstance().getLoader();
    if (!event.getUser().isBot()) {
      ResponsiveMessage responsiveMessage =
          configuration.getResponsiveMessage(event.getMessageIdLong());
      if (responsiveMessage == null) {
        Order order = ticketLoader.getOrderByMessage(event.getMessageIdLong());
        if (order != null && order.getMessage() != null) {
          responsiveMessage = order.getMessage();
        }
      }
      if (responsiveMessage == null) {
        Quote quote = ticketLoader.getQuoteByOfferMessage(event.getMessageIdLong());
        if (quote != null) {
          Offer offer = quote.getOfferByMessageId(event.getMessageIdLong());
          if (offer != null) {
            responsiveMessage = offer.getMessage();
          }
        }
      }
      if (responsiveMessage == null) {
        Product product = ticketLoader.getProductByMessage(event.getMessageIdLong());
        if (product != null
            && product.getStatus() == TicketStatus.SELLING
            && product.getMessage() != null) {
          responsiveMessage = product.getMessage();
        }
      }
      if (responsiveMessage != null) {
        ReactionResponse response = responsiveMessage.getResponse(Unicode.fromReaction(event));
        if (response != null) {
          response.onReaction(event);
        }
        event.getReaction().removeReaction(event.getUser()).queue(aVoid -> {}, exception -> {});
      }
    }
  }
}
