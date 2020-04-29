package com.starfishst.ethot.objects.management;

import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.util.Discord;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/** Checks if a user is allowed to close/manage tickets */
public class AllowedTicketCloserChecker implements AllowedChecker {

  /** The instance for static usage */
  @NotNull
  private static final AllowedTicketCloserChecker instance = new AllowedTicketCloserChecker();

  /**
   * Get the instance of the checker
   *
   * @return the instance of the checker
   */
  @NotNull
  public static AllowedTicketCloserChecker getInstance() {
    return instance;
  }

  @Override
  public boolean isAllowed(@NotNull Member member) {
    DiscordConfiguration config = DiscordConfiguration.getInstance();
    return Discord.hasRole(member, config.getRolesByKeys(config.getRolesKeys("ticketCloser")));
  }
}
