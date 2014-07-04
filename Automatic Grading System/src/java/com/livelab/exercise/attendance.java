/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livelab.exercise;


import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.sql.rowset.CachedRowSet;

@ManagedBean
@SessionScoped
public class attendance implements Serializable {
    
    String username;
    java.util.Date date1 = new java.util.Date();
    java.util.Date date2 = new java.util.Date();
    List<String> selectAddStudents;
    List<String> selectedAddStudents;
    List<String> selectRemoveStudents;
    List<String> selectedRemoveStudents;
    private List<RecordData> recordData;
    private List<SummaryData> summaryData;
    
    public attendance() {
        
    }

    public java.util.Date getDate1() {
        return date1;
    }

    public void setDate1(java.util.Date date1) {
        this.date1 = date1;
    }

    public java.util.Date getDate2() {
        return date2;
    }

    public void setDate2(java.util.Date date2) {
        this.date2 = date2;
    }

    public List<String> getSelectAddStudents() {
        return selectAddStudents;
    }

    public void setSelectAddStudents(List<String> selectAddStudents) {
        this.selectAddStudents = selectAddStudents;
    }

    public List<String> getSelectedAddStudents() {
        return selectedAddStudents;
    }

    public void setSelectedAddStudents(List<String> selectedAddStudents) {
        this.selectedAddStudents = selectedAddStudents;
    }

    public List<String> getSelectRemoveStudents() {
        return selectRemoveStudents;
    }

    public void setSelectRemoveStudents(List<String> selectRemoveStudents) {
        this.selectRemoveStudents = selectRemoveStudents;
    }

    public List<String> getSelectedRemoveStudents() {
        return selectedRemoveStudents;
    }

    public void setSelectedRemoveStudents(List<String> selectedRemoveStudents) {
        this.selectedRemoveStudents = selectedRemoveStudents;
    }

    public List<RecordData> getRecordData() {
        return recordData;
    }

    public void setRecordData(List<RecordData> recordData) {
        this.recordData = recordData;
    }

    public List<SummaryData> getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(List<SummaryData> summaryData) {
        this.summaryData = summaryData;
    }
    
    public List<attendance.RecordData> getRecord() throws SQLException {
        recordData = new ArrayList<attendance.RecordData>();

        PreparedStatement p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            p = conn.prepareStatement("SELECT agsstudent.firstName, agsstudent.lastName, attendance.dateAttended, attendance.attended FROM agsstudent, attendance, agsinstructor"
                    + " WHERE agsstudent.username=attendance.studentUsername AND agsstudent.courseId = agsinstructor.courseId AND agsinstructor.username = '" + username + "' "
                    + "ORDER BY attendance.dateAttended;");
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }

        CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();

        rowSet.populate(p.executeQuery());
        List<attendance.RecordData> table = new ArrayList<attendance.RecordData>();

        while (rowSet.next()) {
            attendance.RecordData item = new attendance.RecordData();
            item.setFullName(rowSet.getString(1) + " " + rowSet.getString(2));
            item.setDateAttended(rowSet.getDate(3).toString());
            item.setAttended(rowSet.getBoolean(4));
            table.add(item);
            recordData.add(item);
        }
        
        refresh(username);

