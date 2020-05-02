package com.starfishst.bot.objects.invoicing;

import org.jetbrains.annotations.NotNull;

/** A fee is applied to the final invoice if its inside the min and the max */
public class Fee {

  /** A brief description of the fee */
  @NotNull private final String description;
  /** The percentage to add according to the subtotal */
  private final double percentage;
  /** The addition to the subtotal */
  private final double addition;
  /** The minimum amount to apply the fee */
  private final double min;
  /** The max amount to apply the fee */
  private final double max;

  /**
   * Create an instance
   *
   * @param description a brief description of the fee
   * @param percentage the percentage to add according to the subtotal
   * @param addition the addition to the subtotal
   * @param min the min amount to apply
   * @param max the max amount to apply
   */
  public Fee(
      @NotNull String description, double percentage, double addition, double min, double max) {
    this.description = description;
    this.percentage = percentage;
    this.addition = addition;
    this.min = min;
    this.max = max;
  }

  /**
   * Apply the fee to a subtotal
   *
   * @param subtotal the subtotal to apply
   * @return the total with the fee applied
   */
  public double apply(double subtotal) {
    if (this.percentage != 0) {
      subtotal = (this.percentage * subtotal) / 100;
    }
    return subtotal + this.addition;
  }

  /**
   * Get the description of the fee
   *
   * @return the description of the fee
   */
  @NotNull
  public String getDescription() {
    return description;
  }

  /**
   * Get the percentage to add according to the subtotal
   *
   * @return the percentage to add
   */
  public double getPercentage() {
    return percentage;
  }

  /**
   * Get the addition to the subtotal
   *
   * @return the addition to the subtotal
   */
  public double getAddition() {
    return addition;
  }

  /**
   * Get the min amount to apply the fee
   *
   * @return the min amount to apply the fee
   */
  public double getMin() {
    return min;
  }

  /**
   * Get the max amount to apply the fee
   *
   * @return the max amount to apply the fee
   */
  public double getMax() {
    return max;
  }
}
