package com.starfishst.api.tickets;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;
import me.googas.commands.jda.result.ResultType;
import me.googas.starbox.Strings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
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
  default boolean addUser(@NonNull BotUser user, @NonNull String role) {
    if (!this.getUsersIdMap().containsKey(user.getId())
        && !new TicketAddUserEvent(this, user, role).callAndGet()) {
      this.getUsersIdMap().put(user.getId(), role);
      return true;
    }
    return false;
  }

  /**
   * Removes an user from the ticket
   *
   * @param user the user to remove from the ticket
   * @return whether the user was removed. true if removed
   */
  default boolean removeUser(@NonNull BotUser user) {
    if (this.getUsersIdMap().containsKey(user.getId())
        && !new TicketRemoveUserEvent(this, user).callAndGet()) {
      this.getUsersIdMap().remove(user.getId());
      return true;
    }
    return false;
  }

  /**
   * Get the list of users matching a role
   *
   * @param roleToMatch the role to match
   * @return the list of users matching it
   */
  @NonNull
  default List<BotUser> getUsers(@NonNull String roleToMatch) {
    return this.getUsers().entrySet().stream().filter(entry -> {
      String role = entry.getValue();
      return role.equalsIgnoreCase(roleToMatch) || (roleToMatch.equalsIgnoreCase("customer") && role.equalsIgnoreCase("owner"));
    }).map(Map.Entry::getKey).collect(Collectors.toList());
  }

  /**
   * Get the complete information from a ticket
   *
   * @param locale the locale that will read the ticket information
   * @param appendUsers whether the users of the ticket must be added in the information
   * @return the complete information of a ticket
   */
  @NonNull
  default EmbedBuilder toCompleteInformation(@NonNull LocaleFile locale, boolean appendUsers) {
    Map<String, String> placeholders = this.getPlaceholders();
    LinkedHashMap<String, String> fields = new LinkedHashMap<>();

    this.getDetails().toStringMap().forEach(fields::put);
    if (appendUsers) {
      StringBuilder usersBuilder = new StringBuilder();
      this.getUsers()
          .forEach(
              (ticketUser, role) -> {
                Optional<Member> optionalMember = ticketUser.getMember();
                if (optionalMember.isPresent()) {
                  usersBuilder.append(optionalMember.get().getAsMention());
                } else {
                  usersBuilder.append(ticketUser.getId());
                }
                usersBuilder.append(" role: ")
                        .append(role)
                        .append("\n");
                ticketUser.getMember().ifPresent(member -> {
                  usersBuilder.append(member.getAsMention());
                });
              });
      fields.put("users", usersBuilder.toString());
    }


    EmbedBuilder embedQuery =
        Messages.build(
            locale.get("ticket-info.title", placeholders),
            locale.get("ticket-info.description", placeholders),
            ResultType.GENERIC,
            locale);
    fields.forEach((key, value) -> embedQuery.addField(key, value, true));
    return embedQuery;
  }

  /**
   * Get the complete information from a ticket
   *
   * @param user the user that will read the information
   * @param appendUsers whether the users of the ticket must be added in the information
   * @return the complete information of a ticket
   */
  @NonNull
  default EmbedBuilder toCompleteInformation(@NonNull BotUser user, boolean appendUsers) {
    return this.toCompleteInformation(user.getLocaleFile(), appendUsers);
  }

  /**
   * Get how many freelancers there is in the ticket
   *
   * @return the amount of freelancers
   */
  default int countFreelancers() {
    int size = 0;
    for (String value : this.getUsersIdMap().values()) {
      if (value.equalsIgnoreCase("freelancer")) {
        size++;
      }
    }
    return size;
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
   * Get the id of the text channel where this ticket is on
   *
   * @return the id of the channel
   */
  long getTextChannelId();

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
   * Get the owner of the ticket. This will user {@link #getUsers(String)} with the role 'owner'
   *
   * @return a {@link java.util.Optional} holding the nullable user
   */
  @NonNull
  default Optional<BotUser> getOwner() {
    BotUser user = null;
    List<BotUser> owners = this.getUsers("owner");
    if (!owners.isEmpty()) {
      user = owners.get(0);
    }
    return Optional.ofNullable(user);
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
   * Get the users inside the ticket. The map shows the user and its role in the ticket: 'user' or
   * 'freelancer'
   *
   * @return a map of users inside the ticket
   */
  @NonNull
  Map<Long, String> getUsersIdMap();

  /**
   * Get the Map of users and their role in the ticket
   *
   * @return the map of users
   */
  @NonNull
  default Map<BotUser, String> getUsers() {
    Map<BotUser, String> map = new HashMap<>();
    this.getUsersIdMap()
        .forEach(
            (id, role) -> {
              BotUser user = Starfish.getLoader().getStarfishUser(id);
              if (user != null) map.put(user, role);
            });
    return map;
  }

  /**
   * Check whether the ticket has freelancers
   *
   * @return true if the ticket has freelancers
   */
  default boolean hasFreelancers() {
    return this.countFreelancers() > 0;
  }

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
    this.getOwner().ifPresent(owner -> {
      placeholders.put("owner", owner.getName());
      placeholders.put("owner_tag", owner.getMention());
    });
    this.getTextChannel().ifPresent(channel -> {
      placeholders.put("channel", channel.getAsMention());
    });
    return placeholders;
  }

  /**
   * Get the freelancer operating in this ticket
   *
   * @return the freelancers on this ticket
   */
  @NonNull
  default Collection<BotUser> getFreelancers() {
    return this.getUsers("freelancer");
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
