package com.starfishst.ethot.config.questions;

import com.starfishst.ethot.exception.QuestionsInitException;
import com.starfishst.ethot.objects.questions.Question;
import com.starfishst.ethot.tickets.TicketType;
import com.starfishst.simple.config.JsonConfiguration;
import com.starfishst.simple.files.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Handles everything that has to do with questions configuration */
public class QuestionsHandler {

  /** The instance for static usage */
  @NotNull private static final QuestionsHandler instance;

  static {
    try {
      instance = new QuestionsHandler();
    } catch (IOException e) {
      throw new QuestionsInitException();
    }
  }

  /** The questions for applies */
  @NotNull private final QuestionsConfiguration apply;
  /** The questions for support ticket */
  @NotNull private final QuestionsConfiguration support;
  /** The questions for orders */
  @NotNull private final QuestionsConfiguration order;
  /** The questions for products */
  @NotNull private final QuestionsConfiguration product;
  /** The questions for reports */
  @NotNull private final QuestionsConfiguration report;
  /** The questions for suggestions */
  @NotNull private final QuestionsConfiguration suggestion;

  /**
   * Create an instance
   *
   * @throws IOException in case that the file of some of the questions could not be initialized
   */
  public QuestionsHandler() throws IOException {
    this.product = JsonConfiguration.getInstance(getFile("product"), QuestionsConfiguration.class);
    this.apply = JsonConfiguration.getInstance(getFile("apply"), QuestionsConfiguration.class);
    this.support = JsonConfiguration.getInstance(getFile("support"), QuestionsConfiguration.class);
    this.order = JsonConfiguration.getInstance(getFile("order"), QuestionsConfiguration.class);
    this.report = JsonConfiguration.getInstance(getFile("report"), QuestionsConfiguration.class);
    this.suggestion =
        JsonConfiguration.getInstance(getFile("suggestion"), QuestionsConfiguration.class);
  }

  /**
   * Get the file for some question configuration
   *
   * @param name the name of the questions to get
   * @return the file
   * @throws IOException in case that the file could not be gotten
   */
  @NotNull
  public static File getFile(@NotNull String name) throws IOException {
    return FileUtils.getFileOrResource("questions/" + name + ".json");
  }

  /**
   * Get the list of questions for certain ticket type
   *
   * @param type the ticket type looking for questions
   * @return the type looking for questions
   * @throws IllegalArgumentException if the type that requests questions does not have any
   */
  @NotNull
  public List<Question> getQuestions(@NotNull TicketType type) {
    switch (type) {
      case APPLY:
        return apply.getQuestions();
      case ORDER:
      case QUOTE:
        return order.getQuestions();
      case SUPPORT:
        return support.getQuestions();
      case PRODUCT:
        return product.getQuestions();
      case REPORT:
        return report.getQuestions();
      case SUGGESTION:
        return suggestion.getQuestions();
      default:
        String error = type + " is not a valid type for questions";
        throw new IllegalArgumentException(error);
    }
  }

  /**
   * Get the instance for static usage
   *
   * @return the instance
   */
  @NotNull
  public static QuestionsHandler getInstance() {
    return instance;
  }
}
