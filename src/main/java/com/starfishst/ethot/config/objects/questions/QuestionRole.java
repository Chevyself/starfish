package com.starfishst.ethot.config.objects.questions;

import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.Strings;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.util.Discord;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/**
 * This represents a role question. Used in {@link
 * com.starfishst.ethot.tickets.type.QuestionsTicket}
 *
 * <p>The representation in json:
 *
 * <p>{ "title": "Something here", "description": "Something descriptive", "simple": "Something",
 * "limit": 10, "role": "roles" }
 *
 * @author Chevy
 * @version 1.0.0
 */
public class QuestionRole extends Question {

  @NotNull private final String role;

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
    List<Role> roles = Main.getDiscordConfiguration().getRolesByKey(role);
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
  public @NotNull String getDescription() {
    HashMap<String, String> placeholders = new HashMap<>();
    try {
      placeholders.put("roles", Lots.pretty(Discord.getAsMention(getRoles())));
      placeholders.put("limit", String.valueOf(limit));
      return Strings.buildMessage(super.getDescription(), placeholders);
    } catch (TicketCreationException e) {
      Errors.addError(e.getMessage());
      return super.getDescription();
    }
  }
}
