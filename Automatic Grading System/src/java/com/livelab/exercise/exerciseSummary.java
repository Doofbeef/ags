package com.livelab.exercise;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.sql.rowset.CachedRowSet;

@ManagedBean
@SessionScoped
public class exerciseSummary implements Serializable {

    private String username;
    private double totalScore;
    private double maxTotalScore;
    private double percentage;
    private int exercisesAssigned;
    private List<ExerciseData> data;

    public exerciseSummary() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getTotalScore() throws SQLException{
        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT exerciseAssigned.exerciseName, exerciseAssigned.dueDate, exerciseAssigned.score, agslog.score FROM exerciseassigned, agslog WHERE agslog.username = '" + username + "' AND agslog.exerciseName = exerciseAssigned.exerciseName ORDER BY whenAssigned DESC;");
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();

        rowSet.populate(p.executeQuery());
        double cumulativeScore = 0;
        double cumulativeMaxScore = 0;
        int cumulativeExercisesAssigned = 0;
        while (rowSet.next()) {
            cumulativeExercisesAssigned++;
            cumulativeScore += rowSet.getDouble(4);
            cumulativeMaxScore += rowSet.getDouble(3);        
        } 
        
        totalScore = cumulativeScore;
        
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getMaxTotalScore() throws SQLException{
        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT exerciseAssigned.exerciseName, exerciseAssigned.dueDate, exerciseAssigned.score, agslog.score FROM exerciseassigned, agslog WHERE agslog.username = '" + username + "' AND agslog.exerciseName = exerciseAssigned.exerciseName ORDER BY whenAssigned DESC;");
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();

        rowSet.populate(p.executeQuery());
        double cumulativeScore = 0;
        double cumulativeMaxScore = 0;
        int cumulativeExercisesAssigned = 0;
        while (rowSet.next()) {
            cumulativeExercisesAssigned++;
            cumulativeScore += rowSet.getDouble(4);
            cumulativeMaxScore += rowSet.getDouble(3);        
        }
        
        maxTotalScore = cumulativeMaxScore;
        
        return maxTotalScore;
    }

    public void setMaxTotalScore(double maxTotalScore) {
        this.maxTotalScore = maxTotalScore;
    }

    public double getPercentage() throws SQLException {
        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT exerciseAssigned.exerciseName, exerciseAssigned.dueDate, exerciseAssigned.score, agslog.score FROM exerciseassigned, agslog WHERE agslog.username = '" + username + "' AND agslog.exerciseName = exerciseAssigned.exerciseName ORDER BY whenAssigned DESC;");
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();

        rowSet.populate(p.executeQuery());
        double cumulativeScore = 0;
        double cumulativeMaxScore = 0;
        int cumulativeExercisesAssigned = 0;
        while (rowSet.next()) {
            cumulativeExercisesAssigned++;
            cumulativeScore += rowSet.getDouble(4);
            cumulativeMaxScore += rowSet.getDouble(3);        
        }
        
        percentage = (cumulativeScore / cumulativeMaxScore) * 100.0;
        
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getExercisesAssigned() throws SQLException {
        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT exerciseAssigned.exerciseName, exerciseAssigned.dueDate, exerciseAssigned.score, agslog.score FROM exerciseassigned, agslog WHERE agslog.username = '" + username + "' AND agslog.exerciseName = exerciseAssigned.exerciseName ORDER BY whenAssigned DESC;");
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();

        rowSet.populate(p.executeQuery());
        exercisesAssigned = 0;
        while (rowSet.next()) {
            exercisesAssigned++;
        }
        return exercisesAssigned;
    }

    public void setExercisesAssigned(int exercisesAssigned) {
        this.exercisesAssigned = exercisesAssigned;
    }

    public void refresh(String username) {
        this.username = username;
    }

    public List<ExerciseData> getExercises() throws SQLException {
        data = new ArrayList<ExerciseData>();

        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT exerciseAssigned.exerciseName, exerciseAssigned.dueDate, exerciseAssigned.score, agslog.score FROM exerciseassigned, agslog WHERE agslog.username = '" + username + "' AND agslog.exerciseName = exerciseAssigned.exerciseName ORDER BY whenAssigned DESC;");
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();

        rowSet.populate(p.executeQuery());
        List<ExerciseData> table = new ArrayList<ExerciseData>();

        while (rowSet.next()) {
            ExerciseData item = new ExerciseData();
            item.setExerciseName(rowSet.getString(1));
            item.setDueDate(rowSet.getString(2));
            item.setMaxScore(rowSet.getDouble(3));
            item.setScore(rowSet.getDouble(4));
            table.add(item);
            data.add(item);
        }
        
        refresh(username);

        return table;
    }

    public static class ExerciseData {

        private String exerciseName;
        private String dueDate;
        private double score;
        private double maxScore;

        public String getExerciseName() {
            return exerciseName;
        }

        public void setExerciseName(String exerciseName) {
            this.exerciseName = exerciseName;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public double getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(double maxScore) {
            this.maxScore = maxScore;
        }
    }
}
