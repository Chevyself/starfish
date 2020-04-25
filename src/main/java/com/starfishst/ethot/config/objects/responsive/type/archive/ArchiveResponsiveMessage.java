package com.starfishst.ethot.config.objects.responsive.type.archive;

import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.config.objects.management.AllowedTicketCloserChecker;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import com.starfishst.ethot.util.Unicode;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public class ArchiveResponsiveMessage extends ResponsiveMessage {

  public ArchiveResponsiveMessage(long id, @NotNull AllowedTicketCloserChecker checker) {
    super(
        ResponsiveMessageType.ARCHIVE,
        id,
        true,
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.WHITE_CHECK_MARK;
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            if (checker.isAllowed(event.getMember())) {
              Ticket ticket =
                  Main.getManager().getLoader().getTicketByChannel(event.getChannel().getIdLong());
              if (ticket != null && ticket.getChannel() != null) {
                if (ticket.getStatus() != TicketStatus.ARCHIVED) {
                  ticket.setStatus(TicketStatus.ARCHIVED);
                  ResponsiveMessage responsiveMessage =
                      Main.getConfiguration().getResponsiveMessage(event.getMessageIdLong());
                  if (responsiveMessage != null) {
                    Main.getConfiguration().removeResponsiveMessage(responsiveMessage);
                  }
                } else {
                  Messages.error(Lang.get("ALREADY_ARCHIVED", Tickets.getPlaceholders(ticket)))
                      .send(
                          ticket.getChannel(),
                          msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
                }
              }
            } else {
              Messages.error(Lang.get("NO_PERMISSION"))
                  .send(event.getChannel(), msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            }
          }
        });
  }
}
