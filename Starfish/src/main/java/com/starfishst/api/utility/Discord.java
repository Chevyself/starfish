package com.starfishst.api.utility;

import com.starfishst.api.Starfish;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.JDA;
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

/** Various utils for discord */
public class Discord {

  /** This list contains the permissions needed for a user to read and write inside a channel */
  @NonNull
  public static final List<Permission> ALLOWED =
      Lots.list(
              Permission.VOICE_CONNECT,
              Permission.VOICE_SPEAK,
          Permission.VOICE_STREAM,
          Permission.VOICE_USE_VAD,
          Permission.MESSAGE_READ,
          Permission.MESSAGE_WRITE,
          Permission.MESSAGE_EMBED_LINKS,
          Permission.MESSAGE_HISTORY);

  /** This list contains the permissions needed for a user to read inside a channel */
  @NonNull
  public static final List<Permission> ALLOWED_SEE =
      Lots.list(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY);

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
  @NonNull
  public static <I extends IPermissionHolder> Category validateCategory(
      Category category,
      @NonNull Guild guild,
      @NonNull String name,
      boolean removePerms,
      List<I> allow,
      List<I> allowSee) {
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
      @NonNull C channel, @NonNull List<I> toAllow, @NonNull Collection<Permission> permissions) {
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
      @NonNull C channel, @NonNull I toAllow, @NonNull Collection<Permission> permissions) {
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
  public static void removePublicPerms(@NonNull GuildChannel channel) {
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
  @NonNull
  public static <I extends IMentionable> List<String> getAsMention(@NonNull List<I> mentionables) {
    List<String> tags = new ArrayList<>();
    mentionables.forEach(mentionable -> tags.add(mentionable.getAsMention()));
    return tags;
  }

  /**
   * Get a collection of roles ids as mentions
   *
   * @param ids the ids of the roles
   * @return the mention of the roles
   */
  @NonNull
  public static List<String> getRolesAsMention(Collection<Long> ids) {
    List<String> mentions = new ArrayList<>();
    if (ids == null || ids.isEmpty()) return mentions;
    for (Long id : ids) {
      if (id != null) {
        Role role = getRole(id);
        if (role != null) mentions.add(role.getAsMention());
      }
    }
    return mentions;
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
  @NonNull
  public static <I extends IPermissionHolder> TextChannel validateChannel(
      TextChannel channel,
      @NonNull Guild guild,
      @NonNull String channelName,
      boolean removePerms,
      List<I> allow,
      List<I> allowSee) {
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
      @NonNull C channel, boolean removePerms, List<I> allow, List<I> allowSee) {
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
      @NonNull C channel, @NonNull I member) {
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
      @NonNull C channel, @NonNull List<I> members) {
    for (I member : members) {
      disallow(channel, member);
    }
  }

  /**
   * Checks if a member has certain role
   *
   * @param member the member to check
   * @param query the role querying
   * @return true if the member has the role
   */
  public static boolean hasRole(@NonNull Member member, @NonNull Role query) {
    for (Role role : member.getRoles()) {
      if (role.getIdLong() == query.getIdLong()) return true;
    }
    return false;
  }

  /**
   * Checks if the member has at least one role from the querying list
   *
   * @param member the member to check
   * @param query the list querying
   * @return true if the member has at least one role
   */
  public static boolean hasRole(@NonNull Member member, @NonNull List<Role> query) {
    if (query.isEmpty()) return false;
    for (Role role : query) {
      if (hasRole(member, role)) return true;
    }
    return false;
  }

  /**
   * Get a role by its id
   *
   * @param id the id of the role to get
   * @return the role if found null otherwise
   */
  public static Role getRole(long id) {
    JDA jda = Starfish.getJdaConnection().getJda();
    if (jda == null) return null;
    return jda.getRoleById(id);
  }

  /**
   * Get a list of roles from a collection of them ids
   *
   * @param ids the ids of the roles to get
   * @return the roles
   */
  @NonNull
  public static List<Role> getRoles(Collection<Long> ids) {
    List<Role> roles = new ArrayList<>();
    if (ids == null || ids.isEmpty()) return roles;
    for (Long id : ids) {
      if (id != null) {
        Role role = getRole(id);
        if (role != null) {
          roles.add(role);
        }
      }
    }
    return roles;
  }
}
