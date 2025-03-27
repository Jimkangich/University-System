import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Person class (Super class)
class Person {
    public String name;
    public int age;
    public String gender;
    private String email;
    private String phone;

    public Person(String name, int age, String gender, String email, String phone) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
    }

    public void updateContactInfo(String newEmail, String newPhone) {
        this.email = newEmail;
        this.phone = newPhone;
    }

    public String getDetails() {
        return "Name: " + name + ", Age: " + age + ", Gender: " + gender +
                ", Email: " + email + ", Phone: " + phone;
    }
}

// Student class (Subclass of Person)
class Student extends Person {
    public String studentID;

    public Student(String name, int age, String gender, String email, String phone, String studentID) {
        super(name, age, gender, email, phone);
        this.studentID = studentID;
    }


    @Override
    public String getDetails() {
        return super.getDetails() + ", Student ID: " + studentID;
    }
}

// Lecturer class
class Lecturer {
    public String employeeID;
    public String department;
    public List<Course> coursesTaught;
    public String name;

    public Lecturer(String name, String employeeID, String department) {
        this.name = name;
        this.employeeID = employeeID;
        this.department = department;
        this.coursesTaught = new ArrayList<>();
    }

    public void assignCourse(Course course) {
        if (!coursesTaught.contains(course)) {
            coursesTaught.add(course);
        }
    }

    public void removeCourse(Course course) {
        coursesTaught.remove(course);
    }

    public String getName(){
        return name;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getDepartment() {
        return department;
    }

    public List<Course> getCoursesTaught() {
        return new ArrayList<>(coursesTaught);
    }
}

// Course class
class Course {
    private final String courseCode;
    public String description;
    private Lecturer lecturer;
    private final List<Student> enrolledStudents;

    public Course(String courseCode, String title, String description, Program program) {
        this.courseCode = courseCode;
        this.description = description;
        this.enrolledStudents = new ArrayList<>();
        program.addRequiredCourse(this);
    }

    public void addStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
        }
    }

    public void removeStudent(Student student) {
        enrolledStudents.remove(student);
    }

    public void assignLecturer(Lecturer lecturer) {
        if (this.lecturer != null) {
            this.lecturer.removeCourse(this);
        }
        this.lecturer = lecturer;
        lecturer.assignCourse(this);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }
}

// Program class
class Program {
    public String programCode;
    public String name;
    public int duration;
    public List<Course> requiredCourses;
    public List<Student> enrolledStudents;

    public Program(String programCode, String name, int duration) {
        this.programCode = programCode;
        this.name = name;
        this.duration = duration;
        this.requiredCourses = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
    }

    public void addStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
        }
    }

    public void removeStudent(Student student) {
        enrolledStudents.remove(student);
    }

    public void assignLecturer(Lecturer lecturer, Course course) {
        if (requiredCourses.contains(course)) {
            course.assignLecturer(lecturer);
        }
    }

    public void addRequiredCourse(Course course) {
        if (!requiredCourses.contains(course)) {
            requiredCourses.add(course);
        }
    }

    public String getProgramCode() {
        return programCode;
    }

    public List<Course> getRequiredCourses() {
        return new ArrayList<>(requiredCourses);
    }
}

// Database Connection:


// Example usage
public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/universitySystem";
    private static final String USER = "universityUser"; // Use the user created in MySQL
    private static final String PASSWORD = "your_password"; // Use the password set in MySQL

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // For MySQL Connector/J 8.0+

            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found.");
            throw new SQLException("MySQL JDBC driver not found.", e);
        }
    }

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = getConnection();
            System.out.println("Database connection successful!");

            // Example database operation (e.g., a simple query)
            // ... (your SQL queries here) ...

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Database connection closed.\n");
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }

        // Create a program
        Program computerScience = new Program("CS", "Computer Science", 4);

        // Create courses
        Course cs101 = new Course("CS101", "Introduction to Programming",
                "Basic programming concepts", computerScience);
        Course cs201 = new Course("CS201", "Data Structures",
                "Fundamental data structures", computerScience);

        // Create lecturer
        Lecturer drSmith = new Lecturer( "Fredrick", "E123", "Computer Science");

        // Assign lecturer to courses
        computerScience.assignLecturer(drSmith, cs101);
        computerScience.assignLecturer(drSmith, cs201);

        // Create students
        Student alice = new Student("Alice Johnson", 20, "Female",
                "alice@uni.com", "555-0101", "S10001");
        Student bob = new Student("Bob Wilson", 21, "Male",
                "bob@uni.com", "555-0102", "S10002");

        // Enroll students in program
        computerScience.addStudent(alice);
        computerScience.addStudent(bob);

        // Enroll students in courses
        cs101.addStudent(alice);
        cs101.addStudent(bob);
        cs201.addStudent(alice);

        // Display student details
        System.out.println("Student Details:");
        System.out.println(alice.getDetails());
        System.out.println(bob.getDetails());

        // Update contact info
        alice.updateContactInfo("alice.new@uni.com", "555-0123");
        System.out.println("\nAfter contact info update:");
        System.out.println(alice.getDetails());

        // Display Lecturer Details
        System.out.println("\nLecturer Info: ");
        System.out.println("Name: " + drSmith.getName());
        System.out.println( "Employee ID: " + drSmith.getEmployeeID());
        System.out.println( "Department: " + drSmith.getDepartment());

    }
}