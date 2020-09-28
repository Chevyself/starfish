package com.starfishst.bot.handlers.freelancer;

import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.commands.result.ResultType;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import net.dv8tion.jda.api.entities.User;

/** Handles freelancers */
public class FreelancerHandler implements StarfishEventHandler {

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
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "freelancers";
  }
}
