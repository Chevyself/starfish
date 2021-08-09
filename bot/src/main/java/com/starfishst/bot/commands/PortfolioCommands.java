package com.starfishst.bot.commands;

import com.starfishst.api.utility.ValuesMap;
import com.starfishst.bot.commands.objects.Freelancer;
import java.util.Collections;
import java.util.List;
import me.googas.commands.annotations.Parent;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;

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
  public Result portfolio(Freelancer freelancer) {
    String string;
    if (freelancer.getPreferences().getList("portfolio").isEmpty()) {
      string = "Empty";
    } else {
      string = ValuesMap.pretty(freelancer.getPreferences().getList("portfolio"));
    }
    return new Result(
        freelancer
            .getLocaleFile()
            .get("portfolio.freelancer", Collections.singletonMap("portfolio", string)));
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
      Freelancer freelancer,
      @Required(name = "portfolio.value", description = "portfolio.value.desc") String value) {
    List<Object> portfolio = freelancer.getPreferences().getList("portfolio");
    portfolio.add(value);
    freelancer.getPreferences().add("portfolio", portfolio);
    return new Result(
        freelancer
            .getLocaleFile()
            .get("portfolio.add.result", Collections.singletonMap("value", value)));
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
      Freelancer freelancer,
      @Required(name = "portfolio.value", description = "portfolio.value.remove.desc")
          String value) {
    List<String> portfolio = freelancer.getPreferences().getList("portfolio");
    portfolio.removeIf(string -> string.equalsIgnoreCase(value));
    freelancer.getPreferences().add("portfolio", portfolio);
    return new Result(
        freelancer
            .getLocaleFile()
            .get("portfolio.remove.result", Collections.singletonMap("value", value)));
  }
}
