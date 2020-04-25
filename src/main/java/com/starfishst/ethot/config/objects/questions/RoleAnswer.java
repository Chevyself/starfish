package com.starfishst.ethot.config.objects.questions;

import com.starfishst.core.utils.Lots;
import com.starfishst.ethot.util.Discord;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/**
 * When the answer is related to roles
 *
 * @author Chevy
 * @version 1.0.0
 */
public class RoleAnswer implements Answer {

  @NotNull private final List<Role> answer;

  public RoleAnswer(@NotNull List<Role> answer) {
    this.answer = answer;
  }

  @Override
  @NotNull
  public List<Role> getAnswer() {
    return answer;
  }

  /**
   * Get the answer but as tags
   *
   * @return the answer but as tags
   */
  public @NotNull List<String> getAnswerTags() {
    return Discord.getAsMention(answer);
  }

  @NotNull
  public String getTags() {
    return Lots.pretty(getAnswerTags());
  }
}
