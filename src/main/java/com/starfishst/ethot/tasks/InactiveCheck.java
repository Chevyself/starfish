package com.starfishst.ethot.tasks;

import com.starfishst.core.utils.Lots;
import com.starfishst.ethot.config.Configuration;
import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.objects.responsive.type.inactive.InactiveCheckResponsiveMessage;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
                        && ((InactiveCheckResponsiveMessage) responsiveMessage).getTicket()
                            == ticketId)
            .findFirst()
            .orElse(null);
  }

  @Override
  public void run() {
    try {
      DiscordConfiguration discConfig = DiscordConfiguration.getInstance();
      discConfig
          .getGuild()
          .getTextChannels()
          .forEach(
              channel -> {
                Ticket ticket =
                    TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
                if (ticket != null && ticket.getStatus() == TicketStatus.OPEN) {
                  InactiveCheckResponsiveMessage responsiveMessage =
                      getResponsiveMessage(ticket.getId());
                  Configuration config = Configuration.getInstance();
                  HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
                  if (responsiveMessage == null) {
                    // If the message is null check if you are able to create one
                    channel
                        .getHistory()
                        .retrievePast(10)
                        .queue(
                            messages ->
                                messages.forEach(
                                    message -> {
                                      if (message.getIdLong() == channel.getLatestMessageIdLong()
                                          && message
                                              .getTimeCreated()
                                              .isBefore(config.getInactiveTime().previousDate())) {
                                        Messages.create(
                                                "INACTIVITY_CHECK_TITLE",
                                                "INACTIVITY_CHECK_DESCRIPTION",
                                                placeholders,
                                                placeholders)
                                            .send(
                                                channel,
                                                msg -> {
                                                  new InactiveCheckResponsiveMessage(
                                                      msg,
                                                      System.currentTimeMillis(),
                                                      ticket.getId(),
                                                      new ArrayList<>(),
                                                      false);
                                                });
                                      }
                                    }));
                  } else {
                    if (!responsiveMessage.isFinished()
                        && responsiveMessage
                            .getCreatedAtDate()
                            .isBefore(config.getTimeToFinishInactiveTest().previousDate())) {
                      responsiveMessage.finish();

                      List<Member> notReacted = new ArrayList<>();
                      channel
                          .getPermissionOverrides()
                          .forEach(
                              permissionOverride -> {
                                if (permissionOverride.getMember() != null) {
                                  notReacted.add(permissionOverride.getMember());
                                }
                              });

                      try {
                        placeholders.put(
                            "notReacted", Lots.pretty(Discord.getAsMention(notReacted)));
                        discConfig
                            .getGuild()
                            .getMembersWithRoles(
                                discConfig.getRolesByKeys(
                                    discConfig.getRolesKeys("sendInactiveCheck")))
                            .forEach(
                                member -> {
                                  System.out.println(member.getUser());
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
                                                  .send(privateChannel));
                                });
                      } catch (DiscordManipulationException e) {
                        Messages.error(e.getMessage()).send(channel);
                      }
                    }
                  }
                }
              });
    } catch (DiscordManipulationException | IllegalStateException ignored) {

    }
  }
}
