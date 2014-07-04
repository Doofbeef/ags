package com.livelab.exercise;

// Test

/*Common Constants*/
 
/**
 *
 * @author liang
 */
public interface CommonConstant {
    String BOOK_TITLE = "Java 9E";
    String agsCommonRoot = "c:\\agsCommonRoot\\";
    String LANGUAGE = "JAVA";
    String JAVARUNCOMMAND = "C:\\Program Files (x86)\\Java\\jdk1.7.0_07\\bin\\java";
    String JAVACOMPILECOMMAND = "C:\\Program Files (x86)\\Java\\jdk1.7.0_07\\bin\\javac";
    String PYTHONCOMMAND = "c:\\Python32\\python";
    int EXECUTION_TIME_ALLOWED = 5000; // in milliseconds
 
    int EXECUTION_TIME_INTERVAL = 150; // in milliseconds
 
    int NUMBER_OF_TESTS_USED_FOR_STUDENTS = 6;
    int NUMBER_OF_TESTS_USED_FOR_INSTRUCTORS = 10;
    int NUMBER_OF_CHAPTERS = 48;
    String DATASOURCE = "jdbc/javalivelab9e_MySQL";
    String AGS_ROOT = "C:\\ags5520\\";
    String AGS_ROOT_NAME = "ags5520";
    String FILE_EXTENSION_NAME = ".java";
    // Please also chanage the directory in the .bat files!!!
    String COMPILE_COMMAND = "C:\\ags9e\\Compile.bat ";
    String RUN_COMMAND = "C:\\ags9e\\Run.bat ";
    String WINDOWS_FILE_SEPARATOR = "\\";
    String SELFTEST_ROOT = "c:\\selftest\\selftest9e\\";
    String DATABASE_USERNAME = "agsuser";
    String DATABASE_PASSWROD = "12michelle12";
    String connectionString = "jdbc:mysql://localhost/javalivelab9e?autoReconnect=true&zeroDateTimeBehavior=convertToNull";
    String SYSTEM_NAME = "Java";
    String STUDENT_REGISTRATION_TITLE = "<div id=\"booktitle\">Java LiveLab Student Registration</div>";
    String INSTRUCTOR_REGISTRATION_TITLE = "<div id=\"booktitle\">Java LiveLab Instructor Registration</div>";
    String GMAIL_USERNAME = "donotreply.livelab@gmail.com";
    String GMAIL_PASSWORD = "aasu11192011";
    String GRADING_RULES = "Click Compile, Run, and Submit buttons in sequence. After you click the Submit button, your score will be changed. If the program runs correctly, " + "the system automatically assigns the full score allowed for this exercise, else if " +
            "the program does not run, but compiles correctly, the system assigns a score of 1, else " +
            "you will get a score of 0.5 just for submitting the exercise. " +
            "The instructor can enter comments and override the score from the Instructor tab under Examine/Grade Exercise.";
    String HOWTOCOMPILERUNSUBMIT =
            "<p id=\"parastyle\" style=\"margin-left: 1em; margin-right: 1em\">Enter your source code below. Click the <b>Compile</b> button to compile your program. Click the <b>Run</b> button to test your program against the test data in the database. Click the <b>Submit</b> button to submit the program. " +
            "Everytime, you click the <b>Compile</b> button, the program is stored. Everytime, you click the <b>Submit</b> button, a new score is set. " +
            "You should click the <b>Compile</b>, <b>Run</b>, and <b>Submit</b> buttons in this order. " +
            "To know how a program is graded, read item #6 in the <a href=\"http://www.cs.armstrong.edu/liang/LiveLabBriefManualv2.doc\">LiveLab Brief Manual</a>";
}