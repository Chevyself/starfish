package com.starfishst.api.data.user;

import com.starfishst.api.Permissible;
import com.starfishst.api.ValuesMap;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.lang.Localizable;
import com.starfishst.bot.Starfish;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An user that uses the Starfish Studios services */
public interface BotUser extends Localizable, Permissible {

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
}
