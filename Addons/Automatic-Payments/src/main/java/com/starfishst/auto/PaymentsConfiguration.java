package com.starfishst.auto;

import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.CoreFiles;

import java.io.FileReader;
import java.io.IOException;

public class PaymentsConfiguration {

    @Getter
    private final int port;
    @NonNull @Getter
    private final String key;
    @NonNull @Getter
    private final String password;
    @NonNull @Getter
    private final String alias;
    @NonNull @Getter
    private final String note;
    @NonNull @Getter
    private final String url;
    @NonNull @Getter
    private final String clientId;
    @NonNull @Getter
    private final String clientSecret;
    @NonNull @Getter
    private final String node;

    public PaymentsConfiguration(int port, @NonNull String key, @NonNull String password, @NonNull String alias, @NonNull String note, @NonNull String url, @NonNull String node) {
        this.port = port;
        this.key = key;
        this.password = password;
        this.alias = alias;
        this.note = note;
        this.url = url;
        this.node = node;
    }

    /**
     * @deprecated this constructor may only be used by gson
     */
    public PaymentsConfiguration() {
        this(3000, "", "", "", "", "", "");
    }

    @NonNull
    public static PaymentsConfiguration init() {
        FileReader reader = null;
        PaymentsConfiguration configuration = fallback();
        try {
            reader =
                    new FileReader(CoreFiles.getFileOrResource(CoreFiles.currentDirectory() + "/addons/Payments/", "config.json"));
            configuration = Payments.GSON.fromJson(reader, PaymentsConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configuration;
    }

    @NonNull
    private static PaymentsConfiguration fallback() {
        return new PaymentsConfiguration(3000, "", "", "", note, url, node);
    }
}
