module coursework.listofgames {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires telegrambots.meta;
    requires telegrambots;

    opens taskTelegramBot.main to javafx.fxml;
    exports taskTelegramBot.main;
    exports taskTelegramBot.module.dao;
    exports taskTelegramBot.module.service;
}