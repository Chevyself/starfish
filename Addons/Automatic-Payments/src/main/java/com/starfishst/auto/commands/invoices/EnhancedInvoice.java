package com.starfishst.auto.commands.invoices;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;
import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Exclude;
import com.starfishst.jda.result.Result;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;

/** The command for enhanced invoices */
public class EnhancedInvoice {

  /**
   * Creates an invoice embed
   *
   * @param checker the check to see if the checker is allowed to create invoices
   * @param channel the channel to generate the invoice
   * @param subtotal the subtotal of the service
   * @param service the service
   * @return a successful result sending the invoice
   * @throws PayPalRESTException if PayPal could not be connected
   */
  @Exclude
  @Command(aliases = "invoice", description = "Generates an invoice")
  public Result invoice(
      AllowedTicketManagerChecker checker,
      TextChannel channel,
      @Required(name = "subtotal", description = "The subtotal of the service") double subtotal,
      @Required(name = "service", description = "The service applying") @Multiple
          JoinedStrings service)
      throws PayPalRESTException {
    Ticket ticket = TicketManager.getInstance().getLoader().getTicketByChannel(channel.getIdLong());
    if (ticket != null) {
      String formattedSubtotal = String.format("%.02f", subtotal);
      List<Fee> applyingFees = Configuration.getInstance().getApplyingFees(subtotal);
      List<Transaction> transactions =
          PayPalUtils.getTransactions(subtotal, service.getString(), applyingFees);
      String total = SimpleMath.getTotalFormatted(subtotal, applyingFees);
      Payment payment = Payments.createPayment(total, transactions);
      ticket.getPayments().add(payment.getId());
      String approvalUrl = PayPalUtils.getUrl(payment, "approval_url");
      HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
      placeholders.put("link", approvalUrl);
      placeholders.put("subtotal", formattedSubtotal);
      placeholders.put("total", total);
      placeholders.put("service", service.getString());
      EmbedBuilder builder =
          OldMessages.create(
                  "ENHANCED_INVOICE_TITLE",
                  "ENHANCED_INVOICE_DESCRIPTION",
                  placeholders,
                  placeholders)
              .getEmbedBuilder();
      InvoiceCommand.appendApplyingFees(applyingFees, builder);
      return new Result(new EmbedQuery(builder));
    } else {
      return new Result(ResultType.ERROR, Lang.get("TICKET_NOT_CHANNEL"));
    }
  }
}
