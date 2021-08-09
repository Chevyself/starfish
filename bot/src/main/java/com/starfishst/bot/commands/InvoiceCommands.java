package com.starfishst.bot.commands;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.user.BotUser;
import com.starfishst.api.utility.Fee;
import com.starfishst.api.utility.Messages;
import java.util.Collection;
import java.util.HashMap;
import me.googas.commands.annotations.Multiple;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import me.googas.commands.objects.JoinedStrings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands for invoicing */
public class InvoiceCommands {

  /**
   * Creates an invoice embed
   *
   * @param user the user creating the invoice
   * @param channel the channel to generate the invoice
   * @param subtotal the subtotal of the service
   * @param strings the service
   * @return a successful result sending the invoice
   */
  @Command(
      aliases = "invoice",
      description = "Generates an invoice",
      node = "starfish.invoice",
      excluded = true)
  public Result invoice(
      BotUser user,
      TextChannel channel,
      @Required(name = "subtotal", description = "The subtotal of the service") double subtotal,
      @Required(name = "service", description = "The service applying") @Multiple
          JoinedStrings strings) {
    Collection<Fee> applyingFees = Starfish.getConfiguration().getFees(subtotal);
    double total = subtotal;
    for (Fee fee : applyingFees) {
      total += fee.getApply(subtotal);
    }
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("subtotal", String.format("%.2f", subtotal));
    placeholders.put("total", String.format("%.2f", total));
    placeholders.put("service", strings.getString());
    LocaleFile locale = user.getLocaleFile();
    EmbedBuilder embedBuilder =
        Messages.build(
            locale.get("invoice.title", placeholders),
            locale.get("invoice.description", placeholders),
            ResultType.GENERIC,
            user);
    for (Fee fee : applyingFees) {
      embedBuilder.addField(
          fee.getDescription(), "+" + String.format("%.2f", fee.getApply(subtotal)), false);
    }
    return Result.builder().setMessage(new MessageBuilder(embedBuilder)).build();
  }
}
