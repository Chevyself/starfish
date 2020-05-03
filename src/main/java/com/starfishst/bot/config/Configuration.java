package com.starfishst.bot.config;

import com.starfishst.bot.listeners.questions.QuestionSendType;
import com.starfishst.bot.objects.invoicing.Fee;
import com.starfishst.bot.objects.questions.Question;
import com.starfishst.bot.objects.responsive.ResponsiveMessage;
import com.starfishst.bot.tickets.TicketType;
import com.starfishst.commands.ManagerOptions;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.Validate;
import com.starfishst.core.utils.time.Time;
import com.starfishst.core.utils.time.Unit;
import com.starfishst.simple.config.JsonConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This class represents the 'config.json' as a java object */
public class Configuration extends JsonConfiguration {

  /** The instance of this class for static usage */
  @Nullable private static Configuration instance;
  /** The token for discord authentication */
  @NotNull private final String token;
  /** The prefix to use in commands */
  @NotNull private final String prefix;
  /** The mongo configuration */
  @NotNull private final MongoConfiguration mongo;
  /** The limit of tickets that a user can have open */
  private final long openTicketsByUserLimit;
  /** The time to delete tickets */
  @NotNull private final Time toUnload;
  /** The time to unload freelancers */
  @NotNull private final Time toUnloadFreelancer;
  /** The time to start an inactive check */
  @NotNull private final Time inactiveTime;
  /** The time to finish an inactive check */
  @NotNull private final Time timeToFinishInactiveTest;
  /** The auto-save configuration */
  @NotNull private final AutoSaveConfiguration autoSave;
  /** The times to announce that the ticket is going to be deleted (when is creating) */
  @NotNull private final List<Time> toAnnounce;
  /** The questions for applies tickets */
  @NotNull private final List<Question> applyQuestions;
  /** The questions for orders tickets */
  @NotNull private final List<Question> orderQuestions;
  /** The questions for support tickets */
  @NotNull private final List<Question> supportQuestions;
  /** The questions for product tickets */
  @NotNull private final List<Question> productQuestions;
  /** The list of responsive messages */
  @NotNull private final List<ResponsiveMessage> responsiveMessages;
  /** The list of fees */
  @NotNull private final List<Fee> fees;
  /** The configuration for commands */
  @NotNull private final ManagerOptions commands;
  /** The type of questions send */
  @NotNull private final QuestionSendType questionSendType;
  /** The list of ticket types that cannot be created */
  @NotNull private final List<TicketType> bannedTypes;
  /** The total of tickets */
  private long total;
  /** The limit of quotes send by a freelancer */
  private long limitOfQuotes;

  /** Constructor for gson! */
  public Configuration() {
    instance = this;
    this.token = "";
    this.prefix = "-";
    this.mongo = new MongoConfiguration("", "");
    this.openTicketsByUserLimit = 2;
    this.toUnload = new Time(15, Unit.MINUTES);
    this.toUnloadFreelancer = new Time(15, Unit.MINUTES);
    this.inactiveTime = new Time(2, Unit.WEEKS);
    this.timeToFinishInactiveTest = new Time(1, Unit.DAYS);
    this.autoSave = new AutoSaveConfiguration(false, new Time(5, Unit.MINUTES));
    this.toAnnounce = new ArrayList<>();
    this.applyQuestions = new ArrayList<>();
    this.orderQuestions = new ArrayList<>();
    this.supportQuestions = new ArrayList<>();
    this.productQuestions = new ArrayList<>();
    this.responsiveMessages = new ArrayList<>();
    this.fees = new ArrayList<>();
    this.commands = new ManagerOptions();
    this.questionSendType = QuestionSendType.NONE;
    this.bannedTypes = new ArrayList<>();
    this.total = 0;
    this.limitOfQuotes = 2;
  }

  /**
   * Get the fees that apply to a subtotal
   *
   * @param subtotal the subtotal looking for fees
   * @return a list of fees that apply to the subtotal
   */
  @NotNull
  public List<Fee> getApplyingFees(double subtotal) {
    return fees.stream()
        .filter(fee -> fee.getMin() <= subtotal && subtotal <= fee.getMax())
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

  /**
   * Get the list of questions for certain ticket type
   *
   * @param type the ticket type looking for questions
   * @return the type looking for questions
   * @throws IllegalArgumentException if the type that requests questions does not have any
   */
  @NotNull
  @Deprecated
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
        Fallback.addError(error);
        throw new IllegalArgumentException(error);
    }
  }

  /**
   * Get the time to finish an inactive test
   *
   * @return the time to finish an inactive test
   */
  @NotNull
  public Time getTimeToFinishInactiveTest() {
    return timeToFinishInactiveTest;
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
  public Time getToUnload() {
    return toUnload;
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
   * {@link com.starfishst.bot.listeners.ResponsiveMessagesListener}
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

  /**
   * Get the time to start an inactive test
   *
   * @return the time to start an inactive test
   */
  @NotNull
  public Time getInactiveTime() {
    return inactiveTime;
  }

  /**
   * Get the questions for products
   *
   * @return the questions for products
   */
  @NotNull
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
  @Deprecated
  @NotNull
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

  /**
   * Get the instance of configuration for static usage
   *
   * @return the instance
   */
  @NotNull
  public static Configuration getInstance() {
    return Validate.notNull(instance, "Configuration may not have been initialized");
  }

  /**
   * Get the list of banned ticket types
   *
   * @return the list of banned ticket types
   */
  @NotNull
  public List<TicketType> getBannedTypes() {
    return bannedTypes;
  }

  /**
   * Get the limit that a freelancer has to send quotes
   *
   * @return the limit that a freelancer has to send quotes
   */
  public long getLimitOfQuotes() {
    return limitOfQuotes;
  }
}
