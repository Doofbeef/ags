/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livelab.exercise;

import java.io.Serializable;
import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.*;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class checkExerciseProgress implements Serializable {

    private String username;
    private String selectedExercise;
    private int totalStudents = 0;
    private List<Data> data;
    private List<SelectItem> assignedExercise;
    private static String email = "";

    public checkExerciseProgress() {
    }

    public String getSelectedExercise() {
        return selectedExercise;
    }

    public void setSelectedExercise(String selectedExercise) {
        this.selectedExercise = selectedExercise;
    }

    public List<SelectItem> getAssignedExercise() {
        return assignedExercise;
    }

    public void setAssignedExercise(List<SelectItem> assignedExercise) {
        this.assignedExercise = assignedExercise;
    }

    public void refresh(String username) {
        this.username = username;
        assignedExercise = new ArrayList<SelectItem>();
        String courseId  = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT courseId FROM agsinstructor WHERE agsinstructor.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                courseId = rs.getString(1);
            }
            
            p = conn.prepareStatement("SELECT COUNT(*) FROM agsStudent WHERE agsStudent.courseId = '" + courseId + "';");
            rs = p.executeQuery();
            if (rs.next()) {
                totalStudents = rs.getInt(1);
            }

            p = conn.prepareStatement("SELECT exerciseName, dueDate FROM exerciseassigned WHERE exerciseassigned.username = '" + username + "' ORDER BY dueDate DESC;");
            rs = p.executeQuery();
            while (rs.next()) {
                String date = rs.getString(2);
                date = date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
                assignedExercise.add(new SelectItem(rs.getString(1), rs.getString(1) + " Due: " + date));
            }
            assignedExercise.add(new SelectItem("*", "All(may take time to load)"));
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        selectedExercise = assignedExercise.get(0).getValue().toString();
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public List<Data> getExerciseProgressSummary() throws SQLException {

        totalStudents = 0;

        List<Data> table = new ArrayList<Data>();

        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT courseID FROM agsinstructor WHERE agsinstructor.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String courseId = rs.getString(1);
                p = conn.prepareStatement("SELECT lastname, firstname, username FROM agsstudent WHERE agsstudent.courseId = '" + courseId + "';");
                rs = p.executeQuery();

                while (rs.next()) {
                    totalStudents++;

                    Data item = new Data();
                    String firstName = rs.getString(1);
                    String lastName = rs.getString(2);
                    String studentUsername = rs.getString(3);

                    item.setFirstName(firstName);
                    item.setLastName(lastName);

                    p = conn.prepareStatement("SELECT score FROM agslog WHERE agslog.username = '" + studentUsername + "';");
                    ResultSet rs2 = p.executeQuery();

                    double totalScore = 0.0;
                    while (rs2.next()) {
                        if (rs2.getString(1) != null) {
                            totalScore += Double.parseDouble(rs2.getString(1));
                        }
                    }

                    item.setTotalScore(totalScore);

                    p = conn.prepareStatement("SELECT score FROM exerciseassigned WHERE exerciseassigned.username = '" + username + "';");
                    rs2 = p.executeQuery();

                    double maxTotalScore = 0.0;
                    while (rs2.next()) {
                        maxTotalScore += Double.parseDouble(rs2.getString(1));
                    }

                    item.setMaxTotalScore(maxTotalScore);

                    item.setPercentage(MessageFormat.format("{0,number,#.##%}", totalScore / maxTotalScore));

                    p = conn.prepareStatement("SELECT COUNT(*) FROM agslog WHERE agslog.username = '" + studentUsername + "' AND agslog.submitted = 1;");
                    rs2 = p.executeQuery();

                    int numberOfExercisesSubmitted = 0;
                    if (rs2.next()) {
                        numberOfExercisesSubmitted = Integer.parseInt(rs2.getString(1));
                    }

                    item.setNumberOfExercisesSubmitted(numberOfExercisesSubmitted);

                    table.add(item);
                }
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        return table;
    }

    public List<Data> getExerciseProgress() throws SQLException {

        List<Data> table = new ArrayList<Data>();
        data = new ArrayList<Data>();

        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT courseID FROM agsinstructor WHERE agsinstructor.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String courseId = rs.getString(1);
                
                if(selectedExercise.equals("*")) {
                    p = conn.prepareStatement("SELECT lastName, firstName, exerciseName, score, agslog.username, email FROM agsstudent, agslog WHERE agsstudent.username = agslog.username AND agsstudent.courseid = '" + courseId + "';");
                }
                else {
                    p = conn.prepareStatement("SELECT lastName, firstName, exerciseName, score, agslog.username, email FROM agsstudent, agslog WHERE agsstudent.username = agslog.username AND agsstudent.courseid = '" + courseId + "' AND agslog.exerciseName='" + selectedExercise + "';");
                }
                rs = p.executeQuery();

                while (rs.next()) {
                    Data item = new Data();

                    item.setFirstName(rs.getString(1));
                    item.setLastName(rs.getString(2));
                    item.setExerciseName(rs.getString(3));
                    item.setScore(Double.parseDouble(rs.getString(4)));
                    item.setUsername(rs.getString(5));
                    item.setEmail(rs.getString(6));
                    
                    p = conn.prepareStatement("SELECT score FROM exerciseassigned WHERE exerciseassigned.username = '" + username + "' AND exerciseassigned.exerciseName = '" + rs.getString(3) + "';");
                    ResultSet rs2 = p.executeQuery();

                    if (rs2.next()) {
                        item.setMaxScore(Double.parseDouble(rs2.getString(1)));
                    }
email += rs.getString(6) + ',';
                    data.add(item);
                    table.add(item);
                }
                

            }


        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        return table;

    }

    public void saveNewScore() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            for (int i = 0; i < data.size(); i++) {
                String studentUsername = data.get(i).getUsername();
                String score = Double.toString(data.get(i).getScore());
                String exerciseName = data.get(i).getExerciseName();
                PreparedStatement p = conn.prepareStatement("UPDATE agslog SET score = '" + score + "' WHERE agslog.username = '" + studentUsername + "' AND agslog.exerciseName = '" + exerciseName + "';");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public String getEmail(){
        return email;
    }
    public static class Data {

        private String username;
        private String lastName;
        private String firstName;
        private double totalScore;
        private double maxTotalScore;
        private String Percentage;
        private int numberOfExercisesSubmitted;
        private String exerciseName;
        private double score;
        private double maxScore;
        private String email;

        private String getUsername() {
            return username;
        }

        private void setUsername(String username) {
            this.username = username;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setTotalScore(double totalScore) {
            this.totalScore = totalScore;
        }

        public double getTotalScore() {
            return totalScore;
        }

        public void setMaxTotalScore(double maxTotalScore) {
            this.maxTotalScore = maxTotalScore;
        }

        public double getMaxTotalScore() {
            return maxTotalScore;
        }

        public void setPercentage(String Percentage) {
            this.Percentage = Percentage;
        }

        public String getPercentage() {
            return Percentage;
        }

        public void setNumberOfExercisesSubmitted(int numberOfExercisesSubmitted) {
            this.numberOfExercisesSubmitted = numberOfExercisesSubmitted;
        }

        public int getNumberOfExercisesSubmitted() {
            return numberOfExercisesSubmitted;
        }

        public void setExerciseName(String exerciseName) {
            this.exerciseName = exerciseName;
        }

        public String getExerciseName() {
            return exerciseName;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public double getScore() {
            return score;
        }

        public void setMaxScore(double maxScore) {
            this.maxScore = maxScore;
        }

        public double getMaxScore() {
            return maxScore;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }
}