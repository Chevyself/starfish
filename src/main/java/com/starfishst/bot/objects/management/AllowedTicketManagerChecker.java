package com.starfishst.bot.objects.management;

import com.starfishst.bot.config.DiscordConfiguration;
import com.starfishst.bot.util.Discord;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

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
    DiscordConfiguration config = DiscordConfiguration.getInstance();
    List<Role> roles = config.getRolesByKeys(config.getRolesKeys("ticketManager"));
    roles.addAll(config.getRolesByKeys(config.getRolesKeys("ticketCloser")));
    return Discord.hasRole(member, roles);
  }
}
