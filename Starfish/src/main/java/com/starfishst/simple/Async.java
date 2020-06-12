package com.starfishst.simple;

import org.jetbrains.annotations.NotNull;

/** Run something in a different thread */
public class Async {

  /**
   * Start the thread
   *
   * @param runnable to run async
   */
  public Async(@NotNull Runnable runnable) {
    new Thread(runnable).start();
  }
}
