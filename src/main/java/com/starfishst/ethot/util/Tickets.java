package com.starfishst.ethot.util;

import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.questions.Question;
import com.starfishst.ethot.objects.questions.RoleAnswer;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.type.FreelancingTicket;
import com.starfishst.ethot.tickets.type.QuestionsTicket;
import com.starfishst.ethot.tickets.type.Ticket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Many utilities for tickets
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Tickets {

  /**
   * Gets all the tickets from 'tickets' that have the status 'status'
   *
   * @param status the status to match
   * @param tickets the ticket to match
   * @return a list of tickets matching the status
   */
  public static List<Ticket> getTicketsMatchingStatus(
      @NotNull TicketStatus status, @NotNull List<Ticket> tickets) {
    return tickets.stream()
        .filter(ticket -> ticket.getStatus() == status)
        .collect(Collectors.toList());
  }

  /**
   * Get the placeholders of a ticket
   *
   * @param ticket the ticket looking for placeholders
   * @return the placeholders of a ticket
   */
  @NotNull
  public static HashMap<String, String> getPlaceholders(@NotNull Ticket ticket) {
    HashMap<String, String> placeholders =
        getPlaceholders(
            ticket.getType(),
            ticket.getUser(),
            ticket.getId(),
            ticket.getStatus(),
            ticket.getChannel());
    if (ticket instanceof FreelancingTicket) {
      Freelancer freelancer = ((FreelancingTicket) ticket).getFreelancer();
      if (freelancer != null) {
        placeholders.putAll(Freelancers.getPlaceholders(freelancer));
      }
    }
    return placeholders;
  }

  /**
   * Gets placeholders of ticket information
   *
   * @param type the type of ticket
   * @param customer the customer creating the ticket
   * @param id the id of the ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @return the possible placeholders of the ticket
   */
  @NotNull
  public static HashMap<String, String> getPlaceholders(
      @NotNull TicketType type,
      @Nullable User customer,
      long id,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel) {
    HashMap<String, String> placeHolders = new HashMap<>();
    placeHolders.put("type", type.toString().toLowerCase());
    placeHolders.put("creator", customer == null ? "null" : customer.getName());
    placeHolders.put("id", String.valueOf(id));
    placeHolders.put("status", status.toString().toLowerCase());
    placeHolders.put("channel", channel == null ? "null" : channel.getAsMention());
    return placeHolders;
  }

  /**
   * Get the question matching
   *
   * @param questions the questions to check
   * @param simple the simple to match
   * @return the question if matches else null
   */
  @Nullable
  public static Question getQuestionUsingSimple(
      @NotNull List<Question> questions, @NotNull String simple) {
    return questions.stream()
        .filter(question -> question.getSimple().equalsIgnoreCase(simple))
        .findFirst()
        .orElse(null);
  }

  /**
   * Get the ids of some tickets from a list of them
   *
   * @param tickets the list of tickets
   * @return the list of ids
   */
  @NotNull
  public static List<Long> toIdList(@NotNull List<Ticket> tickets) {
    List<Long> ids = new ArrayList<>();
    tickets.forEach(ticket -> ids.add(ticket.getId()));
    return ids;
  }

  /**
   * Creates the fields mainly used in messages with information about tickets
   *
   * @param ticket the ticket to get the fields from
   * @return the linked map
   */
  @NotNull
  static LinkedHashMap<String, String> getFields(@NotNull QuestionsTicket ticket) {
    LinkedHashMap<String, String> fields = new LinkedHashMap<>();
    ticket
        .getAnswers()
        .forEach(
            (string, answer) -> {
              if (answer instanceof RoleAnswer) {
                fields.put(
                    Lang.getRaw(string) == null ? string : Lang.get(string),
                    ((RoleAnswer) answer).getTags());
              } else {
                fields.put(
                    Lang.getRaw(string) == null ? string : Lang.get(string),
                    answer.getAnswer().toString());
              }
            });
    return fields;
  }
}
