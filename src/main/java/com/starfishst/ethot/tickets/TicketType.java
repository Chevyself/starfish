package com.starfishst.ethot.tickets;

import com.starfishst.core.utils.Errors;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.util.Discord;
import java.util.List;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public enum TicketType {
  ORDER(Lang.get("CATEGORY_NAME_ORDERS"), Lang.get("CHANNEL_NAME_ORDERS")),
  APPLY(Lang.get("CATEGORY_NAME_APPLIES"), "none"),
  SUPPORT(Lang.get("CATEGORY_NAME_SUPPORT"), "none"),
  TICKET("ticket", "none"),
  TICKET_CREATOR(Lang.get("CATEGORY_NAME_TICKET_CREATOR"), "none"),
  QUOTE(Lang.get("CATEGORY_NAME_QUOTES"), Lang.get("CHANNEL_NAME_QUOTES")),
  PRODUCT(Lang.get("CATEGORY_NAME_PRODUCTS"), Lang.get("CHANNEL_NAME_PRODUCTS")),
  CHECK_OUT(Lang.get("CATEGORY_NAME_CHECK_OUT"), "none");

  @NotNull private final String categoryName;
  @NotNull private final String channelName;

  TicketType(@NotNull String categoryName, @NotNull String channelName) {
    this.categoryName = categoryName;
    this.channelName = channelName;
  }

  @NotNull
  public static TicketType fromDocument(@NotNull Document document) {
    try {
      return TicketType.valueOf(document.getString("type"));
    } catch (NullPointerException | IllegalArgumentException e) {
      Errors.addError(e.getMessage());
      throw e;
    }
  }

  public static void main(String[] args) {
    System.out.println(TicketType.valueOf("ORDER"));
    System.out.println(TicketType.valueOf("APPLY"));
    System.out.println(TicketType.valueOf(null));
  }

  @NotNull
  public Category getCategory() throws DiscordManipulationException {
    DiscordConfiguration config = Main.getDiscordConfiguration();

    List<Role> allowed = config.getRolesByKeys(config.getRoleKeys("allowedInCategories"));
    List<Role> allowedSee =
        config.getRolesByKeys(config.getRoleKeys("allowedToSeeInCategories"));

    Category category = config.getCategory(this);
    if (category == null || category.getChannels().size() >= 50) {
      category = Discord.validateCategory(category, categoryName, true, allowed, allowedSee);
      config.setCategory(this, category);
    }

    return category;
  }

  @NotNull
  public TextChannel getChannel() throws DiscordManipulationException {
    DiscordConfiguration config = Main.getDiscordConfiguration();
    TextChannel channel = config.getChannel(this);
    if (channel == null) {
      List<Role> allowed = config.getRolesByKeys(config.getRoleKeys("allowedInCategories"));
      List<Role> allowedSee =
          config.getRolesByKeys(config.getRoleKeys("allowedToSeeInCategories"));
      boolean removePerms = this != TicketType.PRODUCT;

      channel = Discord.validateChannel(channel, channelName, removePerms, allowed, allowedSee);
      config.setChannel(this, channel);
    }
    return channel;
  }

  @NotNull
  public String getCategoryName() {
    return categoryName;
  }

  @NotNull
  public String getChannelName() {
    return channelName;
  }
}
