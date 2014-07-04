/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livelab.exercise;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class completeExercise implements Serializable {

    private List<SelectItem> assignedExercise;
    private String selectedExercise;
    private String username;
    private String instructorUsername;
    private String exercise;
    private String courseId;
    private boolean exerciseChosen;
    private String exerciseInput;
    private String runOutputText;
    private boolean runRendered = false;


    public completeExercise() {
    }

    public List<SelectItem> getAssignedExercise() {
        return assignedExercise;
    }

    public void setAssignedExercise(List<SelectItem> assignedExercise) {
        this.assignedExercise = assignedExercise;
    }

    public String getSelectedExercise() {
        return selectedExercise;
    }

    public void setSelectedExercise(String selectedExercise) {
        this.selectedExercise = selectedExercise;
    }

    public boolean getExerciseChosen() {
        return exerciseChosen;
    }

    public void setExerciseChosen(boolean exerciseChosen) {
        exercise = selectedExercise;
        try {
            exerciseInput = readFile(CommonConstant.AGS_ROOT + courseId + File.separator + username + File.separator + selectedExercise + ".java");
        } catch (Exception e) {
        }
        exercise = selectedExercise;
        this.exerciseChosen = exerciseChosen;
    }

    public String getExerciseInput() {
        return exerciseInput;
    }

    public void setExerciseInput(String exerciseInput) {
        this.exerciseInput = exerciseInput;
    }

    public String getRunOutputText() {
        return runOutputText;
    }

    public boolean getRunRendered() {
        return runRendered;
    }

    public void saveAndCompile() {
        Writer writer = null;
        runRendered = true;

        try {

            File file = new File(CommonConstant.AGS_ROOT + courseId + File.separator + username + File.separator + exercise + ".java");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(exerciseInput);


        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            try {

                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
            btRun_action();

        }

        //CommandSynchronized test = new CommandSynchronized();
        String path = courseId + File.separator + username + File.separator + selectedExercise + ".java";
        CommandSynchronized.compileProgram("javac", CommonConstant.AGS_ROOT, path);
        if (CommandSynchronized.isCompiled) {
            runOutputText += "<br/><span>Compiled Successfully!</span>";
        } else {
            runOutputText += "<span style=\"background-color: #FF0000\">Compiled Not Successful!</span>";
        }
    }

    public void refresh(String username) {
        this.username = username;
        assignedExercise = new ArrayList<SelectItem>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT courseId FROM AGSStudent WHERE AGSStudent.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                courseId = rs.getString(1);
            }

            p = conn.prepareStatement("SELECT username FROM AGSInstructor WHERE AGSInstructor.courseID = '" + courseId + "';");
            rs = p.executeQuery();
            if (rs.next()) {
                instructorUsername = rs.getString(1);
            }

            p = conn.prepareStatement("SELECT exerciseName, dueDate FROM exerciseassigned WHERE exerciseassigned.username = '" + instructorUsername + "' ORDER BY dueDate DESC;");
            rs = p.executeQuery();
            while (rs.next()) {
                String date = rs.getString(2);
                date = date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
                assignedExercise.add(new SelectItem(rs.getString(1), rs.getString(1) + " Due: " + date));
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public String btRun_action() {
        // TODO: Process the action. Return value is a navigation
        // case name where null will return to the same page.

        /*
         * if (exercise.isPastDue()) {
         String out =
         "<div id=\"boxstyle\" ; style=\"width: 100%; text-align: left; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"><h3 id=\"h3style\" ; style=\"background-color: blue; text-align: left; padding-left: 1em\">Run Status</h3>  <div style = \"margin-left:1em\"><font face=\"Courier New\">";
         out += "Past due.";
         runOutputText = out;
         runRendered = true;
         return null;
         }
         
         if (exercise.isPastDue() && exercise.isStudentSession()) {
         String out =
         "<div id=\"boxstyle\" ; style=\"width: 100%; text-align: left; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"><h3 id=\"h3style\" ; style=\"background-color: blue; text-align: left; padding-left: 1em\">Run Status</h3>  <div style = \"margin-left:1em\"><font face=\"Courier New\">";
         out += "Past due.";
         runOutputText = out;
         runRendered = true;
         return null;
         }
        
         if (!exercise.isGradable()) {
         String out = "<div id=\"boxstyle\"; style=\"width: 100%; text-align: left; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"> <h3 id=\"h3style\"; style=\"background-color: blue; text-align: left; padding-left: 1em\">Run Status: Not Gradable</h3><div style = \"margin-left:1em; margin-right:1em\"><font face=\"Courier New\">This exercise cannot be run and automatically graded. But you can still submit it</font></div></div>";
         runOutputText = out;
         runRendered = true;
         return null;
         }
        
         if (exercise.getSourceCode() == null) {
         String out = "<div id=\"boxstyle\"; style=\"width: 100%; text-align: left; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"> <h3 id=\"h3style\"; style=\"background-color: blue; text-align: left; padding-left: 1em\">Run Status: Not Compiled</h3><div style = \"margin-left:1em; margin-right:1em\"><font face=\"Courier New\">You have to click the Compile button before Run. You can still submit it even though the program cannot run.</font></div></div>";
         runOutputText = out;
         runRendered = true;
         return null;
         }
         /*
         if (exercise.failedSecurityCheck()) {
         String out = "<div id=\"boxstyle\"; style=\"width: 100%; text-align: left; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"><h3 id=\"h3style\"; style=\"background-color: blue; text-align: left; padding-left: 1em\">Run Status: Violations</h3><div style = \"margin-left:1em; margin-right:1em\"><font face=\"Courier New\">Your program is prohibited from accessing files, running network applications, or GUI applications. You cannot import javax.swing, java.io, java.net, and java.awt packages. </font>  </div>  </div>";
         runOutputText = out;
         runRendered = true;
         return null;
         }
         */
        String out = "";
        //exercise.setExecuted(true);

        /*if (exercise.isCustom()) {
         String inputa = getInputAForCustomExercise(student.getInstructorUsername(), exercise.getExerciseName());
        
         if (inputa == null || inputa.trim().length() == 0) {
         CommandSynchronized.Output result = executeAProgram(
         student.getCourseID(), student.getUsername(), null, exercise.getExerciseName());
         if (result.isInfiniteLoop) {
         out = runOfTimeMessage();
         } else {
         out = gradeCustomExerciseWithoutInput(result, student.getCourseID(),
         student.getUsername(), exercise);
         }
         } else {
         int limit = CommonConstant.NUMBER_OF_TESTS_USED_FOR_STUDENTS;
         if (this.getSessionBean1().isInstructorConnected()) {
         limit = CommonConstant.NUMBER_OF_TESTS_USED_FOR_INSTRUCTORS;
         }
        
         for (int i = 0; i < limit; i++) {
         if (!new File(CommonConstant.AGS_ROOT + "gradecustomexercise" + File.separator + student.getCourseID() + File.separator + exercise.getExerciseName() + (char) (i + 'a') + ".input").exists()) {
         break;
         }
        
         String inputFilename = CommonConstant.AGS_ROOT + "gradecustomexercise" + File.separator
         + student.getCourseID() + File.separator + exercise.getExerciseName()
         + (char) (i + 'a') + ".input";
         CommandSynchronized.Output result = executeAProgram(
         student.getCourseID(), student.getUsername(), inputFilename, exercise.getExerciseName());
         if (result.isInfiniteLoop) {
         out = runOfTimeMessage();
         } else {
         String studentOutputFile = CommonConstant.AGS_ROOT + student.getCourseID() + File.separator
         + student.getUsername() + File.separator + exercise.getExerciseName() + ".out";
         String studentOutput = Utility.readFile(studentOutputFile);
         String keyFile = CommonConstant.AGS_ROOT + "gradecustomexercise" + File.separator + student.getCourseID() + File.separator + exercise.getExerciseName() + (char) (i + 'a') + ".output";
         String keyOutput = Utility.readKeyFile(keyFile);
        
         RunResult runResult = gradeOneRun(studentOutput, keyOutput, i, result.timeUsed);
        
         if (runResult.isCorrect) {
         out += runResult.outForOneRun;
         } else {
         exercise.setRunCorrect(false);
         stCompileRunSubmitResult.setText(runResult.outForOneRun);
         stCompileRunSubmitResult.setRendered(true);
         return null;
         }
         exercise.setRunCorrect(true);
         }
         }
         } 
         } else {*/
        if (!new File(CommonConstant.AGS_ROOT + "gradeexercise" + File.separator + selectedExercise + "a.input").exists()) {
            // Grade book exercises without input file
            CommandSynchronized.Output result = executeAProgram(
                    courseId, username, null, selectedExercise);
            if (result.isInfiniteLoop) {
                out = runOfTimeMessage();
            } else {
                out = gradeBookExerciseWithoutInput(result, courseId,
                        username);
            }
        } else {  // Grade book exercises with input files

            int limit = CommonConstant.NUMBER_OF_TESTS_USED_FOR_STUDENTS;
            //if (this.getSessionBean1().isInstructorConnected()) {
            //limit = CommonConstant.NUMBER_OF_TESTS_USED_FOR_INSTRUCTORS;
            //}
            for (int i = 0; i < limit; i++) {
                if (!new File(CommonConstant.AGS_ROOT + "gradeexercise" + File.separator + selectedExercise + (char) (i + 'a') + ".input").exists()) {
                    break;
                }
                String inputFilename = CommonConstant.AGS_ROOT + File.separator + "gradeexercise" + File.separator + selectedExercise + (char) (i + 'a') + ".input";
                CommandSynchronized.Output result = executeAProgram(
                        courseId, username,
                        inputFilename, selectedExercise);
                if (result.isInfiniteLoop) {
                    out = runOfTimeMessage();
                } else {

                    String studentOutputFile = CommonConstant.AGS_ROOT + courseId + File.separator
                            + username + File.separator + selectedExercise + ".out";
                    String studentOutput = readFile(studentOutputFile);

                    String keyFile = CommonConstant.AGS_ROOT + "gradeexercise" + File.separator + selectedExercise + (char) (i + 'a') + ".output";
                    String keyOutput = readFile(keyFile);

                    RunResult runResult = gradeOneRun(studentOutput, keyOutput, i, result.timeUsed);

                    if (runResult.isCorrect) {
                        out += runResult.outForOneRun;
                    } else {
                        submit(false);
                        submit(false);
                        runOutputText = runResult.outForOneRun;
                        runRendered = true;
                        return null;
                    }
                    submit(true);
                    submit(true);
                }
            }
        }
        //}

        runOutputText = out;
        runRendered = true;

        return null;
    }

    public String howtocompilerunsubmit() {
        return CommonConstant.HOWTOCOMPILERUNSUBMIT;
    }

    public String gradingrules() {
        return CommonConstant.GRADING_RULES;
    }

    public String exerciseDescription() {
        String exerciseDescription =
                "<div id=\"boxstyle\" ; style=\"width: 100%; text-align: left; color:#CCCCCC;  border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"><h3 id=\"h3style\" ; style=\"background-color: blue; text-align: left; padding-left: 1em\">" + selectedExercise + "</h3>  <div style = \"margin-left:1em\"><font face=\"Courier New\">";
        String keyFile = readFile(CommonConstant.AGS_ROOT + "exercisedescription" + File.separator + selectedExercise);
        exerciseDescription += keyFile;
        return exerciseDescription;
    }

    private static class RunResult {

        String outForOneRun = "";
        boolean isCorrect = false;
    }

    private RunResult gradeOneRun(String studentOutput, String keyOutput, int i, int cpuTime) {
        RunResult runResult = new RunResult();

        String outForOneRun = "<div id=\"boxstyle\" ; style=\"width: 750px; text-align: left; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\">"
                + "<h3 id=\"h3style\" ; style=\"background-color: blue; text-align: left;  padding-left: 1em\">"
                + " Status from Test Data Set " + (i + 1) + ": " + cpuTime + "ms CPU time used </h3>"
                + " <div style = \"margin-left:1em\"> <font face=\"Courier New\">";
        outForOneRun += "<pre>" + studentOutput + "</pre>";
        outForOneRun += "<br />";

        if (grade(studentOutput, keyOutput)) {
            outForOneRun += "<span style=\"background-color: #00FF00\">Correct result!</span>";

            outForOneRun += "</font> </div> </div>";



            runResult.isCorrect = true;
        } else {
            outForOneRun += "<span style=\"background-color: #FF0000\">Incorrect result!</span>";
            outForOneRun += "</font> </div> </div>";

            runResult.isCorrect = false;
        }

        runResult.outForOneRun = outForOneRun;
        return runResult;
    }

    private String gradeBookExerciseWithoutInput(CommandSynchronized.Output result,
            String instructorEmail, String studentUsername) {
        String out = "<div id=\"boxstyle\" ; style=\"width: 750px; text-align: left; color:#CCCCCC; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"><h3 id=\"h3style\" ; style=\"background-color: blue; text-align: left; padding-left: 1em\">Run Status: " + result.timeUsed + "ms CPU time used " + "</h3> <div style = \"margin-left:1em\">  <font face=\"Courier New\">";
        String studentOutputFile = CommonConstant.AGS_ROOT + instructorEmail + File.separator
                + studentUsername + File.separator + selectedExercise + ".out";
        String studentOutput = readFile(studentOutputFile);

        String keyFile = CommonConstant.AGS_ROOT + "gradeexercise" + File.separator + selectedExercise + ".output";
        String keyOutput = readFile(keyFile);

        out += "<pre>" + studentOutput + "</pre>";
        out += "<br />";

        if (grade(studentOutput, keyOutput)) {
            out += "<span style=\"background-color: #00FF00\">Correct result!</span>";
            submit(true);
            setRunCorrect(true);
        } else {
            out += "<span style=\"background-color: #FF0000\">Incorrect result!</span>";
            submit(false);
            setRunCorrect(false);
        }
        out += " </font>    </div>  </div> ";
        return out;
    }

    private CommandSynchronized.Output executeAProgram(String instructorEmail, String studentUsername, String inputFilename, String exerciseName) {
        String programDirectory = CommonConstant.AGS_ROOT + instructorEmail + File.separator
                + studentUsername;
//                        CommandSynchronized.Output result = CommandSynchronized.runACommand(command, "java5");

        if (inputFilename == null) {
            return CommandSynchronized.executeProgram(CommonConstant.JAVARUNCOMMAND,
                    exerciseName, programDirectory, null, programDirectory + File.separator + exerciseName + ".out");
        } else {
            return CommandSynchronized.executeProgram(CommonConstant.JAVARUNCOMMAND,
                    exerciseName, programDirectory, inputFilename, programDirectory + File.separator + exerciseName + ".out");
        }
    }

    private String runOfTimeMessage() {
        String out = "";
        out += "<div id=\"boxstyle\" ; style=\"width: 750px; text-align: left; border-color: blue; margin-left: 0em; margin-right: 0em; margin-top: 0em\"><h3 id=\"h3style\" ; style=\"background-color: blue; text-align: left; padding-left: 1em\">Run Status: running out of allowed CPU time </h3> <div style = \"margin-left:1em\">  <font face=\"Courier New\">";
        out += "Your program takes too long. It runs out of the allowed CPU time " + CommonConstant.EXECUTION_TIME_ALLOWED + "ms. It may have an infinite loop.";
        out += " </font> </div>  </div> ";
        return out;
    }

    public void setRunCorrect(boolean b) {
        // set runCorrect in agslog
    }

    private static String readFile(String filePath) {
        String output = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str = in.readLine()) != null) {
                output += str + "\n";
            }
            in.close();
        } catch (IOException e) {
        }

        return output;
    }

    private boolean grade(String a, String b) {
        String check = a;
        String[] key = b.split("#");

        for (int i = 0; i < key.length; i++) {
            if (a.contains(key[i])) {
                if (i != key.length) {
                    String[] temp = check.split(key[i], 2);
                    check = temp[1];
                }


            } else {
                return false;
            }
        }
        return true;
    }
    
    public void submit(boolean run) {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            String boolrun;
            double score = 1.0;
            double currentScore = 0.0;
            PreparedStatement p = conn.prepareStatement("SELECT score FROM agslog WHERE agslog.username = '" + username + "' AND agslog.exerciseName = '" + exercise + "';");
            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                currentScore = Double.parseDouble(rs.getString(1));
            }

            if (run == true) {
                boolrun = "1";

                p = conn.prepareStatement("SELECT score FROM exerciseassigned WHERE exerciseassigned.username = '" + instructorUsername + "' AND exerciseassigned.exerciseName = '" + exercise + "';");
                rs = p.executeQuery();

                if (rs.next()) {
                    score = Double.parseDouble(rs.getString(1));
                }
            } else {
                boolrun = "0";
            }
            
            score = Math.max(score, currentScore);

            p = conn.prepareStatement("UPDATE agslog SET compileCorrect = 1, runCorrect = " + boolrun + ", submitted = 1, score = '" + score + "'  WHERE agslog.username = '" + username + "' AND agslog.exerciseName = '" + exercise + "';");
            p.executeUpdate();

        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }
}
