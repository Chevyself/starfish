package com.starfishst.bot.config;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.bot.util.Discord;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Validate;
import com.starfishst.simple.config.JsonConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This class is 'discord.json' as a java object */
public class DiscordConfiguration extends JsonConfiguration {

  /** The instance of this class for static usage */
  @Nullable private static DiscordConfiguration instance;
  /** A map of roles and their keys */
  @NotNull private final HashMap<String, List<Role>> roles;
  /** A map of categories and their keys */
  @NotNull private final HashMap<String, Category> categories;
  /** A map of channels and their keys */
  @NotNull private final HashMap<String, TextChannel> channels;
  /** A map of role list and their keys */
  @NotNull private final HashMap<String, List<String>> roleKeyMap;
  /** The guild that's being used in the bot */
  @Nullable private Guild guild;

  /** Constructor for json */
  public DiscordConfiguration() {
    instance = this;
    this.roles = new HashMap<>();
    this.categories = new HashMap<>();
    this.channels = new HashMap<>();
    this.roleKeyMap = new HashMap<>();
  }

  /**
   * Get a list of roles using many keys
   *
   * @param keys the keys to get the roles from
   * @return a list of roles (it could be empty)
   */
  @NotNull
  public List<Role> getRolesByKeys(@NotNull List<String> keys) {
    List<Role> roles = new ArrayList<>();
    keys.forEach(
        key -> {
          List<Role> rolesByKey = getRolesByKey(key);
          if (rolesByKey != null) {
            roles.addAll(rolesByKey);
          } else {
            Fallback.addError("Roles with key " + key + " are empty or null");
          }
        });
    return roles;
  }

  /**
   * Checks if the bot has a guild
   *
   * @return true if it has a guild
   */
  public boolean hasGuild() {
    return guild != null;
  }

  /**
   * Sets the new guild
   *
   * @param guild the guild to set
   */
  public void setGuild(@Nullable Guild guild) {
    this.guild = guild;
  }

  /**
   * Get the category using a ticket type
   *
   * @param type the ticket type
   * @return the category if found
   * @throws DiscordManipulationException when the validation of the category goes wrong
   */
  @NotNull
  public Category getCategory(TicketType type) throws DiscordManipulationException {
    return getCategoryByKey(type.getCategoryName());
  }

  /**
   * Get a list of roles using a key
   *
   * @param key the key representation the roles
   * @return a list of roles if found else null
   */
  @Nullable
  public List<Role> getRolesByKey(@NotNull String key) {
    return roles.getOrDefault(key, null);
  }

  /**
   * Get some category using a name
   *
   * @param key the name to get the category from
   * @return the category if found else null
   * @throws DiscordManipulationException when the validation of the category goes wrong
   */
  @NotNull
  public Category getCategoryByKey(@NotNull String key) throws DiscordManipulationException {
    Category category = categories.getOrDefault(key, null);
    if (category == null || category.getChannels().size() > 50) {
      category =
          Discord.validateCategory(
              category,
              key,
              true,
              this.getRolesByKeys(this.getRolesKeys("allowedInCategories")),
              this.getRolesByKeys(this.getRolesKeys("allowedToSeeInCategories")));
      this.categories.put(key, category);
    }
    return category;
  }

  /**
   * Sets a category using a type. This will use the category name of the type allowing to have one
   * category for multiple types
   *
   * @param type the type to set the category
   * @param category the category to set
   */
  @Deprecated
  public void setCategory(@NotNull TicketType type, @NotNull Category category) {
    setCategoryByKey(type.getCategoryName(), category);
  }

  /**
   * Sets a category using a name
   *
   * @param key the name setting the category
   * @param category the category to set
   */
  public void setCategoryByKey(@NotNull String key, @NotNull Category category) {
    categories.put(key, category);
  }

  /**
   * Sets a text channel using a name
   *
   * @param key the name to set
   * @param channel the channel to set
   */
  public void setChannelByKey(@NotNull String key, @NotNull TextChannel channel) {
    channels.put(key, channel);
  }

  /**
   * Get a text channel using a key
   *
   * @param key the key of the channel
   * @return the channel if found else null
   * @throws DiscordManipulationException in case that the channel validation goes wrong
   */
  @NotNull
  public TextChannel getChannelByKey(@NotNull String key) throws DiscordManipulationException {
    TextChannel channel = channels.getOrDefault(key, null);
    if (channel == null) {
      channel =
          Discord.validateChannel(
              null,
              key,
              true,
              this.getRolesByKeys(this.getRolesKeys("allowedInChannels")),
              this.getRolesByKeys(this.getRolesKeys("allowedToSeeInChannels")));
      this.channels.put(key, channel);
    }
    return channel;
  }

  /**
   * Get the channel using a ticket type. It will use the name of the type allowing to have one
   * channel for multiple ticket types
   *
   * @param type the type of ticket
   * @return the channel if found
   * @throws DiscordManipulationException in case that the channel validation goes wrong
   */
  @NotNull
  public TextChannel getChannel(TicketType type) throws DiscordManipulationException {
    return getChannelByKey(type.getChannelName());
  }

  /**
   * Sets a channel using a type. This will use the channel name of the type allowing to have one
   * channel for multiple types
   *
   * @param type the type to set the channel
   * @param channel the channel to set
   */
  @Deprecated
  public void setChannel(@NotNull TicketType type, @NotNull TextChannel channel) {
    setChannelByKey(type.getChannelName(), channel);
  }

  /**
   * Set the roles using a key
   *
   * @param key the key to set
   * @param roles the roles to set
   */
  public void setRolesByKey(@NotNull String key, @NotNull List<Role> roles) {
    this.roles.put(key, roles);
  }

  /**
   * Get the working guild
   *
   * @return if the guild is not set null
   * @throws DiscordManipulationException when the guild has not been set
   */
  @NotNull
  public Guild getGuild() throws DiscordManipulationException {
    return Validate.notNull(guild, new DiscordManipulationException(Lang.GUILD_NOT_SET));
  }

  /**
   * Get the map of roles and its keys
   *
   * @return the map of roles
   */
  @NotNull
  public HashMap<String, List<Role>> getRoles() {
    return roles;
  }

  /**
   * Get the map of categories and its keys
   *
   * @return the map of categories
   */
  @NotNull
  public HashMap<String, Category> getCategories() {
    return categories;
  }

  /**
   * Get the map of channels and its keys
   *
   * @return the map of channels and its keys
   */
  @NotNull
  public HashMap<String, TextChannel> getChannels() {
    return channels;
  }

  /**
   * Get a list of keys using a key of {@link DiscordConfiguration#roleKeyMap}
   *
   * @param key the key of the role map
   * @return the list of roles string
   */
  @NotNull
  public List<String> getRolesKeys(@NotNull String key) {
    return Validate.notNull(roleKeyMap.get(key), "There's no key roles with the key " + key);
  }

  /**
   * Get the static instance
   *
   * @return the static instance
   */
  @NotNull
  public static DiscordConfiguration getInstance() {
    return Validate.notNull(instance, "Discord configuration may not have been initialized");
  }

  /**
   * Get the whole key-roles map
   *
   * @return the map
   */
  @NotNull
  public HashMap<String, List<String>> getRoleKeyMap() {
    return roleKeyMap;
  }
}
