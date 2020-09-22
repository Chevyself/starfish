package com.starfishst.bot.handlers.questions;

import com.starfishst.bot.handlers.questions.Question;
import com.starfishst.bot.oldconfig.DiscordConfiguration;
import com.starfishst.bot.exception.TicketCreationException;
import com.starfishst.bot.util.Discord;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.Strings;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/**
 * This represents a role question. Used in {@link com.starfishst.bot.oldtickets.type.QuestionsTicket}
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
   * Gets the list of roles as actual ones
   *
   * @return the list of roles
   * @throws TicketCreationException in case there's no roles in 'discord.json'
   */
  @NotNull
  public List<Role> getRoles() throws TicketCreationException {
    List<Role> roles = DiscordConfiguration.getInstance().getRolesByKey(role);
    if (roles == null || roles.isEmpty()) {
      throw new TicketCreationException("There is no roles for " + role);
    }
    return roles;
  }

  /**
   * Replaces the super permission to one that has the placeholder %roles%
   *
   * @return the new description
   */
  @Override
  public @NotNull String getBuiltDescription() {
    HashMap<String, String> placeholders = new HashMap<>();
    try {
      placeholders.put("roles", Lots.pretty(Discord.getAsMention(getRoles())));
      placeholders.put("limit", String.valueOf(limit));
      return Strings.buildMessage(super.getBuiltDescription(), placeholders);
    } catch (TicketCreationException e) {
      Fallback.addError(e.getMessage());
      return super.getBuiltDescription();
    }
  }
}
