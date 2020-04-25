package com.starfishst.ethot.config.objects.responsive.type.product;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.Product;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public class ProductShopResponsiveMessage extends ResponsiveMessage {

  public ProductShopResponsiveMessage(long id) {
    super(
        ResponsiveMessageType.PRODUCT,
        id,
        false,
        new ReactionResponse() {

          @Override
          public @NotNull String getUnicode() {
            return Unicode.WHITE_CHECK_MARK;
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            try {
              TicketManager manager = Main.getManager();
              Product parent = manager.getLoader().getProductByMessage(event.getMessageIdLong());
              Ticket ticket = manager.createTicket(TicketType.CHECK_OUT, event.getMember(), parent);
            } catch (DiscordManipulationException | TicketCreationException e) {
              Messages.error(e.getMessage())
                  .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            }
          }
        });
  }
}
