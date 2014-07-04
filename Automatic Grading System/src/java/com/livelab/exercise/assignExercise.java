package com.livelab.exercise;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javax.faces.bean.*;
import javax.sql.rowset.CachedRowSet;

@ManagedBean
@SessionScoped
public class assignExercise implements Serializable {

    private List<String> selectDefaultExercise;
    private List<String> selectedDefaultExercise;
    private List<String> selectAssignedExercise;
    private List<String> selectedAssignedExercise;
    private List<String> selectCustomExercise;
    private List<String> selectedCustomExercise;
    private List<Data> data;
    private String username;
    private String timeZone;
    private String selectedTimeZone;

    public assignExercise() {
    }

    public List<String> getSelectDefaultExercise() {
        return selectDefaultExercise;
    }

    public void setSelectDefaultExercise(List<String> selectNotAssignedExercise) {
        this.selectDefaultExercise = selectNotAssignedExercise;
    }

    public List<String> getSelectedDefaultExercise() {
        return selectedDefaultExercise;
    }

    public void setSelectedDefaultExercise(List<String> selectedNotAssignedExercise) {
        this.selectedDefaultExercise = selectedNotAssignedExercise;
    }

    public List<String> getSelectAssignedExercise() {
        return selectAssignedExercise;
    }

    public void setSelectAssignedExercise(List<String> selectAssignedExercise) {
        this.selectAssignedExercise = selectAssignedExercise;
    }

    public List<String> getSelectedAssignedExercise() {
        return selectedAssignedExercise;
    }

    public void setSelectedAssignedExercise(List<String> selectedAssignedExercise) {
        this.selectedAssignedExercise = selectedAssignedExercise;
    }

    public List<String> getSelectCustomExercise() {
        return selectCustomExercise;
    }

    public void setSelectCustomExercise(List<String> selectAssignedCustomExercise) {
        this.selectCustomExercise = selectAssignedCustomExercise;
    }

    public List<String> getSelectedCustomExercise() {
        return selectedAssignedExercise;
    }

    public void setSelectedCustomExercise(List<String> selectedAssignedCustomExercise) {
        this.selectedCustomExercise = selectedAssignedCustomExercise;
    }

    public String getSelectedTimeZone() {
        return selectedTimeZone;
    }

    public void setSelectedTimeZone(String selectedTimeZone) {
        this.selectedTimeZone = selectedTimeZone;
    }

