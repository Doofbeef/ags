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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class examineGradeExercise implements Serializable {

    static private String defaultcomment = "The instructor can enter comments and override the score. If the program runs correctly, the system automatically assigns the full score allowed for this exercise. If the program does not run, but compiles correctly, the system assigns score 1. Instructor can always override the score from the Instructor tab under Examine Exercise.";
    private String comment;
    java.util.Date date;
    private List<SelectItem> assignedExercise;
    private List<SelectItem> students;
    private String selectedExercise;
    private String selectedStudent;
    private String username;
    private String exercise;
    private String student;
    private String courseId;
    private double score;
    private boolean exerciseChosen;
    private String exerciseInput;
    private String runOutputText;
    private boolean runRendered = false;

    public examineGradeExercise() {
    }

    public List<SelectItem> getAssignedExercise() {
        return assignedExercise;
    }

    public void setAssignedExercise(List<SelectItem> assignedExercise) {
        this.assignedExercise = assignedExercise;
    }

    public List<SelectItem> getStudents() {
        return students;
    }

    public void setStudents(List<SelectItem> students) {
        this.students = students;
    }

    public String getSelectedExercise() {
        return selectedExercise;
    }

    public void setSelectedExercise(String selectedExercise) {
        this.selectedExercise = selectedExercise;
    }

    public String getSelectedStudent() {
        return selectedStudent;
    }

    public void setSelectedStudent(String selectedStudent) {
        this.selectedStudent = selectedStudent;
    }

    public boolean getExerciseChosen() {
        return exerciseChosen;
    }

    public void setExerciseChosen(boolean exerciseChosen) {
        try {
            exerciseInput = readFile(CommonConstant.AGS_ROOT + courseId + File.separator + selectedStudent + File.separator + selectedExercise + ".java");
        } catch (Exception e) {
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT score FROM agslog WHERE agslog.username = '" + selectedStudent + "' AND agslog.exerciseName = '" + selectedExercise + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                score = rs.getDouble(1);
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        exercise = selectedExercise;
        student = selectedStudent;
        comment = defaultcomment;

        this.exerciseChosen = exerciseChosen;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public java.util.Date getDate() {
        return date;
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

    public void saveNewScore() {
        System.out.println("Why");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("UPDATE agslog SET score = '" + score + "' WHERE agslog.username = '" + student + "' AND agslog.exerciseName = '" + exercise + "';");
            p.executeUpdate();

            if (comment != defaultcomment) {
                p = conn.prepareStatement("UPDATE agslog SET comment = '" + comment + "' WHERE agslog.username = '" + student + "' AND agslog.exerciseName = '" + exercise + "';");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public void setIndividualExtension() {
        try {
            long time = date.getTime();
            Timestamp timeStamp = new Timestamp(time);

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT * FROM exercisedueextended WHERE exercisedueextended.instructorUsername = '" + username + "' AND exercisedueextended.exerciseName = '" + exercise + "' AND exercisedueextended.studentUsername = '" + student + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                p = conn.prepareStatement("UPDATE exercisedueextended SET dueDate = '" + timeStamp + "' WHERE exercisedueextended.instructorUsername = '" + username + "' AND exercisedueextended.exerciseName = '" + exercise + "' AND exercisedueextended.studentUsername = '" + student + "';");
                p.executeUpdate();
            } else {
                p = conn.prepareStatement("INSERT INTO exercisedueextended (instructorUsername, exerciseName, studentUsername, dueDate) VALUES ('" + username + "', '" + exercise + "', '" + student + "', '" + timeStamp + "');");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

    }

    public void saveAndCompile() {
        Writer writer = null;
        runRendered = true;

        try {

            File file = new File(CommonConstant.AGS_ROOT + courseId + File.separator + student + File.separator + exercise + ".java");
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
        students = new ArrayList<SelectItem>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT courseId FROM agsinstructor WHERE agsinstructor.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                courseId = rs.getString(1);
            }

            p = conn.prepareStatement("SELECT username, firstName, lastName FROM agsstudent WHERE agsstudent.courseid = '" + courseId + "' ORDER BY lastName;");
            rs = p.executeQuery();
            while (rs.next()) {
                students.add(new SelectItem(rs.getString(1), rs.getString(2) + " " + rs.getString(3)));
            }

            p = conn.prepareStatement("SELECT exerciseName, dueDate FROM exerciseassigned WHERE exerciseassigned.username = '" + username + "' ORDER BY dueDate DESC;");
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
                    courseId, student, null, selectedExercise);
            if (result.isInfiniteLoop) {
                out = runOfTimeMessage();
            } else {
                out = gradeBookExerciseWithoutInput(result, courseId,
                        student);
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
                        courseId, student,
                        inputFilename, selectedExercise);
                if (result.isInfiniteLoop) {
                    out = runOfTimeMessage();
                } else {

                    String studentOutputFile = CommonConstant.AGS_ROOT + courseId + File.separator
                            + student + File.separator + selectedExercise + ".out";
                    String studentOutput = readFile(studentOutputFile);

                    String keyFile = CommonConstant.AGS_ROOT + "gradeexercise" + File.separator + selectedExercise + (char) (i + 'a') + ".output";
                    String keyOutput = readFile(keyFile);

                    RunResult runResult = gradeOneRun(studentOutput, keyOutput, i, result.timeUsed);

                    if (runResult.isCorrect) {
                        out += runResult.outForOneRun;
                    } else {
                        runOutputText = runResult.outForOneRun;
                        runRendered = true;
                        return null;
                    }
                }
            }
        }
        //}

        runOutputText = out;
        runRendered = true;

        return null;
    }

    public String howtocompilerunsubmit() {
        return "<p id=\"parastyle\" style=\"margin-left: 1em; margin-right: 1em\">As the instruction, you can click the <b>Save/Compile/Run</b> button to run the student code. This action does not change student's score. However, you can change the score by clicking the <b>Save/Modify Score/Comments</b> button from the top of this page.";
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
            setRunCorrect(true);
        } else {
            out += "<span style=\"background-color: #FF0000\">Incorrect result!</span>";
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
            System.out.println(key[i]);
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
}
