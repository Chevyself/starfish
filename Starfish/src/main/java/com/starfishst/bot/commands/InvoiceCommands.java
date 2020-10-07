package com.starfishst.bot.commands;

import com.starfishst.api.Fee;
import com.starfishst.api.data.user.BotUser;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.api.utility.Messages;
import com.starfishst.bot.Starfish;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Exclude;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.HashMap;
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
  @Exclude
  @Command(
      aliases = "invoice",
      description = "Generates an invoice",
      permission = @Perm(node = "starfish.invoice"))
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
    EmbedQuery query =
        Messages.build(
            locale.get("invoice.title", placeholders),
            locale.get("invoice.description", placeholders),
            ResultType.GENERIC,
            user);
    for (Fee fee : applyingFees) {
      query
          .getEmbedBuilder()
          .addField(
              fee.getDescription(), "+" + String.format("%.2f", fee.getApply(subtotal)), false);
    }
    return new Result(query);
  }
}
