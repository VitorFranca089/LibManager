package io.github.vitorfranca089.libmanager;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;
import io.github.vitorfranca089.libmanager.controller.MainController;

public class Main {

    public static void main(String[] args) {

        DatabaseConfig.migrateDatabase();
        MainController mainController = new MainController();

        mainController.startMenu();

        System.out.println("Fechando...");

    }

}
