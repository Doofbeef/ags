package com.livelab.login;

import java.io.Serializable;
import java.sql.*;
import javax.faces.bean.*;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class instructorLogin implements Serializable {

    private String username;
    private String name;
    private String password;
    private boolean isLoggedin = false;
    private String wrongOutput;
    private String[] currentTab = {"createExercise.xhtml", "assignExercise.xhtml", "examineGradeExercise.xhtml",
        "checkExerciseProgress.xhtml", "plagiarismCheck.xhtml", "manageAccount.xhtml",
        "createQuiz.xhtml", "assignQuiz.xhtml", "examineQuiz.xhtml", "attendance.xhtml"};
    @ManagedProperty(value = "#{assignExercise}")
    private com.livelab.exercise.assignExercise assignExercise;
    @ManagedProperty(value = "#{checkExerciseProgress}")
    private com.livelab.exercise.checkExerciseProgress checkExerciseProgress;
    @ManagedProperty(value = "#{examineGradeExercise}")
    private com.livelab.exercise.examineGradeExercise examineGradeExercise;
    @ManagedProperty(value = "#{attendance}")
    private com.livelab.exercise.attendance attendance;
    @ManagedProperty(value = "#{manage}")
    private com.livelab.exercise.manage manage;

    public instructorLogin() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String wrongOutput() {
        return wrongOutput;
    }

    public boolean getIsLoggedIn() {
        return isLoggedin;
    }

    public String loginCheck() {
        if (isLoggedin) {
            return "true";
        } else {
            return "false";
        }
    }

    public void login() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT username FROM AGSInstructor WHERE AGSInstructor.username ='" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                p = conn.prepareStatement("SELECT studentLoginEnabled FROM AGSInstructor WHERE AGSInstructor.username = '" + username + "';");
                ResultSet rs1 = p.executeQuery();
                if (rs1.next()) {
                    p = conn.prepareStatement("SELECT password FROM AGSInstructor WHERE AGSInstructor.username = '" + username + "';");
                    ResultSet rs2 = p.executeQuery();
                    if (rs2.next()) {
                        if (rs2.getString(1).equals(password)) {
                            p = conn.prepareStatement("UPDATE AGSInstructor SET studentLoginEnabled = 0 WHERE AGSInstructor.username = '" + username + "';");
                            p.executeUpdate();
                            isLoggedin = true;
                            wrongOutput = null;
                            password = null;
                            name = username;
                            refresh();
                        } else {
                            wrongOutput = "Wrong username/password";
                        }
                    }

                }
            } else {
                wrongOutput = "Wrong username/password";
            }

        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:ilob1")).setRendered(true);
    }

    public void refresh() {
        assignExercise.refresh(name);
        checkExerciseProgress.refresh(name);
        examineGradeExercise.refresh(name);
        attendance.refresh(name);
        manage.refresh(name);
    }

    public void logout() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("UPDATE AGSInstructor SET studentLoginEnabled = 1 WHERE AGSInstructor.username = '" + username + "';");
            p.executeUpdate();

        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        username = null;
        isLoggedin = false;
        ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:ilob1")).setRendered(false);
    }

    public String directToTab(int tab) {
        if (isLoggedin) {
            return "/instructor/" + currentTab[tab];
        } else {
            return "/instructor/instructorLogin.xhtml";
        }
    }

    public void setAssignExercise(com.livelab.exercise.assignExercise assignExercise) {
        this.assignExercise = assignExercise;
    }

    public void setCheckExerciseProgress(com.livelab.exercise.checkExerciseProgress checkExerciseProgress) {
        this.checkExerciseProgress = checkExerciseProgress;
    }

    public void setExamineGradeExercise(com.livelab.exercise.examineGradeExercise examineGradeExercise) {
        this.examineGradeExercise = examineGradeExercise;
    }

    public void setAttendance(com.livelab.exercise.attendance attendance) {
        this.attendance = attendance;
    }

    public void setManage(com.livelab.exercise.manage manage) {
        this.manage = manage;
    }
}
