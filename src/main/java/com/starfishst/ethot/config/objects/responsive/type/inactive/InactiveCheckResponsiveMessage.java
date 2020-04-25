package com.starfishst.ethot.config.objects.responsive.type.inactive;

import com.starfishst.core.utils.time.TimeUtils;
import com.starfishst.ethot.config.objects.responsive.ReactionResponse;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessage;
import com.starfishst.ethot.config.objects.responsive.ResponsiveMessageType;
import com.starfishst.ethot.util.Unicode;
import java.time.OffsetDateTime;
import java.util.List;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public class InactiveCheckResponsiveMessage extends ResponsiveMessage {

  private final long createdAt;
  private final long ticket;
  private final List<Long> reacted;
  private boolean finished;

  public InactiveCheckResponsiveMessage(
      long id, long createdAt, long ticket, @NotNull List<Long> reacted, boolean finished) {
    super(
        ResponsiveMessageType.INACTIVE_CHECK,
        id,
        true,
        new ReactionResponse() {
          @Override
          public @NotNull String getUnicode() {
            return Unicode.WHITE_CHECK_MARK;
          }

          @Override
          public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
            long id = event.getUser().getIdLong();
            if (!reacted.contains(id)) {
              reacted.add(id);
            }
          }
        });
    this.createdAt = createdAt;
    this.ticket = ticket;
    this.reacted = reacted;
    this.finished = finished;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getCreatedAtDate() {
    return TimeUtils.millisToOffsetDateTime(createdAt);
  }

  public long getTicket() {
    return ticket;
  }

  public List<Long> getReacted() {
    return reacted;
  }

  public boolean isFinished() {
    return finished;
  }

  public void finish() {
    this.finished = true;
  }
}
