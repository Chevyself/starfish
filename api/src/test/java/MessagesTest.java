import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starfishst.api.AbstractMessageBuilder;
import com.starfishst.api.Fallback;
import com.starfishst.api.Starfish;
import com.starfishst.api.StarfishBot;
import com.starfishst.api.StarfishFiles;
import com.starfishst.api.configuration.Configuration;
import com.starfishst.api.configuration.DiscordConfiguration;
import com.starfishst.api.events.StarfishHandler;
import com.starfishst.api.loader.TicketManager;
import com.starfishst.api.utility.JdaConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commands.jda.CommandManager;
import me.googas.io.context.Json;
import me.googas.lazy.Loader;
import me.googas.net.cache.Cache;
import me.googas.starbox.addons.AddonLoader;
import me.googas.starbox.builders.MapBuilder;
import me.googas.starbox.events.ListenerManager;
import me.googas.starbox.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class MessagesTest {

  public static void main(String[] args) throws LoginException, InterruptedException {
    Starfish.setInstance(new TestBot());
    StarfishFiles.Assets.Messages.DIR.mkdirs();
    JDA jda =
        JDABuilder.create(
                "",
                Arrays.asList(GatewayIntent.values()))
            .build()
            .awaitReady();
    TextChannel channel = Objects.requireNonNull(jda.getTextChannelById(817549065168355371L));
    Arrays.asList("parent", "parent2", "message")
        .forEach(
            key ->
                channel
                    .sendMessage(
                        AbstractMessageBuilder.of(key)
                            .build(MapBuilder.of("placeholder", "._.XD").build()))
                    .queue());
  }

  public static class TestBot implements StarfishBot {

    @NonNull @Getter
    private final Json json = new Json(new GsonBuilder().setPrettyPrinting().create());

    @Override
    public @NonNull Configuration getConfiguration() {
      return null;
    }

    @Override
    public @NonNull DiscordConfiguration getDiscordConfiguration() {
      return null;
    }

    @Override
    public @NonNull Fallback getFallback() {
      return null;
    }

    @Override
    public @NonNull Cache getCache() {
      return null;
    }

    @Override
    public @NonNull Loader getLoader() {
      return null;
    }

    @Override
    public @NonNull Logger getLogger() {
      return null;
    }

    @Override
    public @NonNull CommandManager getCommandManager() {
      return null;
    }

    @Override
    public @NonNull ListenerManager getListenerManager() {
      return null;
    }

    @Override
    public @NonNull Collection<StarfishHandler> getHandlers() {
      return null;
    }

    @Override
    public @NonNull TicketManager getTicketManager() {
      return null;
    }

    @Override
    public @NonNull AddonLoader getAddonLoader() {
      return null;
    }

    @Override
    public @NonNull Scheduler getScheduler() {
      return null;
    }

    @Override
    public @NonNull Gson getGson() {
      return null;
    }

    @Override
    public @NonNull JdaConnection getJdaConnection() {
      return null;
    }
  }
}
