import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class VaultApp {
    private static final String VAULT_FILE_PATH = "data/vault.dat";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        VaultManager vaultManager = new VaultManager();
        FileHandler fileHandler = new FileHandler(VAULT_FILE_PATH);

        try {
            String masterPassword = initializeOrLogin(scanner, vaultManager, fileHandler);
            runMenu(scanner, vaultManager, fileHandler, masterPassword);
        } catch (MasterPasswordException e) {
            System.out.println("Login failed: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static String initializeOrLogin(
        Scanner scanner,
        VaultManager vaultManager,
        FileHandler fileHandler
    ) throws IOException, MasterPasswordException {
        System.out.println("===== SecureVault =====");
        System.out.print("Enter master password: ");
        String enteredPassword = scanner.nextLine().trim();

        FileHandler.LoadedVault loadedVault = fileHandler.load(enteredPassword);
        if (loadedVault.isExistingVault()) {
            vaultManager.setMasterPassword(enteredPassword);
            vaultManager.loadCredentials(loadedVault.getCredentials());
            System.out.println("Vault unlocked successfully.");
            return enteredPassword;
        }

        if (!isStrongEnough(enteredPassword)) {
            throw new MasterPasswordException("Master password must be at least 8 characters and contain one number.");
        }

        vaultManager.setMasterPassword(enteredPassword);
        fileHandler.save(vaultManager, enteredPassword);
        System.out.println("New vault created.");
        return enteredPassword;
    }

    private static void runMenu(
        Scanner scanner,
        VaultManager vaultManager,
        FileHandler fileHandler,
        String masterPassword
    ) throws IOException {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addOrUpdateCredential(scanner, vaultManager);
                    break;
                case "2":
                    viewAllCredentials(vaultManager);
                    break;
                case "3":
                    searchCredentials(scanner, vaultManager);
                    break;
                case "4":
                    deleteCredential(scanner, vaultManager);
                    break;
                case "5":
                    viewPassword(scanner, vaultManager);
                    break;
                case "6":
                    fileHandler.save(vaultManager, masterPassword);
                    System.out.println("Vault saved. Goodbye.");
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1-6.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Add or Update Credential");
        System.out.println("2. View All Credentials");
        System.out.println("3. Search Credential");
        System.out.println("4. Delete Credential");
        System.out.println("5. View Password for Website");
        System.out.println("6. Save and Exit");
        System.out.print("Choose an option: ");
    }

    private static void addOrUpdateCredential(Scanner scanner, VaultManager vaultManager) {
        System.out.print("Website: ");
        String website = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (website.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        String strength = checkPasswordStrength(password);
        System.out.println("Password Strength: " + strength);

        vaultManager.addOrUpdateCredential(website, username, password);
        System.out.println("Credential saved.");
    }

    private static void viewAllCredentials(VaultManager vaultManager) {
        List<Credential> allCredentials = vaultManager.listAllCredentials();
        if (allCredentials.isEmpty()) {
            System.out.println("No credentials found.");
            return;
        }

        for (Credential credential : allCredentials) {
            System.out.println(credential);
        }
    }

    private static void searchCredentials(Scanner scanner, VaultManager vaultManager) {
        System.out.print("Enter website keyword: ");
        String keyword = scanner.nextLine().trim();

        List<Credential> results = vaultManager.searchByWebsite(keyword);
        if (results.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }

        for (Credential credential : results) {
            System.out.println(credential);
        }
    }

    private static void deleteCredential(Scanner scanner, VaultManager vaultManager) {
        System.out.print("Enter website to delete: ");
        String website = scanner.nextLine().trim();

        try {
            vaultManager.deleteCredential(website);
            System.out.println("Credential deleted.");
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewPassword(Scanner scanner, VaultManager vaultManager) {
        System.out.print("Enter website to view password: ");
        String website = scanner.nextLine().trim();

        try {
            Credential credential = vaultManager.getCredential(website);
            System.out.println("Saved password for " + credential.getWebsite() + ": " + credential.getPassword());
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean isStrongEnough(String password) {
        if (password.length() < 8) {
            return false;
        }
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private static String checkPasswordStrength(String password) {
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        if (password.length() >= 12 && hasUpper && hasLower && hasDigit && hasSpecial) {
            return "Strong";
        }
        if (password.length() >= 8 && hasLower && hasDigit) {
            return "Medium";
        }
        return "Weak";
    }
}
