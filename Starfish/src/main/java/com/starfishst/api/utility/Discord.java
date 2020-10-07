package com.starfishst.api.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.googas.commons.Atomic;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Various utils for discord */
public class Discord {

  /** This list contains the permissions needed for a user to read and write inside a channel */
  @NotNull
  public static final List<Permission> ALLOWED =
      Lots.list(
          Permission.MESSAGE_READ,
          Permission.MESSAGE_WRITE,
          Permission.MESSAGE_EMBED_LINKS,
          Permission.MESSAGE_HISTORY);

  /** This list contains the permissions needed for a user to read inside a channel */
  @NotNull
  public static final List<Permission> ALLOWED_SEE =
      Lots.list(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY);

  /**
   * Validates a category by checking that it is not null and is not full. If it is full it will
   * create a copy
   *
   * @param category to check
   * @param guild the guild where to create the category if null or full
   * @param name to put in case you have to create a new category
   * @param removePerms if you want to remove the perms for @everyone
   * @param allow the permission holders that will be able to read and write inside the category
   * @param allowSee the permission holders that will be able to read inside the category
   * @param <I> the permission holders type
   * @return the category
   */
  @NotNull
  public static <I extends IPermissionHolder> Category validateCategory(
      @Nullable Category category,
      @NotNull Guild guild,
      @NotNull String name,
      boolean removePerms,
      @Nullable List<I> allow,
      @Nullable List<I> allowSee) {
    if (category != null && category.getChannels().size() < 50) {
      return category;
    } else {
      if (category != null) {
        return category.createCopy().complete();
      } else {
        category = guild.createCategory(name).complete();
        applyPermissions(category, removePerms, allow, allowSee);
        return category;
      }
    }
  }

  /**
   * Allows a list of permission holders inside a channel
   *
   * @param channel the guild channel
   * @param toAllow the list to allow
   * @param permissions the permissions to allow
   * @param <I> the permission holders type
   * @param <C> the channel type
   */
  public static <I extends IPermissionHolder, C extends GuildChannel> void allow(
      @NotNull C channel, @NotNull List<I> toAllow, @NotNull Collection<Permission> permissions) {
    toAllow.forEach(allow -> allow(channel, allow, permissions));
  }

  /**
   * Allows a permission holder inside a channel
   *
   * @param channel the guild channel
   * @param toAllow the holder to allow
   * @param permissions the permissions to allow
   * @param <I> the permission holders type
   * @param <C> the channel type
   */
  public static <I extends IPermissionHolder, C extends GuildChannel> void allow(
      @NotNull C channel, @NotNull I toAllow, @NotNull Collection<Permission> permissions) {
    if (channel.getPermissionOverride(toAllow) != null) {
      channel.putPermissionOverride(toAllow).setAllow(permissions).queue();
    } else {
      channel.createPermissionOverride(toAllow).setAllow(permissions).queue();
    }
  }

  /**
   * Removes all the permissions of @everyone (public role)
   *
   * @param channel the channel to remove the public perms from
   */
  public static void removePublicPerms(@NotNull GuildChannel channel) {
    channel
        .putPermissionOverride(channel.getGuild().getPublicRole())
        .setDeny(Permission.ALL_GUILD_PERMISSIONS)
        .queue();
  }

  /**
   * Get a list of mentions from a list of mentionables
   *
   * @param mentionables the mentionables to get the mentions from
   * @return the list of mentions
   * @param <I> the mentionable interface
   */
  @NotNull
  public static <I extends IMentionable> List<String> getAsMention(@NotNull List<I> mentionables) {
    List<String> tags = new ArrayList<>();
    mentionables.forEach(mentionable -> tags.add(mentionable.getAsMention()));
    return tags;
  }

  /**
   * Validates a text channel
   *
   * @param channel the channel to validate
   * @param guild the guild to create the channel in case it is not found
   * @param channelName the channel name in case creating one is required
   * @param removePerms if removing @everyone perms is needed
   * @param allow the allowed roles in the channel
   * @param allowSee the allowed roles to see in the channel
   * @return the not null challenge
   * @param <I> the permission holder interface
   */
  @NotNull
  public static <I extends IPermissionHolder> TextChannel validateChannel(
      @Nullable TextChannel channel,
      @NotNull Guild guild,
      @NotNull String channelName,
      boolean removePerms,
      @Nullable List<I> allow,
      @Nullable List<I> allowSee) {
    if (channel == null) {
      channel = guild.createTextChannel(channelName).complete();
      applyPermissions(channel, removePerms, allow, allowSee);
    }
    return channel;
  }

  /**
   * Applies permissions in a discord channel
   *
   * @param channel the channel to apply permissions to
   * @param removePerms if set to true every permission for the public role (@everyone) will be set
   *     to disabled
   * @param allow the allowed roles inside the channel
   * @param allowSee the allowed roles to see inside the channel
   * @param <C> the guild channel interface
   * @param <I> the permission holder interface
   */
  private static <C extends GuildChannel, I extends IPermissionHolder> void applyPermissions(
      @NotNull C channel,
      boolean removePerms,
      @Nullable List<I> allow,
      @Nullable List<I> allowSee) {
    if (removePerms) {
      removePublicPerms(channel);
    }
    if (allow != null && !allow.isEmpty()) {
      allow(channel, allow, ALLOWED);
    }
    if (allowSee != null && !allowSee.isEmpty()) {
      allow(channel, allowSee, ALLOWED_SEE);
    }
  }

  /**
   * Disallows a permission holder from a channel. This deletes its permission override
   *
   * @param channel the channel to disallow the holder from
   * @param member the holder to disallow
   * @param <I> the holder type
   * @param <C> the channel type
   */
  public static <I extends IPermissionHolder, C extends GuildChannel> void disallow(
      @NotNull C channel, @NotNull I member) {
    PermissionOverride permissionOverride = channel.getPermissionOverride(member);
    if (permissionOverride != null) {
      permissionOverride.delete().queue();
    }
  }

  /**
   * Disallows a list of permission holders from a channel. This deletes its permission override
   *
   * @param channel the channel to disallow the holders from
   * @param members the holders to disallow
   * @param <I> the holder type
   * @param <C> the channel type
   */
  public static <I extends IPermissionHolder, C extends GuildChannel> void disallow(
      @NotNull C channel, @NotNull List<I> members) {
    members.forEach(member -> disallow(channel, member));
  }

  /**
   * Checks if a member has certain role
   *
   * @param member the member to check
   * @param query the role querying
   * @return true if the member has the role
   */
  public static boolean hasRole(@NotNull Member member, @NotNull Role query) {
    Atomic<Boolean> atomic = new Atomic<>(false);
    member
        .getRoles()
        .forEach(
            role -> {
              if (role.getIdLong() == query.getIdLong()) {
                atomic.set(true);
              }
            });
    return atomic.get();
  }

  /**
   * Checks if the member has at least one role from the querying list
   *
   * @param member the member to check
   * @param query the list querying
   * @return true if the member has at least one role
   */
  public static boolean hasRole(@NotNull Member member, @NotNull List<Role> query) {
    Atomic<Boolean> atomic = new Atomic<>(false);
    query.forEach(
        role -> {
          if (hasRole(member, role)) {
            atomic.set(true);
          }
        });
    return atomic.get();
  }
}
