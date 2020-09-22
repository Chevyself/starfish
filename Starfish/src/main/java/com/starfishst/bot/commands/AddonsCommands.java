package com.starfishst.bot.commands;

import com.starfishst.api.addons.AddonLoader;
import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.core.utils.Lots;
import java.util.ArrayList;
import java.util.List;
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

  @Command(aliases = "addonds", description = "See the loaded addons")
  public Result addons() {
    List<String> names = new ArrayList<>();
    loader
        .getLoaded()
        .forEach(
            addon -> {
              if (addon.getInformation() != null) {
                names.add(addon.getInformation().getName());
              }
            });
    return new Result(Lots.pretty(names));
  }
}
