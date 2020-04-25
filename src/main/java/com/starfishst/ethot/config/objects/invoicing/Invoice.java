package com.starfishst.ethot.config.objects.invoicing;

import org.jetbrains.annotations.NotNull;

public class Invoice {

  private final long total;
  @NotNull private final String service;

  public Invoice(long total, @NotNull String service) {
    this.total = total;
    this.service = service;
  }

  public long getTotal() {
    return total;
  }

  @NotNull
  public String getService() {
    return service;
  }
}
