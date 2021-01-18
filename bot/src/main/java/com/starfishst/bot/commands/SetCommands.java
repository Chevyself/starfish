package com.starfishst.bot.commands;

import com.starfishst.api.Starfish;
import com.starfishst.api.utility.Discord;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import java.util.ArrayList;
import java.util.List;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands for setting configuration */
public class SetCommands {

  /**
   * Set the guild that the server is going to work
   *
   * @param guild the guild to set it
   * @return the result of the command
   */
  @Parent
  @Command(
      aliases = "set",
      description = "Set the settings for the configuration",
      node = "starfish.admin")
  public Result set(Guild guild) {
    Starfish.getDiscordConfiguration().setGuild(guild);
    return new Result("Guild has been set " + guild.getName());
  }

  /**
   * Set the roles in a key
   *
   * @param message the message to get the mentioned roles
   * @param key the key to set the roles on
   * @return the result of the command
   */
  @Command(aliases = "roles", description = "Set the roles in a key", node = "starfish.admin")
  public Result roles(
      Message message,
      @Required(name = "key", description = "The key to set the roles on") String key) {
    List<Long> ids = new ArrayList<>();
    for (Role role : message.getMentionedRoles()) {
      ids.add(role.getIdLong());
    }
    Starfish.getDiscordConfiguration().getRoles().put(key, ids);
    return new Result(
        "Roles in "
            + key
            + " has been set to "
            + Lots.pretty(Discord.getAsMention(message.getMentionedRoles())));
  }

  /**
   * Set the category in a key
   *
   * @param category the category to set in the key
   * @param key the key to set the category
   * @return the result of the command
   */
  @Command(aliases = "category", description = "Set a category in a key", node = "starfish.admin")
  public Result category(
      Category category,
      @Required(name = "key", description = "The key to set the category on") String key) {
    Starfish.getDiscordConfiguration().getCategories().put(key, category.getIdLong());
    return new Result("Category in " + key + " has been set to " + category.getName());
  }

  /**
   * Set the channel in a key
   *
   * @param channel the channel to set in the key
   * @param key the key to set the channel
   * @return the result of the command
   */
  @Command(aliases = "channel", description = "Set a channel in a key", node = "starfish.admin")
  public Result channel(
      TextChannel channel,
      @Required(name = "key", description = "The key to set the channel on") String key) {
    Starfish.getDiscordConfiguration().getChannels().put(key, channel.getIdLong());
    return new Result("Channel in " + key + " has been set to " + channel.getName());
  }
}
