package com.starfishst.bot.legacy;

/** The result of the {@link CopyLegacyProcess} */
public enum CopyLegacyProcessResult {
  /** This means that the process is currently running */
  RUNNING,
  /** This means that the process ended because of an exception */
  EXCEPTION,
  /** This means that the process ended successfully */
  ENDED,
}
