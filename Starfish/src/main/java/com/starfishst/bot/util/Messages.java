package com.starfishst.bot.util;

import com.starfishst.bot.Main;
import com.starfishst.bot.oldconfig.DiscordConfiguration;
import com.starfishst.bot.oldconfig.language.Lang;
import com.starfishst.bot.oldconfig.language.LangMessagesProvider;
import com.starfishst.bot.objects.freelancers.Freelancer;
import com.starfishst.bot.objects.questions.RoleAnswer;
import com.starfishst.bot.oldtickets.type.Apply;
import com.starfishst.bot.oldtickets.type.FreelancingTicket;
import com.starfishst.bot.oldtickets.type.QuestionsTicket;
import com.starfishst.bot.oldtickets.type.Quote;
import com.starfishst.bot.oldtickets.type.Report;
import com.starfishst.bot.oldtickets.type.Support;
import com.starfishst.bot.oldtickets.type.Ticket;
import com.starfishst.commands.ManagerOptions;
import com.starfishst.commands.listener.CommandListener;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedFactory;
import com.starfishst.commands.utils.embeds.EmbedQuery;
import com.starfishst.commands.utils.message.MessageQuery;
import com.starfishst.commands.utils.message.MessagesFactory;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.Strings;
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
  @Deprecated
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
   * Get the embed that is used for the announcement of a ticket
   *
   * @param ticket the ticket that is being announced
   * @return the embed query for the ticket that is going to be announced
   */
  public static EmbedQuery announceEmbed(@NotNull QuestionsTicket ticket) {
    HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
    ManagerOptions options = Main.getCommandManager().getManagerOptions();
    Color color = ResultType.GENERIC.getColor(options);
    LinkedHashMap<String, String> fields = Tickets.getFields(ticket);
    String imageUrl = getImageUrl(ticket);
    String footer = getAnnounceFooter(ticket);
    EmbedQuery embed =
        EmbedFactory.newEmbed(
            Lang.get("TICKET_ANNOUNCE_TITLE", placeholders),
            Lang.get("TICKET_ANNOUNCE_DESCRIPTION", placeholders),
            imageUrl,
            null,
            footer,
            color,
            fields,
            true);
    return new EmbedQuery(embed.getEmbedBuilder());
  }

  /**
   * Get the footer that is used in the announcement of a ticket
   *
   * @param ticket the ticket that is being announced
   * @return the footer
   */
  private static String getAnnounceFooter(QuestionsTicket ticket) {
    String init = Lang.get("FOOTER");
    if (ticket instanceof Quote) {
      StringBuilder builder = Strings.getBuilder().append(init);
      String tip = Lang.get("QUOTE_TIP", Tickets.getPlaceholders(ticket));
      if (!tip.isEmpty()) {
        builder.append(tip);
      }
      return builder.toString();
    } else {
      return init;
    }
  }

  /**
   * Get the image url from a ticket
   *
   * @param ticket the ticket to get the image for
   * @return the image url or null if it may not have one
   */
  @Nullable
  private static String getImageUrl(@NotNull QuestionsTicket ticket) {
    if (ticket instanceof FreelancingTicket) {
      return Main.getCommandManager().getMessagesProvider().thumbnailUrl();
    } else {
      return ticket.getUser() == null ? null : ticket.getUser().getAvatarUrl();
    }
  }

  /**
   * Creates the announce message for tickets
   *
   * @param ticket the ticket to create the message from
   * @return the message query to use
   */
  public static MessageQuery announce(@NotNull QuestionsTicket ticket) {
    MessageBuilder builder = MessagesFactory.getMessageBuilder();
    appendRoleTags(ticket, builder);
    return new MessageQuery(builder.setEmbed(announceEmbed(ticket).getEmbed()));
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
    DiscordConfiguration config = DiscordConfiguration.getInstance();
    if (ticket instanceof Apply) {
      messageBuilder.append(
          Lots.pretty(
              Discord.getAsMention(config.getRolesByKeys(config.getRolesKeys("applyRoles")))));
    } else if (ticket instanceof Support) {
      messageBuilder.append(
          Lots.pretty(
              Discord.getAsMention(config.getRolesByKeys(config.getRolesKeys("supportRoles")))));
    } else if (ticket instanceof Report) {
      messageBuilder.append(
          Lots.pretty(
              Discord.getAsMention(config.getRolesByKeys(config.getRolesKeys("reportRoles")))));
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

  /**
   * Create a message to tell freelancers that {@link FreelancingTicket} is claimed
   *
   * @param ticket the ticket that is now claimed
   * @param titleKey the key of the title of the embed
   * @param descriptionKey the key of the description of the embed
   * @return the message query
   */
  public static MessageQuery claimed(
      @NotNull QuestionsTicket ticket, String titleKey, String descriptionKey) {
    MessageBuilder builder = MessagesFactory.getMessageBuilder();
    ManagerOptions options = Main.getCommandManager().getManagerOptions();
    Color color = ResultType.GENERIC.getColor(options);
    HashMap<String, String> placeholders = Tickets.getPlaceholders(ticket);
    appendRoleTags(ticket, builder);
    EmbedBuilder embedBuilder = announceEmbed(ticket).getEmbedBuilder();
    embedBuilder.setTitle(Lang.get(titleKey, placeholders));
    embedBuilder.setDescription(Lang.get(descriptionKey, placeholders));
    embedBuilder.setColor(color);
    return new MessageQuery(builder.setEmbed(embedBuilder.build()));
  }
}
