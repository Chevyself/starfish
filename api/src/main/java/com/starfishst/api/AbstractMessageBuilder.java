package com.starfishst.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.starbox.Strings;
import me.googas.starbox.builders.Builder;
import me.googas.starbox.builders.SuppliedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class AbstractMessageBuilder
    implements Builder<Message>, SuppliedBuilder<Map<String, String>, Message> {

  @NonNull @Getter
  private static final Map<String, AbstractMessageBuilder> parents = new HashMap<>();

  @Getter private final String parent;
  @NonNull @Getter private String content;
  @NonNull @Getter private List<EmbedBuilder> embeds;
  @Getter private boolean tts;
  @Getter private String nonce;
  @NonNull @Getter private Set<String> userMentions;
  @NonNull @Getter private Set<String> roleMentions;

  protected AbstractMessageBuilder(
      String parent,
      @NonNull String content,
      @NonNull List<EmbedBuilder> embeds,
      boolean tts,
      String nonce,
      @NonNull Set<String> userMentions,
      @NonNull Set<String> roleMentions) {
    this.parent = parent;
    this.content = content;
    this.embeds = embeds;
    this.tts = tts;
    this.nonce = nonce;
    this.userMentions = userMentions;
    this.roleMentions = roleMentions;
  }

  private AbstractMessageBuilder() {
    this(null, "", new ArrayList<>(), false, null, new HashSet<>(), new HashSet<>());
  }

  @NonNull
  public static AbstractMessageBuilder of(@NonNull String key) {
    return StarfishFiles.Assets.Messages.getMessageFile(key)
        .read(Starfish.getJson(), AbstractMessageBuilder.class)
        .handle(
            e ->
                Starfish.getLogger()
                    .log(Level.SEVERE, e, () -> "Could not load file for message: " + key))
        .provide()
        .orElseThrow(NullPointerException::new);
  }

  @NonNull
  public static AbstractMessageBuilder getParent(String key) {
    AbstractMessageBuilder message = AbstractMessageBuilder.parents.get(key);
    if (message == null) {
      message = AbstractMessageBuilder.of(key);
      AbstractMessageBuilder.parents.put(key, message);
    }
    return message;
  }

  private static String formatOrNull(String string, @NonNull Map<String, String> map) {
    return string != null ? Strings.format(string, map) : null;
  }

  @NonNull
  public AbstractMessageBuilder duplicate() {
    return new AbstractMessageBuilder(
        this.getParent(),
        this.getContent(),
        this.getEmbeds().stream().map(EmbedBuilder::new).collect(Collectors.toList()),
        this.isTts(),
        this.getNonce(),
        new HashSet<>(this.getUserMentions()),
        new HashSet<>(this.getRoleMentions()));
  }

  @NonNull
  public AbstractMessageBuilder append(@NonNull AbstractMessageBuilder other) {
    this.content = other.getContent().isEmpty() ? this.content : other.getContent();
    this.embeds.addAll(other.getEmbeds());
    this.tts = other.isTts();
    this.nonce = other.getNonce();
    this.userMentions.addAll(other.getUserMentions());
    this.roleMentions.addAll(other.getRoleMentions());
    return this;
  }

  @NonNull
  public AbstractMessageBuilder prepare() {
    if (this.parent != null) {
      return AbstractMessageBuilder.getParent(this.parent).prepare().append(this);
    } else {
      return this.duplicate();
    }
  }

  @NonNull
  private AbstractMessageBuilder apply(@NonNull Map<String, String> map) {
    this.setContent(Strings.format(this.getContent(), map));
    this.getEmbeds()
        .forEach(
            builder -> {
              List<MessageEmbed.Field> fieldsCopy = new ArrayList<>(builder.getFields());
              builder.getFields().clear();
              fieldsCopy.forEach(
                  copy ->
                      builder.addField(
                          AbstractMessageBuilder.formatOrNull(copy.getName(), map),
                          AbstractMessageBuilder.formatOrNull(copy.getValue(), map),
                          copy.isInline()));
              builder.setDescription(
                  AbstractMessageBuilder.formatOrNull(
                      builder.getDescriptionBuilder().toString(), map));
            });
    return this;
  }

  @NonNull
  public AbstractMessageBuilder setContent(@NonNull String content) {
    this.content = content;
    return this;
  }

  @NonNull
  public AbstractMessageBuilder setEmbeds(@NonNull List<EmbedBuilder> embeds) {
    this.embeds = embeds;
    return this;
  }

  @NonNull
  public AbstractMessageBuilder setTts(boolean tts) {
    this.tts = tts;
    return this;
  }

  @NonNull
  public AbstractMessageBuilder setNonce(String nonce) {
    this.nonce = nonce;
    return this;
  }

  @NonNull
  public AbstractMessageBuilder setUserMentions(@NonNull Set<String> userMentions) {
    this.userMentions = userMentions;
    return this;
  }

  @NonNull
  public AbstractMessageBuilder setRoleMentions(@NonNull Set<String> roleMentions) {
    this.roleMentions = roleMentions;
    return this;
  }

  @Override
  public @NonNull Message build() {
    return this.build(new HashMap<>());
  }

  @Override
  @NonNull
  public Message build(@NonNull Map<String, String> map) {
    AbstractMessageBuilder message = this.prepare().apply(map);
    return new MessageBuilder()
        .setContent(message.getContent())
        .setEmbeds(
            message.getEmbeds().stream().map(EmbedBuilder::build).collect(Collectors.toList()))
        .setTTS(message.isTts())
        .setNonce(message.getNonce())
        .build();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AbstractMessageBuilder.class.getSimpleName() + "[", "]")
        .add("parent='" + this.parent + "'")
        .add("content='" + this.content + "'")
        .add("embeds=" + this.embeds)
        .add("tts=" + this.tts)
        .add("nonce='" + this.nonce + "'")
        .add("userMentions=" + this.userMentions)
        .add("roleMentions=" + this.roleMentions)
        .toString();
  }
}
