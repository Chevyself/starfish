package com.starfishst.ethot.config.objects.management;

import com.starfishst.core.utils.Atomic;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.DiscordConfiguration;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

public class AllowedTicketManagerChecker implements AllowedChecker {

  @Override
  public boolean isAllowed(@NotNull Member member) {
    Atomic<Boolean> atomic = new Atomic<>(false);
    DiscordConfiguration config = Main.getDiscordConfiguration();
    List<Role> roles = config.getRolesByKeys(config.getRoleKeys("ticketManager"));
    roles.addAll(config.getRolesByKeys(config.getRoleKeys("ticketCloser")));
    roles.forEach(
        role -> {
          if (member.getRoles().contains(role)) {
            atomic.set(true);
          }
        });
    return atomic.get();
  }
}
