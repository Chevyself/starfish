package com.starfishst.api.user;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.lang.Localizable;
import com.starfishst.api.permissions.Permissible;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.commands.jda.utils.embeds.EmbedQuery;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/** An user that uses the Starfish Studios services */
public interface BotUser extends Localizable, Permissible, StarfishCatchable {

  /** The list of the id of developers */
  List<Long> devs = Lots.list(797192253751033876L, 151067971899359233L);

  /**
   * Get the complete information of the user
   *
   * @param user the user that will read the information
   * @return the complete information of the user
   */
  @NonNull
  default EmbedQuery toCompleteInformation(@NonNull BotUser user) {
    return Messages.build(this, user);
  }

  /**
   * Get the rating of the freelancer
   *
   * @return the rating of the freelancer
   */
  FreelancerRating getRating();

  /**
   * Get the user as a discord mention
   *
   * @return the mention of the user
   */
  @NonNull
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
  @NonNull
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
  @NonNull
  ValuesMap getPreferences();

  /**
   * Get the file that is used to handle the language of the user
   *
   * @return the language file
   */
  @NonNull
  LocaleFile getLocaleFile();

  /**
   * Check whether this user is a freelancer
   *
   * @return true if the user is a freelancer
   */
  default boolean isFreelancer() {
    return this.getPreferences().getOr("freelancer", Boolean.class, false);
  }

  /**
   * Get the discord user of this bot user
   *
   * @return the discord user if found else null
   */
  default User getDiscord() {
    JDA jda = Starfish.getJdaConnection().getJda();
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
  default Member getMember() {
    Guild guild = Starfish.getDiscordConfiguration().getGuild();
    if (guild != null) {
      return guild.getMemberById(this.getId());
    }
    return null;
  }

  /**
   * Get the user as a discord mention
   *
   * @return the mention of the user
   */
  @NonNull
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
  @NonNull
  default Map<String, String> getPlaceholders() {
    return Maps.builder("id", String.valueOf(this.getId()))
        .append("mention", this.getMention())
        .append("name", this.getName())
        .append("tag", this.getDiscordTag())
        .build();
  }

  @Override
  @NonNull
  default String getLang() {
    return this.getPreferences().getOr("lang", String.class, "en");
  }

  @Override
  default boolean hasPermission(@NonNull String node, @NonNull String context) {
    if (BotUser.devs.contains(this.getId())) return true;
    return Permissible.super.hasPermission(node, context);
  }
}
