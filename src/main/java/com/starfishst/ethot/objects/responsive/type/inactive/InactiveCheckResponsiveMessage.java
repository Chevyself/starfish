package com.starfishst.ethot.objects.responsive.type.inactive;

import com.starfishst.core.utils.time.TimeUtils;
import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.util.Unicode;
import com.starfishst.simple.Lots;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

/** This is the message send when an inactivity check starts */
public class InactiveCheckResponsiveMessage extends ResponsiveMessage {

  /** When was the inactivity check started */
  private final long createdAt;
  /** The id of the ticket doing the inactivity check */
  private final long ticket;
  /** The list of ids of users that reacted */
  private final List<Long> reacted;
  /** When did the inactivity check finish */
  private boolean finished;

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   * @param createdAt when was the inactivity check started
   * @param ticket the ticket that is inactive
   * @param reacted the list of users that reacted
   * @param finished is the inactive check finished
   */
  public InactiveCheckResponsiveMessage(
      @NotNull Message message, long createdAt, long ticket, List<Long> reacted, boolean finished) {
    super(ResponsiveMessageType.INACTIVE_CHECK, message, getReactions(reacted), true);
    this.createdAt = createdAt;
    this.ticket = ticket;
    this.reacted = reacted;
    this.finished = finished;
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   * @param createdAt when was the inactivity check started
   * @param ticket the ticket that is inactive
   * @param reacted the list of users that reacted
   * @param finished is the inactive check finished
   */
  public InactiveCheckResponsiveMessage(
      long id, long createdAt, long ticket, List<Long> reacted, boolean finished) {
    super(ResponsiveMessageType.INACTIVE_CHECK, id, getReactions(reacted), true);
    this.createdAt = createdAt;
    this.ticket = ticket;
    this.reacted = reacted;
    this.finished = finished;
  }

  /**
   * Get the reactions that will be used in this message
   *
   * @param reacted the list of users that reacted
   * @return the list of reactions
   */
  @NotNull
  private static List<ReactionResponse> getReactions(@NotNull List<Long> reacted) {
    return Lots.list(
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.WHITE_CHECK_MARK;
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            long id = event.getUser().getIdLong();
            if (!reacted.contains(id)) {
              reacted.add(id);
            }
          }
        });
  }

  /** Finish the inactive check */
  public void finish() {
    this.finished = true;
  }

  /**
   * Get when the inactive check started
   *
   * @return when the inactive check started in millis
   */
  public long getCreatedAt() {
    return createdAt;
  }

  /**
   * Get when the inactive check started
   *
   * @return when the inactive check started as {@link OffsetDateTime}
   */
  public OffsetDateTime getCreatedAtDate() {
    return TimeUtils.millisToOffsetDateTime(createdAt);
  }

  /**
   * Get the ticket that is inactive
   *
   * @return the id of the ticket that is inactive
   */
  public long getTicket() {
    return ticket;
  }

  /**
   * Get the list of ids of users that reacted
   *
   * @return the list of ids of users that reacted
   */
  public List<Long> getReacted() {
    return reacted;
  }

  /**
   * Get if the inactive check finished
   *
   * @return if the inactive check finished
   */
  public boolean isFinished() {
    return finished;
  }
}
