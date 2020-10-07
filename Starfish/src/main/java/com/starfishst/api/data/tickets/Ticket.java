package com.starfishst.api.data.tickets;

import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.events.tickets.TicketAddUserEvent;
import com.starfishst.api.events.tickets.TicketRemoveUserEvent;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.Messages;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import me.googas.commons.Strings;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents a ticket made by a customer */
public interface Ticket extends ICatchable {

  /**
   * Adds an user to the ticket
   *
   * @param user the user that is going to be added
   * @param role the role of the user
   * @return whether the user was added or not. true if removed
   */
  default boolean addUser(@NotNull BotUser user, @NotNull String role) {
    if (!this.getUsers().containsKey(user)
        && !new TicketAddUserEvent(this, user, role).callAndGet()) {
      this.getUsers().put(user, role);
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
  default boolean removeUser(@NotNull BotUser user) {
    if (this.getUsers().containsKey(user) && !new TicketRemoveUserEvent(this, user).callAndGet()) {
      this.getUsers().remove(user);
      return true;
    }
    return false;
  }

  /**
   * Refresh the time in cache for the ticket
   *
   * @return this same ticket instance
   */
  @NotNull
  Ticket refresh();

  /**
   * Get the list of users matching a role
   *
   * @param roleToMatch the role to match
   * @return the list of users matching it
   */
  @NotNull
  default List<BotUser> getUsers(@NotNull String roleToMatch) {
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
   * Set the text channel where the ticket is running. This must call {@link
   * com.starfishst.api.events.tickets.TicketNewChannelEvent}
   *
   * @param channel the new text channel where the ticket will run
   */
  void setTextChannel(@Nullable TextChannel channel);

  /**
   * Set the status of the ticket. This must call {@link
   * com.starfishst.api.events.tickets.TicketStatusUpdatedEvent}
   *
   * @param status the new status of the ticket
   */
  void setTicketStatus(@NotNull TicketStatus status);

  /**
   * Get the id to identify the ticket
   *
   * @return the id to identify the ticket
   */
  long getId();

  /**
   * Get the type of ticket
   *
   * @return the type of ticket
   */
  @NotNull
  TicketType getTicketType();

  /**
   * Get the users inside the ticket. The map shows the user and its role in the ticket: 'user' or
   * 'freelancer'
   *
   * @return a map of users inside the ticket
   */
  @NotNull
  HashMap<BotUser, String> getUsers();

  /**
   * Get the details of the ticket
   *
   * @return the details of the ticket
   */
  @NotNull
  TicketDetails getDetails();

  /**
   * Get the text channel where the ticket is running
   *
   * @return the text channel
   */
  @Nullable
  TextChannel getTextChannel();

  /**
   * Get the status in which the ticket is currently in
   *
   * @return the ticket status
   */
  @NotNull
  TicketStatus getTicketStatus();

  /**
   * Get the time left until the ticket is unloaded
   *
   * @return the time left until the ticket is unloaded
   */
  @NotNull
  Time getTimeLeft();

  /**
   * Get the owner of the ticket. This will user {@link #getUsers(String)} with the role 'owner'
   *
   * @return the user if found else null
   */
  @Nullable
  default BotUser getOwner() {
    List<BotUser> owners = this.getUsers("owner");
    if (owners.isEmpty()) {
      return null;
    }
    return owners.get(0);
  }

  /**
   * Get the placeholders of this ticket for messages
   *
   * @return the map of placeholders
   */
  @NotNull
  default HashMap<String, String> getPlaceholders() {
    HashMap<String, String> placeholders =
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
   * Get the complete information from a ticket
   *
   * @param locale the locale that will read the ticket information
   * @param appendUsers whether the users of the ticket must be added in the information
   * @return the complete information of a ticket
   */
  @NotNull
  default EmbedQuery toCompleteInformation(@NotNull LocaleFile locale, boolean appendUsers) {
    HashMap<String, String> placeholders = this.getPlaceholders();
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
    fields.forEach((key, value) -> embedQuery.getEmbedBuilder().addField(key, value, true));
    return embedQuery;
  }

  /**
   * Get the complete information from a ticket
   *
   * @param user the user that will read the information
   * @param appendUsers whether the users of the ticket must be added in the information
   * @return the complete information of a ticket
   */
  @NotNull
  default EmbedQuery toCompleteInformation(@NotNull BotUser user, boolean appendUsers) {
    return this.toCompleteInformation(user.getLocaleFile(), appendUsers);
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
   * Get how many freelancers there is in the ticket
   *
   * @return the amount of freelancers
   */
  default int countFreelancers() {
    int size = 0;
    for (String value : this.getUsers().values()) {
      if (value.equalsIgnoreCase("freelancer")) {
        size++;
      }
    }
    return size;
  }
}
