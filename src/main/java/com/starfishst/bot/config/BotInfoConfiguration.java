package com.starfishst.bot.config;

import com.starfishst.simple.files.FileUtils;
import java.io.IOException;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;

/** The configuration for 'bot-info.properties'. The bot build information */
public class BotInfoConfiguration {

  /** The properties of the bot info configuration */
  @NotNull private final Properties properties;

  /**
   * Create the instance
   *
   * @throws IOException in case the file cannot be loaded
   */
  public BotInfoConfiguration() throws IOException {
    properties = new Properties();
    properties.load(FileUtils.getResource("bot-info.properties"));
  }

  /**
   * Get the hash of the git build
   *
   * @return the hash of the git build
   */
  @NotNull
  public String getGitHash() {
    return properties.getProperty("git.commit-hash", "No Hash");
  }
}
