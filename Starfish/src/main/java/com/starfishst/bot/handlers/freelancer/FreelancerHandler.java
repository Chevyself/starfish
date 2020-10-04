package com.starfishst.bot.handlers.freelancer;

import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.commands.result.ResultType;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

/** Handles freelancers */
public class FreelancerHandler implements StarfishEventHandler {

  /**
   * In the event of a freelancer being added to a ticket check if it can be added
   *
   * @param event the event of an user joining a ticket
   */
  @Listener(priority = ListenPriority.LOWEST)
  public void onTicketAddUserEvent(TicketAddUserEvent event) {
    if (event.getRole().equalsIgnoreCase("freelancer") && !event.getUser().isFreelancer()) {
      event.setCancelled(true);
      User user = event.getUser().getDiscord();
      if (user != null) {
        user.openPrivateChannel()
            .queue(
                privateChannel ->
                    Messages.build(
                            event.getUser().getLocaleFile().get("user.not-freelancer"),
                            ResultType.PERMISSION,
                            event.getUser())
                        .send(privateChannel));
      }
    } else {
      List<Role> allowedRoles = new ArrayList<>();
      event
          .getTicket()
          .getDetails()
          .getMap()
          .forEach(
              (key, value) -> {
                if (key.startsWith("role")) {
                  allowedRoles.addAll(event.getTicket().getDetails().getLisValue(key));
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
                              event.getUser().getLocaleFile().get("freelancer.not-roles"),
                              ResultType.PERMISSION,
                              event.getUser())
                          .send(privateChannel));
          event.setCancelled(true);
        }
      } else {
        // Why would it be null?
        event.setCancelled(true);
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
