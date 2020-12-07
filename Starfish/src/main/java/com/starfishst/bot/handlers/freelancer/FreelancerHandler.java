package com.starfishst.bot.handlers.freelancer;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.Messages;
import com.starfishst.jda.result.ResultType;
import java.util.ArrayList;
import java.util.List;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

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
    if (!event.getRole().equalsIgnoreCase("freelancer")) {
      return;
    }
    if (!event.getUser().isFreelancer()) {
      event.setCancelled(true);
      User user = event.getUser().getDiscord();
      if (user != null) {
        user.openPrivateChannel()
            .queue(
                privateChannel ->
                    Messages.build(
                            event
                                .getUser()
                                .getLocaleFile()
                                .get("user.not-freelancer", ticket.getPlaceholders()),
                            ResultType.PERMISSION,
                            event.getUser())
                        .send(privateChannel));
      }
    } else {
      if (ticket.hasFreelancers()) {
        Member member = event.getUser().getMember();
        if (member != null) {
          member
              .getUser()
              .openPrivateChannel()
              .queue(
                  channel ->
                      Messages.build(
                              event
                                  .getUser()
                                  .getLocaleFile()
                                  .get("freelancer.ticket-claimed", ticket.getPlaceholders()),
                              ResultType.PERMISSION,
                              event.getUser())
                          .send(channel));
        }
        event.setCancelled(true);
      } else {
        List<Role> allowedRoles = new ArrayList<>();
        ticket
            .getDetails()
            .getMap()
            .forEach(
                (key, value) -> {
                  if (value instanceof List) {
                    Class<?> clazz = Lots.getClazz((List<?>) value);
                    if (clazz != null && Long.class.isAssignableFrom(clazz)) {
                      allowedRoles.addAll(Discord.getRoles(ticket.getDetails().getLisValue(key)));
                    }
                  }
                });
        Member member = event.getUser().getMember();
        if (member != null) {
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
                    privateChannel ->
                        Messages.build(
                                event
                                    .getUser()
                                    .getLocaleFile()
                                    .get("freelancer.not-roles", ticket.getPlaceholders()),
                                ResultType.PERMISSION,
                                event.getUser())
                            .send(privateChannel));
            event.setCancelled(true);
          }
        }
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
