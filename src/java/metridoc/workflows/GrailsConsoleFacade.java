package metridoc.workflows;

import org.apache.tools.ant.BuildException;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.codehaus.groovy.runtime.typehandling.NumberMath;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/27/12
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class GrailsConsoleFacade {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String CATEGORY_SEPARATOR = "|";
    public static final String ERROR = "Error";
    public static final String WARNING = "Warning";
    public static final String STACKTRACE_FILTERED_MESSAGE = " (NOTE: Stack trace has been filtered. Use --verbose to see entire trace.)";
    public static final String STACKTRACE_MESSAGE = " (Use --stacktrace to see the full trace)";
    public static final Character SECURE_MASK_CHAR = new Character('*');

    /**
     * Whether to show stack traces
     */
    private boolean stacktrace = true;

    private boolean progressIndicatorActive = false;

    /**
     * The progress indicator to use
     */
    String indicator = ".";
    /**
     * The last message that was printed
     */
    String lastMessage = "";

    Logger logger;

    /**
     * The category of the current output
     */
    @SuppressWarnings("serial")
    Stack<String> category = new Stack<String>() {
        @Override
        public String toString() {
            if (size() == 1) return peek() + CATEGORY_SEPARATOR;
            return DefaultGroovyMethods.join(this, CATEGORY_SEPARATOR) + CATEGORY_SEPARATOR;
        }
    };

    /**
     * Whether ANSI should be enabled for output
     */
    private boolean ansiEnabled = false;

    /**
     * Whether user input is currently active
     */
    private boolean userInputActive;


    private boolean isInteractiveEnabled() {
        return false;
    }

    private boolean isActivateTerminal() {
        return false;
    }

    private boolean readPropOrTrue(String prop) {
        String property = System.getProperty(prop);
        return property == null ? true : Boolean.valueOf(property);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
    }

    /**
     * @return Whether verbose output is being used
     */
    public boolean isVerbose() {
        return logger.isDebugEnabled();
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * @return The last message logged
     */
    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Stack<String> getCategory() {
        return category;
    }

    /**
     * Indicates progress with the default progress indicator
     */
    public void indicateProgress() {
        progressIndicatorActive = true;
        if (isAnsiEnabled()) {
            if (StringUtils.hasText(lastMessage)) {
                updateStatus(lastMessage + indicator);
            }
        } else {
            logger.info(indicator);
        }
    }

    /**
     * Indicate progress for a number and total
     *
     * @param number The current number
     * @param total  The total number
     */
    public void indicateProgress(int number, int total) {
        progressIndicatorActive = true;
        String currMsg = lastMessage;
        try {
            updateStatus(currMsg + ' ' + number + " of " + total);
        } finally {
            lastMessage = currMsg;
        }
    }

    /**
     * Indicates progress as a percentage for the given number and total
     *
     * @param number The number
     * @param total  The total
     */
    public void indicateProgressPercentage(long number, long total) {
        progressIndicatorActive = true;
        String currMsg = lastMessage;
        try {
            int percentage = Math.round(NumberMath.multiply(NumberMath.divide(number, total), 100).floatValue());

            if (!isAnsiEnabled()) {
                logger.info("..");
                logger.info(String.valueOf(percentage) + '%');
            } else {
                updateStatus(currMsg + ' ' + percentage + '%');
            }
        } finally {
            lastMessage = currMsg;
        }
    }

    /**
     * Indicates progress by number
     *
     * @param number The number
     */
    public void indicateProgress(int number) {
        progressIndicatorActive = true;
        String currMsg = lastMessage;
        try {
            if (isAnsiEnabled()) {
                updateStatus(currMsg + ' ' + number);
            } else {
                logger.info("..");
                logger.info(String.valueOf(number));
            }
        } finally {
            lastMessage = currMsg;
        }
    }

    /**
     * Updates the current state message
     *
     * @param msg The message
     */
    public void updateStatus(String msg) {
        outputMessage(msg, 1);
    }

    private void outputMessage(String msg, int replaceCount) {
        if (msg == null || msg.trim().length() == 0) return;
        try {
            if (isAnsiEnabled()) {


            } else {
                if (lastMessage != null && lastMessage.equals(msg)) return;

                if (progressIndicatorActive) {
                    logger.info("");
                }

                logger.info(CATEGORY_SEPARATOR);
                logger.info(msg);
            }
            lastMessage = msg;
        } finally {
            postPrintMessage();
        }
    }

    private void postPrintMessage() {
        progressIndicatorActive = false;
    }

    /**
     * Keeps doesn't replace the status message
     *
     * @param msg The message
     */
    public void addStatus(String msg) {
        outputMessage(msg, 0);
        lastMessage = "";
    }

    /**
     * Prints an error message
     *
     * @param msg The error message
     */
    public void error(String msg) {
        error(ERROR, msg);
    }

    /**
     * Prints an error message
     *
     * @param msg The error message
     */
    public void warning(String msg) {
        error(WARNING, msg);
    }

    /**
     * Prints a warn message
     *
     * @param msg The message
     */
    public void warn(String msg) {
        warning(msg);
    }

    private void logSimpleError(String msg) {
        if (progressIndicatorActive) {
            logger.info("");
        }
        logger.info(CATEGORY_SEPARATOR);
        logger.info(msg);
    }

    public boolean isAnsiEnabled() {
        return false;
    }

    /**
     * Use to log an error
     *
     * @param msg   The message
     * @param error The error
     */
    public void error(String msg, Throwable error) {
        try {
            if ((isVerbose() || stacktrace) && error != null) {
                printStackTrace(msg, error);
                error(ERROR, msg);
            } else {
                error(ERROR, msg + STACKTRACE_MESSAGE);
            }
        } finally {
            postPrintMessage();
        }
    }

    /**
     * Use to log an error
     *
     * @param error The error
     */
    public void error(Throwable error) {
        printStackTrace(null, error);
    }

    private void printStackTrace(String message, Throwable error) {

        if ((error instanceof BuildException) && error.getCause() != null) {
            error = error.getCause();
        }
        if (!isVerbose() && !Boolean.getBoolean("grails.full.stacktrace")) {
            StackTraceUtils.deepSanitize(error);
        }
        StringWriter sw = new StringWriter();
        PrintWriter ps = new PrintWriter(sw);
        message = message == null ? error.getMessage() : message;
        if (!isVerbose()) {
            message = message + STACKTRACE_FILTERED_MESSAGE;
        }
        ps.println(message);
        error.printStackTrace(ps);
        error(sw.toString());
    }

    /**
     * Logs a message below the current status message
     *
     * @param msg The message to log
     */
    public void log(String msg) {
        try {
            if (msg.endsWith(LINE_SEPARATOR)) {
                logger.info(msg);
            } else {
                logger.info(msg);
            }
        } finally {
            postPrintMessage();
        }
    }

    /**
     * Synonym for #log
     *
     * @param msg The message to log
     */
    public void info(String msg) {
        log(msg);
    }

    public void verbose(String msg) {
        logger.debug(msg);
    }

    /**
     * Replacement for AntBuilder.input() to eliminate dependency of
     * GrailsScriptRunner on the Ant libraries. Prints a message and
     * returns whatever the user enters (once they press &lt;return&gt;).
     *
     * @param msg The message/question to display.
     * @return The line of text entered by the user. May be a blank
     *         string.
     */
    public String userInput(String msg) {
        return doUserInput(msg, false);
    }

    /**
     * Like {@link #userInput(String)} except that the user's entered characters will be replaced with ‘*’ on the CLI,
     * masking the input (i.e. suitable for capturing passwords etc.).
     *
     * @param msg The message/question to display.
     * @return The line of text entered by the user. May be a blank
     *         string.
     */
    public String secureUserInput(String msg) {
        return doUserInput(msg, true);
    }

    private String doUserInput(String msg, boolean secure) {
        // Add a space to the end of the message if there isn't one already.
        if (!msg.endsWith(" ") && !msg.endsWith("\t")) {
            msg += ' ';
        }

        lastMessage = "";
        return readLine(msg, secure);
    }

    /**
     * Shows the prompt to request user input
     *
     * @param prompt The prompt to use
     * @return The user input prompt
     */
    private String showPrompt(String prompt) {
        if (!userInputActive) {
            return readLine(prompt, false);
        }

        logger.info(prompt);
        return null;
    }

    private String readLine(String prompt, boolean secure) {
        throw new IllegalStateException("User input is not enabled, cannot obtain input stream");
    }

    /**
     * Replacement for AntBuilder.input() to eliminate dependency of
     * GrailsScriptRunner on the Ant libraries. Prints a message and
     * list of valid responses, then returns whatever the user enters
     * (once they press &lt;return&gt;). If the user enters something
     * that is not in the array of valid responses, the message is
     * displayed again and the method waits for more input. It will
     * display the message a maximum of three times before it gives up
     * and returns <code>null</code>.
     *
     * @param message        The message/question to display.
     * @param validResponses An array of responses that the user is
     *                       allowed to enter. Displayed after the message.
     * @return The line of text entered by the user, or <code>null</code>
     *         if the user never entered a valid string.
     */
    public String userInput(String message, String[] validResponses) {
        if (validResponses == null) {
            return userInput(message);
        }

        String question = createQuestion(message, validResponses);
        String response = userInput(question);
        for (String validResponse : validResponses) {
            if (response != null && response.equalsIgnoreCase(validResponse)) {
                return response;
            }
        }

        return userInput("Invalid input. Must be one of ", validResponses);
    }

    private String createQuestion(String message, String[] validResponses) {
        return message + "[" + DefaultGroovyMethods.join(validResponses, ",") + "] ";
    }

    public void error(String label, String message) {
        if (message == null) {
            return;
        }

        try {
            if (isAnsiEnabled()) {
                throw new RuntimeException("ANSI should not be enabled");
            } else {
                logger.info(label);
                logger.info(" ");
                logSimpleError(message);
            }
        } finally {
            postPrintMessage();
        }
    }

    public void flush() {
        //do nothing
    }
}
