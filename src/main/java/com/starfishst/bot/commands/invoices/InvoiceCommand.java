package com.starfishst.bot.commands.invoices;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.objects.invoicing.Fee;
import com.starfishst.bot.objects.management.AllowedTicketManagerChecker;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.SimpleMath;
import com.starfishst.bot.util.Tickets;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Exclude;
import com.starfishst.commands.annotations.Required;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedQuery;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.core.utils.Strings;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

/** The normal command for invoices that uses PayPal.me */
public class InvoiceCommand {

  /**
   * Add the applying fees to the embed builder
   *
   * @param applyingFees the applying fees
   * @param builder the builder to add the fees to
   */
  static void appendApplyingFees(List<Fee> applyingFees, EmbedBuilder builder) {
    applyingFees.forEach(
        fee -> {
          StringBuilder stringBuilder = Strings.getBuilder();
          HashMap<String, String> placeholders = new HashMap<>();
          placeholders.put("addition", SimpleMath.twoDecimalsFormat(fee.getAddition()));
          placeholders.put("percentage", SimpleMath.twoDecimalsFormat(fee.getPercentage()));
          System.out.println(fee.getAddition());
          if (fee.getAddition() != 0 && fee.getPercentage() != 0) {
            stringBuilder.append(Lang.get("FEE_ADDITION_AND_PERCENTAGE", placeholders));
          } else if (fee.getAddition() != 0) {
            stringBuilder.append(Lang.get("FEE_ADDITION", placeholders));
          } else if (fee.getPercentage() != 0) {
            stringBuilder.append(Lang.get("FEE_PERCENTAGE", placeholders));
          }
          builder.addField(fee.getDescription(), stringBuilder.toString(), false);
        });
  }

  /**
   * Creates an invoice embed
   *
   * @param checker the check to see if the checker is allowed to create invoices
   * @param channel the channel to generate the invoice
   * @param subtotal the subtotal of the service
   * @param strings the service
   * @return a successful result sending the invoice
   */
  @Exclude
  @Command(aliases = "invoice", description = "Generates an invoice")
  public Result invoice(
      AllowedTicketManagerChecker checker,
      TextChannel channel,
      @Required(name = "subtotal", description = "The subtotal of the service") double subtotal,
      @Required(name = "service", description = "The service applying") JoinedStrings strings) {
    List<Fee> applyingFees = Configuration.getInstance().getApplyingFees(subtotal);
    double total = SimpleMath.getTotal(subtotal, applyingFees);
    Ticket ticket = TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put("subtotal", String.format("%.02f", subtotal));
    placeholders.put("total", String.format("%.02f", total));
    placeholders.put("service", strings.getString());
    if (ticket != null) {
      placeholders.putAll(Tickets.getPlaceholders(ticket));
    }
    EmbedBuilder builder =
        Messages.create("INVOICE_TITLE", "INVOICE_DESCRIPTION", placeholders, placeholders)
            .getEmbedBuilder();
    appendApplyingFees(applyingFees, builder);
    return new Result(ResultType.GENERIC, new EmbedQuery(builder));
  }
}
