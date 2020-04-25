package com.starfishst.ethot.config.objects.invoicing;

import org.jetbrains.annotations.NotNull;

public class Fee {

  @NotNull private final String description;
  private final double percentage;
  private final double addition;
  private final double min;
  private final double max;

  public Fee(
      @NotNull String description, double percentage, double addition, double min, double max) {
    this.description = description;
    this.percentage = percentage;
    this.addition = addition;
    this.min = min;
    this.max = max;
  }

  @NotNull
  public String getDescription() {
    return description;
  }

  public double getPercentage() {
    return percentage;
  }

  public double getAddition() {
    return addition;
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public double apply(double number) {
    if (this.percentage != 0) {
      number = (this.percentage + number) / 100;
    }
    return number + this.addition;
  }
}
