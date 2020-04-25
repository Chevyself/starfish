package com.starfishst.ethot.commands;

import com.starfishst.commands.ParentCommand;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedFactory;
import com.starfishst.commands.utils.message.MessagesFactory;
import com.starfishst.core.utils.Strings;
import com.starfishst.ethot.Main;
import net.dv8tion.jda.api.EmbedBuilder;

public class HelpCommands {

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
                stringBuilder.append(command.getDescription()).append(" | **Subcommands**: ");
                ((ParentCommand) command)
                    .getCommands()
                    .forEach(subcommand -> stringBuilder.append(subcommand.getName()).append(", "));
                stringBuilder.setLength(stringBuilder.length() - 2);
                description = stringBuilder.toString();
              } else {
                description = command.getDescription();
              }
              embedBuilder.addField(
                  Main.getConfiguration().getPrefix() + command.getName(), description, false);
            });
    return new Result(
        ResultType.GENERIC,
        MessagesFactory.fromEmbed(embedBuilder.build()).getMessage(),
        null,
        null);
  }
}
