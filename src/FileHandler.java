import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileHandler {
    private final String filePath;

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }

    public void save(VaultManager manager, String masterPassword) throws IOException {
        ensureParentFolderExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            String encryptedMaster = EncryptionUtil.xorEncrypt(masterPassword, masterPassword);
            writer.write("MASTER=" + encryptedMaster);
            writer.newLine();

            for (Credential credential : manager.listAllCredentials()) {
                String raw = credential.getWebsite() + "|" + credential.getUsername() + "|" + credential.getPassword();
                String encrypted = EncryptionUtil.xorEncrypt(raw, masterPassword);
                writer.write(encrypted);
                writer.newLine();
            }
        }
    }

    public LoadedVault load(String enteredMasterPassword) throws IOException, MasterPasswordException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new LoadedVault(false, new HashMap<>());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            if (firstLine == null || !firstLine.startsWith("MASTER=")) {
                throw new IOException("Vault file format is invalid.");
            }

            String encryptedMaster = firstLine.substring("MASTER=".length());
            String decryptedMaster = EncryptionUtil.xorDecrypt(encryptedMaster, enteredMasterPassword);
            if (!enteredMasterPassword.equals(decryptedMaster)) {
                throw new MasterPasswordException("Wrong master password.");
            }

            Map<String, Credential> loadedMap = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String decrypted = EncryptionUtil.xorDecrypt(line, enteredMasterPassword);
                String[] parts = decrypted.split("\\|", -1);
                if (parts.length != 3) {
                    continue;
                }
                Credential credential = new Credential(parts[0], parts[1], parts[2]);
                loadedMap.put(parts[0].toLowerCase(), credential);
            }
            return new LoadedVault(true, loadedMap);
        }
    }

    private void ensureParentFolderExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public static class LoadedVault {
        private final boolean existingVault;
        private final Map<String, Credential> credentials;

        public LoadedVault(boolean existingVault, Map<String, Credential> credentials) {
            this.existingVault = existingVault;
            this.credentials = credentials;
        }

        public boolean isExistingVault() {
            return existingVault;
        }

        public Map<String, Credential> getCredentials() {
            return credentials;
        }
    }
}
