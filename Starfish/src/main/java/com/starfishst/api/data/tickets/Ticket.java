package com.starfishst.api.data.tickets;

import com.starfishst.api.Starfish;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.Messages;
import com.starfishst.api.utility.StarfishCatchable;
import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.handlers.ticket.transcript.TicketTranscript;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
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
    List<BotUser> users = new ArrayList<>();
    this.getUsers()
        .forEach(
            (user, role) -> {
              if (role.equalsIgnoreCase(roleToMatch)
                  || roleToMatch.equalsIgnoreCase("customer") && role.equalsIgnoreCase("owner")) {
                users.add(user);
              }
            });
    return users;
  }

  /**
   * Get the complete information from a ticket
   *
   * @param locale the locale that will read the ticket information
   * @param appendUsers whether the users of the ticket must be added in the information
   * @return the complete information of a ticket
   */
  @NonNull
  default EmbedQuery toCompleteInformation(@NonNull LocaleFile locale, boolean appendUsers) {
    Map<String, String> placeholders = this.getPlaceholders();
    LinkedHashMap<String, String> fields = new LinkedHashMap<>();

    this.getDetails().toStringMap().forEach(fields::put);
    if (appendUsers) {
      StringBuilder usersBuilder = Strings.getBuilder();
      this.getUsers()
          .forEach(
              (ticketUser, role) -> {
                Member member = ticketUser.getMember();
                if (member != null) {
                  usersBuilder
                      .append(member.getAsMention())
                      .append(" role: ")
                      .append(role)
                      .append("\n");
                } else {
                  usersBuilder
                      .append(ticketUser.getId())
                      .append(" role: ")
                      .append(role)
                      .append("\n");
                }
              });
      fields.put("users", usersBuilder.toString());
    }
    EmbedQuery embedQuery =
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
  default EmbedQuery toCompleteInformation(@NonNull BotUser user, boolean appendUsers) {
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
   * Get the id to identify the ticket
   *
   * @return the id to identify the ticket
   */
  long getId();

  /**
   * Set the text channel where the ticket is running. This must call {@link
   * com.starfishst.api.events.tickets.TicketNewChannelEvent}
   *
   * @param channel the new text channel where the ticket will run
   */
  void setTextChannel(TextChannel channel);

  /**
   * Set the status of the ticket. This must call {@link
   * com.starfishst.api.events.tickets.TicketStatusUpdatedEvent}
   *
   * @param status the new status of the ticket
   */
  void setTicketStatus(@NonNull TicketStatus status);

  /**
   * Get the id of the text channel where this ticket is on
   *
   * @return the id of the channel
   */
  long getTextChannelId();

  /**
   * Get the text channel where the ticket is running
   *
   * @return the text channel
   */
  TextChannel getTextChannel();

  /**
   * Get the type of ticket
   *
   * @return the type of ticket
   */
  @NonNull
  TicketType getTicketType();

  /**
   * Get the owner of the ticket. This will user {@link #getUsers(String)} with the role 'owner'
   *
   * @return the user if found else null
   */
  default BotUser getOwner() {
    List<BotUser> owners = this.getUsers("owner");
    if (owners.isEmpty()) {
      return null;
    }
    return owners.get(0);
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
  TicketStatus getTicketStatus();

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
    Map<String, String> placeholders =
        Maps.builder("id", String.valueOf(this.getId()))
            .append("type", this.getTicketType().toString().toLowerCase())
            .append("status", this.getTicketStatus().toString().toLowerCase())
            .build();
    BotUser owner = this.getOwner();
    if (owner != null) {
      placeholders.put("owner", owner.getName());
      placeholders.put("owner_tag", owner.getMention());
    }
    TextChannel channel = this.getTextChannel();
    if (channel != null) {
      placeholders.put("channel", channel.getAsMention());
    }
    return placeholders;
  }

  /**
   * Get the freelancer operating in this ticket
   *
   * @return the freelancers on this ticket
   */
  @NonNull
  default Collection<BotUser> getFreelancers() {
    ArrayList<BotUser> freelancers = new ArrayList<>();
    this.getUsers()
        .forEach(
            (user, role) -> {
              if (role.equalsIgnoreCase("freelancer")) freelancers.add(user);
            });
    return freelancers;
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
