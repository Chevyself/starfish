package com.starfishst.api.configuration;

import com.starfishst.api.Starfish;
import com.starfishst.api.utility.Discord;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** The configuration for discord */
public interface DiscordConfiguration {

  /**
   * Get a category by its key. validates the category using {@link
   * Discord#validateCategory(Category, Guild, String, boolean, List, List)}.
   *
   * @param key the key of the category
   * @return a {@link java.util.Optional} holding the nullable category
   */
  @NonNull
  default Optional<Category> geCategoryOrCreate(@NonNull String key) {
    return this.getGuild().map(guild -> {
      Category category =
              Discord.validateCategory(
                      this.getCategory(key).orElse(null),
                      guild,
                      key,
                      true,
                      this.getRoles("allowed-in-categories"),
                      this.getRoles("allowed-to-see-in-categories"));
      if (category.getIdLong() != this.getCategories().getOrDefault(key, -1L)) {
        this.getCategories().put(key, category.getIdLong());
      }
      return category;
    });
  }

  /**
   * Get a channel using its key
   *
   * @param key the key of the channel
   * @return a {@link java.util.Optional} holding the nullable channel
   */
  @NonNull
  default Optional<TextChannel> getChannelOrCreate(@NonNull String key) {
    return this.getGuild().map(guild -> {
      TextChannel channel = Discord.validateChannel(this.getChannel(key).orElse(null), guild, key, true, this.getRoles("allowed-in-channels"), this.getRoles("allowed-to-see-in-channels"));
      if (channel.getIdLong() != this.getChannels().getOrDefault(key, -1L)) {
        this.getChannels().put(key, channel.getIdLong());
      }
      return channel;
    });
  }

  /**
   * * Get a channel using its key
   *
   * @param key the key of the channel
   * @return a {@link java.util.Optional} holding the nullable channel
   */
  @NonNull
  default Optional<TextChannel> getChannel(@NonNull String key) {
    return this.getGuild().map(guild -> guild.getTextChannelById(this.getChannels().getOrDefault(key, -1L)));
  }

  /**
   * * Get a channel using its key
   *
   * @param key the key of the channel
   * @return a {@link java.util.Optional} holding the nullable category
   */
  @NonNull
  default Optional<Category> getCategory(@NonNull String key) {
    return this.getGuild().map(guild -> guild.getCategoryById(this.getCategories().getOrDefault(key, -1L)));
  }

  /**
   * Get the roles for a key or set it with an empty list
   *
   * @param key the key to get the roles from
   * @return the list of roles
   */
  @NonNull
  default List<Role> getRoles(@NonNull String key) {
    Optional<JDA> optionalJda = Starfish.getJdaConnection().getJda();
    return this.getRoles().computeIfAbsent(key, initialKey -> new ArrayList<>()).stream().map(id -> optionalJda.map(jda -> jda.getRoleById(id)).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * Set the guild for in the configuration
   *
   * @param guild the new guild
   */
  default void setGuild(Guild guild) {
    this.setGuildId(guild == null ? 0 : guild.getIdLong());
  }

  /**
   * Set the guild id in the configuration
   *
   * @param id the id of the guild
   */
  void setGuildId(long id);

  /**
   * Get the id of the guild where the bot is working
   *
   * @return the id of guild where the bot is working
   */
  long getGuildId();

  /**
   * Get the guild where the bot is working
   *
   * @return a {@link java.util.Optional} holding the nullable guild
   */
  @NonNull
  default Optional<Guild> getGuild() {
    return Starfish.getJdaConnection().getJda().map(jda -> jda.getGuildById(this.getGuildId()));
  }

  /**
   * Get the roles that can be used by the bot
   *
   * @return the roles
   */
  @NonNull
  Map<String, List<Long>> getRoles();

  /**
   * Get the categories that can be used by the bot
   *
   * @return the categories
   */
  @NonNull
  Map<String, Long> getCategories();

  /**
   * Get the channels that can be used by the bot
   *
   * @return the channels
   */
  @NonNull
  Map<String, Long> getChannels();
}
