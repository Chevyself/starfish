package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.messages.StarfishReactionResponse;
import java.util.Objects;
import lombok.NonNull;
import me.googas.commands.jda.CommandManager;
import me.googas.commands.jda.JdaCommand;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.listener.CommandListener;
import me.googas.commands.jda.result.Result;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The response for the ticket panel */
public class TicketPanelReactionResponse extends StarfishReactionResponse {

  public TicketPanelReactionResponse(BotResponsiveMessage message) {
    super(message);
  }

  @Override
  public @NonNull String getType() {
    return "ticket-panel";
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (event.getUser() == null) return true;
    CommandManager manager = Starfish.getCommandManager();
    CommandListener listener = manager.getListener();
    JdaCommand command =
        Objects.requireNonNull(
            manager.getCommand("new"), "Ticket Panel: 'new' command was not registered!");
    Result result =
        command.execute(
            new CommandContext(
                event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete(),
                event.getUser(),
                new String[] {"-new"},
                event.getTextChannel(),
                manager.getMessagesProvider(),
                manager.getProvidersRegistry(),
                "new"));
    // TODO Fix
    // EmbedFactory.fromResult(result, listener, null)
    //  .send(event.getTextChannel(), listener.getConsumer(result));
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return Starfish.getLanguageHandler().getFile("en").get("unicode.ticket-panel");
  }
}
