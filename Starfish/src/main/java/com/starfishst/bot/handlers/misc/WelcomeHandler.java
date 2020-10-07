package com.starfishst.bot.handlers.misc;

import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.Starfish;
import com.starfishst.bot.handlers.StarfishHandler;
import com.starfishst.jda.result.ResultType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/** Listens for when a user joins the guild and sends a message greeting them */
public class WelcomeHandler implements StarfishHandler {

  /** The data loader to get the user of the joined user */
  @NotNull private final DataLoader loader;

  /**
   * Create the welcome handler
   *
   * @param loader the data loader to get the user joined
   */
  public WelcomeHandler(@NotNull DataLoader loader) {
    this.loader = loader;
  }

  /**
   * Send a welcome message to the welcome channel
   *
   * @param event the event of someone joining
   */
  @SubscribeEvent
  public void onMemberJoin(GuildMemberJoinEvent event) {
    BotUser user = this.loader.getStarfishUser(event.getMember().getIdLong());
    TextChannel welcome = Starfish.getDiscordConfiguration().getChannel("welcome");
    if (welcome != null && this.getPreferences().getValueOr("enabled", Boolean.class, true)) {
      Messages.build(
              user.getLocaleFile().get("welcome.title", user.getPlaceholders()),
              user.getLocaleFile().get("welcome.description", user.getPlaceholders()),
              ResultType.GENERIC,
              user)
          .send(welcome);
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "welcome";
  }
}
