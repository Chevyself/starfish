package com.starfishst.auto;

import com.starfishst.api.utility.Fee;
import lombok.NonNull;

import java.util.Collection;

public class SimpleMath {

    @NonNull
    public static String getTotalFormatted(double subtotal, @NonNull Collection<Fee> applyingFees) {
        double total = subtotal;
        for (Fee fee : applyingFees) {
            total += fee.getApply(subtotal);
        }
        return String.format("%.2f", total);
    }

}
