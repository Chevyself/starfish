package com.starfishst;

/*
@SpringBootApplication
@RestController

 */

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import java.io.IOException;

/** Simple test for spring */
public class Test {

  /*
  @NotNull
  private static final String clientId =
      "Aelz-BmzZLkzZRDfw-cbceXBzfrZvzdrBDIWb_aCcJe5NVTYg_j1MP4WUjVyzS8Tzy6tka0ijYizPOtD";

  @NotNull
  private static final String clientSecret =
      "EG_6JlvHgb84StP0qxpGEYNUjsnOVULl4YXTUS94W7g8IpS1XtI679-nZLe9i1wp6SH_Kabcxhi-K98_";

  public static void main(String[] args) throws PayPalRESTException {
    SpringApplication app = new SpringApplication(Test.class);
    Properties properties = new Properties();
    properties.setProperty("server.port", "3000");
    properties.setProperty("server.ssl.key-store-type", "PKCS12");
    properties.setProperty("server.ssl.key-store", "C:\\Users\\Chevy\\keystore.p12");
    properties.setProperty("server.ssl.key-store-password", "Amesias123");
    properties.setProperty("server.ssl.key-alias", "tomcat");
    properties.setProperty("security.require-ssl", "true");
    app.setDefaultProperties(properties);
    app.run(args);

    Payment payment = createPayment(createLocalPayment("10.00"));
    System.out.println(getApprovalUrl(payment));
  }

  @GetMapping("/return")
  public String returnMap(
      @RequestParam(value = "paymentId") String paymentId,
      @RequestParam(value = "token") String token,
      @RequestParam(value = "PayerID") String payerId) {
    // get the payment using its id from somewhere

    try {
      Payment payment = Payment.get(getApiContext(), paymentId);

      System.out.println(payment);
      PaymentExecution paymentExecution = new PaymentExecution();
      paymentExecution.setPayerId(payerId);
      System.out.println(payment.execute(getApiContext(), paymentExecution));
    } catch (PayPalRESTException e) {
      e.printStackTrace();
    }
    return "";
  }

  @NotNull
  public static Payment createLocalPayment(@NotNull String total) {
    Amount amout = new Amount();
    amout.setCurrency("USD");
    amout.setTotal(total);

    Transaction transaction = new Transaction();
    transaction.setAmount(amout);
    List<Transaction> transactions = Lots.list(transaction);
    Payer payer = new Payer();
    payer.setPaymentMethod("PAYPAL");

    Payment payment = new Payment();
    payment.setIntent("sale");
    payment.setPayer(payer);
    payment.setTransactions(transactions);

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl("https://localhost:3000/cancel");
    redirectUrls.setReturnUrl("https://localhost:3000/return");
    payment.setRedirectUrls(redirectUrls);
    return payment;
  }

  @NotNull
  private static Payment createPayment(@NotNull Payment payment) throws PayPalRESTException {
    APIContext context = getApiContext();
    return payment.create(context);
  }

  @NotNull
  private static APIContext getApiContext() {
    return new APIContext(clientId, clientSecret, "sandbox");
  }

  public static String getApprovalUrl(@NotNull Payment payment) {
    Links links =
        payment.getLinks().stream()
            .filter(link -> link.getRel().equalsIgnoreCase("approval_url"))
            .findFirst()
            .orElse(null);
    return links == null ? "" : links.getHref();
  }
   */

  public static void main(String[] args) throws IOException, PayPalRESTException {
    // Payments.initialize(3000, "C:\\Users\\Chevy\\keystore.p12", "Amesias123", "tomcat");

    // APIContext context =
    // PayPalUtils.getApiContext(
    // "Aelz-BmzZLkzZRDfw-cbceXBzfrZvzdrBDIWb_aCcJe5NVTYg_j1MP4WUjVyzS8Tzy6tka0ijYizPOtD",
    //   "EG_6JlvHgb84StP0qxpGEYNUjsnOVULl4YXTUS94W7g8IpS1XtI679-nZLe9i1wp6SH_Kabcxhi-K98_",
    // "sandbox");
    // System.out.println(
    // PayPalUtils.createPayment(
    //   "10.00", "localhost:3000/cancel", "ocalhost:3000/return", context));
    APIContext context =
        new APIContext(
            "Aelz-BmzZLkzZRDfw-cbceXBzfrZvzdrBDIWb_aCcJe5NVTYg_j1MP4WUjVyzS8Tzy6tka0ijYizPOtD",
            "EG_6JlvHgb84StP0qxpGEYNUjsnOVULl4YXTUS94W7g8IpS1XtI679-nZLe9i1wp6SH_Kabcxhi-K98_",
            "sandbox");
    System.out.println(Payment.get(context, "PAYID-L2XAGSY7NS05990983851734"));
  }
}
