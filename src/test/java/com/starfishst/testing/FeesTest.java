package com.starfishst.testing;

import com.starfishst.bot.config.Configuration;
import com.starfishst.bot.objects.invoicing.Fee;
import com.starfishst.bot.util.SimpleMath;
import com.starfishst.simple.Lots;

/** Tests for {@link com.starfishst.bot.objects.invoicing.Fee} */
public class FeesTest {

  /**
   * Run the testing for fees
   *
   * @param args there's no arguments
   */
  public static void main(String[] args) {
    Fee fee = new Fee("Some description", 5.6, 0.30, 0, 100000);
    Configuration configuration = new Configuration();
    configuration.getFees().add(fee);

    double subtotal = 10;
    // percentage being 5.6 ->
    // (5.6 * 10) / 100 = 0.56
    // 0.56 + 0.30  = 0.86
    // total must be 10.86
    System.out.println(
        SimpleMath.getTotalFormatted(subtotal, configuration.getApplyingFees(subtotal)));

    configuration.getFees().clear();

    fee = new Fee("Some description", 0.0, 5.0, 0, 100000);
    configuration.getFees().add(fee);
    // total must be 15.00
    System.out.println(
        SimpleMath.getTotalFormatted(subtotal, configuration.getApplyingFees(subtotal)));

    configuration.getFees().clear();

    fee = new Fee("Some description", 5.0, 0.0, 0, 100000);
    configuration.getFees().add(fee);
    // total must be 10.50
    System.out.println(
        SimpleMath.getTotalFormatted(subtotal, configuration.getApplyingFees(subtotal)));

    fee = new Fee("Fee #1", 5.6, 0.30, 0, 100000);
    Fee fee2 = new Fee("Fee #2", 0.0, 5.0, 0, 100000);
    Fee fee3 = new Fee("Some description", 5.0, 0.0, 0, 100000);
    configuration.getFees().addAll(Lots.list(fee, fee2, fee3));
    // the total should be 16.36
    System.out.println(
        SimpleMath.getTotalFormatted(subtotal, configuration.getApplyingFees(subtotal)));
  }
}
