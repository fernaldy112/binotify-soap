package com.binotify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Env {

    public static Env ENV = new Env();

    private final HashMap<String, String> vars;

    private Env() {
        this.vars = new HashMap<>();

        File dotenv = new File(".env");

        if (dotenv.exists() && dotenv.isFile()) {
            try {
                String contents = Files.readString(Paths.get("./.env"));
                String[] lines = contents.split("(\r?\n)+");

                for (String line: lines) {
                    String[] chunk = line.split("=");
                    if (chunk.length == 2) {
                        String value = chunk[1].trim();
                        if (value.matches("\".*\"")) {
                            value = value.replaceAll("\"(.*)\"","$1");
                        }
                        this.vars.put(
                                chunk[0].trim(),
                                value
                        );
                    }
                }

            } catch (IOException ignored) {
            }
        }
    }

    public String get(String key) {
        return this.vars.get(key);
    }

    public Integer getAsInt(String key) {
        return Integer.valueOf(this.vars.get(key));
    }
}
