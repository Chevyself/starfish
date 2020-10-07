package com.starfishst.api.data.user;

import com.starfishst.api.Permissible;
import com.starfishst.api.ValuesMap;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.lang.Localizable;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.Starfish;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.HashMap;
import me.googas.commons.Lots;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An user that uses the Starfish Studios services */
public interface BotUser extends Localizable, Permissible, ICatchable {

  /**
   * Get the complete information of the user
   *
   * @param user the user that will read the information
   * @return the complete information of the suer
   */
  @NotNull
  default EmbedQuery toCompleteInformation(@NotNull BotUser user) {
    LocaleFile locale = user.getLocaleFile();
    HashMap<String, String> placeholders = this.getPlaceholders();
    EmbedQuery embedQuery =
        Messages.build(
            locale.get("user-info.title", placeholders),
            locale.get("user-info.description", placeholders),
            ResultType.GENERIC,
            user);
    HashMap<String, String> fields = new HashMap<>();
    this.getPreferences()
        .getMap()
        .forEach(
            (key, value) -> {
              if (!key.equalsIgnoreCase("lang") && !key.equalsIgnoreCase("freelancer")) {
                if (value instanceof Collection) {
                  fields.put(key, Lots.pretty((Collection<?>) value));
                } else {
                  fields.put(key, value.toString());
                }
              }
            });
    fields.forEach(
        (key, value) -> {
          embedQuery.getEmbedBuilder().addField(key, value, true);
        });
    return embedQuery;
  }

  /**
   * Get the user as a discord mention
   *
   * @return the mention of the user
   */
  @NotNull
  default String getMention() {
    User user = this.getDiscord();
    if (user != null) {
      return user.getAsMention();
    } else {
      return String.valueOf(this.getId());
    }
  }

  /**
   * Get the discord tag of the user
   *
   * @return the tag of the user
   */
  @NotNull
  default String getDiscordTag() {
    User user = this.getDiscord();
    if (user != null) {
      return user.getAsTag();
    } else {
      return String.valueOf(this.getId());
    }
  }
  /**
   * Get the id that is used to represent this used in discord
   *
   * @return the id of the user given in a long
   */
  long getId();

  /**
   * Get the preferences for the user
   *
   * @return the preferences
   */
  @NotNull
  ValuesMap getPreferences();

  /**
   * Get the file that is used to handle the language of the user
   *
   * @return the language file
   */
  @NotNull
  LocaleFile getLocaleFile();

  /**
   * Check whether this user is a freelancer
   *
   * @return true if the user is a freelancer
   */
  default boolean isFreelancer() {
    return this.getPreferences().getValueOr("freelancer", Boolean.class, false);
  }

  /**
   * Get the discord user of this bot user
   *
   * @return the discord user if found else null
   */
  @Nullable
  default User getDiscord() {
    JDA jda = Starfish.getConnection().getJda();
    if (jda != null) {
      return jda.getUserById(this.getId());
    }
    return null;
  }

  /**
   * Get the discord member of this bot user
   *
   * @return the discord user if found else null
   */
  @Nullable
  default Member getMember() {
    Guild guild = Starfish.getDiscordConfiguration().getGuild();
    if (guild != null) {
      return guild.getMemberById(this.getId());
    }
    return null;
  }

  @Override
  @NotNull
  default String getLang() {
    return this.getPreferences().getValueOr("lang", String.class, "en");
  }

  /**
   * Get the user as a discord mention
   *
   * @return the mention of the user
   */
  @NotNull
  default String getName() {
    Member member = this.getMember();
    if (member != null) {
      return member.getNickname() == null ? member.getEffectiveName() : member.getNickname();
    } else {
      User discord = this.getDiscord();
      if (discord != null) {
        return discord.getName();
      }
    }
    return String.valueOf(this.getId());
  }

  /**
   * Get the placeholders of the user
   *
   * @return the placeholders of the user
   */
  @NotNull
  default HashMap<String, String> getPlaceholders() {
    return Maps.builder("id", String.valueOf(this.getId()))
        .append("mention", this.getMention())
        .append("name", this.getName())
        .append("tag", this.getDiscordTag())
        .build();
  }
}
