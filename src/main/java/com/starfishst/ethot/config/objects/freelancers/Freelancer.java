package com.starfishst.ethot.config.objects.freelancers;

import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.ethot.Main;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Freelancers;
import com.starfishst.ethot.util.Maps;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the freelancers inside the server
 *
 * @author Chevy
 * @version 1.0.0
 */
public class Freelancer extends Catchable {

  private final long id;
  @NotNull private final List<String> portfolio;
  @NotNull private final HashMap<Long, Integer> rating;

  public Freelancer(
      @NotNull List<String> portfolio, @NotNull HashMap<Long, Integer> rating, long id) {
    super(Main.getConfiguration().getToUnloadFreelancer());
    this.id = id;
    this.portfolio = portfolio;
    this.rating = rating;
  }

  public void addRating(long id, int value) {
    this.rating.put(id, value);
  }

  @NotNull
  public String getPortfolioAsString() {
    return Lots.pretty(getPortfolio());
  }

  public long getId() {
    return id;
  }

  @NotNull
  public List<String> getPortfolio() {
    return portfolio;
  }

  @NotNull
  public HashMap<Long, Integer> getRating() {
    return rating;
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    Main.getManager().getLoader().saveFreelancer(this);
  }

  /**
   * Get the freelancer as a discord user
   *
   * @return the freelancer as discord user if found
   */
  @Nullable
  public User getUser() {
    return Discord.getUser(id);
  }

  /**
   * Get the freelancer as a a document for mongo
   *
   * @return the document of the freelancer
   */
  @NotNull
  public Document getDocument() {
    Document document = new Document();
    document.append("id", id);
    document.append("portfolio", portfolio);
    document.append("rating", Maps.getWithKeysAsStrings(rating));
    return document;
  }

  /**
   * Return the rating of the freelancer as an string
   *
   * @return the rating as string
   */
  @NotNull
  public String getRatingAsString() {
    double rating = Freelancers.getRating(this);
    return rating == 0 ? Lang.get("NO_RATING") : String.format("%.02f", rating);
  }

  /**
   * Get the freelancer as the member of the guild
   *
   * @return the freelancer as member
   */
  @Nullable
  public Member getMember() throws DiscordManipulationException {
    return Discord.getMember(getUser());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    Freelancer that = (Freelancer) object;

    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
