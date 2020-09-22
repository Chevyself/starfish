package com.starfishst.bot.objects.responsive.type.product;

import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.TicketCreationException;
import com.starfishst.bot.objects.responsive.ReactionResponse;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.objects.responsive.ResponsiveMessageType;
import com.starfishst.bot.oldtickets.TicketManager;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.bot.oldtickets.type.Product;
import com.starfishst.bot.oldtickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.bot.util.Unicode;
import com.starfishst.simple.Lots;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The message that the product sent to create checkouts */
public class ProductShopResponsiveMessage extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   */
  public ProductShopResponsiveMessage(@NotNull Message message) {
    super(ResponsiveMessageType.PRODUCT, message, getReactions(), false);
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   */
  public ProductShopResponsiveMessage(long id) {
    super(ResponsiveMessageType.PRODUCT, id, getReactions(), false);
  }

  /**
   * Get the reactions used in this responsive message
   *
   * @return the reactions used
   */
  @NotNull
  public static List<ReactionResponse> getReactions() {
    return Lots.list(
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.getEmoji("UNICODE_PRODUCT_SHOP");
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            try {
              TicketManager manager = TicketManager.getInstance();
              Product parent = manager.getLoader().getProductByMessage(event.getMessageIdLong());
              Ticket ticket = manager.createTicket(TicketType.CHECK_OUT, event.getMember(), parent);
              HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
              Messages.create(
                      "CHECK_OUT_CREATED_TITLE",
                      "CHECK_OUT_CREATED_DESCRIPTION",
                      placeholders,
                      placeholders)
                  .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            } catch (DiscordManipulationException | TicketCreationException e) {
              Messages.error(e.getMessage())
                  .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            }
          }
        });
  }
}
