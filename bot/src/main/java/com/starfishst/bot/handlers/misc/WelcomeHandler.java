package com.starfishst.bot.handlers.misc;

import com.starfishst.api.Starfish;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.loader.Loader;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Messages;
import lombok.NonNull;
import me.googas.commands.jda.result.ResultType;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** Listens for when a user joins the guild and sends a message greeting them */
public class WelcomeHandler implements StarfishHandler {

  /** The data loader to get the user of the joined user */
  @NonNull private final Loader loader;

  /**
   * Create the welcome handler
   *
   * @param loader the data loader to get the user joined
   */
  public WelcomeHandler(@NonNull Loader loader) {
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
    if (!this.getPreferences().getOr("enabled", Boolean.class, true)) return;
    Starfish.getDiscordConfiguration()
        .getChannelOrCreate("welcome")
        .ifPresent(
            welcome -> {
              welcome
                  .sendMessage(
                      Messages.build(
                              user.getLocaleFile().get("welcome.title", user.getPlaceholders()),
                              user.getLocaleFile()
                                  .get("welcome.description", user.getPlaceholders()),
                              ResultType.GENERIC,
                              user)
                          .build())
                  .queue();
            });
  }

  @Override
  public void onUnload() {}

  @Override
  public String getName() {
    return "welcome";
  }
}
