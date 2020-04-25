package com.starfishst.ethot.tickets;

import com.starfishst.core.utils.Validate;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.exception.TicketCreationException;
import com.starfishst.ethot.tickets.loader.TicketLoader;
import com.starfishst.ethot.tickets.type.Apply;
import com.starfishst.ethot.tickets.type.CheckOut;
import com.starfishst.ethot.tickets.type.Order;
import com.starfishst.ethot.tickets.type.Product;
import com.starfishst.ethot.tickets.type.QuestionsTicket;
import com.starfishst.ethot.tickets.type.Quote;
import com.starfishst.ethot.tickets.type.Support;
import com.starfishst.ethot.tickets.type.Ticket;
import com.starfishst.ethot.tickets.type.TicketCreator;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Tickets;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The ticket manager administrates everything related to tickets. From creating them to even give
 * them channels
 *
 * @author Chevy
 * @version 1.0.0
 */
public class TicketManager {

  @NotNull private final TicketLoader loader;

  public TicketManager(@NotNull TicketLoader loader) {
    this.loader = loader;
  }

  /**
   * Creates a ticket
   *
   * @param type the type of ticket to create
   * @param creator the user that created the ticket
   * @param parent if the ticket was created from another one
   * @return the ticket if everything goes well
   * @throws DiscordManipulationException in case anything with discord goes wrong
   */
  @NotNull
  public Ticket createTicket(
      @NotNull TicketType type, @NotNull Member creator, @Nullable Ticket parent)
      throws DiscordManipulationException, TicketCreationException {
    validateUser(creator);
    long id = getId(parent);

    TextChannel channel = getChannel(type, id, creator, parent);
    User user = getUser(creator, parent);

    Discord.allow(channel, creator, Discord.ALLOWED);

    Ticket ticket;
    switch (type) {
      case TICKET_CREATOR:
        ticket = new TicketCreator(id, user, channel);
        break;
      case APPLY:
        ticket = new Apply(id, user, channel);
        break;
      case ORDER:
        ticket = new Order(id, user, channel);
        break;
      case SUPPORT:
        ticket = new Support(id, user, channel);
        break;
      case PRODUCT:
        ticket = new Product(id, user, channel);
        break;
      case CHECK_OUT:
        ticket =
            new CheckOut(
                id,
                user,
                channel,
                Validate.notNull((Product) parent, "Parent cannot be null for check outs!"));
        break;
      case QUOTE:
        if (parent instanceof QuestionsTicket) {
          ticket = new Quote((Order) parent, channel);
          break;
        } else {
          throw new TicketCreationException("Quotes need to have a Orders parent");
        }
      default:
        throw new IllegalArgumentException(type + " is not a legal ticket type");
    }

    if (parent != null) {
      parent.unload();
    }
    ticket.onCreation();
    return ticket;
  }

  /**
   * Validates if the user can create the ticket
   *
   * @param creator the user trying to create the ticket
   * @throws TicketCreationException in case that the user cannot create more tickets
   */
  private void validateUser(@NotNull Member creator) throws TicketCreationException {
    List<Ticket> tickets =
        Tickets.getTicketsMatchingStatus(TicketStatus.OPEN, loader.getTickets(creator.getUser()));
    if (tickets.size() > Main.getConfiguration().getOpenTicketsByUserLimit() + 1) {
      HashMap<String, String> placeHolders = new HashMap<>();
      placeHolders.put(
          "limit", String.valueOf(Main.getConfiguration().getOpenTicketsByUserLimit()));
      placeHolders.put("have", String.valueOf(tickets.size()));
      throw new TicketCreationException(Lang.get("MORE_THAN_LIMIT", placeHolders), placeHolders);
    }
  }

  /**
   * Gets the user creating the ticket. In case that the ticket is being created by a parent it will
   * use that user if it is not null
   *
   * @param creator the person creating the ticket
   * @param parent the parent ticket
   * @return the user of the person creating the ticket
   */
  @NotNull
  private User getUser(@NotNull Member creator, @Nullable Ticket parent) {
    User user;
    if (parent != null && parent.getUser() != null) {
      user = parent.getUser();
    } else {
      user = creator.getUser();
    }
    return user;
  }

  /**
   * Get the channel of a ticket when it is being created
   *
   * @param type the type of ticket creating
   * @param id the id of the ticket
   * @param creator the future customer
   * @param parent the parent of the ticket if there is one
   * @return the channel if everything goes well
   * @throws DiscordManipulationException in case channel/category creating goes wrong
   */
  @NotNull
  public TextChannel getChannel(
      @NotNull TicketType type, long id, @NotNull Member creator, @Nullable Ticket parent)
      throws DiscordManipulationException {
    TextChannel channel;
    switch (type) {
      case PRODUCT:
      case TICKET_CREATOR:
      case CHECK_OUT:
        channel = getChannel(type, id, creator);
        break;
      case SUPPORT:
      case ORDER:
      case APPLY:
      case QUOTE:
        if (parent != null && parent.getChannel() != null) {
          channel = parent.getChannel();
        } else {
          channel = getChannel(type, id, creator);
        }
        break;
      default:
        throw new IllegalArgumentException(type + " is not a legal ticket type");
    }
    if (channel.getParent() != type.getCategory()) {
      channel.getManager().setParent(type.getCategory()).queue();
    }
    return channel;
  }

  /**
   * Easy method to create a channel when a ticket needs it
   *
   * @param type the type of ticket
   * @param id the id of the ticket
   * @param creator the person creating the ticket
   * @return the channel if everything goes well
   * @throws DiscordManipulationException In case anything related to discord goes wrong
   */
  private TextChannel getChannel(@NotNull TicketType type, long id, @NotNull Member creator)
      throws DiscordManipulationException {
    return type.getCategory()
        .createTextChannel(getTicketName(type, creator.getUser(), id, TicketStatus.CREATING))
        .complete();
  }

  /**
   * Gets the id of the ticket.
   *
   * <p>Gets the total of tickets from config and checks that there is not a ticket with that id
   * already else it will increase the total until it finds a free ticket id
   *
   * @return the ticket id for a ticket
   */
  private long getId(@Nullable Ticket parent) {
    if (parent != null && !(parent instanceof Product)) {
      return parent.getId();
    } else {
      long total = Main.getConfiguration().getTotal();
      Ticket ticket = loader.getTicket(total);
      while (ticket != null) {
        total++;
        ticket = loader.getTicket(total);
      }
      Main.getConfiguration().setTotal(total);
      return total;
    }
  }

  /**
   * The ticket loader is used for database purposes
   *
   * @return the ticket loader
   */
  @NotNull
  public TicketLoader getLoader() {
    return loader;
  }

  /**
   * Gets the name of a ticket. This is used for the channel of the ticket
   *
   * @param ticket the ticket to get the name from
   * @return the name built from the ticket
   */
  @NotNull
  public String getTicketName(@NotNull Ticket ticket) {
    return getTicketName(ticket.getType(), ticket.getUser(), ticket.getId(), ticket.getStatus());
  }

  /**
   * Gets a ticket name using some exact information
   *
   * @param type the type of ticket
   * @param customer the customer creating the ticket
   * @param id the id of the ticket
   * @param status the status of the ticket
   * @return the possible name of the ticket
   */
  @NotNull
  public String getTicketName(
      @NotNull TicketType type, @Nullable User customer, long id, @NotNull TicketStatus status) {
    return Lang.get(
        "TICKET_CHANNEL_NAME", Tickets.getPlaceholders(type, customer, id, status, null));
  }
}
