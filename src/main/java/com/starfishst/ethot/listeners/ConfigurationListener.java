package com.starfishst.ethot.listeners;

import com.starfishst.core.utils.Atomic;
import com.starfishst.ethot.config.Configuration;
import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.tickets.type.Ticket;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.HashMap;
import java.util.List;

/**
 * It can happen that when trying to use a channel, category, etc. In the program can be a non null
 * but it could be deleted in discord, this listens to deletion events to avoid that happening
 */
public class ConfigurationListener {

  /**
   * In case of a channel deletion
   *
   * @param event the event of channel deletions
   */
  @SubscribeEvent
  public void onTextChannelDelete(TextChannelDeleteEvent event) {
    Atomic<String> toRemove = new Atomic<>("");
    HashMap<String, TextChannel> channels = DiscordConfiguration.getInstance().getChannels();
    channels.forEach(
        (key, channel) -> {
          if (event.getChannel().getIdLong() == channel.getIdLong()) {
            toRemove.set(key);
          }
        });
    channels.remove(toRemove.get());

    Ticket ticket =
        TicketManager.getInstance().getLoader().getTicketByChannel(event.getChannel().getIdLong());
    if (ticket != null) {
      ticket.setChannel(null);
    }
  }

  /**
   * In case of a category deletion
   *
   * @param event the event of category deletion
   */
  @SubscribeEvent
  public void onCategoryDelete(CategoryDeleteEvent event) {
    Atomic<String> toRemove = new Atomic<>("");
    HashMap<String, Category> categories = DiscordConfiguration.getInstance().getCategories();
    categories.forEach(
        (key, category) -> {
          if (event.getCategory().getIdLong() == category.getIdLong()) {
            toRemove.set(key);
          }
        });
    categories.remove(toRemove.get());
  }

  /**
   * In case of a category deletion
   *
   * @param event the event of category deletion
   */
  @SubscribeEvent
  public void onRoleDelete(RoleDeleteEvent event) {
    HashMap<String, List<Role>> rolesMap = DiscordConfiguration.getInstance().getRoles();
    rolesMap.forEach((key, roles) -> roles.removeIf(role -> role == event.getRole()));
  }

  /**
   * In case of a message deletion
   *
   * @param event the event of message deletion
   */
  @SubscribeEvent
  public void onMessageDeleteEvent(GuildMessageDeleteEvent event) {
    Configuration.getInstance()
        .getResponsiveMessages()
        .removeIf(message -> message != null && message.getId() == event.getMessageIdLong());
  }
}
