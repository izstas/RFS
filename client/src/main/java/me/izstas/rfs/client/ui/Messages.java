package me.izstas.rfs.client.ui;

import org.eclipse.osgi.util.NLS;

import me.izstas.rfs.client.rfs.RfsAccessException;
import me.izstas.rfs.client.rfs.RfsAuthenticationException;
import me.izstas.rfs.client.rfs.RfsResponseException;

/**
 * A class containing all localized messages.
 */
public final class Messages extends NLS {
    private static final String BUNDLE_NAME = "me.izstas.rfs.client.ui.messages"; // $NON-NLS-1$

    public static String MainWindow_title;
    public static String MainWindow_menu_server;
    public static String MainWindow_action_connect;
    public static String MainWindow_action_refresh;
    public static String MainWindow_action_exit;
    public static String MainWindow_action_download;
    public static String MainWindow_action_attributes;
    public static String MainWindow_tree_column_name;
    public static String MainWindow_tree_column_size;
    public static String MainWindow_tree_column_dateModified;
    public static String MainWindow_tree_column_attributes;
    public static String MainWindow_tree_loading;
    public static String MainWindow_tree_error;
    public static String MainWindow_download_downloading;
    public static String MainWindow_download_error_title;
    public static String MainWindow_download_error_message;

    public static String ServerDialog_title;
    public static String ServerDialog_url;
    public static String ServerDialog_authentication;
    public static String ServerDialog_authentication_anonymous;
    public static String ServerDialog_authentication_username;
    public static String ServerDialog_authentication_password;
    public static String ServerDialog_check;
    public static String ServerDialog_status_checking;
    public static String ServerDialog_status_success_readOnly;
    public static String ServerDialog_status_success_readWrite;
    public static String ServerDialog_status_failure_url;
    public static String ServerDialog_status_failure_noRead;
    public static String ServerDialog_status_failure;

    public static String AttributesDialog_title;
    public static String AttributesDialog_name;
    public static String AttributesDialog_dateCreated;
    public static String AttributesDialog_dateAccessed;
    public static String AttributesDialog_dateModified;
    public static String AttributesDialog_error_title;
    public static String AttributesDialog_error_message;

    public static String rfs_error_response;
    public static String rfs_error_authentication;
    public static String rfs_error_access;
    public static String rfs_error_unknown;


    static {
        initializeMessages(BUNDLE_NAME, Messages.class);
    }


    /**
     * Returns a presumably user-friendly description of an error represented by the specified exception.
     */
    public static String getForException(Throwable e) {
        if (e instanceof RfsResponseException) {
            return rfs_error_response;
        }
        if (e instanceof RfsAuthenticationException) {
            return rfs_error_authentication;
        }
        if (e instanceof RfsAccessException) {
            return rfs_error_access;
        }

        return rfs_error_unknown;
    }


    private Messages() {
        throw new AssertionError();
    }
}
