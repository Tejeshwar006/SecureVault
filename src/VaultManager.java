import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaultManager {
    private final Map<String, Credential> credentialsByWebsite;
    private String masterPassword;

    public VaultManager() {
        this.credentialsByWebsite = new HashMap<>();
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void authenticate(String inputPassword) throws MasterPasswordException {
        if (masterPassword == null) {
            throw new MasterPasswordException("Master password is not set.");
        }
        if (!masterPassword.equals(inputPassword)) {
            throw new MasterPasswordException("Incorrect master password.");
        }
    }

    public boolean isMasterPasswordSet() {
        return masterPassword != null && !masterPassword.isBlank();
    }

    public void addOrUpdateCredential(String website, String username, String password) {
        Credential credential = new Credential(website, username, password);
        credentialsByWebsite.put(website.toLowerCase(), credential);
    }

    public Credential getCredential(String website) throws EntryNotFoundException {
        Credential credential = credentialsByWebsite.get(website.toLowerCase());
        if (credential == null) {
            throw new EntryNotFoundException("No credential found for website: " + website);
        }
        return credential;
    }

    public void deleteCredential(String website) throws EntryNotFoundException {
        Credential removed = credentialsByWebsite.remove(website.toLowerCase());
        if (removed == null) {
            throw new EntryNotFoundException("No credential found for website: " + website);
        }
    }

    public List<Credential> listAllCredentials() {
        List<Credential> list = new ArrayList<>(credentialsByWebsite.values());
        list.sort((a, b) -> a.getWebsite().compareToIgnoreCase(b.getWebsite()));
        return list;
    }

    public List<Credential> searchByWebsite(String keyword) {
        List<Credential> results = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (Credential credential : credentialsByWebsite.values()) {
            if (credential.getWebsite().toLowerCase().contains(key)) {
                results.add(credential);
            }
        }
        results.sort((a, b) -> a.getWebsite().compareToIgnoreCase(b.getWebsite()));
        return results;
    }

    public Map<String, Credential> getCredentialsMap() {
        return Collections.unmodifiableMap(credentialsByWebsite);
    }

    public void loadCredentials(Map<String, Credential> loadedData) {
        credentialsByWebsite.clear();
        credentialsByWebsite.putAll(loadedData);
    }
}
