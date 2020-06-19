package com.starfishst.bot.commands;

import com.starfishst.bot.addons.AddonLoader;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

/** Commands for addons */
public class AddonsCommands {

  /** The addon loader to work with */
  @NotNull private final AddonLoader loader;

  /**
   * Create an instance
   *
   * @param loader the addon loader to work with
   */
  public AddonsCommands(@NotNull AddonLoader loader) {
    this.loader = loader;
  }

  @Command(
      aliases = "reload",
      description = "Reload the loaded addons",
      permission = Permission.ADMINISTRATOR)
  public Result reload() {
    loader.reload();
    return new Result(Lang.get("ADDONS_RELOADED"));
  }
}
