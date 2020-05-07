package com.starfishst.bot.objects.invoicing;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.config.PaymentsConfiguration;
import com.starfishst.bot.config.language.Lang;
import com.starfishst.bot.tickets.TicketManager;
import com.starfishst.bot.tickets.type.Ticket;
import com.starfishst.bot.util.Console;
import com.starfishst.bot.util.Messages;
import com.starfishst.bot.util.Tickets;
import com.starfishst.core.utils.Maps;
import com.starfishst.core.utils.Strings;
import com.starfishst.simple.files.FileUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class handles all the requests made to the server from PayPal
 *
 * <p>Here explains how to get an SSL key:
 * https://www.thomasvitale.com/https-spring-boot-ssl-certificate/
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@RestController
public class Payments implements ErrorController {

  /** The pages and their html */
  @NotNull private static final HashMap<String, String> pages = new HashMap<>();
  /** The configuration for payments */
  @NotNull
  private static final PaymentsConfiguration config = Configuration.getInstance().getPayments();
  /**
   * Initializes the spring listener
   *
   * @throws IOException if one of the pages could not be loaded
   */
  public static void initialize() throws IOException {
    Console.info("Starting spring application");
    SpringApplication app = new SpringApplication(Payments.class);
    Properties properties =
        getProperties(
            config.getPort(),
            config.getKeyStore(),
            config.getKeyStorePassword(),
            config.getKeyAlias());
    app.setDefaultProperties(properties);
    Console.info("Loading html");
    loadPages();
    app.run();
  }

  /**
   * Loads the html pages for response
   *
   * @throws IOException in case a page could not be loaded
   */
  private static void loadPages() throws IOException {
    pages.put("cancel", getPage("cancel"));
    pages.put("confirm", getPage("confirm"));
    pages.put("error", getPage("error"));
    pages.put("not-found", getPage("not-found"));
  }

  /**
   * Get the properties that will be used in the spring application
   *
   * @param port the port to listen
   * @param keyStore the ssl certificate
   * @param keystorePassword the ssl certificate password
   * @param keyAlias the ssl certificate alias
   * @return the properties that can be used to run the spring application
   */
  @NotNull
  private static Properties getProperties(
      int port,
      @NotNull String keyStore,
      @NotNull String keystorePassword,
      @NotNull String keyAlias) {
    Properties properties = new Properties();
    properties.setProperty("server.port", String.valueOf(port));
    properties.setProperty("server.ssl.key-store-type", "PKCS12");
    properties.setProperty("server.ssl.key-store", keyStore);
    properties.setProperty("server.ssl.key-store-password", keystorePassword);
    properties.setProperty("server.ssl.key-alias", keyAlias);
    properties.setProperty("security.require-ssl", "true");
    return properties;
  }

  /**
   * Get the html of certain page
   *
   * @param name the page to get the html from
   * @return the html of the page
   * @throws IOException in case something goes wrong
   */
  @NotNull
  public static String getPage(@NotNull String name) throws IOException {
    InputStream stream = new FileInputStream(FileUtils.getFileOrResource("page/" + name + ".html"));
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder builder = Strings.getBuilder();
    while (reader.ready()) {
      builder.append(reader.readLine()).append("\n");
    }
    return builder.toString();
  }

  /**
   * Create a new payment
   *
   * @param total the total of the payment
   * @param transactions the transactions of the payment
   * @return the payment
   * @throws PayPalRESTException in case the api context could not be connected
   */
  @NotNull
  public static Payment createPayment(
      @NotNull String total, @NotNull List<Transaction> transactions) throws PayPalRESTException {
    return PayPalUtils.createPayment(
        total,
        transactions,
        config.getNote(),
        config.getUrl() + "/cancel",
        config.getUrl() + "/return",
        getContext());
  }

  /**
   * Checks if a payment was completed
   *
   * @param paymentId the id of the payment
   * @param token the token of the payment
   * @param payerId the payer id
   * @return the html response
   */
  @GetMapping("/return")
  public String returnMap(
      @RequestParam(value = "paymentId") String paymentId,
      @RequestParam(value = "token") String token,
      @RequestParam(value = "PayerID") String payerId) {
    try {
      Payment payment = PayPalUtils.getPayment(getContext(), paymentId);
      if (payment != null) {
        Ticket ticket = TicketManager.getInstance().getLoader().getTicketByPayment(payment.getId());
        if (ticket != null) {
          if (payment.getState().equalsIgnoreCase("CREATED")) {
            HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
            ticket.sendMessage(
                Messages.create(
                        "PAYMENT_RECEIVED_TITLE",
                        "PAYMENT_RECEIVED_DESCRIPTION",
                        placeholders,
                        placeholders)
                    .getAsMessageQuery()
                    .getMessage());
            PaymentExecution execution = new PaymentExecution();
            execution.setPayerId(payerId);
            payment.execute(getContext(), execution);
            return Strings.buildMessage(pages.get("confirm"), Tickets.getPlaceholders(ticket));
          } else {
            return Strings.buildMessage(
                pages.get("error"), Maps.singleton("error", Lang.get("PAYMENT_UNPAID")));
          }
        } else {
          return Strings.buildMessage(
              pages.get("returnError"), Maps.singleton("error", Lang.get("TICKET_NULL")));
        }
      } else {
        return Strings.buildMessage(
            pages.get("returnError"), Maps.singleton("error", Lang.get("PAYMENT_NULL")));
      }
    } catch (PayPalRESTException e) {
      e.printStackTrace();
      return Strings.buildMessage(
          pages.get("returnError"), Maps.singleton("error", e.getMessage()));
    }
  }

  /**
   * Send when a payment is cancelled
   *
   * @param token the token of the payment
   * @return the html
   */
  @GetMapping("/cancel")
  public String cancel(@RequestParam(value = "token") String token) {
    return pages.get("cancel");
  }

  /**
   * In case of an error this page will be shown
   *
   * @param request the request that committed an error
   * @return the html response
   */
  @GetMapping("/error")
  public String error(HttpServletRequest request) {
    return pages.get("not-found");
  }

  /**
   * Get the context using the user parameters
   *
   * @return the PayPal context
   */
  @NotNull
  public static APIContext getContext() {
    return PayPalUtils.getApiContext(
        config.getClientId(), config.getClientSecret(), config.getMode());
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
