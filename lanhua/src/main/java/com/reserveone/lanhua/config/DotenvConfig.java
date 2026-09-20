package com.reserveone.lanhua.config;
import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {
    public static void load() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("BOLD_IDENTITY_KEY", dotenv.get("BOLD_IDENTITY_KEY"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
    }
}