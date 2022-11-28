package com.binotify;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KeyProvider {

    private static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static String KEY_FILENAME = "keys";

    public static String generate() {
        StringBuilder keyBuilder = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            Random randomizer = new Random();
            int index = randomizer.nextInt(36);
            keyBuilder.append(
                    CHARS.charAt(index)
            );
        }

        String key = keyBuilder.toString();
        KeyProvider.storeKey(key);

        System.out.println("New API key: " + key);

        return key;
    }

    public static boolean validate(String key) {
        List<String> keys = KeyProvider.getKeys();
        return keys.contains(key);
    }

    private static void ensureStore() {
        File file = new File(KEY_FILENAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ignored) { }
        }
    }

    private static List<String> getKeys() {
        try {
            KeyProvider.ensureStore();
            String raw = Files.readString(Paths.get(KEY_FILENAME));
            String[] keys = raw.split("(\r?\n)+");

            return new ArrayList<>(Arrays.asList(keys));
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private static void storeKey(String key) {
        KeyProvider.ensureStore();
        List<String> keys = KeyProvider.getKeys();
        keys.add(key);

        keys = keys.stream().filter(k -> !k.equals("")).collect(Collectors.toList());

        String store = String.join("\r\n", keys);
        try {
            FileWriter storeWriter = new FileWriter(KEY_FILENAME);
            storeWriter.write(store);
            storeWriter.close();
        } catch (Exception ignored) { }
    }
}
