package com.starfishst.api.user;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.lang.Localizable;
import com.starfishst.api.permissions.Permissible;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/** An user that uses the Starfish Studios services */
public interface BotUser extends Localizable, Permissible, StarfishCatchable {

  /** The list of the id of developers */
  List<Long> devs = Arrays.asList(797192253751033876L, 151067971899359233L);

  /**
   * Get the complete information of the user
   *
   * @param user the user that will read the information
   * @return the complete information of the user
   */
  @NonNull
  default EmbedBuilder toCompleteInformation(@NonNull BotUser user) {
    return Messages.build(this, user);
  }

  /**
   * Get the rating of the freelancer
   *
   * @return the rating of the freelancer
   */
  @NonNull
  FreelancerRating getRating();

  /**
   * Get the user as a discord mention
   *
   * @return the mention of the user
   */
  @NonNull
  default String getMention() {
    return this.getDiscord()
        .map(IMentionable::getAsMention)
        .orElseGet(() -> String.valueOf(this.getId()));
  }

  /**
   * Get the discord tag of the user
   *
   * @return the tag of the user
   */
  @NonNull
  default String getDiscordTag() {
    return this.getDiscord().map(User::getAsTag).orElseGet(() -> String.valueOf(this.getId()));
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
   * @return a {@link Optional} instance holding the nullable user
   */
  @NonNull
  default Optional<User> getDiscord() {
    return Starfish.getJdaConnection().getJda().map(jda -> jda.getUserById(this.getId()));
  }

  /**
   * Get the discord member of this bot user
   *
   * @return a {@link Optional} instance holding the nullable member
   */
  @NonNull
  default Optional<Member> getMember() {
    return Starfish.getDiscordConfiguration()
        .getGuild()
        .map(guild -> guild.getMemberById(this.getId()));
  }

  /**
   * Get the user as a discord mention
   *
   * @return the mention of the user
   */
  @NonNull
  default String getName() {
    return this.getMember()
        .map(
            member -> {
              String nickname = member.getNickname();
              return nickname == null ? member.getEffectiveName() : nickname;
            })
        .orElseGet(() -> this.getDiscord().map(User::getName).orElse(String.valueOf(this.getId())));
  }

  /**
   * Get the placeholders of the user
   *
   * @return the placeholders of the user
   */
  @NonNull
  default Map<String, String> getPlaceholders() {
    Map<String, String> map = new HashMap<>();
    map.put("id", String.valueOf(this.getId()));
    map.put("mention", this.getMention());
    map.put("name", this.getName());
    map.put("tag", this.getDiscordTag());
    return map;
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
