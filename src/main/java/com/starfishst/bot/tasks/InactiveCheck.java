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
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.core.utils.time.TimeUtils;
import com.starfishst.core.utils.time.Unit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This is a task that is ran to check that a ticket is not being inactive */
public class InactiveCheck extends TimerTask {

  /** Construct a new instance of the task */
  public InactiveCheck() {
    Timer timer = new Timer();
    timer.schedule(this, 2000, new Time(10, Unit.MINUTES).millis());
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

  /**
   * Checks if a channel is ignored from the check
   *
   * @param channel to check
   * @return true if it is ignored
   */
  private boolean isIgnored(@Nullable TextChannel channel) {
    if (channel == null) return true;
    Catchable cached =
        Cache.getCache().stream()
            .filter(
                catchable ->
                    catchable instanceof IgnoredChannel
                        && ((IgnoredChannel) catchable).getChannelId() == channel.getIdLong())
            .findFirst()
            .orElse(null);
    return cached != null;
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
                if (isIgnored(channel)) return;
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
                                    } else if (ticket != null) {
                                      new IgnoredChannel(
                                          config.getInactiveTime(), channel.getIdLong());
                                    } else {
                                      new IgnoredChannel(
                                          new Time(2, Unit.YEARS), channel.getIdLong());
                                    }
                                  } else {
                                    Ticket ticket =
                                        TicketManager.getInstance()
                                            .getLoader()
                                            .getTicketByChannel(channel.getIdLong());
                                    if (ticket != null) {
                                      new IgnoredChannel(
                                          config
                                              .getInactiveTime()
                                              .sustract(
                                                  TimeUtils.getTimeFromToday(
                                                      message.getTimeCreated())),
                                          channel.getIdLong());
                                    } else {
                                      new IgnoredChannel(
                                          new Time(2, Unit.YEARS), channel.getIdLong());
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

  /** An ignored channel */
  static class IgnoredChannel extends Catchable {

    /** The id of the channel that is ignored */
    private final long channelId;

    /**
     * Create an instance
     *
     * @param toRemove the time to ignore a channel
     * @param channelId the id of the channel to ignore
     */
    public IgnoredChannel(@NotNull Time toRemove, long channelId) {
      super(toRemove);
      this.channelId = channelId;
    }

    /**
     * Get the id of the ignored channel
     *
     * @return the id of the ignored channel
     */
    public long getChannelId() {
      return channelId;
    }

    @Override
    public void onSecondsPassed() {
      // Ignored
    }

    @Override
    public void onRemove() {
      // Ignored
    }
  }
}
