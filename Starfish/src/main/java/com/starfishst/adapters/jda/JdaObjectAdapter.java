package com.starfishst.adapters.jda;

import me.googas.commons.gson.adapters.JsonAdapter;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/**
 * An adapter for a jda object
 *
 * @param <O> the type of the object
 */
public abstract class JdaObjectAdapter<O> implements JsonAdapter<O> {

  /** The api to use for the adaptation of the object */
  @NotNull protected final JDA api;

  /**
   * Create the adapter
   *
   * @param api the api to use for the adaptation
   */
  protected JdaObjectAdapter(@NotNull JDA api) {
    this.api = api;
  }
}
