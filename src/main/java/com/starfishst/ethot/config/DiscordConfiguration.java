package com.starfishst.ethot.config;

import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.Validate;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.tickets.TicketType;
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
public class DiscordConfiguration {

  /** The instance for static usage */
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

  /**
   * Create a new discord configuration instance
   *
   * @param guild the guild that's going to be used
   * @param roles the map of roles
   * @param categories the map of categories
   * @param channels the map of channels
   * @param roleKeyMap the map of role keys
   */
  public DiscordConfiguration(
      @Nullable Guild guild,
      @NotNull HashMap<String, List<Role>> roles,
      @NotNull HashMap<String, Category> categories,
      @NotNull HashMap<String, TextChannel> channels,
      @NotNull HashMap<String, List<String>> roleKeyMap) {
    DiscordConfiguration.instance = this;
    this.guild = guild;
    this.roles = roles;
    this.categories = categories;
    this.channels = channels;
    this.roleKeyMap = roleKeyMap;
  }

  /**
   * Get the working guild
   *
   * @return if the guild is not set null
   * @throws DiscordManipulationException when the guild has not been set
   */
  @NotNull
  public Guild getGuild() throws DiscordManipulationException {
    return Validate.notNull(
        guild, Lang.GUILD_NOT_SET, new DiscordManipulationException(Lang.GUILD_NOT_SET));
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
   */
  @Nullable
  public Category getCategory(TicketType type) {
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
   */
  @Nullable
  public Category getCategoryByKey(@NotNull String key) {
    return categories.getOrDefault(key, null);
  }

  /**
   * Sets a category using a type. This will use the category name of the type allowing to have one
   * category for multiple types
   *
   * @param type the type to set the category
   * @param category the category to set
   */
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
   */
  @Nullable
  public TextChannel getChannelByKey(@NotNull String key) {
    return channels.getOrDefault(key, null);
  }

  /**
   * Get the channel using a ticket type. It will use the name of the type allowing to have one
   * channel for multiple ticket types
   *
   * @param type the type of ticket
   * @return the channel if found
   */
  public TextChannel getChannel(TicketType type) {
    return getChannelByKey(type.getChannelName());
  }

  /**
   * Sets a channel using a type. This will use the channel name of the type allowing to have one
   * channel for multiple types
   *
   * @param type the type to set the channel
   * @param channel the channel to set
   */
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
          if (rolesByKey != null && !rolesByKey.isEmpty()) {
            roles.addAll(rolesByKey);
          } else {
            Errors.addError("Roles with key " + key + " are empty or null");
          }
        });
    return roles;
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
  public List<String> getRoleKeys(@NotNull String key) {
    return Validate.notNull(roleKeyMap.get(key), "There's no roles with the key " + key);
  }

  /**
   * Get the static instance
   *
   * @return the static instance
   */
  @NotNull
  public static DiscordConfiguration getInstance() {
    return Validate.notNull(instance, "Discord config has not been initialized");
  }
}
