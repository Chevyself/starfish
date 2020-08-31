package com.starfishst.bot.tickets.loader.json;

import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.tickets.loader.TicketLoader;
import com.starfishst.bot.tickets.type.Order;
import com.starfishst.bot.tickets.type.Product;
import com.starfishst.bot.tickets.type.Quote;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.simple.gson.GsonProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Loads tickets from json files */
public class GsonTicketLoader implements TicketLoader {

  @NotNull
  private final String path =
      CoreFiles.currentDirectory() + File.separator + "tickets" + File.separator;

  /**
   * Gets a ticket from the stored json files
   *
   * @param id of the ticket
   * @return the ticket if the file is found
   */
  @Nullable
  private Ticket getTicketFromDatabase(long id) {
    try {
      Reader reader = CoreFiles.getReader(getTicketFile(id));
      Ticket ticket = GsonProvider.GSON.fromJson(reader, Ticket.class);
      reader.close();
      return ticket;
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Gets the file of a ticket
   *
   * @param id the id of the ticket
   * @return the file
   * @throws FileNotFoundException if the file does not exist
   */
  @NotNull
  private File getTicketFile(long id) throws IOException {
    File file = new File(path);
    File[] files = file.listFiles();
    if (file.isDirectory() && files != null) {
      for (File ticketFile : files) {
        if (ticketFile.getName().contains(String.valueOf(id))) {
          return ticketFile;
        }
      }
    } else if (!file.exists()) {
      if (!file.mkdir()) {
        throw new IOException(path + " could not be created!");
      }
    }
    throw new IOException("This should not be able to be reached");
  }

  /**
   * Gets a ticket from
   *
   * @param id
   * @return
   */
  @Nullable
  private Ticket getTicketByChannelFromDatabase(long id) {
    return null;
  }

  @Override
  public @Nullable Ticket getTicket(long id) {
    return (Ticket)
        Cache.getCache().stream()
            .filter(catchable -> catchable instanceof Ticket && ((Ticket) catchable).getId() == id)
            .findFirst()
            .orElseGet(() -> getTicketFromDatabase(id));
  }

  @Override
  public void saveTicket(@NotNull Ticket ticket) {
    try {
      GsonProvider.save(getTicketFile(ticket.getId()), ticket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public @Nullable Ticket getTicketByChannel(long id) {
    return (Ticket)
        Cache.getCache().stream()
            .filter(
                catchable -> {
                  if (catchable instanceof Ticket) {
                    TextChannel channel = ((Ticket) catchable).getChannel();
                    if (channel != null) {
                      return channel.getIdLong() == id;
                    }
                  }
                  return false;
                })
            .findFirst()
            .orElseGet(() -> getTicketByChannelFromDatabase(id));
  }

  @Override
  public void saveFreelancer(Freelancer freelancer) {}

  @Override
  public @NotNull List<Ticket> getTickets(@NotNull User user) {
    return null;
  }

  @Override
  public @Nullable Order getOrderByMessage(long id) {
    return null;
  }

  @Override
  public @Nullable Freelancer getFreelancer(long id) {
    return null;
  }

  @Override
  public @Nullable Quote getQuoteByOfferMessage(long id) {
    return null;
  }

  @Override
  public void close() {}

  @Override
  public @Nullable Product getProductByMessage(long id) {
    return null;
  }

  @Override
  public void demoteFreelancer(Freelancer freelancer) {}

  @Override
  public @Nullable Ticket getTicketByPayment(String id) {
    return null;
  }
}
