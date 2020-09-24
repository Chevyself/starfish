package com.starfishst.bot.configuration;

import com.starfishst.api.configuration.DiscordConfiguration;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for {@link DiscordConfiguration} */
public class StarfishDiscordConfiguration implements DiscordConfiguration {

  /** The roles that the bot can use */
  @NotNull private final HashMap<String, List<Role>> roles;
  /** The categories that the bot can use */
  @NotNull private final HashMap<String, Category> categories;
  /** The channels that the bot can use */
  @NotNull private final HashMap<String, TextChannel> channels;
  /** The guild where the bot is working */
  @Nullable private Guild guild;

  /**
   * Create the discord configuration
   *
   * @param guild the guild where the bot will work
   * @param roles the roles that the bot can use
   * @param categories the categories that the bot can use
   * @param channels the channels that the bot can use
   */
  public StarfishDiscordConfiguration(
      @Nullable Guild guild,
      @NotNull HashMap<String, List<Role>> roles,
      @NotNull HashMap<String, Category> categories,
      @NotNull HashMap<String, TextChannel> channels) {
    this.guild = guild;
    this.roles = roles;
    this.categories = categories;
    this.channels = channels;
  }

  /** This constructor is used for gson */
  @Deprecated
  public StarfishDiscordConfiguration() {
    this(null, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  /**
   * Create a discord configuration with a constructor with no parameters
   *
   * @return a discord configuration for fallback
   */
  @NotNull
  public static StarfishDiscordConfiguration fallback() {
    return new StarfishDiscordConfiguration(
        null, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  @Override
  public @Nullable Guild getGuild() {
    return this.guild;
  }

  @Override
  public @NotNull HashMap<String, List<Role>> getRoles() {
    return this.roles;
  }

  @Override
  public @NotNull HashMap<String, Category> getCategories() {
    return this.categories;
  }

  @Override
  public @NotNull HashMap<String, TextChannel> getChannels() {
    return this.channels;
  }

  @Override
  public void setGuild(@Nullable Guild guild) {
    this.guild = guild;
  }
}
