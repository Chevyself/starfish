package com.starfishst.ethot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Required;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.arguments.JoinedStrings;
import com.starfishst.core.utils.Lots;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.util.Discord;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Set commands are used to "set" values inside configuration properties.
 *
 * <p>In this project the config files are 'config.json', 'discord.json', 'lang.properties'
 *
 * <p>'config.json' stores information used in the bot startup: commands properties, discord
 * authentication, ticket customization, embeds decor
 *
 * @author Chevy
 * @version 1.0.0
 */
public class SetCommand {

  /**
   * This bot requires a 'Guild' or 'Server' to work.
   *
   * <p>Set the desired with this command
   *
   * @param guild the guild to set
   * @return a successful result. Checks are not handled here
   */
  @Command(
      aliases = "set",
      description = "Sets the guild to use",
      permission = Permission.ADMINISTRATOR)
  @Parent
  public Result set(Guild guild) {
    Main.getDiscordConfiguration().setGuild(guild);
    return new Result("Guild has been set to " + guild.getName());
  }

  /**
   * Sets a language key.
   *
   * <p>Properties file have a 'key' and a 'value'. The 'value' is what we use as a message. The key
   * is just the identification of it
   *
   * @param key the key to set the value
   * @param strings the value to set
   * @return a successful result.
   */
  @Command(
      aliases = {"lang", "language"},
      description = "Sets a message inside 'lang.properties'",
      permission = Permission.ADMINISTRATOR)
  public Result lang(
      @Required(name = "key", description = "The key to change in the file") String key,
      @Required(name = "message", description = "The message to set in the key")
          JoinedStrings strings) {
    Lang.set(key, strings.getString());
    return new Result(
        "The new message has been set", msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
  }

  /**
   * Sets the role for a certain key
   *
   * @param message the message to get the roles from
   * @param key the key to set the roles to
   * @return a successful result if the message had mentioned roles
   */
  @Command(
      aliases = "roles",
      description = "Sets the role of a certain key",
      permission = Permission.ADMINISTRATOR)
  public Result roles(
      Message message,
      @Required(name = "key", description = "The key to set the roles to") String key) {
    List<Role> roles = message.getMentionedRoles();
    if (roles.isEmpty()) {
      return new Result(ResultType.USAGE, Lang.get("MENTION_ROLE"));
    } else {
      Main.getDiscordConfiguration().setRolesByKey(key, roles);

      HashMap<String, String> placeholders = new HashMap<>();
      placeholders.put("roles", Lots.pretty(Discord.getAsMention(roles)));
      placeholders.put("key", key);

      return new Result(
          Lang.get("ROLES_UPDATED", placeholders),
          msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
  }

  /**
   * Sets the category for a certain key
   *
   * @param message the message to get the category from
   * @param key the key to set the category to
   * @return a successful result if the message was sent in a channel inside a category
   */
  @Command(
      aliases = "category",
      description = "Sets the category of a certain key",
      permission = Permission.ADMINISTRATOR)
  public Result category(
      Message message,
      @Required(name = "key", description = "The key to set the category to") String key) {
    TextChannel channel = message.getTextChannel();
    Category parent = channel.getParent();
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("channel", channel.getAsMention());
    if (parent == null) {
      return new Result(ResultType.USAGE, Lang.get("NOT_IN_CATEGORY", placeholders));
    } else {
      Main.getDiscordConfiguration().setCategoryByKey(key, parent);

      placeholders.put("category", parent.getName());
      placeholders.put("key", key);
      return new Result(
          Lang.get("CATEGORY_UPDATED", placeholders),
          msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
    }
  }

  /**
   * Sets the channel for a certain key
   *
   * @param message the message to get the channel from
   * @param key the key to set the channel to
   * @return a successful result
   */
  @Command(
      aliases = "channel",
      description = "Sets the channel of a certain key",
      permission = Permission.ADMINISTRATOR)
  public Result channel(
      Message message,
      @Required(name = "key", description = "The key to set the channel to") String key) {
    TextChannel channel = message.getTextChannel();
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("channel", channel.getAsMention());
    placeholders.put("key", key);
    Main.getDiscordConfiguration().setChannelByKey(key, channel);
    return new Result(
            Lang.get("CHANNEL_UPDATED", placeholders),
            msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
  }

}
