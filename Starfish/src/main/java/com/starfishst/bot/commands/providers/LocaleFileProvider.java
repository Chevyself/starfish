package com.starfishst.bot.commands.providers;

import com.starfishst.api.Starfish;
import com.starfishst.api.lang.LocaleFile;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;

/** Provides the locale file in commands */
public class LocaleFileProvider implements JdaExtraArgumentProvider<LocaleFile> {
  @Override
  public @NonNull Class<LocaleFile> getClazz() {
    return LocaleFile.class;
  }

  @NonNull
  @Override
  public LocaleFile getObject(@NonNull CommandContext context) {
    return Starfish.getLanguageHandler().getFile(context);
  }
}
