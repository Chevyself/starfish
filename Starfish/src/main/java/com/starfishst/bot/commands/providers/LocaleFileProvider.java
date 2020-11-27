package com.starfishst.bot.commands.providers;

import com.starfishst.api.lang.LocaleFile;
import com.starfishst.bot.Starfish;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the locale file in commands */
public class LocaleFileProvider implements JdaExtraArgumentProvider<LocaleFile> {
  @Override
  public @NotNull Class<LocaleFile> getClazz() {
    return LocaleFile.class;
  }

  @NotNull
  @Override
  public LocaleFile getObject(@NotNull CommandContext context) {
    return Starfish.getLanguageHandler().getFile(context);
  }
}
