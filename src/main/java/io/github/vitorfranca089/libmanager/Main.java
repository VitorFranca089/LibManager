package io.github.vitorfranca089.libmanager;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;

public class Main {

    public static void main(String[] args) {
        DatabaseConfig.migrateDatabase();
    }

}
