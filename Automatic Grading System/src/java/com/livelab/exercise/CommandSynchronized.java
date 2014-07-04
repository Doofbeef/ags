package com.livelab.exercise;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
import java.lang.ProcessBuilder.Redirect;
import java.io.*;

public class CommandSynchronized {

    public static boolean isCompiled = false;

    public static class Output {

        public String output = "";
        public String error = "";
        public boolean isInfiniteLoop = false;
        public int timeUsed = CommonConstant.EXECUTION_TIME_INTERVAL;
    }

    /** Run a command with the option to kill a process */
    public static synchronized Output runACommand(String command, String processName) throws Exception {
        Output result = new Output();
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(new String[]{"cmd", "/c", command});

        try {
            Thread.sleep(CommonConstant.EXECUTION_TIME_ALLOWED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //  Runtime runtime = Runtime.getRuntime();
        Process proc2 = runtime.exec(new String[]{"cmd", "/c", "taskkill /F /T /IM " + processName + ".exe"});

        // Process output from proc
        Scanner scanner = new Scanner(proc.getErrorStream());

        while (scanner.hasNext()) {
            System.out.println(scanner.nextLine());
        }

        // Process output from proc
        scanner = new Scanner(proc.getInputStream());
        while (scanner.hasNext()) {
            System.out.println(scanner.nextLine());
        }

        // Process output from proc2
        scanner = new Scanner(proc2.getErrorStream());

        while (scanner.hasNext()) {
            System.out.println(scanner.nextLine());
        }

        // Process output from proc
        scanner = new Scanner(proc2.getInputStream());
        while (scanner.hasNext()) {
            System.out.println(scanner.nextLine());
        }

        return result;
    }

    public static Output executeProgram(String command, String program,
            String programDirectory, String inputFile, String outputFile) {
        final Output result = new Output();
        ProcessBuilder pb;

        if (CommonConstant.LANGUAGE.equals("JAVA")) {
            pb = new ProcessBuilder(command, "-Djava.security.manager", program);
        } else if (CommonConstant.LANGUAGE.equals("C++")) {
            pb = new ProcessBuilder(program);
        } else { // For Python

            pb = new ProcessBuilder(command, program);
        }
        pb.directory(new File(programDirectory));

        pb.redirectErrorStream(true);
        if (inputFile != null) {
            pb.redirectInput(Redirect.from(new File(inputFile)));
        }
        pb.redirectOutput(Redirect.to(new File(outputFile)));

        long startTime = System.currentTimeMillis();

        Process proc = null;
        try {
            proc = pb.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // This separate thread destroy the process if it takes too long time
        final Process proc1 = proc;
        new Thread() {

            public void run() {
                int sleepTime = 0;
                boolean isFinished = false;
                while (sleepTime <= CommonConstant.EXECUTION_TIME_ALLOWED && !isFinished) {
                    try {
                        isCompiled =true;
                        try {
                            Thread.sleep(CommonConstant.EXECUTION_TIME_INTERVAL);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
//                System.out.println("sleepTime " + sleepTime);
                        sleepTime += CommonConstant.EXECUTION_TIME_INTERVAL;
                        int exitValue = proc1.exitValue();
                        isFinished = true;
//                System.out.println("exitValue " + exitValue);
                    } catch (IllegalThreadStateException ex) {
                        isCompiled =false;
                    }
                }

                if (!isFinished) {
                    proc1.destroy();
                    result.isInfiniteLoop = true;
//            System.out.println("Infinite loop");
                }
            }
        }.start();

        try {
            int exitCode = proc.waitFor();
            System.out.println("exitCode from waitFor: " + exitCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        result.timeUsed = (int) (System.currentTimeMillis() - startTime);

        return result;
    }

    public static Output compileProgram(String command,
            String sourceDirectory, String program) {
        final Output result = new Output();
        ProcessBuilder pb;
        if (CommonConstant.LANGUAGE.equals("JAVA")) {
            pb = new ProcessBuilder(command, "-classpath", ".",
                    "-Xlint:unchecked", "-nowarn", program);
        } else {
            pb = new ProcessBuilder(command, program);
        }
        pb.directory(new File(sourceDirectory));

        long startTime = System.currentTimeMillis();

        Process proc = null;
        try {

            proc = pb.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // This separate thread destroy the process if it takes too long time
        final Process proc1 = proc;

        new Thread() {

            public void run() {

                Scanner scanner1 = new Scanner(proc1.getInputStream());
                while (scanner1.hasNext()) {

                    result.output += scanner1.nextLine() + "<br />";
                    //  scanner1.close(); // You could have closed it too soon
                }
            }
        }.start();

        new Thread() {

            public void run() {
                // Process output from proc
                Scanner scanner2 = new Scanner(proc1.getErrorStream());
                while (scanner2.hasNext()) {
                    
                    result.error += scanner2.nextLine() + "<br />";
                }
                // scanner2.close(); // You could have closed it too soon
            }
        }.start();
        try {

            //Wait for the external process to finish
            int exitCode = proc.waitFor();
            System.out.println("javac command exit code " + exitCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        result.output.replace(" ", "&nbsp;");
        result.error.replace(" ", "&nbsp;");
        result.timeUsed = (int) (System.currentTimeMillis() - startTime);
        return result;
    }

    public static Output runPlagiarismProgram(String[] files, String outputFile) {
        final Output result = new Output();
        ProcessBuilder pb = new ProcessBuilder(files);

        pb.redirectErrorStream(true);
        pb.redirectOutput(Redirect.to(new File(outputFile)));

        Process proc = null;
        try {
            proc = pb.start();

            int exitCode = proc.waitFor();
            System.out.println("exitCode from waitFor: " + exitCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
    }
}
