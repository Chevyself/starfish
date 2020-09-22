package com.starfishst.bot.objects.questions;

import com.starfishst.bot.handlers.questions.QuestionImage;
import org.jetbrains.annotations.NotNull;

/** The answer for {@link QuestionImage} */
public class ImageAnswer extends StringAnswer {
  /**
   * Create an instance
   *
   * @param answer the answer
   */
  public ImageAnswer(@NotNull String answer) {
    super("image-" + answer);
  }

  /**
   * Removes the 'image-' prefix
   *
   * @param string the string to remove the prefix from
   * @return the string with out the prefix
   */
  public static String removePrefix(@NotNull String string) {
    return string.substring("image-".length());
  }

  /**
   * Get the url of the image
   *
   * @return the url of the image
   */
  @NotNull
  public String getUrl() {
    return removePrefix(getAnswer());
  }
}
