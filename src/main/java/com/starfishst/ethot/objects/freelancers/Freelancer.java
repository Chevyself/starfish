package com.starfishst.ethot.objects.freelancers;

import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.ethot.config.Configuration;
import com.starfishst.ethot.config.language.Lang;
import com.starfishst.ethot.exception.DiscordManipulationException;
import com.starfishst.ethot.tickets.TicketManager;
import com.starfishst.ethot.util.Discord;
import com.starfishst.ethot.util.Freelancers;
import com.starfishst.ethot.util.Maps;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

/** This class represents the freelancers inside the server */
public class Freelancer extends Catchable {

  /** The id of the freelancer */
  private final long id;
  /** The portfolio of the freelancer */
  @NotNull private final List<String> portfolio;
  /** The map of ratings of the freelancer */
  @NotNull private final HashMap<Long, Integer> rating;

  /**
   * Create an instance
   *
   * @param portfolio the portfolio of the freelancer
   * @param rating the rating of the freelancer
   * @param id the id of the freelancer
   */
  public Freelancer(
      @NotNull List<String> portfolio, @NotNull HashMap<Long, Integer> rating, long id) {
    super(Configuration.getInstance().getToUnloadFreelancer());
    this.id = id;
    this.portfolio = portfolio;
    this.rating = rating;
  }

  /**
   * Adds a rating for the freelancer
   *
   * @param id the id of the user rating the freelancer
   * @param value the rate given by the user
   */
  public void addRating(long id, int value) {
    this.rating.put(id, value);
  }

  /**
   * Get the portfolio as a single string
   *
   * @return the portfolio as a string
   */
  @NotNull
  public String getPortfolioAsString() {
    return Lots.pretty(getPortfolio());
  }

  /**
   * Get the id of the freelancer
   *
   * @return the id of the freelancer
   */
  public long getId() {
    return id;
  }

  /**
   * Get the portfolio of the freelancer
   *
   * @return the portfolio of the freelancer
   */
  @NotNull
  public List<String> getPortfolio() {
    return portfolio;
  }

  /**
   * Get the map of ratings of the freelancer
   *
   * @return the map of ratings
   */
  @NotNull
  public HashMap<Long, Integer> getRating() {
    return rating;
  }

  @Override
  public void onSecondsPassed() {}

  /**
   * Get the freelancer as the member of the guild
   *
   * @return the freelancer as member
   * @throws DiscordManipulationException in case that the member could not be gotten because of a
   *     discord problem
   */
  @Nullable
  public Member getMember() throws DiscordManipulationException {
    return Discord.getMember(getUser());
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

  @Override
  public void onRemove() {
    TicketManager.getInstance().getLoader().saveFreelancer(this);
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
