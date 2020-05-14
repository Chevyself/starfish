package com.starfishst.bot.tasks;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.DiscordConfiguration;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.objects.responsive.type.inactive.InactiveCheckResponsiveMessage;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Discord;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.core.utils.Lots;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Nullable;

/** This is a task that is ran to check that a ticket is not being inactive */
public class InactiveCheck extends TimerTask {

  /** Construct a new instance of the task */
  public InactiveCheck() {
    Timer timer = new Timer();
    timer.schedule(this, 2000, 1000);
  }

  /**
   * Get the responsive message checking for the active members using the id of a ticket
   *
   * @param ticketId the id of the ticket
   * @return the responsive message if found else null
   */
  @Nullable
  public static InactiveCheckResponsiveMessage getResponsiveMessage(long ticketId) {
    return (InactiveCheckResponsiveMessage)
        Configuration.getInstance().getResponsiveMessages().stream()
            .filter(
                responsiveMessage ->
                    responsiveMessage instanceof InactiveCheckResponsiveMessage
                        && ((InactiveCheckResponsiveMessage) responsiveMessage).getTicketId()
                            == ticketId)
            .findFirst()
            .orElse(null);
  }

  @Override
  public void run() {
    try {
      Configuration config = Configuration.getInstance();
      DiscordConfiguration discConfig = DiscordConfiguration.getInstance();
      Guild guild = discConfig.getGuild();
      guild
          .getTextChannels()
          .forEach(
              channel -> {
                channel
                    .getHistory()
                    .retrievePast(1)
                    .queue(
                        messages ->
                            messages.forEach(
                                message -> {
                                  if (message
                                      .getTimeCreated()
                                      .isBefore(config.getInactiveTime().previousDateOffset())) {
                                    Ticket ticket =
                                        TicketManager.getInstance()
                                            .getLoader()
                                            .getTicketByChannel(channel.getIdLong());
                                    if (ticket != null && ticket.getStatus() == TicketStatus.OPEN) {
                                      HashMap<String, String> placeholders =
                                          Tickets.getPlaceholders(ticket);
                                      InactiveCheckResponsiveMessage responsiveMessage =
                                          getResponsiveMessage(ticket.getId());
                                      if (responsiveMessage == null) {
                                        Messages.create(
                                                "INACTIVITY_CHECK_TITLE",
                                                "INACTIVITY_CHECK_DESCRIPTION",
                                                placeholders,
                                                placeholders)
                                            .send(
                                                channel,
                                                msg ->
                                                    new InactiveCheckResponsiveMessage(
                                                        msg,
                                                        System.currentTimeMillis(),
                                                        ticket.getId(),
                                                        new ArrayList<>(),
                                                        false));
                                      }
                                    }
                                  }
                                }));
                config
                    .getResponsiveMessages()
                    .forEach(
                        message -> {
                          if (message instanceof InactiveCheckResponsiveMessage) {
                            InactiveCheckResponsiveMessage responsiveMessage =
                                (InactiveCheckResponsiveMessage) message;
                            if (!responsiveMessage.isFinished()
                                && responsiveMessage
                                    .getCreatedAtDate()
                                    .isBefore(
                                        config.getTimeToFinishInactiveTest().previousDate())) {
                              responsiveMessage.finish();
                              if (responsiveMessage.getTicket() != null) {
                                HashMap<String, String> placeholders =
                                    Tickets.getPlaceholders(responsiveMessage.getTicket());
                                List<Member> notReacted = new ArrayList<>();
                                channel
                                    .getPermissionOverrides()
                                    .forEach(
                                        permissionOverride -> {
                                          if (permissionOverride.getMember() != null) {
                                            notReacted.add(permissionOverride.getMember());
                                          }
                                        });
                                placeholders.put(
                                    "notReacted", Lots.pretty(Discord.getAsMention(notReacted)));
                                guild
                                    .getMembersWithRoles(
                                        discConfig.getRolesByKeys(
                                            discConfig.getRolesKeys("sendInactiveCheck")))
                                    .forEach(
                                        member ->
                                            member
                                                .getUser()
                                                .openPrivateChannel()
                                                .queue(
                                                    privateChannel ->
                                                        Messages.create(
                                                                "INACTIVITY_CHECK_RESULT_TITLE",
                                                                "INACTIVITY_CHECK_RESULT_DESCRIPTION",
                                                                placeholders,
                                                                placeholders)
                                                            .send(privateChannel)));
                              }
                              responsiveMessage.remove();
                            }
                          }
                        });
              });
    } catch (DiscordManipulationException ignored) {

    }
  }
}
