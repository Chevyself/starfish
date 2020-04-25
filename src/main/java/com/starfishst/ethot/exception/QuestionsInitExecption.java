package com.starfishst.ethot.exception;

import com.starfishst.core.exceptions.type.SimpleRuntimeException;

/**
 * Thrown when {@link com.starfishst.ethot.config.questions.QuestionsHandler} could not be initialized
 */
public class QuestionsInitExecption extends SimpleRuntimeException {

    /**
     * Create an instance
     */
    public QuestionsInitExecption() {
        super("Questions could not be initialized!");
    }

}
