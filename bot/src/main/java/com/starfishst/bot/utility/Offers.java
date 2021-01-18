package com.starfishst.bot.utility;

import com.starfishst.api.messages.BotResponsiveMessage;
import com.starfishst.api.tickets.Offer;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public class Offers {

  @NonNull
  public static List<Offer> fromMessages(@NonNull List<BotResponsiveMessage> many) {
    List<Offer> offers = new ArrayList<>();
    for (BotResponsiveMessage message : many) {
      offers.add(new OfferMessage(message));
    }
    return offers;
  }
}
