package me.izstas.rfs.client.ui;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {
    private static final String BUNDLE_NAME = "me.izstas.rfs.client.ui.messages";

    public static String ServerDialog_title;
    public static String ServerDialog_url;
    public static String ServerDialog_authentication;
    public static String ServerDialog_authentication_anonymous;
    public static String ServerDialog_authentication_username;
    public static String ServerDialog_authentication_password;
    public static String ServerDialog_check;
    public static String ServerDialog_browse;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }


    private Messages() {
        throw new AssertionError();
    }
}
