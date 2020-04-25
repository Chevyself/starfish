package com.starfishst.ethot.util;

import com.starfishst.ethot.objects.invoicing.Fee;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Simple math utils
 *
 * @author Chevy
 * @version 1.0.0
 */
public class SimpleMath {

  /**
   * Calculates the total of a subtotal applying fees
   *
   * @param subtotal the subtotal
   * @param toApply the fees to apply
   * @return the total of the operation
   */
  public static double getTotal(double subtotal, @NotNull List<Fee> toApply) {
    for (Fee fee : toApply) {
      subtotal = fee.apply(subtotal);
    }
    return subtotal;
  }

  /**
   * Calculates the simple average of a sum and the total values applied in the operation
   *
   * @param size the total values applied in the operation
   * @param sum the total of the operation
   * @return the average dividing the sum over the total values applied in the operation or 0 if the
   *     values are 0
   */
  public static double average(int size, double sum) {
    return size == 0 ? 0 : sum / size;
  }
}
