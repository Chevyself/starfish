package com.starfishst.api.tickets;

import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/** Represents a ticket made by a customer */
public interface Ticket extends StarfishCatchable {

  /**
   * Adds an user to the ticket
   *
   * @param user the user that is going to be added
   * @param role the role of the user
   * @return whether the user was added or not. true if removed
   */
  @Deprecated
  boolean addUser(@NonNull BotUser user, @NonNull String role);

  /**
   * Adds an user to the ticket
   *
   * @param user the user that is going to be added
   * @param role the role of the user
   * @return whether the user was added or not. true if removed
   */
  boolean addUser(@NonNull BotUser user, @NonNull TicketRole role);

  /**
   * Removes an user from the ticket
   *
   * @param user the user to remove from the ticket
   * @return whether the user was removed. true if removed
   */
  boolean removeUser(@NonNull BotUser user);

  /**
   * Get the list of users matching a role
   *
   * @param roleToMatch the role to match
   * @return the list of users matching it
   */
  @NonNull
  @Deprecated
  default List<BotUser> getUsers(@NonNull String roleToMatch) {
    return this.getUsers(TicketRole.valueOf(roleToMatch.toUpperCase()));
  }

  /**
   * Get the list of users matching a role
   *
   * @param toMatch the role to match
   * @return the list of users matching it
   */
  @NonNull
  default List<BotUser> getUsers(@NonNull TicketRole toMatch) {
    return this.getUsers().entrySet().stream()
        .filter(
            entry -> {
              TicketRole role = entry.getValue();
              return toMatch == role
                  || (toMatch == TicketRole.CUSTOMER && role == TicketRole.OWNER);
            })
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  /**
   * Get the complete information from a ticket
   *
   * @param locale the locale that will read the ticket information
   * @param appendUsers whether the users of the ticket must be added in the information
   * @return the complete information of a ticket
   */
  @NonNull
  default Message toCompleteInformation(@NonNull LocaleFile locale, boolean appendUsers) {
    // Don't remove locale it might be useful in the future
    return Messages.of("ticket-info")
        .withEmbeds(
            builders -> {
              if (!builders.isEmpty()) {
                // TODO name could be changed for the name in parameter locale
                EmbedBuilder builder = builders.get(0);
                this.getDetails()
                    .toStringMap()
                    .forEach((name, value) -> builder.addField(name, value, true));
                if (appendUsers) {
                  StringBuilder usersBuilder = new StringBuilder();
                  this.getUsers()
                      .forEach(
                          (user, role) -> usersBuilder
                              .append(user.getDiscordTag())
                              .append(" role: ")
                              .append(role)
                              .append("\n"));
                  builder.addField("users", usersBuilder.toString(), true);
                }
              }
            })
        .build(this.getPlaceholders());
  }

  /**
   * Get the complete information from a ticket
   *
   * @param user the user that will read the information
   * @param appendUsers whether the users of the ticket must be added in the information
   * @return the complete information of a ticket
   */
  @NonNull
  default Message toCompleteInformation(@NonNull BotUser user, boolean appendUsers) {
    return this.toCompleteInformation(user.getLocaleFile(), appendUsers);
  }

  /**
   * Set the status of the ticket. This must call {@link
   * com.starfishst.api.events.tickets.TicketStatusUpdatedEvent}
   *
   * @param status the new status of the ticket
   */
  void setStatus(@NonNull TicketStatus status);

  /**
   * Set the text channel where the ticket is running. This must call {@link
   * com.starfishst.api.events.tickets.TicketNewChannelEvent}
   *
   * @param channel the new text channel where the ticket will run
   */
  void setTextChannel(TextChannel channel);

  /**
   * Get the id to identify the ticket
   *
   * @return the id to identify the ticket
   */
  long getId();

  /**
   * Get the text channel where the ticket is running
   *
   * @return a {@link java.util.Optional} holding the nullable channel
   */
  @NonNull
  Optional<TextChannel> getTextChannel();

  /**
   * Get the type of ticket
   *
   * @return the type of ticket
   */
  @NonNull
  TicketType getType();

  /**
   * Check whether the ticket has freelancers
   *
   * @return true if the ticket has freelancers
   */
  default boolean hasFreelancers() {
    return this.getUsers(TicketRole.FREELANCER).size() > 0;
  }

  /**
   * Get the details of the ticket
   *
   * @return the details of the ticket
   */
  @NonNull
  ValuesMap getDetails();

  /**
   * Get the status in which the ticket is currently in
   *
   * @return the ticket status
   */
  @NonNull
  TicketStatus getStatus();

  /**
   * Get the owner of the ticket. This will user {@link #getUsers(String)} with the role 'owner'
   *
   * @return a {@link java.util.Optional} holding the nullable user
   */
  @NonNull
  default Optional<BotUser> getOwner() {
    BotUser user = null;
    List<BotUser> owners = this.getUsers(TicketRole.OWNER);
    if (!owners.isEmpty()) {
      user = owners.get(0);
    }
    return Optional.ofNullable(user);
  }

  /**
   * Get the Map of users and their role in the ticket
   *
   * @return the map of users
   */
  @NonNull
  Map<BotUser, TicketRole> getUsers();

  /**
   * Get the placeholders of this ticket for messages
   *
   * @return the map of placeholders
   */
  @NonNull
  default Map<String, String> getPlaceholders() {
    Map<String, String> placeholders = new HashMap<>();
    placeholders.put("id", String.valueOf(this.getId()));
    placeholders.put("type", this.getType().toString().toLowerCase());
    placeholders.put("status", this.getStatus().toString().toLowerCase());
    this.getOwner()
        .ifPresent(
            owner -> {
              placeholders.put("owner", owner.getName());
              placeholders.put("owner_tag", owner.getMention());
            });
    this.getTextChannel().ifPresent(channel -> placeholders.put("channel", channel.getAsMention()));
    return placeholders;
  }

  /**
   * Get the freelancer operating in this ticket
   *
   * @return the freelancers on this ticket
   */
  @NonNull
  default Collection<BotUser> getFreelancers() {
    return this.getUsers(TicketRole.FREELANCER);
  }

  /**
   * Get the first freelancer operating on this ticket
   *
   * @return the first freelancer
   */
  @NonNull
  default BotUser getFreelancer() {
    if (!this.getFreelancers().isEmpty()) {
      for (BotUser freelancer : this.getFreelancers()) {
        return freelancer;
      }
    }
    throw new IllegalStateException("There's no freelancer on this ticket");
  }
}
