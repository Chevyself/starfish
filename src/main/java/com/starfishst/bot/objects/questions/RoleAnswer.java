package com.starfishst.bot.objects.questions;

import com.starfishst.core.utils.Lots;
import com.starfishst.bot.util.Discord;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/** When the answer is related to roles */
public class RoleAnswer implements Answer {

  /** The answered roles */
  @NotNull private final List<Role> answer;

  /**
   * Create an instance
   *
   * @param answer the answered roles
   */
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

  /**
   * Get the roles answered as tags
   *
   * @return the roles answered as tags
   */
  @NotNull
  public String getTags() {
    return Lots.pretty(getAnswerTags());
  }
}
