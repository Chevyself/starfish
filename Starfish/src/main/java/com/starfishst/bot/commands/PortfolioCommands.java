package com.starfishst.bot.commands;

import com.starfishst.bot.data.StarfishFreelancer;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.maps.Maps;
import java.util.List;

/** Commands for managing portfolio */
public class PortfolioCommands {

  /**
   * Makes a freelancer see their portfolio
   *
   * @param freelancer the freelancer to see its portfolio
   * @return the result of the command
   */
  @Parent
  @Command(aliases = "portfolio", description = "portfolio.desc")
  public Result portfolio(StarfishFreelancer freelancer) {
    String string;
    if (freelancer.getPreferences().getLisValue("portfolio").isEmpty()) {
      string = "Empty";
    } else {
      string = Lots.pretty(freelancer.getPreferences().getLisValue("portfolio"));
    }
    return new Result(
        freelancer
            .getLocaleFile()
            .get("portfolio.freelancer", Maps.singleton("portfolio", string)));
  }

  /**
   * Adds a value to a freelancers portfolio
   *
   * @param freelancer the freelancer to add a value to its its portfolio
   * @param value the value to add
   * @return the result of the command
   */
  @Command(aliases = "add", description = "portfolio.add")
  public Result add(
      StarfishFreelancer freelancer,
      @Required(name = "portfolio.value", description = "portfolio.value.desc") String value) {
    List<Object> portfolio = freelancer.getPreferences().getLisValue("portfolio");
    portfolio.add(value);
    freelancer.getPreferences().addValue("portfolio", portfolio);
    return new Result(
        freelancer.getLocaleFile().get("portfolio.add.result", Maps.singleton("value", value)));
  }

  /**
   * Removes a value to a freelancers portfolio
   *
   * @param freelancer the freelancer to remove a value to its its portfolio
   * @param value the value to remove from the portfolio
   * @return the result of the command
   */
  @Command(aliases = "remove", description = "portfolio.remove")
  public Result remove(
      StarfishFreelancer freelancer,
      @Required(name = "portfolio.value", description = "portfolio.value.remove.desc")
          String value) {
    List<String> portfolio = freelancer.getPreferences().getLisValue("portfolio");
    portfolio.removeIf(string -> string.equalsIgnoreCase(value));
    freelancer.getPreferences().addValue("portfolio", portfolio);
    return new Result(
        freelancer.getLocaleFile().get("portfolio.remove.result", Maps.singleton("value", value)));
  }
}
