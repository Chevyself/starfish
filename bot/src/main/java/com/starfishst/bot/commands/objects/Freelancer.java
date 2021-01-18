package com.starfishst.bot.commands.objects;

import com.starfishst.api.user.BotUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;

/** This object is used in commands to give an easy way to provide users that are freelancers */
public class Freelancer {

  @NonNull @Getter @Delegate private final BotUser user;

  public Freelancer(@NonNull BotUser user) {
    this.user = user;
  }
}
