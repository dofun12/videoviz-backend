package org.lemanoman.videoviz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevim Such
 */
public class SysCommandsUtils {
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static final String REMOTE_HOST = "192.168.15.102";

    public static void scp(String host, String remotePath, String destination) throws Exception {
        Runtime rt = Runtime.getRuntime();
        String[] arrayCommands = new String[]{"scp", host + ":" + remotePath, destination};
        Process proc = rt.exec(new String[]{"scp", host + ":" + remotePath, destination});
        proc.waitFor();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        // read the output from the command
        StringBuilder info = new StringBuilder();
        StringBuilder error = new StringBuilder();

        String s = null;

        boolean isError = false;

        List<String> lines = new ArrayList<>();
        while ((s = stdInput.readLine()) != null) {
            info.append(s);
        }
        while ((s = stdError.readLine()) != null) {
            isError = true;
            error.append(s);
        }
        if (isError && !error.toString().isEmpty()) {
            throw new Exception("Error to run command: " + commandToString(arrayCommands) + "\n " + error.toString());
        }

    }

    private static String commandToString(String[] array) {
        String value = "";
        for (String arr : array) {
            value = value + " " + arr;
        }
        return value;
    }

    public static List<String> getRemoteFiles(String path) {
        try {
            String[] commands = {"ssh", REMOTE_HOST, "ls -l " + path + "|awk '{print($5\",\"$9)}'"};
            return runCommand(commands, false);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getAvailableSize() {
        try {
            String[] commandMaxSize = new String[]{"/bin/bash", "-c", "df|head -n 2|tail -n 1|awk '{print $4}'"};
            return Long.parseLong(runCommand(commandMaxSize, false).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long getUsedSize() {
        try {
            String[] commandMaxSize = new String[]{"/bin/bash", "-c", "df|head -n 2|tail -n 1|awk '{print $3}'"};
            return Long.parseLong(runCommand(commandMaxSize, false).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteRemoteFile(String path) {
        try {
            String[] commands = {"ssh", REMOTE_HOST, "rm -f " + path};
            runCommand(commands, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> runCommandBash(String command, boolean wait) throws IOException {
        String[] bash = new String[]{"/bin/bash", "-c", command};
        if (isWindows()) {
            bash = new String[]{"cmd", "/C", command};
        }
        return runCommand(bash, wait);
    }

    public static String simpleRun(String command) throws IOException, InterruptedException {
        String[] bash = new String[]{"/bin/bash", "-c", command};

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(bash);
        proc.waitFor();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        // read the output from the command
        StringBuilder info = new StringBuilder();
        StringBuilder error = new StringBuilder();

        String s = null;

        boolean isError = false;


        List<String> lines = new ArrayList<>();
        while ((s = stdInput.readLine()) != null) {
            info.append(s + "\n");
        }
        while ((s = stdError.readLine()) != null) {
            isError = true;
            error.append(s + "\n");
        }
        if (!isError && !error.toString().isEmpty()) {
            return error.toString();
        } else {
            return info.toString();
        }
    }

    public static void runCommandBash(String command, CommandInterface ci) throws Exception {
        String[] bash = new String[]{"/bin/bash", "-c", command};
        if (isWindows()) {
            bash = new String[]{"cmd", "/C", command};
        }
        runCommand(bash, ci);
    }


    public static void runCommand(String[] commands, CommandInterface ci) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        // read the output from the command
        StringBuilder info = new StringBuilder();
        StringBuilder error = new StringBuilder();

        String s = null;

        boolean isError = false;

        List<String> lines = new ArrayList<>();
        while ((s = stdInput.readLine()) != null) {
            info.append(s);
        }
        while ((s = stdError.readLine()) != null) {
            isError = true;
            error.append(s);
        }
        if (!isError && !error.toString().isEmpty()) {
            ci.onError(error.toString());
            throw new Exception("Error to run command: " + Arrays.toString(commands) + "\n " + error.toString());
        } else {
            ci.onRun(info.toString());
        }
        proc.waitFor();
    }

    public static List<String> runCommand(String[] commands, boolean wait) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        if (wait) {
            try {
                proc.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<String> lines = new ArrayList<>();
        String s = null;
        String x = null;

        while ((s = stdInput.readLine()) != null) {
            lines.add(s);
            System.out.println(s);
        }

        while ((x = stdError.readLine()) != null) {
            lines.add(x);
            System.err.println(x);
        }
        // read the output from the command

        /**
         * // read any errors from the attempted command
         * System.out.println("Here is the standard error of the command (if
         * any):\n"); while ((s = stdError.readLine()) != null) {
         * System.out.println(s); }*
         */

        return lines;
    }

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
}