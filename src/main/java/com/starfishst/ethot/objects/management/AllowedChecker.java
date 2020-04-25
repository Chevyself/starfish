package com.starfishst.ethot.objects.management;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/** Allows a way to verify if an user is allowed to do something */
public interface AllowedChecker {

  /**
   * Check if a member is allowed
   *
   * @param member the member to check if is allowed
   * @return true if allowed
   */
  boolean isAllowed(@NotNull Member member);
}
