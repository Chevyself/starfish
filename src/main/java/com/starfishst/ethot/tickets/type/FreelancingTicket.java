package com.starfishst.ethot.tickets.type;

import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.FreelancerJoinTicketException;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Freelancers;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Is the kind of ticket that needs of a freelancer to exist */
public class FreelancingTicket extends QuestionsTicket {

  /** The freelancer in charge of the ticket */
  @Nullable protected Freelancer freelancer;

  /**
   * The generic constructor for databases
   *
   * @param id the id of the ticket
   * @param customer the customer owner of the ticket
   * @param status the status of the ticket
   * @param channel the channel of the ticket
   * @param answers the answers provided by the ticket
   * @param freelancer the freelancer in charge of the ticket
   */
  protected FreelancingTicket(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> answers,
      @Nullable Freelancer freelancer) {
    super(id, customer, status, channel, answers);
    this.freelancer = freelancer;
  }

  /**
   * Get the freelancer in charge of the ticket
   *
   * @return the freelancer in charge of the ticket
   */
  @Nullable
  public Freelancer getFreelancer() {
    return freelancer;
  }

  /**
   * Sets the freelancer in charge of the ticket
   *
   * @param freelancer the freelancer to set
   * @throws DiscordManipulationException when using discord goes wrong
   * @throws FreelancerJoinTicketException in case adding the freelancer goes wrong
   */
  public void setFreelancer(@Nullable Freelancer freelancer)
      throws FreelancerJoinTicketException, DiscordManipulationException {
    if (this.freelancer != null && freelancer != null) {
      throw new FreelancerJoinTicketException(
          Lang.get("TICKET_CLAIMED", Tickets.getPlaceholders(this)));
    } else if (this.freelancer == null && freelancer != null) {
      if (getStatus() != TicketStatus.OPEN) {
        throw new FreelancerJoinTicketException(
            Lang.get("FREELANCER_NOT_JOINABLE", Tickets.getPlaceholders(this)));
      }
      if (!Freelancers.hasRole(getAnswers(), freelancer)) {
        if (freelancer.getUser() != null) {
          freelancer
              .getUser()
              .openPrivateChannel()
              .queue(channel -> Messages.error(Lang.get("FREELANCER_NO_ROLE")).send(channel));
        }
      } else {
        this.freelancer = freelancer;

        if (channel != null) {
          if (freelancer.getMember() != null) {
            Discord.allow(channel, freelancer.getMember(), Discord.ALLOWED);
          }
          HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
          Messages.create(
                  "FREELANCER_JOIN_TITLE",
                  "FREELANCER_JOIN_DESCRIPTION",
                  placeholders,
                  placeholders)
              .send(channel);
        }
      }
    } else if (this.freelancer != null) {
      if (channel != null && this.freelancer.getMember() != null) {
        HashMap<String, String> placeholders = Freelancers.getPlaceholders(this.freelancer);
        Messages.create(
                "FREELANCER_LEAVE_TITLE",
                "FREELANCER_LEAVE_DESCRIPTION",
                placeholders,
                placeholders)
            .send(channel);
        Discord.disallow(channel, this.freelancer.getMember());
      }
      this.freelancer = null;
    }
    refresh();
  }

  @Override
  protected void archive() {
    super.archive();
    if (this.freelancer != null && this.freelancer.getUser() != null) {
      this.freelancer.getUser().openPrivateChannel().queue(this::sendTranscript);
    }
  }

  @Override
  public @NotNull Document getDocument() {
    Document document = super.getDocument();
    if (freelancer != null) document.append("freelancer", freelancer.getId());
    return document;
  }
}
