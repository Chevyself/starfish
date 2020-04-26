package com.starfishst.ethot.listeners;

import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.util.Messages;
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
      placeholders.put("user", event.getMember().getAsMention());
      Messages.create(
              "WELCOME_MESSAGE_TITLE", "WELCOME_MESSAGE_DESCRIPTION", placeholders, placeholders)
          .send(channel);
    } catch (DiscordManipulationException ignored) {

    }
  }
}
