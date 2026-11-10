package org.delcom.ifs25034pbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Memuat file .env (KEY=VALUE) ke system properties sebelum Spring Boot start. */
public final class DotEnv {
    private DotEnv() {
    }

    public static void load() throws IOException {
        load(Path.of(".env"));
    }

    public static void load(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        for (String line : Files.readAllLines(file)) {
            String text = line.trim();
            if (text.isEmpty() || text.startsWith("#")) {
                continue;
            }
            int idx = text.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String key = text.substring(0, idx).trim();
            String value = text.substring(idx + 1).trim();
            // Variabel lingkungan / -D yang sudah ada tidak ditimpa
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        }
    }
}
