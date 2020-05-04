package com.starfishst.bot.util;

import com.starfishst.bot.objects.invoicing.Fee;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Simple math utils
 *
 * @author Chevy
 * @version 1.0.0
 */
public class SimpleMath {

  /**
   * Get the total in a string "$$.$$"
   *
   * @param subtotal the subtotal to get the total
   * @param toApply the fees to apply
   * @return the total formatted as string
   */
  @NotNull
  public static String getTotalFormatted(double subtotal, @NotNull List<Fee> toApply) {
    return twoDecimalsFormat(getTotal(subtotal, toApply));
  }

  /**
   * Get a two decimal format from a double
   *
   * @param toFormat the double to format
   * @return the double formatted as string
   */
  @NotNull
  public static String twoDecimalsFormat(double toFormat) {
    return String.format("%.02f", toFormat);
  }

  /**
   * Calculates the total of a subtotal applying fees
   *
   * @param subtotal the subtotal
   * @param toApply the fees to apply
   * @return the total of the operation
   */
  public static double getTotal(double subtotal, @NotNull List<Fee> toApply) {
    double total = subtotal;
    for (Fee fee : toApply) {
      total += fee.getApply(subtotal);
    }
    return total;
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
