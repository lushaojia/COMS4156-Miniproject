package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class contains the unit tests for the Department class.
 */
public class DepartmentUnitTests {
  /**
   * A department instance used for testing.
   */
  private Department department;

  /**
   * Set up a department for testing.
   */
  @BeforeEach
  public void setupForTesting() {
    HashMap<String, Course> courses;
    courses = new HashMap<>();
    Course ieor2500 = new Course(
        "Uday Menon", "627 MUDD",
        "11:45-1:30", 50
    );
    ieor2500.setEnrolledStudentCount(52);
    courses.put("2500", ieor2500);

    Course ieor3404 = new Course(
        "Christopher J Dolan", "303 MUDD",
        "1:20-2:35", 73
    );
    ieor3404.setEnrolledStudentCount(80);
    courses.put("3404", ieor3404);

    department = new Department(
        "IEOR",
        courses,
        "Jay Sethuraman",
        0
    );
  }

  @Test
  public void toStringTest() {
    String expected1 = "IEOR 2500: "
        + "\nInstructor: Uday Menon; Location: 627 MUDD; Time: 11:45-1:30\n"
        + "IEOR 3404: "
        + "\nInstructor: Christopher J Dolan; Location: 303 MUDD; Time: 11:45-1:30\n";
    String expected2 = "IEOR 3404: "
        + "\nInstructor: Christopher J Dolan; Location: 303 MUDD; Time: 11:45-1:30\n"
        + "IEOR 2500: "
        + "\nInstructor: Uday Menon; Location: 627 MUDD; Time: 11:45-1:30\n";
  }

  @Test
  public void getNumberOfMajorsTest() {
    assertEquals(0, department.getNumberOfMajors());
  }

  @Test
  public void getDepartmentChairTest() {
    assertEquals("Jay Sethuraman", department.getDepartmentChair());
  }

  @Test
  public void addPersonToMajorTest() {
    assertEquals(0, department.getNumberOfMajors());
    department.addPersonToMajor();
    assertEquals(1, department.getNumberOfMajors());
  }

  @Test
  public void dropPersonFromMajorTest() {
    assertEquals(0, department.getNumberOfMajors());
    department.dropPersonFromMajor();
    assertEquals(0, department.getNumberOfMajors());

    department.addPersonToMajor();
    assertEquals(1, department.getNumberOfMajors());
    department.dropPersonFromMajor();
    assertEquals(0, department.getNumberOfMajors());
  }
}
