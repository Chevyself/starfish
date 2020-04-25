package com.starfishst.ethot.config.questions;

import com.starfishst.ethot.objects.questions.Question;
import com.starfishst.simple.config.JsonConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A configuration file for questions
 */
public class QuestionsConfiguration extends JsonConfiguration {

    /**
     * The list of questions for ticket creation
     */
    @NotNull
    private final List<Question> questions;

    /**
     * Create an instance using gson
     */
    public QuestionsConfiguration() {
        this.questions = new ArrayList<>();
    }

    /**
     * Get the list of questions
     *
     * @return the list of questions
     */
    @NotNull
    public List<Question> getQuestions() {
        return questions;
    }

}
