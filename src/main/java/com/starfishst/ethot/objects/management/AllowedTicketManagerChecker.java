package com.starfishst.ethot.objects.management;

import com.starfishst.core.utils.Atomic;
import com.starfishst.ethot.config.DiscordConfiguration;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Checks if a user is allowed to manage tickets */
public class AllowedTicketManagerChecker implements AllowedChecker {

  /** The instance for static usage */
  @NotNull
  private static final AllowedTicketManagerChecker instance = new AllowedTicketManagerChecker();

  /**
   * Get the instance of the checker
   *
   * @return the instance
   */
  @NotNull
  public static AllowedTicketManagerChecker getInstance() {
    return instance;
  }

  @Override
  public boolean isAllowed(@NotNull Member member) {
    Atomic<Boolean> atomic = new Atomic<>(false);
    DiscordConfiguration config = DiscordConfiguration.getInstance();
    List<Role> roles = config.getRolesByKeys(config.getRolesKeys("ticketManager"));
    roles.addAll(config.getRolesByKeys(config.getRolesKeys("ticketCloser")));
    roles.forEach(
        role -> {
          if (member.getRoles().contains(role)) {
            atomic.set(true);
          }
        });
    return atomic.get();
  }
}
