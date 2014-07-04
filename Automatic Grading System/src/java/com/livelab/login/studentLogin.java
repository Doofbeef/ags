package com.livelab.login;

import java.io.Serializable;
import java.sql.*;
import javax.faces.bean.*;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class studentLogin implements Serializable {

    private String username;
    private String name;
    private String password;
    private boolean isLoggedin = false;
    private String wrongOutput;
    private String[] currentTab = {"completeExercise.xhtml", "exerciseSummary.xhtml",
        "peerEvaluation.xhtml", "selfTest.xhtml", "takeInstructorAssignedQuiz.xhtml"};
    @ManagedProperty(value = "#{completeExercise}")
    private com.livelab.exercise.completeExercise completeExercise;
    @ManagedProperty(value = "#{exerciseSummary}")
    private com.livelab.exercise.exerciseSummary exerciseSummary;

    public studentLogin() {
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

            PreparedStatement p = conn.prepareStatement("SELECT username FROM AGSStudent WHERE AGSStudent.username ='" + username + "';");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                p = conn.prepareStatement("SELECT studentLoginEnabled FROM AGSStudent WHERE AGSStudent.username = '" + username + "';");
                ResultSet rs1 = p.executeQuery();
                if (rs1.next()) {
                    p = conn.prepareStatement("SELECT password FROM AGSStudent WHERE AGSStudent.username = '" + username + "';");
                    ResultSet rs2 = p.executeQuery();
                    if (rs2.next()) {
                        if (com.livelab.exercise.manage.enabled == true) {
                            if (rs2.getString(1).equals(password)) {
                                p = conn.prepareStatement("UPDATE AGSStudent SET studentLoginEnabled = 0 WHERE AGSStudent.username = '" + username + "';");
                                p.executeUpdate();
                                isLoggedin = true;
                                wrongOutput = null;
                                password = null;
                                name = username;
                                refresh();
                                System.out.println("Why?");
                            } else {
                                wrongOutput = "Wrong username/password";
                            }
                        } else {
                            wrongOutput = "Login has been disabled!";
                        }
                    }
                }
            } else {
                wrongOutput = "Wrong username/password";
            }

        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:slob1")).setRendered(true);
    }

    public void refresh() {
        completeExercise.refresh(name);
        exerciseSummary.refresh(name);
    }

    public void logout() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("UPDATE AGSStudent SET studentLoginEnabled = 1 WHERE AGSStudent.username = '" + username + "';");
            p.executeUpdate();

        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        username = null;
        isLoggedin = false;
        ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:slob1")).setRendered(false);
    }

    public String directToTab(int tab) {
        if (isLoggedin) {
            return "/student/" + currentTab[tab];
        } else {
            return "/student/studentLogin.xhtml";
        }
    }

    public void setCompleteExercise(com.livelab.exercise.completeExercise completeExercise) {
        this.completeExercise = completeExercise;
    }

    public void setExerciseSummary(com.livelab.exercise.exerciseSummary exerciseSummary) {
        this.exerciseSummary = exerciseSummary;
    }
}
