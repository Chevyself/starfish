package com.starfishst.bot.data.messages.panel;

import com.starfishst.bot.Starfish;
import com.starfishst.jda.AnnotatedCommand;
import com.starfishst.jda.CommandManager;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.listener.CommandListener;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.utils.embeds.EmbedFactory;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The response for the ticket panel */
class TicketPanelReactionResponse implements ReactionResponse {

  @Override
  public boolean removeReaction() {
    return true;
  }

  @Override
  public void onReaction(@NotNull MessageReactionAddEvent event) {
    if (event.getUser() == null) return;
    CommandManager manager = Starfish.getManager();
    CommandListener listener = manager.getListener();
    AnnotatedCommand command =
        Validate.notNull(
            manager.getCommand("new"), "Ticket Panel: 'new' command was not registered!");
    Result result =
        command.execute(
            new CommandContext(
                event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete(),
                event.getUser(),
                new String[] {"-new"},
                event.getTextChannel(),
                manager.getMessagesProvider(),
                manager.getRegistry(),
                "new"));
    EmbedFactory.fromResult(result, listener, null)
        .send(event.getTextChannel(), listener.getConsumer(result));
  }

  @Override
  public @NotNull String getUnicode() {
    return Starfish.getLanguageHandler().getFile("en").get("unicode.ticket-panel");
  }
}
