package main;

import entities.Admin;
import storage.DatabaseStorage;
import storage.LibrarySystem;
import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) throws Exception {
        LibrarySystem system = LibrarySystem.getInstance();

        DatabaseStorage storage = new DatabaseStorage();
        storage.loadData(system);

        if (system.usersSize() == 0) {
            Admin admin = new Admin("admin123", "Nouran");
            system.addUser(admin);
            System.out.println(" Default admin created (ID: admin123, Name: Nouran)");
        }

        ConsoleUI ui = new ConsoleUI();
        ui.handleWelcomeMenu();

        storage.saveData(system);
    }
}
