package com.starfishst.bot.listeners;

import com.starfishst.bot.oldconfig.DiscordConfiguration;
import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.exception.DiscordManipulationException;
import com.starfishst.bot.util.Messages;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** Listens for when a user joins the guild and sends a message greeting them */
public class WelcomeListener {

  /**
   * Send a welcome message to the welcome channel
   *
   * @param event the event of someone joining
   */
  @SubscribeEvent
  public void onMemberJoin(GuildMemberJoinEvent event) {
    try {
      TextChannel channel =
          DiscordConfiguration.getInstance().getChannelByKey(Lang.get("CHANNEL_NAME_WELCOME"));
      HashMap<String, String> placeholders = new HashMap<>();
      placeholders.put("username", event.getMember().getEffectiveName());
      placeholders.put("user", event.getMember().getAsMention());
      Messages.create(
              "WELCOME_MESSAGE_TITLE", "WELCOME_MESSAGE_DESCRIPTION", placeholders, placeholders)
          .send(channel);
    } catch (DiscordManipulationException ignored) {

    }
  }
}
