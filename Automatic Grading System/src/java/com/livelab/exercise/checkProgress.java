/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livelab.exercise;

import java.sql.*;
import java.util.*;

public class checkProgress {
    private String username;
    private String numberOfStudents;
    private List<String> nOfStudents;
    
    public void numOfStudents(List<String> numOfStudents) {
        this.nOfStudents = numOfStudents;
    }
    public void check() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            Iterator itr = nOfStudents.iterator();
            while (itr.hasNext()) {
                String num = (String) itr.next();
                PreparedStatement p = conn.prepareStatement("Select lastname, firstname, from agsuser where courseid = 'ags';");
                p.setString(1, username);
                 
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }
    public String getEmail(){
        check();
        String a = "";
        return a;
    }
    public String getNumOfStudents(){
        check();
        numberOfStudents = "2";        
        return numberOfStudents;
    }
    public String table(){
        String table = "";
        table = "<table></table>";
        table = "Select lastname, firstname, from agsuser where courseid = 'ags'";
        return table;
    }
    
}
