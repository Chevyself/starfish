package com.starfishst.bot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.addons.Addon;
import me.googas.addons.dependencies.DependencyAddon;
import me.googas.addons.dependencies.DependencyInformation;
import me.googas.addons.dependencies.DependencyManager;
import me.googas.commons.CoreFiles;
import me.googas.commons.Lots;

public class StarfishDependencies implements DependencyManager {

  @NonNull @Getter
  private final File parent =
      new File(CoreFiles.validatePath(CoreFiles.currentDirectory() + "/libs"));

  @NonNull @Getter
  private final List<DependencyAddon> dependencies =
      Lots.list(
          DependencyAddon.create(
              "https://github.com/DV8FromTheWorld/JDA/releases/download/v4.2.0/JDA-4.2.0_168-withDependencies.jar",
              "JDA",
              "4.2.0_168",
              "",
              this),
          DependencyAddon.create(
              "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar",
              "Gson",
              "2.8.6",
              "",
              this),
          DependencyAddon.create(
              "https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.12.3/mongo-java-driver-3.12.3.jar",
              "Mongo",
              "3.12.7",
              "",
              this));

  @NonNull @Getter private final List<Addon> loaded = new ArrayList<>();

  @Override
  public @NonNull File getDependencyFile(@NonNull DependencyInformation information) {
    System.out.println("this.parent.getAbsolutePath() = " + this.parent.getAbsolutePath());
    File fi = DependencyManager.super.getDependencyFile(information);
    System.out.println("fi.getPath() = " + fi.getAbsolutePath());
    return fi;
  }
}
