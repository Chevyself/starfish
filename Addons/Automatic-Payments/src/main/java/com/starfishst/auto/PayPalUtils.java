package com.starfishst.auto;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.starfishst.bot.commands.invoices.Fee;
import com.starfishst.bot.util.SimpleMath;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Utilities for PayPal */
public class PayPalUtils {

  /**
   * Create a payment
   *
   * @param total the total of the payment
   * @param transactions the transactions of the payment
   * @param note the not for the payer
   * @param cancelUrl the url to cancel the payment
   * @param returnUrl the url to accept the payment
   * @param context the context api to create the payment
   * @return the created payment
   * @throws PayPalRESTException in case the api context could not be connected
   */
  @NotNull
  public static Payment createPayment(
      @NotNull String total,
      @NotNull List<Transaction> transactions,
      @NotNull String note,
      @NotNull String cancelUrl,
      @NotNull String returnUrl,
      @NotNull APIContext context)
      throws PayPalRESTException {
    Amount amount = new Amount();
    amount.setCurrency("USD");
    amount.setTotal(total);

    Payer payer = new Payer();
    payer.setPaymentMethod("PAYPAL");

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl(cancelUrl);
    redirectUrls.setReturnUrl(returnUrl);

    Payment payment = new Payment();
    payment.setIntent("sale");
    payment.setPayer(payer);
    payment.setTransactions(transactions);
    payment.setRedirectUrls(redirectUrls);
    payment.setNoteToPayer(note);
    return payment.create(context);
  }

  /**
   * Get a payment using the payment id
   *
   * @param context the api context to connect with PayPal
   * @param paymentId the id of the payment
   * @return the payment if found else null
   * @throws PayPalRESTException in case the context could not connect with PayPal
   */
  @Nullable
  public static Payment getPayment(@NotNull APIContext context, @NotNull String paymentId)
      throws PayPalRESTException {
    return Payment.get(context, paymentId);
  }

  /**
   * Get the api context
   *
   * @param clientId the client id
   * @param clientSecret the client secret
   * @param mode the mode of the context
   * @return the context
   */
  @NotNull
  public static APIContext getApiContext(
      @NotNull String clientId, @NotNull String clientSecret, @NotNull String mode) {
    return new APIContext(clientId, clientSecret, mode);
  }

  /**
   * Get some url of a payment.
   *
   * <p>You can get the url:
   *
   * <p>approval_url
   *
   * @param payment the payment to get the url from
   * @param url the rul to get
   * @return the url if found else
   */
  @NotNull
  public static String getUrl(@NotNull Payment payment, @NotNull String url) {
    Links link =
        payment.getLinks().stream()
            .filter(loadedLink -> loadedLink.getRel().equalsIgnoreCase("approval_url"))
            .findFirst()
            .orElse(null);
    if (link != null) {
      return link.getHref();
    } else {
      throw new IllegalArgumentException(url + " wasn't found for the payment " + payment.getId());
    }
  }

  /**
   * Get the transaction using a subtotal, a service and its applying fees
   *
   * @param subtotal the subtotal
   * @param service the service
   * @param applyingFees the applying fees
   * @return the list of transactions
   */
  public static List<Transaction> getTransactions(
      double subtotal, @NotNull String service, @NotNull List<Fee> applyingFees) {
    List<Transaction> transactions = new ArrayList<>();
    transactions.add(getTotalTransaction(subtotal, applyingFees, service));
    // currently only one transaction is supported
    // transactions.add(getTransactionFromService(subtotal, service));
    // applyingFees.forEach(fee -> transactions.add(getTransactionFromFee(subtotal, fee)));
    return transactions;
  }

  /**
   * Get the transaction for the total of the payment
   *
   * @param subtotal the subtotal of the payment
   * @param applyingFees the fees of the payment
   * @param service the service also the description of the transaction
   * @return the total
   */
  private static Transaction getTotalTransaction(
      double subtotal, @NotNull List<Fee> applyingFees, @NotNull String service) {
    Transaction transaction = new Transaction();
    transaction.setAmount(new Amount("USD", SimpleMath.getTotalFormatted(subtotal, applyingFees)));
    transaction.setDescription(service);
    return transaction;
  }

  /**
   * Get the transaction of certain fee
   *
   * @param subtotal the amount that is being added to the fee
   * @param fee the fee to get the transaction from
   * @return the transaction
   */
  private static Transaction getTransactionFromFee(double subtotal, @NotNull Fee fee) {
    Transaction transaction = new Transaction();
    transaction.setAmount(new Amount("USD", String.format("%.02f", subtotal)));
    transaction.setDescription(fee.getDescription());
    return transaction;
  }

  /**
   * Get the transaction of a certain service
   *
   * @param subtotal how much did the service cost
   * @param service the service to get the transaction form
   * @return the transaction
   */
  @NotNull
  private static Transaction getTransactionFromService(double subtotal, @NotNull String service) {
    Transaction serviceTransaction = new Transaction();
    serviceTransaction.setAmount(new Amount("USD", String.format("%.02f", subtotal)));
    serviceTransaction.setDescription(service);
    return serviceTransaction;
  }
}
