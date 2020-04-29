package com.starfishst.ethot.listeners;

import com.starfishst.ethot.config.PunishmentsConfiguration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.util.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** When a user joins the guild they must validate that they are real by reacting to a message */
public class ModerationListener {

  /**
   * When a message is received it will check if the user who sent it is not muted
   *
   * @param event the event of the message received
   */
  @SubscribeEvent
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    if (PunishmentsConfiguration.getInstance().isMuted(event.getAuthor())) {
      event.getMessage().delete().queue();
      event
          .getAuthor()
          .openPrivateChannel()
          .queue(channel -> Messages.error(Lang.get("YOU_ARE_MUTED")).send(channel));
    }
  }
}
