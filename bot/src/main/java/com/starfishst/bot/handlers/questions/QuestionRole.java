package com.starfishst.bot.handlers.questions;

import com.starfishst.api.Starfish;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Discord;
import com.starfishst.api.utility.Messages;
import com.starfishst.commands.jda.result.ResultType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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
  @NonNull private final String role;

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
      @NonNull String title,
      @NonNull String simple,
      @NonNull String description,
      @NonNull String role,
      int limit) {
    super(title, simple, description, limit);
    this.role = role;
  }

  /**
   * This checks that the list 'toCheck' does not contain any tickets outside of 'roles'
   *
   * @param toCheck the list to check
   * @return a list of the roles mentioned that cannot be
   */
  @NonNull
  private List<Role> getNotMentionableRoles(@NonNull List<Role> toCheck) {
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
   * 'role' is a string to identify a list of roles inside 'discord.json'
   *
   * @return the identification for roles that can be used in the answer of this question
   */
  @NonNull
  public String getRole() {
    return this.role;
  }

  /**
   * Gets the list of roles as actual ones
   *
   * @return the list of roles
   */
  @NonNull
  public List<Role> getRoles() {
    return Starfish.getDiscordConfiguration().getRoles(this.role);
  }

  /**
   * Replaces the super permission to one that has the placeholder %roles%
   *
   * @return the new description
   */
  @Override
  public @NonNull String getBuiltDescription() {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("roles", Lots.pretty(Discord.getAsMention(this.getRoles())));
    placeholders.put("limit", String.valueOf(this.limit));
    return Strings.build(super.getBuiltDescription(), placeholders);
  }

  @Override
  public Object getAnswer(@NonNull GuildMessageReceivedEvent event, @NonNull BotUser user) {
    List<Long> roles = new ArrayList<>();
    for (Role role : event.getMessage().getMentionedRoles()) {
      roles.add(role.getIdLong());
    }
    if (roles.isEmpty()) {
      Messages.build(user.getLocaleFile().get("questions.empty-roles"), ResultType.ERROR, user)
          .send(event.getChannel(), Messages.getErrorConsumer());
      return null;
    } else if (roles.size() > this.limit) {
      Messages.build(
              user.getLocaleFile()
                  .get(
                      "questions.more-roles-than-limit",
                      Maps.singleton("limit", String.valueOf(this.getLimit()))),
              ResultType.ERROR,
              user)
          .send(event.getChannel(), Messages.getErrorConsumer());
      return null;
    }
    List<Role> notMentionable = this.getNotMentionableRoles(event.getMessage().getMentionedRoles());
    if (notMentionable.isEmpty()) {
      return new ArrayList<>(roles);
    } else {
      Messages.build(
              user.getLocaleFile()
                  .get(
                      "questions.roles-not-mentionable",
                      Maps.singleton("roles", Lots.pretty(Discord.getAsMention(notMentionable)))),
              ResultType.ERROR,
              user)
          .send(event.getChannel(), Messages.getErrorConsumer());
      return null;
    }
  }
}
