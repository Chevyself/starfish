package com.starfishst.api.configuration;

import com.starfishst.api.Starfish;
import com.starfishst.api.utility.Discord;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

/** The configuration for discord */
public interface DiscordConfiguration {

  /**
   * Get a category by its key. validates the category using {@link
   * Discord#validateCategory(Category, Guild, String, boolean, List, List)}.
   *
   * @param key the key of the category
   * @return if this returns null it means that the guild has not been set
   */
  default Category requireCategory(@NonNull String key) {
    Guild guild = this.getGuild();
    if (guild != null) {
      Category validated =
          Discord.validateCategory(
              this.getCategory(key),
              guild,
              key,
              true,
              this.getRoles("allowed-in-categories"),
              this.getRoles("allowed-to-see-in-categories"));
      if (validated.getIdLong() != this.getCategories().getOrDefault(key, -1L)) {
        this.getCategories().put(key, validated.getIdLong());
      }
      return validated;
    }
    return null;
  }

  /**
   * * Get a channel using its key
   *
   * @param key the key of the channel
   * @return the channel
   */
  default TextChannel requireChannel(@NonNull String key) {
    Guild guild = this.getGuild();
    if (guild != null) {
      TextChannel validated =
          Discord.validateChannel(
              this.getChannel(key),
              guild,
              key,
              true,
              this.getRoles("allowed-in-channels"),
              this.getRoles("allowed-to-see-in-channels"));
      if (validated.getIdLong() != this.getChannels().getOrDefault(key, -1L)) {
        this.getChannels().put(key, validated.getIdLong());
      }
      return validated;
    }
    return null;
  }

  /**
   * * Get a channel using its key
   *
   * @param key the key of the channel
   * @return the channel
   */
  default TextChannel getChannel(@NonNull String key) {
    Guild guild = this.getGuild();
    if (guild != null) {
      long id = this.getChannels().getOrDefault(key, -1L);
      return guild.getTextChannelById(id);
    }
    return null;
  }

  /**
   * * Get a channel using its key
   *
   * @param key the key of the channel
   * @return the channel
   */
  default Category getCategory(@NonNull String key) {
    Guild guild = this.getGuild();
    if (guild != null) {
      long id = this.getCategories().getOrDefault(key, -1L);
      return guild.getCategoryById(id);
    }
    return null;
  }

  /**
   * Get the roles for a key or set it with an empty list
   *
   * @param key the key to get the roles from
   * @return the list of roles
   */
  @NonNull
  default List<Role> getRoles(@NonNull String key) {
    List<Role> roles = new ArrayList<>();
    List<Long> validated = Validate.notNullOr(this.getRoles().get(key), new ArrayList<>());
    if (validated != this.getRoles().get(key)) {
      this.getRoles().put(key, validated);
    }
    JDA jda = Starfish.getJdaConnection().getJda();
    if (jda != null) {
      for (Long id : validated) {
        Role role = jda.getRoleById(id);
        if (role != null) {
          roles.add(role);
        }
      }
    }
    return roles;
  }

  /**
   * Set the guild for in the configuration
   *
   * @param guild the new guild
   */
  void setGuild(Guild guild);

  /**
   * Get the id of the guild where the bot is working
   *
   * @return the id of guild where the bot is working
   */
  long getGuildId();

  /**
   * Get the guild where the bot is working
   *
   * @return the guild where the bot is working
   */
  default Guild getGuild() {
    JDA jda = Starfish.getJdaConnection().getJda();
    if (jda != null) {
      return jda.getGuildById(this.getGuildId());
    }
    return null;
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
