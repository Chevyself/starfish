package com.starfishst.ethot.config.objects.management;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * Allows a way to verify if an user is allowed to do something
 *
 * @author Chevy
 * @version 1.0.0
 */
public interface AllowedChecker {

  boolean isAllowed(@NotNull Member member);
}
