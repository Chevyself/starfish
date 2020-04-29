package com.starfishst.ethot.objects.responsive.type.verification;

import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.objects.responsive.ReactionResponse;
import com.starfishst.ethot.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Messages;
import com.starfishst.ethot.util.Unicode;
import com.starfishst.simple.Lots;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** Is a message that when a user clicks it it will give the user a role */
public class VerificationResponsiveMessage extends ResponsiveMessage {

  /**
   * The primary constructor
   *
   * @param message the message that this will be listening to
   */
  public VerificationResponsiveMessage(@NotNull Message message) {
    super(ResponsiveMessageType.JOIN_VERIFICATION, message, getReactions(), true);
  }

  /**
   * This constructor must be used to load it from databases as this one cannot add the reactions to
   * the message
   *
   * @param id the id of the message
   */
  public VerificationResponsiveMessage(long id) {
    super(ResponsiveMessageType.JOIN_VERIFICATION, id, getReactions(), true);
  }

  /**
   * Get the reactions that will be used in this message
   *
   * @return the list of reactions
   */
  public static List<ReactionResponse> getReactions() {
    return Lots.list(
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.getEmoji("UNICODE_JOIN_VERIFICATION");
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            try {
              DiscordConfiguration config = DiscordConfiguration.getInstance();
              Discord.addRoles(
                  event.getMember(),
                  config.getRolesByKeys(config.getRolesKeys("verificationRoles")));
            } catch (DiscordManipulationException e) {
              Messages.error(e.getMessage()).send(event.getChannel());
            }
          }
        });
  }
}
