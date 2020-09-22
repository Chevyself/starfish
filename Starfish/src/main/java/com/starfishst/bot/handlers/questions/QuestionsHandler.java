package com.starfishst.bot.handlers.questions;

import com.starfishst.api.data.tickets.Ticket;
import com.starfishst.api.data.tickets.TicketStatus;
import com.starfishst.api.data.tickets.TicketType;
import com.starfishst.api.events.tickets.TicketLoadedEvent;
import com.starfishst.bot.handlers.StarfishEventHandler;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.simple.Lots;
import com.starfishst.simple.gson.GsonProvider;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.Null;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Handles questions tickets
 */
public class QuestionsHandler implements StarfishEventHandler {

    /**
     * The map of the ticket and the current question it is in
     */
    @NotNull
    private final HashMap<Ticket, Integer> current = new HashMap<>();

    @NotNull
    private final HashMap<TicketType, QuestionsConfiguration> questions;


    /**
     * Create the questions handler
     */
    public QuestionsHandler() {
        this.questions = new HashMap<>();
        this.loadQuestions();
    }

    /**
     * Loads the questions
     */
    private void loadQuestions() {
        List<TicketType> questionable = Lots.list(TicketType.APPLY, TicketType.ORDER, TicketType.PRODUCT, TicketType.SUPPORT);
        for (TicketType type : questionable) {
            try {
                File file = CoreFiles.getFileOrResource(CoreFiles.currentDirectory() + "/assets/questions/" + type.toString().toLowerCase() + ".json", "questions/" + type.toString().toLowerCase() + ".json");
                FileReader reader = new FileReader(file); //  This can't throw an exception for not finding the file
                questions.put(type, GsonProvider.GSON.fromJson(reader, QuestionsConfiguration.class));
                reader.close();
                // TODO print that questions were loaded
            } catch (IOException e) {
                // TODO add fallback
                e.printStackTrace();
            }
        }
    }

    @Listener(priority = ListenPriority.HIGHEST)
    public void onTicketLoadEvent(TicketLoadedEvent event) {
        QuestionsConfiguration questions = this.questions.get(event.getTicket().getTicketType());
        TextChannel channel = event.getTicket().getTextChannel();
        if (event.getTicket().getTicketStatus() == TicketStatus.CREATING && questions != null && !questions.getQuestions().isEmpty() && channel != null) {
            current.put(event.getTicket(), 0);
            questions.getQuestions().get(0).getQuery().send(channel);
        }
    }

    @SubscribeEvent
    public void onMessageReceived(GuildMessageReceivedEvent event) {
        Ticket ticket = getTicketByChannel(event.getChannel());
        if (ticket != null) {
            QuestionsConfiguration questions = this.questions.get(ticket.getTicketType());
            Question current = questions.getQuestions().get(this.current.get(ticket));

        }
    }

    /**
     * Get a ticket using a channel
     *
     * @param channel the channel to get the ticket from
     * @return the ticket if found else null
     */
    @Nullable
    private Ticket getTicketByChannel(@NotNull TextChannel channel) {
        for (Ticket ticket : this.current.keySet()) {
            TextChannel textChannel = ticket.getTextChannel();
            if (textChannel != null && textChannel.equals(channel)) {
                return ticket;
            }
        }
        return null;
    }


    @Override
    public String getName() {
        return "questions";
    }

    @Override
    public void onUnload() {
        this.questions.clear();
    }
}
