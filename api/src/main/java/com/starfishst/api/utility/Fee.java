package com.starfishst.api.utility;

import lombok.Getter;
import lombok.NonNull;

/** A fee is applied to the final invoice if its inside the min and the max */
public class Fee {

  /** A brief description of the fee */
  @NonNull @Getter private final String description;
  /** The percentage to add according to the subtotal */
  @Getter private final double percentage;
  /** The addition to the subtotal */
  @Getter private final double addition;
  /** The minimum amount to apply the fee */
  @Getter private final double min;
  /** The max amount to apply the fee */
  @Getter private final double max;

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
      @NonNull String description, double percentage, double addition, double min, double max) {
    this.description = description;
    this.percentage = percentage;
    this.addition = addition;
    this.min = min;
    this.max = max;
  }

  /** Create an instance. This is used by Gson */
  @Deprecated
  public Fee() {
    this("No description", 0, 0, -1, -1);
  }

  /**
   * Apply the fee to a subtotal
   *
   * @param subtotal the subtotal to apply
   * @return the total with the fee applied
   */
  @Deprecated
  public double apply(double subtotal) {
    return this.getApply(subtotal) + subtotal;
  }

  /**
   * Get how much it would apply for a subtotal
   *
   * @param subtotal the subtotal to get how much it would apply
   * @return the amount to apply
   */
  public double getApply(double subtotal) {
    double apply = 0;
    if (this.percentage != 0) {
      apply = (this.percentage * subtotal) / 100;
    }
    return apply + this.addition;
  }

  /**
   * Check whether this fee applies to a value
   *
   * @param value the value to check
   * @return true if the fee applies
   */
  public boolean applies(double value) {
    return (this.min < 0 || value >= this.min) && (this.max < 0 || value <= this.max);
  }
}