        return table;
    }
    
    public List<attendance.SummaryData> getSummary() throws SQLException {
        summaryData = new ArrayList<attendance.SummaryData>();   
        List<attendance.SummaryData> table = new ArrayList<attendance.SummaryData>();

        try {
            
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            
            int totalDays = 0;
            PreparedStatement p = conn.prepareStatement("SELECT DISTINCT(dateAttended) FROM attendance, "
                    + "agsinstructor WHERE agsinstructor.email = attendance.instructorEmail AND agsinstructor.username = '" + username + "';");
            CachedRowSet rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate(p.executeQuery());
            while (rowSet.next()) {
                totalDays++;
            }
            
            p = conn.prepareStatement("SELECT agsstudent.username, agsstudent.firstName, agsstudent.lastName, attendance.attended FROM agsstudent, attendance, agsinstructor"
                    + " WHERE agsstudent.username=attendance.studentUsername AND agsstudent.courseId = agsinstructor.courseId AND agsinstructor.username = '" + username + "'"
                    + "ORDER BY agsstudent.username;");
        

            rowSet = new com.sun.rowset.CachedRowSetImpl();

            rowSet.populate(p.executeQuery());

            int currentItem = -1;
            String currentStudent = "";
            int attendanceCount = 0;
            attendance.SummaryData item = new attendance.SummaryData();
            while (rowSet.next()) {
                
                if (rowSet.getString(1).equals(currentStudent)) {
                    if (rowSet.getBoolean(4) && totalDays != 0) {
                        attendanceCount++;
                        table.get(currentItem).setCount(attendanceCount);
                        table.get(currentItem).setPercentage(((double)attendanceCount / (double)totalDays) * 100.0);
                    }
                } else {
                    attendanceCount = 0;
                    currentStudent = rowSet.getString(1);
                    item = new attendance.SummaryData();
                    
                    if (rowSet.getBoolean(4)) {
                        attendanceCount++;
                    }
                    
                    item.setCount(attendanceCount);
                    if (totalDays != 0) {
                        item.setPercentage(((double)attendanceCount / (double)totalDays) * 100.0);
                    }
                    
                    item.setLastName(rowSet.getString(3));
                    item.setFirstName(rowSet.getString(2));
                    table.add(item);
                    currentItem++;
                }
            }
            
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        
        refresh(username);

        return table;
    }
    
    public void refresh(String username) {
        this.username = username;
        selectAddStudents = new ArrayList<String>();
        selectRemoveStudents = new ArrayList<String>();
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            PreparedStatement p = conn.prepareStatement("SELECT agsstudent.firstName, agsstudent.lastName FROM agsstudent, agsinstructor WHERE "
                    + "agsstudent.courseID = agsinstructor.courseID AND agsinstructor.username = '" + username + "';");
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String firstName = rs.getString(1);
                String lastName = rs.getString(2);
                selectAddStudents.add(firstName + " " + lastName);
            }
            
                    

            p = conn.prepareStatement("SELECT agsstudent.firstName, agsstudent.lastName FROM agsstudent, agsinstructor WHERE "
                    + "agsstudent.courseID = agsinstructor.courseID AND agsinstructor.username = '" + username + "';");
            rs = p.executeQuery();
            while (rs.next()) {
                String firstName = rs.getString(1);
                String lastName = rs.getString(2);
                selectRemoveStudents.add(firstName + " " + lastName);
            }
            
            
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
    }
    
    public void addAttendance() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");
            PreparedStatement p = conn.prepareStatement("SELECT DISTINCT(dateAttended) FROM attendance, "
                    + "agsinstructor WHERE agsinstructor.email = attendance.instructorEmail AND agsinstructor.username = '" + username + "';");
            
            ResultSet rs = p.executeQuery();
            boolean repeatDate = false;
            Timestamp ts = new Timestamp(date1.getTime());
            while(rs.next()) {
                if (ts.equals(rs.getTimestamp(1))) {
                    repeatDate = true;
                }
            }
            
            if (repeatDate) {
                Iterator itr = selectedAddStudents.iterator();
                while (itr.hasNext()) {          
                    String[] fullName = ((String)itr.next()).split(" ");

                    p = conn.prepareStatement("SELECT username FROM agsstudent WHERE lastName = '" + fullName[1] + "';");
                    rs = p.executeQuery();    
                    String studentUsername = "";
                    while (rs.next()) {
                        studentUsername = rs.getString(1);
                    }

                    Timestamp timeStamp = new Timestamp(date1.getTime());
                    p = conn.prepareStatement("UPDATE attendance SET attended = 1 WHERE studentUsername ='" + studentUsername + "' AND dateAttended ='" + timeStamp + "';");
                    p.executeUpdate();
                }
            } else {            
                List<String> notSelectedAddStudents = new ArrayList<String>();
                for (int i = 0; i < selectAddStudents.size(); i++) {
                    boolean hit = false;
                    for (int j = 0; j < selectedAddStudents.size(); j++) {
                        if (selectedAddStudents.get(j).equals(selectAddStudents.get(i))) {
                            hit = true;
                        }
                    }
                    if (!hit) {
                        notSelectedAddStudents.add(selectAddStudents.get(i));
                    }
                }

                Iterator itr = selectedAddStudents.iterator();
                while (itr.hasNext()) {          
                    String[] fullName = ((String)itr.next()).split(" ");

                    p = conn.prepareStatement("SELECT username FROM agsstudent WHERE lastName = '" + fullName[1] + "';");
                    rs = p.executeQuery();    
                    String studentUsername = "";
                    while (rs.next()) {
                        studentUsername = rs.getString(1);
                    }

                    p = conn.prepareStatement("SELECT agsinstructor.email FROM agsinstructor, agsstudent WHERE agsstudent.courseID = agsinstructor.courseID;");
                    rs = p.executeQuery();
                    String instructorEmail = "";
                    while (rs.next()) {
                        instructorEmail = rs.getString(1);
                    }

                    p = conn.prepareStatement("INSERT INTO attendance(dateAttended, studentUsername, instructorEmail, attended)"
                            + "VALUES(?, ?, ?, ?);");

                    Timestamp timeStamp = new Timestamp(date1.getTime());
                    p.setTimestamp(1, timeStamp);              
                    p.setString(2, studentUsername);
                    p.setString(3, instructorEmail);
                    p.setBoolean(4, true);
                    p.executeUpdate();
                }

                itr = notSelectedAddStudents.iterator();
                while (itr.hasNext()) {          
                    String[] fullName = ((String)itr.next()).split(" ");

                    p = conn.prepareStatement("SELECT username FROM agsstudent WHERE lastName = '" + fullName[1] + "';");
                    rs = p.executeQuery();    
                    String studentUsername = "";
                    while (rs.next()) {
                        studentUsername = rs.getString(1);
                    }

                    p = conn.prepareStatement("SELECT agsinstructor.email FROM agsinstructor, agsstudent WHERE agsstudent.courseID = agsinstructor.courseID;");
                    rs = p.executeQuery();
                    String instructorEmail = "";
                    while (rs.next()) {
                        instructorEmail = rs.getString(1);
                    }

                    p = conn.prepareStatement("INSERT INTO attendance(dateAttended, studentUsername, instructorEmail, attended)"
                            + "VALUES(?, ?, ?, ?);");

                    Timestamp timeStamp = new Timestamp(date1.getTime());
                    p.setTimestamp(1, timeStamp);              
                    p.setString(2, studentUsername);
                    p.setString(3, instructorEmail);
                    p.setBoolean(4, false);
                    p.executeUpdate();
                }
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        refresh(username);
    }
    
    public void removeAttendance() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/javalivelab9e", "agsuser", "agsuser");

            Iterator itr = selectedRemoveStudents.iterator();
            while (itr.hasNext()) {          
                String[] fullName = ((String)itr.next()).split(" ");
                
                PreparedStatement p = conn.prepareStatement("SELECT username FROM agsstudent WHERE lastName = '" + fullName[1] + "';");
                ResultSet rs = p.executeQuery();    
                String studentUsername = "";
                while (rs.next()) {
                    studentUsername = rs.getString(1);
                }
                
                Timestamp timeStamp = new Timestamp(date2.getTime());
                p = conn.prepareStatement("UPDATE attendance SET attended = 0 WHERE studentUsername ='" + studentUsername + "' AND dateAttended ='" + timeStamp + "';");
                p.executeUpdate();
            }
        } catch (SQLException e) {
        } catch (ClassNotFoundException e) {
        }
        refresh(username);
    }
    
    
    public static class RecordData {
        String dateAttended;
        String fullName;
        boolean attended;

        public String getDateAttended() {
            return dateAttended;
        }

        public void setDateAttended(String dateAttended) {
            this.dateAttended = dateAttended;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public boolean getAttended() {
            return attended;
        }

        public void setAttended(boolean attended) {
            this.attended = attended;
        }
    }
    
    public static class SummaryData {
        String lastName;
        String firstName;
        int count;
        double percentage;

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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
        
    }
}
