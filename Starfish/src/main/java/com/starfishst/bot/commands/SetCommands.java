package com.starfishst.bot.commands;

import com.starfishst.api.utility.Discord;
import com.starfishst.bot.Starfish;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.result.Result;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.utils.Lots;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
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
      permission = @Perm(node = "starfish.admin"))
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
  @Command(
      aliases = "roles",
      description = "Set the roles in a key",
      permission = @Perm(node = "starfish.admin"))
  public Result roles(
      Message message,
      @Required(name = "key", description = "The key to set the roles on") String key) {
    Starfish.getDiscordConfiguration().getRoles().put(key, message.getMentionedRoles());
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
  @Command(
      aliases = "category",
      description = "Set a category in a key",
      permission = @Perm(node = "starfish.admin"))
  public Result category(
      Category category,
      @Required(name = "key", description = "The key to set the category on") String key) {
    Starfish.getDiscordConfiguration().getCategories().put(key, category);
    return new Result("Category in " + key + " has been set to " + category.getName());
  }

  /**
   * Set the channel in a key
   *
   * @param channel the channel to set in the key
   * @param key the key to set the channel
   * @return the result of the command
   */
  @Command(
      aliases = "channel",
      description = "Set a channel in a key",
      permission = @Perm(node = "starfish.admin"))
  public Result channel(
      TextChannel channel,
      @Required(name = "key", description = "The key to set the channel on") String key) {
    Starfish.getDiscordConfiguration().getChannels().put(key, channel);
    return new Result("Channel in " + key + " has been set to " + channel.getName());
  }
}
