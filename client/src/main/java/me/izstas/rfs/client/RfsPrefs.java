package me.izstas.rfs.client;

import java.security.GeneralSecurityException;
import java.util.prefs.Preferences;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

/**
 * A helper class with static methods to access stored user preferences.
 */
public final class RfsPrefs {
    private static final Preferences prefs = Preferences.userNodeForPackage(RfsPrefs.class);

    // Ugly hardcoded key, but better than nothing
    private static final byte[] PREFS_KEY = new byte[] { 74, 105, -55, -21, 47, 101, 59, -123, 64, -89, -70, -5, 76, 95, 104, 90 };

    /**
     * Returns the URL of the last connection from the stored user preferences.
     * Returns an empty string if no data is available.
     */
    public static String getLastConnectionUrl() {
        return prefs.get("lastConnectionUrl", "");
    }

    /**
     * Returns {@code true} if the last connection was using anonymous authentication according to the stored user preferences,
     * or {@code false} otherwise.
     * Returns {@code true} if no data is available.
     */
    public static boolean wasLastConnectionAuthAnonymous() {
        return prefs.getBoolean("lastConnectionAnonymous", true);
    }

    /**
     * Returns the authentication username of the last connection from the stored user preferences.
     * Returns an empty string if no data is available.
     */
    public static String getLastConnectionAuthUsername() {
        return prefs.get("lastConnectionUsername", "");
    }

    /**
     * Returns the authentication password of the last connection from the stored user preferences.
     * Returns an empty string if no data is available or a cryptographic error occurs.
     */
    public static String getLastConnectionAuthPassword() {
        byte[] password = prefs.getByteArray("lastConnectionPassword", null);
        if (password == null) {
            return "";
        }

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(PREFS_KEY, "AES"));
            return new String(cipher.doFinal(password), Charsets.UTF_8);
        }
        catch (GeneralSecurityException e) {
            return "";
        }
    }

    /**
     * Stores the last connection data into the user preferences.
     * @param url the URL
     * @param authAnonymous {@code true} if anonymous authentication was used, {@code false} otherwise
     * @param authUsername the authentication username
     * @param authPassword the authentication password
     */
    public static void setLastConnection(String url, boolean authAnonymous, String authUsername, String authPassword) {
        prefs.put("lastConnectionUrl", url);
        prefs.putBoolean("lastConnectionAnonymous", authAnonymous);
        prefs.put("lastConnectionUsername", Strings.nullToEmpty(authUsername));

        if (!Strings.isNullOrEmpty(authPassword)) {
            try {
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(PREFS_KEY, "AES"));
                prefs.putByteArray("lastConnectionPassword", cipher.doFinal(authPassword.getBytes(Charsets.UTF_8)));
            }
            catch (GeneralSecurityException e) {
                // Ignore
            }
        }
        else {
            prefs.remove("lastConnectionPassword");
        }
    }


    private RfsPrefs() {
        throw new AssertionError();
    }
}
