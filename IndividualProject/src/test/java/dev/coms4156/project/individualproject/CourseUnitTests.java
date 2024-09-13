package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * This class contains the unit tests for the Course class.
 */
@SpringBootTest
@ContextConfiguration
public class CourseUnitTests {
  /**
   * A course instance used for testing.
   */
  private Course testCourse;

  /**
   * Create a Course instance for testing.
   */
  @BeforeEach
  public void setupCourseForTesting() {
    testCourse = new Course(
        "Griffin Newbold",
        "417 IAB",
        "11:40-12:55",
        250);
  }

  /*
   * Testing strategy
   *
   * enrollStudent, dropStudent:
   *  - partition on whether the student can be successfully enrolled/dropped: yes, no
   *  - partition on number of enrolled students: =0, (0, capacity), =capacity
   *
   * reassignInstructor, reassignLocation, reassignTime:
   *  - partition on new instructor/location/time: same as before, not
   *
   * isCourseFull:
   *  - partition on course: is full, not
   *  - - partition on number of enrolled students: =0, (0, capacity), =capacity
   */
  @Test
  public void getCourseLocationTest() {
    String expectedLocation = "417 IAB";
    assertEquals(expectedLocation, testCourse.getCourseLocation());
  }

  @Test
  public void getInstructorNameTest() {
    String expectedInstructor = "Griffin Newbold";
    assertEquals(expectedInstructor, testCourse.getInstructorName());
  }

  @Test
  public void getCourseTimeSlotTest() {
    String expectedTimeSlot = "11:40-12:55";
    assertEquals(expectedTimeSlot, testCourse.getCourseTimeSlot());
  }

  @Test
  public void toStringTest() {
    String expectedResult = "\nInstructor: Griffin Newbold; Location: 417 IAB; Time: 11:40-12:55";
    assertEquals(expectedResult, testCourse.toString());
  }

  @Test
  public void enrollStudentTest() {
    assertTrue(testCourse.enrollStudent(),
        "Expected student to be successfully enrolled in course.");

    testCourse.setEnrolledStudentCount(1);
    assertTrue(testCourse.enrollStudent(),
        "Expected student to be successfully enrolled in course.");

    testCourse.setEnrolledStudentCount(250);
    assertFalse(testCourse.enrollStudent(),
        "Should not be able to enroll student in course at full capacity.");
  }

  @Test
  public void dropStudentTest() {
    assertFalse(testCourse.dropStudent(),
        "Should not be able to drop student from empty course.");

    testCourse.setEnrolledStudentCount(1);
    assertTrue(testCourse.dropStudent(),
        "Should be able to drop a student from the course.");

    testCourse.setEnrolledStudentCount(250);
    assertTrue(testCourse.dropStudent(),
        "Should be able to drop a student from the course.");
  }

  @Test
  public void reassignInstructorTest() {
    testCourse.reassignInstructor("Griffin Newbold");
    String expectedResult = "\nInstructor: Griffin Newbold; Location: 417 IAB; Time: 11:40-12:55";
    assertEquals(expectedResult, testCourse.toString());

    testCourse.reassignInstructor("Max Goldman");
    String expectedResult2 = "\nInstructor: Max Goldman; Location: 417 IAB; Time: 11:40-12:55";
    assertEquals(expectedResult2, testCourse.toString());
  }

  @Test
  public void reassignLocation() {
    testCourse.reassignLocation("417 IAB");
    String expectedResult = "\nInstructor: Griffin Newbold; Location: 417 IAB; Time: 11:40-12:55";
    assertEquals(expectedResult, testCourse.toString());

    testCourse.reassignLocation("12-100");
    String expectedResult2 = "\nInstructor: Griffin Newbold; Location: 12-100; Time: 11:40-12:55";
    assertEquals(expectedResult2, testCourse.toString());
  }

  @Test
  public void reassignTime() {
    testCourse.reassignTime("11:40-12:55");
    String expectedResult = "\nInstructor: Griffin Newbold; Location: 417 IAB; Time: 11:40-12:55";
    assertEquals(expectedResult, testCourse.toString());

    testCourse.reassignTime("9:30-11");
    String expectedResult2 = "\nInstructor: Griffin Newbold; Location: 417 IAB; Time: 9:30-11";
    assertEquals(expectedResult2, testCourse.toString());
  }

  @Test
  public void isCourseFullTest() {
    assertFalse(testCourse.isCourseFull(),
        "Expected new course to have no enrolled students.");

    testCourse.setEnrolledStudentCount(249);
    assertFalse(testCourse.isCourseFull(),
        "Expected course to not be full.");

    testCourse.setEnrolledStudentCount(250);
    assertTrue(testCourse.isCourseFull(),
        "Expected course to be full.");

    testCourse.setEnrolledStudentCount(300);
    assertTrue(testCourse.isCourseFull(),
        "Expected course to be full.");
  }
}

