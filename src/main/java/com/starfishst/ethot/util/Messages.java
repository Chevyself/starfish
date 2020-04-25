package com.starfishst.ethot.util;

import com.starfishst.commands.ManagerOptions;
import com.starfishst.commands.listener.CommandListener;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedFactory;
import com.starfishst.commands.utils.embeds.EmbedQuery;
import com.starfishst.commands.utils.message.MessageQuery;
import com.starfishst.commands.utils.message.MessagesFactory;
import com.starfishst.core.utils.Lots;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.DiscordConfiguration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.config.language.LangMessagesProvider;
import com.starfishst.ethot.config.objects.freelancers.Freelancer;
import com.starfishst.ethot.config.objects.questions.RoleAnswer;
import com.starfishst.ethot.tickets.type.Apply;
import com.starfishst.ethot.tickets.type.FreelancingTicket;
import com.starfishst.ethot.tickets.type.QuestionsTicket;
import com.starfishst.ethot.tickets.type.Support;
import com.starfishst.ethot.tickets.type.Ticket;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utils for sending messages in discord easily
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Messages {

  /**
   * Creates an embed just like {@link EmbedFactory#fromResult(Result, CommandListener)} but without
   * the need of a result or listener
   *
   * @param type the type of result
   * @param description the description of the embed
   * @return the embed as a query to send
   */
  public static MessageQuery embed(@NotNull ResultType type, @NotNull String description) {
    LangMessagesProvider provider = Lang.getProvider();
    ManagerOptions options = Main.getCommandManager().getManagerOptions();
    return EmbedFactory.newEmbed(
            type.getTitle(provider),
            description,
            provider.thumbnailUrl(),
            null,
            provider.footer(),
            type.getColor(options),
            null,
            false)
        .getAsMessageQuery();
  }

  /**
   * Note: The title of the embed will be the 'TITLE_ERROR'
   *
   * <p>Creates a message query with an error embed, using a key to get from 'lang.properties' and
   * change it with certain placeholders
   *
   * @param key to get the message from
   * @param placeholders the placeholders to build the message
   * @return the query to use
   */
  public static MessageQuery error(
      @NotNull String key, @NotNull HashMap<String, String> placeholders) {
    return error(Lang.get(key, placeholders));
  }

  /**
   * Note: The title of the embed will be the 'TITLE_ERROR'
   *
   * <p>Creates a message query with an error message
   *
   * @param description the message to send as an error
   * @return the message query to se
   */
  public static MessageQuery error(@NotNull String description) {
    return embed(ResultType.ERROR, description);
  }

  /**
   * Creates an embed query using the title and description from the 'lang.properties' that's why
   * the parameters are the key of those messages. You can also add placeholders to change them
   *
   * @param titleKey the key of the title message
   * @param descriptionKey the key of the description message
   * @param titlePlaceHolders the placeholders for the title
   * @param descriptionPlaceHolders the placeholders for the description
   * @return the embed query to use
   */
  public static EmbedQuery create(
      @NotNull String titleKey,
      @NotNull String descriptionKey,
      @Nullable HashMap<String, String> titlePlaceHolders,
      @Nullable HashMap<String, String> descriptionPlaceHolders) {
    return create(
        titlePlaceHolders == null ? Lang.get(titleKey) : Lang.get(titleKey, titlePlaceHolders),
        descriptionPlaceHolders == null
            ? Lang.get(descriptionKey)
            : Lang.get(descriptionKey, descriptionPlaceHolders));
  }

  /**
   * Creates a message query using the title and description from the 'lang.properties' that's why
   * the parameters are the key of those messages. You can also add placeholders to change them
   *
   * @param titleKey the key of the title message
   * @param descriptionKey the key of the description message
   * @param titlePlaceHolders the placeholders for the title
   * @param descriptionPlaceHolders the placeholders for the description
   * @return the message query to use
   */
  @Deprecated
  public static MessageQuery createMessage(
      @NotNull String titleKey,
      @NotNull String descriptionKey,
      @Nullable HashMap<String, String> titlePlaceHolders,
      @Nullable HashMap<String, String> descriptionPlaceHolders) {
    return createMessage(
        titlePlaceHolders == null ? Lang.get(titleKey) : Lang.get(titleKey, titlePlaceHolders),
        descriptionPlaceHolders == null
            ? Lang.get(descriptionKey)
            : Lang.get(descriptionKey, descriptionPlaceHolders));
  }

  /**
   * Creates an embed query using the generic color. The title and description of the embed are the
   * parameters
   *
   * @param title the title for the embed
   * @param description the description for the embed
   * @return the created embed query
   */
  public static EmbedQuery create(@NotNull String title, @NotNull String description) {
    LangMessagesProvider provider = Lang.getProvider();
    ManagerOptions options = Main.getCommandManager().getManagerOptions();
    Color color = ResultType.GENERIC.getColor(options);
    return EmbedFactory.newEmbed(
        title, description, provider.thumbnailUrl(), null, provider.footer(), color, null, false);
  }

  /**
   * Creates a message query using the title and description from the parameters
   *
   * @param title the title of the message
   * @param description the description of the message
   * @return the message query to use
   */
  @Deprecated
  public static MessageQuery createMessage(@NotNull String title, @NotNull String description) {
    LangMessagesProvider provider = Lang.getProvider();
    ManagerOptions options = Main.getCommandManager().getManagerOptions();
    Color color = ResultType.GENERIC.getColor(options);
    return EmbedFactory.newEmbed(
            title,
            description,
            provider.thumbnailUrl(),
            null,
            provider.footer(),
            color,
            null,
            false)
        .getAsMessageQuery();
  }

  /**
   * Creates the announce message for tickets
   *
   * @param ticket the ticket to create the message from
   * @return the message query to use
   */
  public static MessageQuery announce(@NotNull QuestionsTicket ticket) {
    MessageBuilder messageBuilder = MessagesFactory.getMessageBuilder();
    HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
    ManagerOptions options = Main.getCommandManager().getManagerOptions();
    Color color = ResultType.GENERIC.getColor(options);
    LinkedHashMap<String, String> fields = Tickets.getFields(ticket);
    appendRoleTags(ticket, messageBuilder);
    EmbedQuery embed =
        EmbedFactory.newEmbed(
            Lang.get("TICKET_ANNOUNCE_TITLE", placeholders),
            Lang.get("TICKET_ANNOUNCE_DESCRIPTION", placeholders),
            ticket.getUser() == null ? null : ticket.getUser().getAvatarUrl(),
            null,
            Lang.get("FOOTER"),
            color,
            fields,
            true);
    return new MessageQuery(messageBuilder.setEmbed(embed.getEmbed()));
  }

  /**
   * Appends the roles tagged as answers in questions that asked for them.
   *
   * <p>Also for apply and support it will look for the apply or support roles in the config
   *
   * @param ticket the ticket to get the answers for
   * @param messageBuilder the builder to append the tags
   */
  private static void appendRoleTags(
      @NotNull QuestionsTicket ticket, MessageBuilder messageBuilder) {
    DiscordConfiguration config = Main.getDiscordConfiguration();
    if (ticket instanceof Apply) {
      messageBuilder.append(
          Lots.pretty(
              Discord.getAsMention(config.getRolesByKeys(config.getRoleKeys("applyRoles")))));
    } else if (ticket instanceof Support) {
      messageBuilder.append(
          Lots.pretty(
              Discord.getAsMention(config.getRolesByKeys(config.getRoleKeys("supportRoles")))));
    } else {
      ticket
          .getAnswers()
          .forEach(
              (simple, answer) -> {
                if (answer instanceof RoleAnswer) {
                  messageBuilder.append(((RoleAnswer) answer).getTags());
                }
              });
    }
  }

  /**
   * Message query with information about a ticket
   *
   * @param ticket tp get the information from
   * @return the message query to use
   */
  @NotNull
  public static MessageQuery info(@NotNull Ticket ticket) {
    HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
    EmbedBuilder embedBuilder =
        create("TICKET_INFO_TITLE", "TICKET_INFO_DESCRIPTION", placeholders, placeholders)
            .getEmbedBuilder();
    LinkedHashMap<String, String> fields = new LinkedHashMap<>();
    if (ticket instanceof QuestionsTicket) {
      fields.putAll(Tickets.getFields((QuestionsTicket) ticket));
    }
    if (ticket instanceof FreelancingTicket) {
      appendFreelancer((FreelancingTicket) ticket, fields);
    }

    fields.forEach((key, value) -> embedBuilder.addField(key, value, false));
    return new EmbedQuery(embedBuilder).getAsMessageQuery();
  }

  /**
   * Adds the freelancers in the fields to add
   *
   * @param ticket the ticket to get the freelancer from
   * @param fields the fields to append the freelancer as mention from
   */
  private static void appendFreelancer(
      @NotNull FreelancingTicket ticket, LinkedHashMap<String, String> fields) {
    Freelancer freelancer = ticket.getFreelancer();
    if (freelancer != null) {
      User user = freelancer.getUser();
      fields.put(Lang.get("FREELANCER"), user == null ? "Null" : user.getAsMention());
    }
  }
}
