package com.starfishst.ethot.util;

import com.starfishst.core.utils.Atomic;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.objects.freelancers.Freelancer;
import com.starfishst.ethot.objects.questions.Answer;
import com.starfishst.ethot.objects.questions.RoleAnswer;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * Various utils for freelancers
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Freelancers {

  /**
   * Gets the rating as a double.
   *
   * <p>Basically gets all the rating and gets the average
   *
   * @param freelancer the freelancer to get the rating from
   * @return the average of the freelancer ratings or 0 if it doesn't have any
   */
  public static double getRating(@NotNull Freelancer freelancer) {
    Atomic<Double> atomic = new Atomic<>(0.0);
    freelancer.getRating().forEach((user, rate) -> atomic.set(atomic.get() + rate));
    return SimpleMath.average(freelancer.getRating().size(), atomic.get());
  }

  /**
   * Get the placeholders of a freelancer
   *
   * @param freelancer the freelancer to get the placeholders from
   * @return the placeholders of the freelancer
   */
  @NotNull
  public static HashMap<String, String> getPlaceholders(@NotNull Freelancer freelancer) {
    HashMap<String, String> placeholders = new HashMap<>();
    placeholders.put(
        "freelancerName", freelancer.getUser() == null ? "Null" : freelancer.getUser().getName());
    placeholders.put(
        "freelancer", freelancer.getUser() == null ? "Null" : freelancer.getUser().getAsTag());
    placeholders.put("rating", freelancer.getRatingAsString());
    placeholders.put("portfolio", freelancer.getPortfolioAsString());
    return placeholders;
  }

  /**
   * Checks if a freelancer has a role according to some answers.
   *
   * @param answers the answers to get role answers from
   * @param freelancer the freelancer to check
   * @return checks if the role answers contain a role that the freelancer also has
   * @throws DiscordManipulationException when the discord has not been set and getting the
   *     freelancer's member is ntt possible
   */
  public static boolean hasRole(
      @NotNull HashMap<String, Answer> answers, @NotNull Freelancer freelancer)
      throws DiscordManipulationException {
    Member member = freelancer.getMember();
    if (member != null) {
      Atomic<Boolean> atomic = new Atomic<>(false);
      answers.forEach(
          (simple, answer) -> {
            if (answer instanceof RoleAnswer) {
              ((RoleAnswer) answer)
                  .getAnswer()
                  .forEach(
                      role -> {
                        if (member.getRoles().contains(role)) {
                          atomic.set(true);
                        }
                      });
            }
          });
      return atomic.get();
    } else {
      return false;
    }
  }
}
