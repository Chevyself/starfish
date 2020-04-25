package com.starfishst.ethot.listeners;

import com.starfishst.core.utils.Atomic;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.tickets.type.Ticket;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class ConfigurationListener {

  @SubscribeEvent
  public void onGuildTextChannelDelete(TextChannelDeleteEvent event) {
    Atomic<String> toRemove = new Atomic<>("");
    HashMap<String, TextChannel> channels = Main.getDiscordConfiguration().getChannels();
    channels.forEach(
        (key, channel) -> {
          if (event.getChannel().getIdLong() == channel.getIdLong()) {
            toRemove.set(key);
          }
        });
    channels.remove(toRemove.get());

    Ticket ticket =
        Main.getManager().getLoader().getTicketByChannel(event.getChannel().getIdLong());
    if (ticket != null) {
      ticket.setChannel(null);
    }
  }

  @SubscribeEvent
  public void onCategoryDelete(CategoryDeleteEvent event) {
    Atomic<String> toRemove = new Atomic<>("");
    HashMap<String, Category> categories = Main.getDiscordConfiguration().getCategories();
    categories.forEach(
        (key, category) -> {
          if (event.getCategory().getIdLong() == category.getIdLong()) {
            toRemove.set(key);
          }
        });
    categories.remove(toRemove.get());
  }

  @SubscribeEvent
  public void onGuildTextChannelDelete(RoleDeleteEvent event) {
    HashMap<String, List<Role>> rolesMap = Main.getDiscordConfiguration().getRoles();
    rolesMap.forEach((key, roles) -> roles.removeIf(role -> role == event.getRole()));
  }

  @SubscribeEvent
  public void onMessageDeleteEvent(GuildMessageDeleteEvent event) {
    Main.getConfiguration()
        .getResponsiveMessages()
        .removeIf(message -> message != null && message.getId() == event.getMessageIdLong());
  }
}
