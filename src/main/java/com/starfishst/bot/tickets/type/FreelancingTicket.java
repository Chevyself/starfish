package com.starfishst.bot.tickets.type;

import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.exception.FreelancerJoinTicketException;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.questions.Answer;
import com.starfishst.bot.tickets.TicketStatus;
import com.starfishst.bot.util.Discord;
import com.starfishst.bot.util.Freelancers;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
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
   * @param payments the payments of the ticket
   */
  protected FreelancingTicket(
      long id,
      @Nullable User customer,
      @NotNull TicketStatus status,
      @Nullable TextChannel channel,
      @NotNull HashMap<String, Answer> answers,
      @Nullable Freelancer freelancer,
      @NotNull List<String> payments) {
    super(id, customer, status, channel, answers, payments);
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
   * @return true if the freelancer is set
   * @throws DiscordManipulationException when using discord goes wrong
   * @throws FreelancerJoinTicketException in case adding the freelancer goes wrong
   */
  public boolean setFreelancer(@Nullable Freelancer freelancer)
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
        return false;
      } else {
        this.freelancer = freelancer;

        if (channel != null) {
          if (freelancer.getMember() != null) {
            Discord.allow(channel, freelancer.getMember(), Discord.ALLOWED);
          }
          HashMap<String, String> placeholders = Freelancers.getPlaceholders(freelancer);
          MessageBuilder builder =
              Messages.create(
                      "FREELANCER_JOIN_TITLE",
                      "FREELANCER_JOIN_DESCRIPTION",
                      placeholders,
                      placeholders)
                  .getAsMessageQuery()
                  .getBuilder();
          Member member = Discord.getMember(this.user);
          if (member != null) {
            builder.append(member);
          }
          channel.sendMessage(builder.build()).queue();
        }
        return true;
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
        return false;
      }
      this.freelancer = null;
    }
    refresh();
    return false;
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
