package me.izstas.rfs.client.ui;

import org.eclipse.osgi.util.NLS;

/**
 * A class containing all localized messages.
 */
public final class Messages extends NLS {
    private static final String BUNDLE_NAME = "me.izstas.rfs.client.ui.messages"; //$NON-NLS-1$

    public static String ServerDialog_title;
    public static String ServerDialog_url;
    public static String ServerDialog_authentication;
    public static String ServerDialog_authentication_anonymous;
    public static String ServerDialog_authentication_username;
    public static String ServerDialog_authentication_password;
    public static String ServerDialog_check;
    public static String ServerDialog_browse;
    public static String ServerDialog_status_checking;
    public static String ServerDialog_status_success_readOnly;
    public static String ServerDialog_status_success_readWrite;
    public static String ServerDialog_status_failure_url;
    public static String ServerDialog_status_failure_response;
    public static String ServerDialog_status_failure_authentication;
    public static String ServerDialog_status_failure_noRead;
    public static String ServerDialog_status_failure_unknown;

    static {
        initializeMessages(BUNDLE_NAME, Messages.class);
    }


    private Messages() {
        throw new AssertionError();
    }
}
