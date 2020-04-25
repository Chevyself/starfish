package com.starfishst.ethot.tickets.type;

import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.tickets.TicketStatus;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.ethot.tickets.transcript.TicketTranscript;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Tickets;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is the parent of all tickets. It all starts here, methods can be overwritten but this may
 * control all of them
 *
 * @author Chevy
 * @version 1.0.0
 */
public abstract class Ticket extends Catchable {

  protected final long id;
  @Nullable protected final User user;
  @Nullable protected TextChannel channel;
  @NotNull private TicketStatus status;

  protected Ticket(
      long id, @Nullable User user, @NotNull TicketStatus status, @Nullable TextChannel channel) {
    super(Main.getConfiguration().getToDelete());
    this.id = id;
    this.user = user;
    this.status = status;
    this.channel = channel;
  }

  /** When the ticket is created this will be executed */
  public abstract void onCreation();

  /** On done will be run when the ticket is finished creating */
  public abstract void onDone();

  /** Saves the ticket into the database */
  private void save() {
    if (channel == null && status == TicketStatus.OPEN) {
      status = TicketStatus.CLOSED;
    }
    Main.getManager().getLoader().saveTicket(this);
  }

  /**
   * Sets the channel of the ticket
   *
   * @param channel the new channel to set in the ticket
   */
  public void setChannel(@Nullable TextChannel channel) {
    this.channel = channel;
  }

  /**
   * Sets the status of a ticket
   *
   * @param status the new status to set
   */
  public void setStatus(@NotNull TicketStatus status) {
    this.status = status;
    if (status == TicketStatus.CLOSED) {
      if (channel != null) {
        Messages.create("TICKET_CLOSING_TITLE", "TICKET_CLOSING_DESCRIPTION", null, null)
            .send(channel);
        channel.delete().queueAfter(15, TimeUnit.SECONDS);
        channel = null;
        save();
      }
    } else if (status == TicketStatus.ARCHIVED) {
      archive();
    }
    if (channel != null) {
      channel.getManager().setName(Main.getManager().getTicketName(this)).queue();
    }
  }

  protected void archive() {
    refresh();
    if (channel != null) {
      try {
        String name = Lang.get("CATEGORY_NAME_ARCHIVED");
        DiscordConfiguration config = Main.getDiscordConfiguration();
        List<Role> allowed = config.getRolesByKeys(config.getRoleKeys("allowedInCategories"));
        List<Role> allowedSee =
            config.getRolesByKeys(config.getRoleKeys("allowedToSeeInCategories"));
        Category category = config.getCategoryByKey(name);
        if (category == null || category.getChannels().size() >= 50) {
          category = Discord.validateCategory(category, name, true, allowed, allowedSee);
          config.setCategoryByKey(name, category);
        }
        if (channel.getParent() != category) {
          channel.getManager().setParent(category).queue();
        }
      } catch (DiscordManipulationException e) {
        Errors.addError(e.getMessage());
        Messages.error(e.getMessage());
      }
      save();
    }
    if (this.user != null) {
      this.user.openPrivateChannel().queue(this::sendTranscript);
    }
  }

  /**
   * Sends the transcript txt file to the channel
   *
   * @param channel the channel to send the transcript to
   */
  protected void sendTranscript(@NotNull PrivateChannel channel) {
    try {
      HashMap<String, String> placeholders = Tickets.getPlaceholders(this);
      Messages.create(
              "TRANSCRIPT_SEND_TITLE", "TRANSCRIPT_SEND_DESCRIPTION", placeholders, placeholders)
          .send(channel);
      channel.sendFile(getTranscript().getFile()).queue();
    } catch (IOException e) {
      e.printStackTrace();
      Errors.addError(e.getMessage());
      Messages.error("Transcript could not be send. Check errors").send(channel);
    }
  }

  /**
   * Get the status of the ticket
   *
   * @return the status of the ticket
   */
  @NotNull
  public TicketStatus getStatus() {
    return status;
  }

  /**
   * Get the channel of the ticket
   *
   * @return the channel of the ticket or null if it does not have one
   */
  @Nullable
  public TextChannel getChannel() {
    return channel;
  }

  /**
   * Get the id of the ticket
   *
   * @return the id of the ticket
   */
  public long getId() {
    return id;
  }

  /**
   * Get the customer that created the ticket
   *
   * @return the customer that created the ticket
   */
  @Nullable
  public User getUser() {
    return user;
  }

  /**
   * Get the customer as a member
   *
   * @return the discord member
   */
  @NotNull
  public Member getMember() throws DiscordManipulationException {
    Member member = Discord.getMember(user);
    if (member == null) {
      throw new DiscordManipulationException(
          "Could not get a member. Is guild set? "
              + (Main.getDiscordConfiguration().hasGuild())
              + " or is the customer null?"
              + (user == null));
    } else {
      return member;
    }
  }

  /**
   * Get the ticket as a document for mongo
   *
   * @return the document
   */
  @NotNull
  public Document getDocument() {
    Document document = new Document();
    document.append("id", id);
    if (user != null) document.append("user", user);
    document.append("status", status);
    document.append("type", getType());
    if (channel != null) document.append("channel", channel);
    return document;
  }

  /**
   * Get the type of the ticket
   *
   * @return the type of the ticket
   */
  @NotNull
  public TicketType getType() {
    return TicketType.TICKET;
  }

  @Override
  public @NotNull Ticket refresh() {
    return (Ticket) super.refresh();
  }

  @Override
  public void onSecondsPassed() {
    Main.getConfiguration()
        .getToAnnounce()
        .forEach(
            time -> {
              if (status == TicketStatus.CREATING) {
                if (channel != null) {
                  if (time.millis() == getTimeLeft().millis()) {
                    HashMap<String, String> placeHolders = new HashMap<>();
                    placeHolders.put("time", getTimeLeft().toEffectiveString());
                    Messages.create(
                            "TICKET_CLOSING_IN_TITLE",
                            "TICKET_CLOSING_IN_DESCRIPTION",
                            placeHolders,
                            placeHolders)
                        .send(channel);
                  }
                }
              }
            });
  }

  @NotNull
  public TicketTranscript getTranscript() throws IOException {
    TicketTranscript ticketTranscript =
        (TicketTranscript)
            Cache.getCache().stream()
                .filter(
                    catchable ->
                        catchable instanceof TicketTranscript
                            && ((TicketTranscript) catchable).getTicket().getId() == this.id)
                .findFirst()
                .orElse(null);
    if (ticketTranscript == null) {
      ticketTranscript = new TicketTranscript(this);
    }
    return ticketTranscript;
  }

  @Override
  public void onRemove() {
    if (status == TicketStatus.CREATING) {
      setStatus(TicketStatus.CLOSED);
    } else {
      save();
    }
  }

  @Override
  public String toString() {
    return "Ticket{"
        + "id="
        + id
        + ", customer="
        + user
        + ", status="
        + status
        + ", channel="
        + channel
        + ", type="
        + getType()
        + '}';
  }
}