    public String[] getTimeZoneList() {
        return TimeZone.getAvailableIDs();
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void assign() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            Iterator itr = selectedDefaultExercise.iterator();
            while (itr.hasNext()) {
                String exercise = (String) itr.next();
                PreparedStatement p = conn.prepareStatement("INSERT INTO exerciseassigned(username, exerciseName)"
                        + "VALUES(?, ?);");
                p.setString(1, username);
                p.setString(2, exercise);
                p.executeUpdate();

                addToagslog(exercise);

                p = conn.prepareStatement("UPDATE exerciseassigned SET dueDate = DATE_ADD(exerciseassigned.whenAssigned ,INTERVAL 7 DAY) WHERE exerciseassigned.username = '" + username + "' AND exerciseassigned.exerciseName = '" + exercise + "';");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        refresh(username);
    }

    public void assignCustom() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            Iterator itr = selectedCustomExercise.iterator();
            while (itr.hasNext()) {
                String exercise = (String) itr.next();
                PreparedStatement p = conn.prepareStatement("INSERT INTO exerciseassigned(username, exerciseName)"
                        + "VALUES(?, ?);");
                p.setString(1, username);
                p.setString(2, exercise);
                p.executeUpdate();

                addToagslog(exercise);

                p = conn.prepareStatement("UPDATE exerciseassigned SET dueDate = DATE_ADD(exerciseassigned.whenAssigned ,INTERVAL 7 DAY) WHERE exerciseassigned.username = '" + username + "' AND exerciseassigned.exerciseName = '" + exercise + "';");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        refresh(username);
    }

    public void unassign() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            Iterator itr = selectedAssignedExercise.iterator();
            while (itr.hasNext()) {
                String exercise = (String) itr.next();
                PreparedStatement p = conn.prepareStatement("DELETE FROM exerciseassigned WHERE exerciseassigned.username = '" + username + "' AND exerciseassigned.exerciseName = '" + exercise + "';");
                p.executeUpdate();

                deleteFromagslog(exercise);
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        refresh(username);
    }

    public void refresh(String username) {
        this.username = username;
        selectDefaultExercise = new ArrayList<String>();
        selectedDefaultExercise = new ArrayList<String>();
        selectAssignedExercise = new ArrayList<String>();
        selectedAssignedExercise = new ArrayList<String>();
        selectCustomExercise = new ArrayList<String>();
        selectedCustomExercise = new ArrayList<String>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT exerciseName FROM exercisenotassignedview WHERE exercisenotassignedview.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                selectDefaultExercise.add(rs.getString(1));
            }

            p = conn.prepareStatement("SELECT exerciseName FROM exerciseassigned WHERE exerciseassigned.username = '" + username + "';");
            rs = p.executeQuery();
            while (rs.next()) {
                selectAssignedExercise.add(rs.getString(1));
            }

            p = conn.prepareStatement("SELECT exerciseName FROM customexercisenotassignedview WHERE customexercisenotassignedview.username = '" + username + "';");
            rs = p.executeQuery();
            while (rs.next()) {
                selectCustomExercise.add(rs.getString(1));
            }

            p = conn.prepareStatement("SELECT timeZoneId FROM AGSInstructor WHERE AGSInstructor.username = '" + username + "';");
            rs = p.executeQuery();
            while (rs.next()) {
                timeZone = rs.getString(1);
                selectedTimeZone = timeZone;
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public void setANewTimeZone() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("UPDATE AGSInstructor SET timeZoneId = '" + selectedTimeZone + "' WHERE AGSInstructor.username = '" + username + "';");
            p.executeUpdate();
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        refresh(username);
    }

    public void saveChanges() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            for (int i = 0; i < data.size(); i++) {
                String dueDate = data.get(i).getDueDate();
                String score = data.get(i).getScore();
                String exerciseName = data.get(i).getExerciseName();
                PreparedStatement p = conn.prepareStatement("UPDATE exerciseassigned SET dueDate = CAST('" + dueDate + "' AS DATETIME), score = '" + score + "' WHERE exerciseassigned.username = '" + username + "' AND exerciseassigned.exerciseName = '" + exerciseName + "';");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public List<Data> getExercises() throws SQLException {
        data = new ArrayList<Data>();

        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT exerciseName, whenAssigned, dueDate, score FROM exerciseassigned WHERE exerciseassigned.username = '" + username + "' ORDER BY whenAssigned DESC;");
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();

        rowSet.populate(p.executeQuery());
        List<Data> table = new ArrayList<Data>();

        while (rowSet.next()) {
            Data item = new Data();
            item.setExerciseName(rowSet.getString(1));
            item.setWhenAssigned(rowSet.getString(2));
            item.setDueDate(rowSet.getString(3));
            item.setScore(rowSet.getString(4));
            table.add(item);
            data.add(item);
        }

        return table;

    }

    public void addToagslog(String exercise) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT courseID FROM agsinstructor WHERE agsinstructor.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String courseID = rs.getString(1);
                p = conn.prepareStatement("SELECT username FROM agsstudent WHERE agsstudent.courseId = '" + courseID + "';");
                ResultSet rs2 = p.executeQuery();
                while (rs2.next()) {
                    String student = rs2.getString(1);
                    p = conn.prepareStatement("INSERT INTO agslog(username, exerciseName)"
                            + "VALUES(?, ?);");
                    p.setString(1, student);
                    p.setString(2, exercise);
                    p.executeUpdate();
                }
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public void deleteFromagslog(String exercise) {
        try {
            System.out.println(exercise);
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT courseID FROM agsinstructor WHERE agsinstructor.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String courseID = rs.getString(1);
                p = conn.prepareStatement("SELECT username FROM agsstudent WHERE agsstudent.courseId = '" + courseID + "';");
                System.out.println(courseID);
                ResultSet rs2 = p.executeQuery();
                while (rs2.next()) {
                    String student = rs2.getString(1);
                    System.out.println(student);
                    p = conn.prepareStatement("DELETE FROM agslog WHERE agslog.username = '" + student + "' AND agslog.exerciseName = '" + exercise + "';");
                    p.executeUpdate();
                }
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public static class Data {

        private String exerciseName;
        private String whenAssigned;
        private String dueDate;
        private String score;

        public String getExerciseName() {
            return exerciseName;
        }

        public void setExerciseName(String exerciseName) {
            this.exerciseName = exerciseName;
        }

        public String getWhenAssigned() {
            return whenAssigned;
        }

        public void setWhenAssigned(String whenAssigned) {
            this.whenAssigned = whenAssigned;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }
}
