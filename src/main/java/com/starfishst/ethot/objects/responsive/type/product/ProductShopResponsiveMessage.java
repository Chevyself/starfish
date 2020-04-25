package com.starfishst.ethot.objects.responsive.type.product;

import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.Product;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import com.starfishst.simple.Lots;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
              // TODO Send the message saying that the checkout has been created
            } catch (DiscordManipulationException | TicketCreationException e) {
              Messages.error(e.getMessage())
                  .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            }
          }
        });
  }
}
