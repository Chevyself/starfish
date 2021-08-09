package com.starfishst.api;

import com.starfishst.api.tickets.TicketType;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.io.context.Json;
import me.googas.io.context.PropertiesContext;

import java.net.URL;
import java.util.Objects;

public class StarfishFiles {

    @NonNull
    public static final StarboxFile CONFIG = new StarboxFile(StarboxFile.DIR, "config.json");

    @NonNull
    public static final StarboxFile DISCORD = new StarboxFile(StarboxFile.DIR, "discord.json");

    @NonNull
    public static final StarboxFile LIBS = new StarboxFile(StarboxFile.DIR, "libs");

    @NonNull
    public static final StarboxFile LOGS = new StarboxFile(StarboxFile.DIR, "logs");

    @NonNull
    public static final StarboxFile ADDONS = new StarboxFile(StarboxFile.DIR, "addons");

    @NonNull
    public static StarboxFile getLogFile() {
        return new StarboxFile(StarfishFiles.LOGS, "log-" + System.currentTimeMillis() + ".log");
    }

    public static class Resources {

        @NonNull
        public static final ClassLoader LOADER = StarfishFiles.class.getClassLoader();
        @NonNull
        public static final URL LIBS = Resources.getResource("libs.json");

        @NonNull
        public static URL getResource(@NonNull String name) {
            return Objects.requireNonNull(Resources.LOADER.getResource(name));
        }

        @NonNull
        public static URL getLangResource(@NonNull String lang) {
            return StarfishFiles.Resources.getResource("lang/" + lang + ".properties");
        }

        @NonNull
        public static URL getQuestionResource(@NonNull TicketType type) {
            return Resources.getResource("questions/" + type.toString().toLowerCase() + ".json");
        }
   }

   public static class Contexts {

        @NonNull
       public static final PropertiesContext PROPERTIES =  new PropertiesContext();
    }

   public static class Transcripts {

        @NonNull
       public static final StarboxFile DIR = new StarboxFile(StarboxFile.DIR, "transcripts");

        @NonNull
        public static StarboxFile getTicketFile(long ticketId) {
            return new StarboxFile(Transcripts.DIR, "ticket-" + ticketId + ".txt");
        }
   }


   public static class Assets {

        @NonNull
        public static final StarboxFile DIR = new StarboxFile(StarboxFile.DIR, "assets");

        public static class Lang {

            @NonNull
            public static final StarboxFile DIR = new StarboxFile(Assets.DIR, "lang");

            @NonNull
            public static StarboxFile getLangFile(@NonNull String lang) {
                return new StarboxFile(Lang.DIR, lang + ".properties");
            }
        }

        public static class Questions {

            @NonNull
            public static final StarboxFile DIR = new StarboxFile(Assets.DIR, "questions");

            @NonNull
        public static StarboxFile getQuestionsFile(@NonNull TicketType type) {
                return new StarboxFile(Questions.DIR, type.toString().toLowerCase() + ".json");
            }

        }

   }
}
