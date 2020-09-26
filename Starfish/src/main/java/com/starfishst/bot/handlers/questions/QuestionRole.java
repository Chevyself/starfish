package com.starfishst.bot.handlers.questions;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Discord;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.util.Messages;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.Strings;
import com.starfishst.core.utils.maps.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This represents a role question
 *
 * <p>The representation in json:
 *
 * <p>{ "title": "Something here", "description": "Something descriptive", "simple": "Something",
 * "limit": 10, "role": "roles" }
 */
public class QuestionRole extends Question {

  /** The key of the roles */
  @NotNull private final String role;

  /**
   * Create an instance
   *
   * @param title the title of the question
   * @param simple the simple of the question
   * @param description the description of the question
   * @param role the role of the question
   * @param limit the limit of roles in the answer
   */
  public QuestionRole(
      @NotNull String title,
      @NotNull String simple,
      @NotNull String description,
      @NotNull String role,
      int limit) {
    super(title, simple, description, limit);
    this.role = role;
  }

  /**
   * 'role' is a string to identify a list of roles inside 'discord.json'
   *
   * @return the identification for roles that can be used in the answer of this question
   */
  @NotNull
  public String getRole() {
    return role;
  }

  /**
   * This checks that the list 'toCheck' does not contain any tickets outside of 'roles'
   *
   * @param toCheck the list to check
   * @return a list of the roles mentioned that cannot be
   */
  @NotNull
  private List<Role> getNotMentionableRoles(@NotNull List<Role> toCheck) {
    List<Role> notMentionable = new ArrayList<>();
    toCheck.forEach(
        role -> {
          if (!this.getRoles().contains(role)) {
            notMentionable.add(role);
          }
        });
    return notMentionable;
  }

  /**
   * Gets the list of roles as actual ones
   *
   * @return the list of roles
   */
  @NotNull
  public List<Role> getRoles() {
    return Starfish.getDiscordConfiguration().getRoles(role);
  }

  /**
   * Replaces the super permission to one that has the placeholder %roles%
   *
   * @return the new description
   */
  @Override
  public @NotNull String getBuiltDescription() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("roles", Lots.pretty(Discord.getAsMention(getRoles())));
    placeholders.put("limit", String.valueOf(limit));
    return Strings.buildMessage(super.getBuiltDescription(), placeholders);
  }

  @Override
  public @Nullable Object getAnswer(
      @NotNull GuildMessageReceivedEvent event, @NotNull BotUser user) {
    List<Role> roles = event.getMessage().getMentionedRoles();
    if (roles.isEmpty()) {
      Messages.build(user.getLocaleFile().get("questions.empty-roles"), ResultType.ERROR, user);
      return null;
    } else if (roles.size() > limit) {
      Messages.build(
          user.getLocaleFile()
              .get(
                  "questions.more-roles-than-limit",
                  Maps.singleton("limit", String.valueOf(this.getLimit()))),
          ResultType.ERROR,
          user);
      return null;
    }
    List<Role> notMentionable = this.getNotMentionableRoles(roles);
    if (notMentionable.isEmpty()) {
      return roles;
    } else {
      Messages.build(
          user.getLocaleFile()
              .get(
                  "questions.roles-not-mentionable",
                  Maps.singleton("roles", Lots.pretty(Discord.getAsMention(notMentionable)))),
          ResultType.ERROR,
          user);
      return null;
    }
  }

  @Override
  public @NotNull String getSimple() {
    return "role" + super.getSimple();
  }
}
