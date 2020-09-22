package com.starfishst.bot.oldconfig;

import org.jetbrains.annotations.NotNull;

/** The configuration for payments */
public class PaymentsConfiguration {

  /** Whether or not to enable this type of payments */
  private final boolean enable;
  /** The port to listen to */
  private final int port;
  /** The path to the ssl key */
  @NotNull private final String keyStore;
  /** The password of the ssl key */
  @NotNull private final String keyStorePassword;
  /** The alias of the ssl key */
  @NotNull private final String keyAlias;
  /** The url to use */
  @NotNull private final String url;
  /** The client id of the PayPal user */
  @NotNull private final String clientId;
  /** The client secret of the PayPal user */
  @NotNull private final String clientSecret;
  /** The mode of the PayPalUsage */
  @NotNull private final String mode;
  /** The note for the payer */
  @NotNull private final String note;

  /** Create an instance for gson */
  public PaymentsConfiguration() {
    this.enable = false;
    this.port = 1000;
    this.keyStore = "keyStore";
    this.keyStorePassword = "keyStorePassword";
    this.keyAlias = "keyAlias";
    this.url = "url";
    this.clientId = "clientId";
    this.clientSecret = "clientSecret";
    this.mode = "sandbox";
    this.note = "note";
  }

  /**
   * Get if paypal payments are enabled
   *
   * @return true if they are enabled
   */
  public boolean isEnable() {
    return enable;
  }

  /**
   * Get the port of the server
   *
   * @return the port of the server
   */
  public int getPort() {
    return port;
  }

  /**
   * Get the path to the ssl key
   *
   * @return the path to the ssl key
   */
  public String getKeyStore() {
    return keyStore;
  }

  /**
   * Get ssl password
   *
   * @return the ssl password
   */
  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  /**
   * Get the ssl key alias
   *
   * @return the key alias
   */
  public String getKeyAlias() {
    return keyAlias;
  }

  /**
   * Get the url of the server
   *
   * @return the url of the server
   */
  public String getUrl() {
    return url;
  }

  /**
   * Get the client id for PayPal authentication
   *
   * @return the client id for PayPal authentication
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Get the client secret for PayPal authentication
   *
   * @return the client secret for PayPal authentication
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Get the mode where the payments will be ran.
   *
   * <p>'sandbox' means testing 'live' means production
   *
   * @return the mode where the payments will be ran.
   */
  public String getMode() {
    return mode;
  }

  /**
   * Get the note for the buyer
   *
   * @return the note for the buyer
   */
  public String getNote() {
    return note;
  }
}
