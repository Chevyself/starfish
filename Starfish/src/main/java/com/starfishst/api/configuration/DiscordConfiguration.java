package com.starfishst.api.configuration;

import com.starfishst.api.utility.Discord;
import com.starfishst.core.utils.Validate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The configuration for discord */
public interface DiscordConfiguration {

  /**
   * Get a category by its key. validates the category using {@link
   * Discord#validateCategory(Category, Guild, String, boolean, List, List)}.
   *
   * @param key the key of the category
   * @return if this returns null it means that the guild has not been set
   */
  @Nullable
  default Category getCategory(@NotNull String key) {
    Guild guild = this.getGuild();
    if (guild != null) {
      Category validated =
          Discord.validateCategory(
              this.getCategories().get(key),
              guild,
              key,
              true,
              this.getRoles("allowed-in-categories"),
              this.getRoles("allowed-to-see-in-categories"));
      if (validated != this.getCategories().get(key)) {
        this.getCategories().put(key, validated);
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
  default TextChannel getChannel(@NotNull String key) {
    Guild guild = this.getGuild();
    if (guild != null) {
      TextChannel validated =
          Discord.validateChannel(
              this.getChannels().get(key),
              guild,
              key,
              true,
              this.getRoles("allowed-in-channels"),
              this.getRoles("allowed-to-see-in-channels"));
      if (validated != this.getChannels().get(key)) {
        this.getChannels().put(key, validated);
      }
      return validated;
    }
    return null;
  }

  /**
   * Get the roles for a key or set it with an empty list
   *
   * @param key the key to get the roles from
   * @return the list of roles
   */
  @NotNull
  default List<Role> getRoles(@NotNull String key) {
    List<Role> validated = Validate.notNullOr(this.getRoles().get(key), new ArrayList<>());
    if (validated != this.getRoles().get(key)) {
      this.getRoles().put(key, validated);
    }
    return validated;
  }

  /**
   * Set the guild for in the configuration
   *
   * @param guild the new guild
   */
  void setGuild(@Nullable Guild guild);

  /**
   * Get the guild where the bot is working
   *
   * @return the guild where the bot is working
   */
  @Nullable
  Guild getGuild();

  /**
   * Get the roles that can be used by the bot
   *
   * @return the roles
   */
  @NotNull
  HashMap<String, List<Role>> getRoles();

  /**
   * Get the categories that can be used by the bot
   *
   * @return the categories
   */
  @NotNull
  HashMap<String, Category> getCategories();

  /**
   * Get the channels that can be used by the bot
   *
   * @return the channels
   */
  @NotNull
  HashMap<String, TextChannel> getChannels();
}
