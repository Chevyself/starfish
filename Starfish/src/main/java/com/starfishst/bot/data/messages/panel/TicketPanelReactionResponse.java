package com.starfishst.bot.data.messages.panel;

import com.starfishst.bot.Starfish;
import com.starfishst.commands.AnnotatedCommand;
import com.starfishst.commands.CommandManager;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.listener.CommandListener;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.utils.embeds.EmbedFactory;
import com.starfishst.commands.utils.responsive.ReactionResponse;
import com.starfishst.core.utils.Validate;
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
