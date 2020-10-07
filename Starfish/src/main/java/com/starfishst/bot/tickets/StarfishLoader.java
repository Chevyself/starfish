package com.starfishst.bot.tickets;

import com.starfishst.api.data.loader.DataLoader;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.jda.utils.responsive.controller.ResponsiveMessageController;

/** An implementation for {@link com.starfishst.api.data.loader.DataLoader} */
public interface StarfishLoader
    extends StarfishEventHandler, DataLoader, ResponsiveMessageController {}
