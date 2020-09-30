package com.starfishst.bot.data.messages.offer;

import com.starfishst.bot.data.StarfishResponsiveMessage;
import com.starfishst.bot.data.StarfishValuesMap;
import com.starfishst.commands.utils.responsive.ReactionResponse;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A message sent to a quote ticket
 * TODO
 */
public class OfferMessage extends StarfishResponsiveMessage {


    public OfferMessage(long id, @NotNull Set<ReactionResponse> reactions, @NotNull String type, @NotNull StarfishValuesMap data) {
        super(id, reactions, type, data);
    }

    public OfferMessage(@NotNull Message message, @NotNull Set<ReactionResponse> reactions, @NotNull String type, @NotNull StarfishValuesMap data) {
        super(message, reactions, type, data);
    }
}
