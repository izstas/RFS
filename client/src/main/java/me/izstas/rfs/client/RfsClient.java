package me.izstas.rfs.client;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.eclipse.jdt.internal.jarinjarloader.RsrcURLStreamHandlerFactory;
import org.eclipse.swt.widgets.Display;

import me.izstas.rfs.client.rfs.Rfs;
import me.izstas.rfs.client.ui.MainWindow;

public class RfsClient {
    public static void main(String[] args) {
        new PreInitializationThread().start();
        injectSwtIntoClasspath();

        Display display = new Display();

        MainWindow window = new MainWindow();
        window.setBlockOnOpen(true);
        window.open();

        display.dispose();
    }

    private static void injectSwtIntoClasspath() {
        // Determine SWT jar to inject
        String swtPlatform, swtArch;

        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("win")) {
            swtPlatform = "windows";
        }
        else if (osName.contains("linux") || osName.contains("nix")) {
            swtPlatform = "linux";
        }
        else {
            System.err.println("SWT injection error: Unknown os.name: " + osName);
            return;
        }

        if (osArch.contains("64")) {
            swtArch = "x64";
        }
        else if (osArch.contains("32") || osArch.contains("86")) {
            swtArch = "x86";
        }
        else {
            System.err.println("SWT injection error: Unknown os.arch: " + osArch);
            return;
        }

        // Set URLStreamHandlerFactory so we can handle rsrc URLs
        URLClassLoader classLoader = (URLClassLoader) RfsClient.class.getClassLoader();
        URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(classLoader));

        // Construct the URL
        URL swtUrl;
        try {
            swtUrl = new URL("rsrc:swt." + swtPlatform + "." + swtArch + ".jar");
        }
        catch (MalformedURLException e) {
            System.err.println("SWT injection error: Malformed SWT URL: " + e);
            return;
        }

        // Do the injection
        Method addUrlMethod;
        try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        }
        catch (NoSuchMethodException e) {
            System.err.println("SWT injection error: Unable to get addURL method of URLClassLoader");
            return;
        }

        addUrlMethod.setAccessible(true);

        try {
            addUrlMethod.invoke(classLoader, swtUrl);
        }
        catch (ReflectiveOperationException e) {
            System.err.println("SWT injection error: Failed to invoke addURL method of URLClassLoader: " + e);
        }
    }


    private static final class PreInitializationThread extends Thread {
        @Override
        public void run() {
            Rfs.preInitialize();
        }
    }
}
