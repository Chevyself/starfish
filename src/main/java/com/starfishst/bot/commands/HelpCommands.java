package com.starfishst.bot.commands;

import com.starfishst.commands.ParentCommand;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedFactory;
import com.starfishst.commands.utils.message.MessagesFactory;
import com.starfishst.core.utils.Strings;
import com.starfishst.bot.Main;
import com.starfishst.bot.config.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;

/** Commands for helping the user */
public class HelpCommands {

  /**
   * The main command for helping
   *
   * @return the message with a list of commands
   */
  @Command(
      aliases = {"help", "commands"},
      description = "Get the list of commands")
  public Result help() {
    EmbedBuilder embedBuilder = EmbedFactory.getEmbedBuilder();
    Main.getCommandManager()
        .getCommands()
        .forEach(
            command -> {
              StringBuilder stringBuilder = Strings.getBuilder();
              String description;
              if (command instanceof ParentCommand) {
                stringBuilder.append(command.getDescription()).append(" | **Sub-Commands**: ");
                ((ParentCommand) command)
                    .getCommands()
                    .forEach(
                        subCommands -> stringBuilder.append(subCommands.getName()).append(", "));
                stringBuilder.setLength(stringBuilder.length() - 2);
                description = stringBuilder.toString();
              } else {
                description = command.getDescription();
              }
              embedBuilder.addField(
                  Configuration.getInstance().getPrefix() + command.getName(), description, false);
            });
    return new Result(
        ResultType.GENERIC,
        MessagesFactory.fromEmbed(embedBuilder.build()).getMessage(),
        null,
        null);
  }
}
