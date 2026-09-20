package com.reserveone.lanhua.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class DotenvConfig {

    private static final List<String> REQUIRED = List.of(
            "DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_PASSWORD",
            "BOLD_IDENTITY_KEY", "JWT_SECRET"
    );

    public static void load() {
        // Local: si hay .env, se carga. En Render no existe y se omite.
        findDotenvDirectory().ifPresent(dir -> {
            Dotenv dotenv = Dotenv.configure()
                    .directory(dir.toString())
                    .ignoreIfMissing()
                    .load();

            dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).forEach(entry -> {
                // Las variables reales del sistema tienen prioridad sobre el .env
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        });

        // Validar que todo esté disponible, venga del .env o del sistema
        REQUIRED.forEach(DotenvConfig::requireVariable);
    }

    private static Optional<Path> findDotenvDirectory() {
        Path directory = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (directory != null) {
            if (Files.isRegularFile(directory.resolve(".env"))) {
                return Optional.of(directory);
            }
            directory = directory.getParent();
        }
        return Optional.empty();
    }

    private static void requireVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la variable requerida " + name);
        }
    }
}