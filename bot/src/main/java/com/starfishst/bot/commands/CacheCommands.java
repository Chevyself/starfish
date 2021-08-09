package com.starfishst.bot.commands;

import com.starfishst.api.Starfish;
import java.lang.ref.SoftReference;
import me.googas.commands.annotations.Parent;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.net.cache.Catchable;

public class CacheCommands {

  @Parent
  @Command(aliases = "cache", node = "guido.cache")
  public Result cache() {
    for (SoftReference<Catchable> catchable : Starfish.getCache().keySetCopy()) {
      System.out.println("Cache element: " + catchable.get());
    }
    return new Result("Check console for the output");
  }

  @Command(aliases = "clear", node = "guido.cache.clear")
  public Result cacheClear() {
    Starfish.validated().saveCache();
    return new Result("Cache emptied");
  }
}
