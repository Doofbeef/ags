package com.livelab.register;

import com.livelab.exercise.CommonConstant;
import java.io.File;
import java.io.Serializable;
import java.sql.*;
import javax.faces.bean.*;

@ManagedBean
@ViewScoped
public class studentRegister implements Serializable{

    private String signupCode;
    private String username;
    private String password;
    private String confirmPassword;
    private String courseID;
    private String firstName;
    private String lastName;
    private String email;
    private String major;
    private String[] errorMessage;
    private boolean errorCheck = false;
    private boolean success = false;

    public studentRegister() {
        errorMessage = new String[7];
        for (int i = 0; i < errorMessage.length; i++) {
            errorMessage[i] = "";
        }
    }

    public String getSignupCode() {
        return signupCode;
    }

    public void setSignupCode(String signupCode) {
        this.signupCode = signupCode;
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

    public String getConfirmPassword() {
        return null;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
    
    public String success() {
        if (success) {
            return "true";
        } else {
            return "false";
        }
    }

    public String message(int i) {
        return errorMessage[i];
    }

    public String fontColor(int i) {
        if (errorMessage[i] == "") {
            return "black";
        } else {
            return "red";
        }
    }

    public String inputColor(int i) {
        if (errorMessage[i] == "") {
            return "white";
        } else {
            return "antiquewhite";
        }
    }
    

    public void register() {
        errorCheck = false;
        success = false;
        for (int i = 0; i < errorMessage.length; i++) {
            errorMessage[i] = "";
        }
        if (!signupCode.equals("student")) {
            errorMessage[0] = "Enter a valid sign up code";
            errorCheck = true;
        }
        if (signupCode.equals("")) {
            errorMessage[0] = "Signup Code:: Validation Error: Value is required.";
            errorCheck = true;
        }
        checkUsername();
        if (username.equals("")) {
            errorMessage[1] = "Username: (use FirstnameLastname convention, e.g., JohnSmith): Validation Error: Value is required.";
            errorCheck = true;
        }
        if (!password.equals(confirmPassword)) {
            errorMessage[2] = "Passwords does not match";
            errorCheck = true;
        }
        if (password.equals("")) {
            errorMessage[2] = "Password:: Validation Error: Value is required.";
            errorCheck = true;
        }
        if (courseID.equals("")) {
            errorMessage[3] = "Course ID:: Validation Error: Value is required.";
            errorCheck = true;
        }
        if (firstName.equals("")) {
            errorMessage[4] = "Student First Name:: Validation Error: Value is required.";
            errorCheck = true;
        }
        if (lastName.equals("")) {
            errorMessage[5] = "Student Last Name:: Validation Error: Value is required.";
            errorCheck = true;
        }
        if (email.equals("")) {
            errorMessage[6] = "Student Email:: Validation Error: Value is required.";
            errorCheck = true;
        }

        if (!errorCheck) {           
            enterIntoDatabase();
            success = true;
            for (int i = 0; i < errorMessage.length; i++) {
                errorMessage[i] = "";
            }
            errorCheck = false;
        }
    }
    
    public void checkUsername() {
        try {           
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT username FROM AGSStudent");
                   ResultSet rs = p.executeQuery();
                   while(rs.next()) {
                       if(rs.getString(1).equals(username)) {
                           errorMessage[1] = "Username already in use.";
                           errorCheck = true;
                       }                          
                   }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public void enterIntoDatabase() {
        try {           
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("INSERT INTO AGSStudent(username, password, lastname, firstname, "
                    + "email, courseId, major)"
                    + "VALUES(?,?,?,?,?,?,?);");
                    p.setString(1, username);
                    p.setString(2, password);
                    p.setString(3, lastName);
                    p.setString(4, firstName);
                    p.setString(5, email);
                    p.setString(6, courseID);
                    p.setString(7, major);
                    p.executeUpdate();
                    
            File studentDirectory = new File(CommonConstant.AGS_ROOT + File.separator + courseID + File.separator + username);
            studentDirectory.mkdirs();
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }
}
