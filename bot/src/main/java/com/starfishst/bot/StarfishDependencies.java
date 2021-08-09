package com.starfishst.bot;

import com.google.gson.GsonBuilder;
import com.starfishst.api.StarfishFiles;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import me.googas.io.context.Json;
import me.googas.starbox.addons.dependencies.DependencyAddon;
import me.googas.starbox.addons.dependencies.DependencyManager;

public class StarfishDependencies {

  public static void main(String[] args) throws IOException {
    Json json = new Json(new GsonBuilder().create());
    AtomicBoolean failed = new AtomicBoolean();
    DependencyAddon.DependencyAddonBuilder[] builders =
        json.read(StarfishFiles.Resources.LIBS, DependencyAddon.DependencyAddonBuilder[].class)
            .handle(
                e -> {
                  e.printStackTrace();
                  failed.set(true);
                })
            .provide()
            .orElseGet(() -> new DependencyAddon.DependencyAddonBuilder[0]);
    DependencyManager.DependencyManagerBuilder builder =
        DependencyManager.using(StarfishFiles.LIBS)
            .handle(
                e -> {
                  e.printStackTrace();
                  failed.set(true);
                });
    if (failed.get()) throw new IllegalStateException("Could not load dependencies information");
    for (DependencyAddon.DependencyAddonBuilder addonBuilder : builders) {
      builder.add(addonBuilder);
    }
    builder.build().load();
  }
}
