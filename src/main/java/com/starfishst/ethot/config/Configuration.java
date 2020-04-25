package com.starfishst.ethot.config;

import com.starfishst.commands.ManagerOptions;
import com.starfishst.core.utils.Errors;
import com.starfishst.core.utils.time.Time;
import com.starfishst.ethot.config.objects.invoicing.Fee;
import com.starfishst.ethot.config.objects.questions.Question;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.listeners.questions.QuestionSendType;
import com.starfishst.ethot.tickets.TicketType;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the 'config.json' as a java object
 *
 * <ul>
 *   <li>'token' is used for Discord authentication. Create your app and get your token <a
 *       href="https://discordapp.com/developers/applications">here</a>
 *   <li>'prefix' is how the bot will listen to command execution
 *   <li>'total' the total of tickets. This number is kept in memory so we don't waste memory
 *       counting
 *   <li>'openTicketsByUser' the limit of open tickets that a user can have
 *   <li>'toDelete' ignore the word 'delete' as tickets are only going to be deleted if they are
 *       still in creation. this actually sets the time to unload tickets out of memory and save
 *       them into the database
 *   <li>'toAnnounce' as said above if a ticket is still in creation it will be deleted. So, when
 *       should we notify that it is going to be deleted
 *   <li>'-Questions' anything related to questions is here.
 *       <p>Questions need a 'title', 'description', 'simple' and 'limit'. If in a question you want
 *       to ask for roles you also have to add a key called 'roles' in which you will have to add
 *       the identification of the list of roles.
 *       <p>In role questions 'limit' is going to be the limit of roles to tag else it is the limit
 *       of characters. Also role questions have a place holder %roles% which is the list of roles
 *       that you can use
 *       <p>'simple' must be single words for identification. This means no spaces ' ' you can use
 *       '_' instead
 *   <li>'responsiveMessages' do not touch this setting
 *   <li>'commands' everything related to command responses also embed decor
 *       <ul>
 *         <li>'deleteCommands' true if you want to delete the message send by the client to execute
 *             a command
 *         <li>'embedMessages' true if you want messages to be embed
 *         <li>'deleteErrors' should the messages related to errors be deleted
 *         <li>'deleteTime' if option of above is true. The value of time to be deleted
 *         <li>'deleteUnit' the unit for the value above. Use SECONDS, MINUTES or HOURS
 *         <li>'success' if using embeds set this for the color to use in generic messages
 *         <li>'error' if using embeds set this for the color to use in errors
 *         <li>for the two options above: Colors must be in <a href="https://www.color-hex.com/">hex
 *             code</a>
 *       </ul>
 * </ul>
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Configuration {

  @NotNull private final String token;
  @NotNull private final String prefix;
  @NotNull private final MongoConfiguration mongo;
  private final long openTicketsByUserLimit;
  @NotNull private final Time toDelete;
  @NotNull private final Time toUnloadFreelancer;
  @NotNull private final Time inactiveTime;
  @NotNull private final Time timeToFinishInactiveTest;
  @NotNull private final AutoSaveConfiguration autoSave;
  @NotNull private final List<Time> toAnnounce;
  @NotNull private final List<Question> applyQuestions;
  @NotNull private final List<Question> orderQuestions;
  @NotNull private final List<Question> supportQuestions;
  @NotNull private final List<Question> productQuestions;
  @NotNull private final List<ResponsiveMessage> responsiveMessages;
  @NotNull private final List<Fee> fees;
  @NotNull private final ManagerOptions commands;
  @NotNull private final QuestionSendType questionSendType;
  private long total;

  public Configuration(
      @NotNull String token,
      @NotNull String prefix,
      @NotNull MongoConfiguration mongo,
      long openTicketsByUserLimit,
      @NotNull List<Question> orderQuestions,
      @NotNull List<ResponsiveMessage> messages,
      @NotNull Time toDelete,
      @NotNull Time toUnloadFreelancer,
      @NotNull Time inactiveTime,
      @NotNull Time timeToFinishInactiveTest,
      @NotNull AutoSaveConfiguration autoSave,
      @NotNull List<Time> toAnnounce,
      int total,
      @NotNull List<Question> applyQuestions,
      @NotNull List<Question> supportQuestions,
      @NotNull List<Question> productQuestions,
      @NotNull List<Fee> fees,
      @NotNull ManagerOptions commands,
      @NotNull QuestionSendType questionSendType) {
    this.token = token;
    this.prefix = prefix;
    this.mongo = mongo;
    this.openTicketsByUserLimit = openTicketsByUserLimit;
    this.orderQuestions = orderQuestions;
    this.responsiveMessages = messages;
    this.toDelete = toDelete;
    this.toUnloadFreelancer = toUnloadFreelancer;
    this.inactiveTime = inactiveTime;
    this.timeToFinishInactiveTest = timeToFinishInactiveTest;
    this.autoSave = autoSave;
    this.toAnnounce = toAnnounce;
    this.total = total;
    this.applyQuestions = applyQuestions;
    this.supportQuestions = supportQuestions;
    this.productQuestions = productQuestions;
    this.fees = fees;
    this.commands = commands;
    this.questionSendType = questionSendType;
  }

  @NotNull
  public List<Fee> getApplyingFees(double total) {
    return fees.stream()
        .filter(fee -> fee.getMin() <= total && total <= fee.getMax())
        .collect(Collectors.toList());
  }

  /**
   * Removes a responsive message from the list
   *
   * @param responsiveMessage the message to remove
   */
  public void removeResponsiveMessage(@NotNull ResponsiveMessage responsiveMessage) {
    responsiveMessages.remove(responsiveMessage);
  }

  @NotNull
  public Time getTimeToFinishInactiveTest() {
    return timeToFinishInactiveTest;
  }

  @NotNull
  public Time getInactiveTime() {
    return inactiveTime;
  }

  /**
   * Get the time to unload freelancers
   *
   * @return the time
   */
  @NotNull
  public Time getToUnloadFreelancer() {
    return toUnloadFreelancer;
  }

  /**
   * Get the token for discord authentication. If a token is not provided the program will fail in
   * startup
   *
   * @return the token used for discord authentication
   */
  @NotNull
  public String getToken() {
    return token;
  }

  /**
   * Get the questions for orders
   *
   * @return the questions for orders
   */
  @NotNull
  public List<Question> getOrderQuestions() {
    return orderQuestions;
  }

  /**
   * Get the total number of tickets so that the program doesn't have to count. In case that you
   * accidentally set this to 0 or something else tickets wont have the same ids as it will check
   * them first.
   *
   * @return the total number of tickets
   */
  public long getTotal() {
    return total;
  }

  /**
   * Set the new total of tickets
   *
   * @param total the new total to set
   */
  public void setTotal(long total) {
    this.total = total;
  }

  /**
   * Get how the next questions should be send
   *
   * @return the type on how the next question should be send
   */
  @NotNull
  public QuestionSendType getQuestionSendType() {
    return questionSendType;
  }

  /**
   * Get the time to unload/delete tickets
   *
   * @return the time to unload or delete tickets
   */
  @NotNull
  public Time getToDelete() {
    return toDelete;
  }

  /**
   * Get the time to announce that a ticket is going to be deleted
   *
   * @return a list of times when should be announced
   */
  @NotNull
  public List<Time> getToAnnounce() {
    return toAnnounce;
  }

  /**
   * Get a responsive message using the id of the message
   *
   * @param id the id of the message
   * @return a responsive message if found else null
   */
  @Nullable
  public ResponsiveMessage getResponsiveMessage(long id) {
    return responsiveMessages.stream()
        .filter(responsiveMessage -> responsiveMessage != null && responsiveMessage.getId() == id)
        .findFirst()
        .orElse(null);
  }

  /**
   * Adds a responsive message into memory. Add the message here in case you want it to be heard in
   * {@link com.starfishst.ethot.listeners.ResponsiveMessagesListener}
   *
   * @param message the message to add
   */
  public void addResponsiveMessage(@NotNull ResponsiveMessage message) {
    responsiveMessages.add(message);
  }

  /**
   * Get the prefix to listen to commands
   *
   * @return the prefix for commands
   */
  @NotNull
  public String getPrefix() {
    return prefix;
  }

  /**
   * Get the options for commands
   *
   * @return the options for commands
   */
  @NotNull
  public ManagerOptions getCommands() {
    return commands;
  }

  /**
   * Get the questions for applications
   *
   * @return the questions for applications
   */
  @NotNull
  public List<Question> getApplyQuestions() {
    return applyQuestions;
  }

  /**
   * Get the questions for support
   *
   * @return the questions for support
   */
  @NotNull
  public List<Question> getSupportQuestions() {
    return supportQuestions;
  }

  @NotNull
  public List<Question> getQuestions(@NotNull TicketType type) {
    switch (type) {
      case APPLY:
        return getApplyQuestions();
      case ORDER:
      case QUOTE:
        return getOrderQuestions();
      case SUPPORT:
        return getSupportQuestions();
      case PRODUCT:
        return getProductQuestions();
      default:
        String error = type + " is not a valid type for questions";
        Errors.addError(error);
        throw new IllegalArgumentException(error);
    }
  }

  /**
   * Get the questions for products
   *
   * @return the questions for products
   */
  private List<Question> getProductQuestions() {
    return productQuestions;
  }

  /**
   * Get the limit of tickets that a user can have open
   *
   * @return the limit
   */
  public long getOpenTicketsByUserLimit() {
    return openTicketsByUserLimit;
  }

  /**
   * Get the mongo configuration
   *
   * @return the mongo configuration
   */
  @NotNull
  public MongoConfiguration getMongo() {
    return mongo;
  }

  /**
   * Get the list of fees active for bot
   *
   * @return the list of fees
   */
  public List<Fee> getFees() {
    return fees;
  }

  /**
   * Get the list of responsive messages
   *
   * @return the list of responsive messages
   */
  @NotNull
  public List<ResponsiveMessage> getResponsiveMessages() {
    return responsiveMessages;
  }

  /**
   * Get the settings for auto saving
   *
   * @return the settings for auto saving
   */
  @NotNull
  public AutoSaveConfiguration getAutoSave() {
    return autoSave;
  }
}
