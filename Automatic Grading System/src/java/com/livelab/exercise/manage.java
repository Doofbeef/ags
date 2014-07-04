/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livelab.exercise;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import javax.faces.bean.*;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class manage implements Serializable {

    public static boolean enabled = true;
    public boolean login;
    private List<SelectItem> selectStudentNames;
    private List<String> selectedStudentNames;
    private String username;
    private String email;
    private List<String> studentNames;
    List<Data> data;

    public List<SelectItem> getSelectStudentNames() {
        return selectStudentNames;
    }

    public void setSelectStudentNames(List<SelectItem> selectStudentNames) {
        this.selectStudentNames = selectStudentNames;
    }

    public List<String> getSelectedStudentNames() {
        return selectedStudentNames;
    }

    public void setSelectedStudentNames(List<String> selectedStudentNames) {
        this.selectedStudentNames = selectedStudentNames;
    }

    public void refresh(String username) {
        this.username = username;
        selectStudentNames = new ArrayList<SelectItem>();
        selectedStudentNames = new ArrayList<String>();

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
                    String lastName = rs.getString(1);
                    String firstName = rs.getString(2);
                    String studentUsername = rs.getString(3);
                    selectStudentNames.add(new SelectItem(studentUsername, lastName + " " + firstName));
                }
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public List<Data> getStudentAccount() throws SQLException {
        List<Data> table = new ArrayList<Data>();
        data = new ArrayList<Data>();
        selectStudentNames = new ArrayList<SelectItem>();

        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT courseID FROM agsinstructor WHERE agsinstructor.username = '" + username + "';");

            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                String courseId = rs.getString(1);

                p = conn.prepareStatement("SELECT lastname, firstname, username, email, studentLoginEnabled, major FROM agsstudent WHERE agsstudent.courseId = '" + courseId + "';");
                rs = p.executeQuery();

                while (rs.next()) {
                    Data item = new Data();

                    String lastName = rs.getString(1);
                    String firstName = rs.getString(2);
                    String studentUsername = rs.getString(3);
                    String studentEmail = rs.getString(4);
                    int logins = rs.getInt(5);
                    String major = rs.getString(6);
                    item.setUsername(studentEmail);
//                    studentNames.add(item.toString());
                    selectStudentNames.add(new SelectItem(studentUsername, lastName + " " + firstName));
                    item.setFirstName(firstName);
                    item.setLastName(lastName);
                    item.setStudentUsername(studentUsername);
                    item.setEmail(studentEmail);
                    if (logins == 1) {
                        login = true;
                    } else {
                        login = false;
                    }
                    item.setLoginEnabled(login);
                    item.setMajor(major);


                    table.add(item);
                    data.add(item);
                }
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        return table;
    }

    public void setstudentNames(List<String> selectAddStudents) {
        this.studentNames = selectAddStudents;
    }

    public List<String> getstudentNames() {
        return studentNames;
    }

    public void removeSelf() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            for(int i = 0; i < selectStudentNames.size(); i++) {
                PreparedStatement p = conn.prepareStatement("DELETE FROM agsstudent WHERE agsstudent.username = '" + selectStudentNames.get(i).getValue().toString() + "';");
                p.executeUpdate();
            }
                PreparedStatement p = conn.prepareStatement("DELETE FROM agsinstructor WHERE username = '" + username + "';");
                p.executeUpdate();
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }     
    }

    public void remove() {
        System.out.println(selectedStudentNames.size());
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            for(int i = 0; i < selectedStudentNames.size(); i++) {
                PreparedStatement p = conn.prepareStatement("DELETE FROM agsstudent WHERE agsstudent.username = '" + selectedStudentNames.get(i) + "';");
                p.executeUpdate();
            }
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
                String username = data.get(i).getStudentUsername();
                String email = data.get(i).getEmail();
                int login;
                if(data.get(i).getLoginEnabled() == true) {
                    login = 1;
                }
                else {
                    login = 0;
                }
                String major = data.get(i).getMajor();
                PreparedStatement p = conn.prepareStatement("UPDATE agsstudent SET email = '"
                        + email + "', studentLoginEnabled = " + login + ", major = '" + major + "' WHERE agsstudent.username = '" + username + "';");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        refresh(username);
    }

    public static class Data {

        private String userName;
        private String lastName;
        private String firstName;
        private String email;
        private String major;
        private boolean login;
        private String studentUsername;

        public String getUsername() {
            return userName;
        }

        public void setUsername(String userName) {
            this.userName = userName;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public boolean enable() {
            return enabled;
        }

        public boolean disable() {
            enabled = false;
            return enabled;
        }

        public void setLoginEnabled(boolean login) {
            this.login = login;
        }

        public boolean getLoginEnabled() {
            return login;
        }

        private void setStudentUsername(String username) {
            this.studentUsername = username;
        }

        public String getStudentUsername() {
            return studentUsername;
        }
    }
}
