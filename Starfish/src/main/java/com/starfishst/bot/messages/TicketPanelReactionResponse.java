package com.starfishst.bot.messages;

import com.starfishst.api.Starfish;
import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.jda.AnnotatedCommand;
import com.starfishst.jda.CommandManager;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.listener.CommandListener;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.utils.embeds.EmbedFactory;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The response for the ticket panel */
public class TicketPanelReactionResponse extends StarfishReactionResponse {

  public TicketPanelReactionResponse(@Nullable BotResponsiveMessage message) {
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
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return Starfish.getLanguageHandler().getFile("en").get("unicode.ticket-panel");
  }
}
