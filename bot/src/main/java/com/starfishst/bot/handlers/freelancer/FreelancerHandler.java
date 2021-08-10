package com.starfishst.bot.handlers.freelancer;

import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.tickets.Ticket;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.ValuesMap;
import java.util.ArrayList;
import java.util.List;
import me.googas.commands.jda.result.ResultType;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

/** Handles freelancers */
public class FreelancerHandler implements StarfishHandler {

  /**
   * In the event of a freelancer being added to a ticket check if it can be added
   *
   * @param event the event of an user joining a ticket
   */
  @Listener(priority = ListenPriority.LOWEST)
  public void onTicketAddUserEvent(TicketAddUserEvent event) {
    Ticket ticket = event.getTicket();
    if (!event.getRole().equalsIgnoreCase("freelancer")) return;
    BotUser botUser = event.getUser();
    if (!botUser.isFreelancer()) {
      event.setCancelled(true);
      botUser
          .getDiscord()
          .ifPresent(
              user -> {
                user.openPrivateChannel()
                    .queue(
                        privateChannel -> {
                          EmbedBuilder embedBuilder =
                              Messages.build(
                                  botUser
                                      .getLocaleFile()
                                      .get("user.not-freelancer", ticket.getPlaceholders()),
                                  ResultType.PERMISSION,
                                  botUser);
                          privateChannel.sendMessageEmbeds(embedBuilder.build()).queue();
                        });
              });
    } else {
      if (ticket.hasFreelancers()) {
        botUser
            .getMember()
            .ifPresent(
                member -> {
                  member
                      .getUser()
                      .openPrivateChannel()
                      .queue(
                          channel -> {
                            EmbedBuilder embedBuilder =
                                Messages.build(
                                    botUser
                                        .getLocaleFile()
                                        .get("freelancer.ticket-claimed", ticket.getPlaceholders()),
                                    ResultType.PERMISSION,
                                    botUser);
                            channel.sendMessageEmbeds(embedBuilder.build()).queue();
                          });
                });
        event.setCancelled(true);
      } else {
        List<Role> allowedRoles = new ArrayList<>();
        ticket
            .getDetails()
            .getMap()
            .forEach(
                (key, value) -> {
                  if (value instanceof List) {
                    Class<?> clazz = ValuesMap.getClazz((List<?>) value);
                    if (clazz != null && Long.class.isAssignableFrom(clazz)) {
                      allowedRoles.addAll(Discord.getRoles(ticket.getDetails().getList(key)));
                    }
                  }
                });
        botUser
            .getMember()
            .ifPresent(
                member -> {
                  // Check if the freelancer has an allowed role
                  boolean allowed = false;
                  for (Role role : member.getRoles()) {
                    if (allowedRoles.contains(role)) {
                      allowed = true;
                      break;
                    }
                  }
                  if (!allowed) {
                    member
                        .getUser()
                        .openPrivateChannel()
                        .queue(
                            privateChannel -> {
                              EmbedBuilder embedBuilder =
                                  Messages.build(
                                      botUser
                                          .getLocaleFile()
                                          .get("freelancer.not-roles", ticket.getPlaceholders()),
                                      ResultType.PERMISSION,
                                      botUser);
                              privateChannel.sendMessageEmbeds(embedBuilder.build());
                            });
                    event.setCancelled(true);
                  }
                });
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "freelancers";
  }
}
