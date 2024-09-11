package dev.coms4156.project.individualproject;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains the unit tests for the RouteController class.
 */
@SpringBootTest
@ContextConfiguration
public class RouteControllerUnitTests {
  /**
   * Route controller and file database instances used for testing.
   */
  private RouteController routeController;

  @BeforeEach
  public void setupForTesting() {
    routeController = new RouteController();

    // Set up a mock database with departmentsMap and course offerings for testing
    HashMap<String, Department> departmentsMap = new HashMap<>();

    HashMap<String, Course> courses = new HashMap<>();
    Course coms1004 = new Course(
        "Adam Cannon",
        "417 IAB",
        "11:40-12:55",
        400);
    coms1004.setEnrolledStudentCount(0);
    Course coms3134 = new Course(
        "Brian Borowski",
        "301 URIS",
        "4:10-5:25",
        250);
    coms3134.setEnrolledStudentCount(1);
    courses.put("1004", coms1004);
    courses.put("3134", coms3134);
    Department comsDepartment = new Department(
        "COMS",
        courses,
        "Luca Carloni",
        2700);
    departmentsMap.put("COMS", comsDepartment);

    courses = new HashMap<String, Course>();
    Course econ3211 = new Course(
        "Murat Yilmaz",
        "310 FAY",
        "4:10-5:25",
        96);
    econ3211.setEnrolledStudentCount(11);

    Course econ3213 = new Course(
        "Miles Leahey",
        "702 HAM",
        "4:10-5:25",
        86);
    econ3213.setEnrolledStudentCount(86);
    courses.put("3211", econ3211);
    courses.put("3213", econ3213);
    Department econDepartment = new Department(
        "ECON",
        courses,
        "Michael Woodford",
        0
    );
    departmentsMap.put("ECON", econDepartment);

    IndividualProjectApplication.myFileDatabase = new MyFileDatabase(1, "");
    IndividualProjectApplication.myFileDatabase.setMapping(departmentsMap);
  }

  /*
   * Testing strategy
   * All methods:
   *  - partition on status of ResponseEntity: internal server error, not
   *
   * retrieveDept, retrieveCourse, isCourseFull, getMajorCountFromDept, getMajorCountFromDept,
   * idDeptChair, findCourseLocation, findCourseInstructor, findCourseTime, addMajorToDept,
   * removeMajorFromDept, dropStudent, setEnrollmentCount, changeCourseTime, changeCourseTeacher,
   * changeCourseLocation:
   *  - partition on database and department code: department exists in database, not
   *
   * retrieveCourse, isCourseFull, findCourseLocation, findCourseInstructor, findCourseTime,
   * dropStudent, setEnrollmentCount, changeCourseTime, changeCourseTeacher, changeCourseLocation:
   *  - partition on database, department code and course code:
   *      course is offered by department,
   *      not
   *
   * isCourseFull
   *  - partition on course: course is full, not
   *
   * dropStudent
   *  - partition on number of students in course: =0, (0, capacity), capacity
   */
  @Test
  public void indexTest() {
    assertEquals(
        "Welcome, " +
            "in order to make an API call direct your browser or Postman to an endpoint "
            + "\n\n This can be done using the following format: \n\n http:127.0.0"
            + ".1:8080/endpoint?arg=value",
        routeController.index());
  }

  @Test
  public void retrieveDepartmentOkTest() {
    String responseBody =
        "COMS 1004: "
            + "\nInstructor: Adam Cannon; Location: 417 IAB; Time: 11:40-12:55\n"
            + "COMS 3134: "
            + "\nInstructor: Brian Borowski; Location: 301 URIS; Time: 4:10-5:25\n";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.retrieveDepartment("COMS");
    System.out.println(expectedResponse.getBody());
    System.out.println(actualResponse.getBody());
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveDepartmentNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found", HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.retrieveDepartment("PSYC");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveDepartmentErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.retrieveDepartment("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveCourseOkTest() {
    String responseBody = "\nInstructor: Murat Yilmax; Location: 702 HAM; Time: 4:10-5:25";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.retrieveCourse("ECON", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveCourseNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Course Not Found", HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.retrieveCourse("ECON", 3212);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveCourseDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.retrieveCourse("PSYC", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveCourseErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.retrieveCourse("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void courseIsFullOkTest() {
    ResponseEntity<Boolean> expectedResponse = new ResponseEntity<>(true, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.isCourseFull("ECON", 3213);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void courseIsNotFullOkTest() {
    ResponseEntity<Boolean> expectedResponse = new ResponseEntity<>(false, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.isCourseFull("ECON", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void isCourseFullNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Course Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.isCourseFull("ECON", 3212);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void isCourseFullDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.isCourseFull("PSYC", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void isCourseFullErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.isCourseFull("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void getMajorCountFromDeptOkTest() {
    ResponseEntity<?> expectedResponse = new ResponseEntity<>(2700, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.getMajorCtFromDept("ECON");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void getMajorCountFromDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.getMajorCtFromDept("PSYC");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void getMajorCountFromDeptErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.getMajorCtFromDept("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void identifyDeptChairOkTest() {
    ResponseEntity<?> expectedResponse = new ResponseEntity<>(
        "Luca Carloni is the department chair.", HttpStatus.OK
    );
    ResponseEntity<?> actualResponse = routeController.identifyDeptChair("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void identifyDeptChairNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.identifyDeptChair("PSYC");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void identifyDeptChairErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.identifyDeptChair("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseLocationOkTest() {
    String responseBody = "310 FAY is where the course is located.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.findCourseLocation("ECON", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseLocationNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Course Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.findCourseLocation("ECON", 3212);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseLocationDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.findCourseLocation("PSYC", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseLocationErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.findCourseLocation("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseInstructorOkTest() {
    String responseBody = "Murat Yilmaz is the instructor for the course.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.findCourseInstructor("ECON", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseInstructorNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Course Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.findCourseInstructor("ECON", 3212);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseInstructorDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.findCourseInstructor("PSYC", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseInstructorErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.findCourseInstructor("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseTimeOktTest() {
    String responseBody = "The course meets at: 11:40-12:55.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.findCourseTime("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseTimeNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Course Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.findCourseTime("ECON", 3212);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseTimeDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.findCourseTime("PSYC", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseTimeErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.findCourseTime("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void addMajorToDeptOkTest() {
    ResponseEntity<?> expectedResponse = new ResponseEntity<>(
        "Attribute was updated successfully", HttpStatus.OK
    );
    ResponseEntity<?> actualResponse = routeController.addMajorToDept("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals(2701, routeController.getMajorCtFromDept("COMS").getBody());
  }

  @Test
  public void addMajorToDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.identifyDeptChair("PSYC");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void addMajorToDeptErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.addMajorToDept("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void removeMajorFromDeptOkTest() {
    ResponseEntity<?> expectedResponse = new ResponseEntity<>(
        "Attribute was or is at minimum", HttpStatus.OK
    );
    assertEquals(2701, routeController.getMajorCtFromDept("COMS").getBody());
    ResponseEntity<?> actualResponse = routeController.removeMajorFromDept("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals(2700, routeController.getMajorCtFromDept("COMS").getBody());
  }

  @Test
  public void removeMajorFromEmptyDeptOkTest() {
    ResponseEntity<?> expectedResponse = new ResponseEntity<>(
        "Attribute was or is at minimum", HttpStatus.OK
    );
    assertEquals(0, routeController.getMajorCtFromDept("ECON").getBody());
    ResponseEntity<?> actualResponse = routeController.removeMajorFromDept("ECON");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals(0, routeController.getMajorCtFromDept("ECON").getBody());
  }

  @Test
  public void removeMajorToDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>("Department Not Found",
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.removeMajorFromDept("PSYC");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void removeMajorToDeptErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.removeMajorFromDept("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void dropStudentFromCourseOkTest() {
    String responseBody = "Student has been dropped.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.dropStudent("COMS", 3134);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void dropStudentFromCourseBadRequestTest() {
    String responseBody = "Student has not been dropped.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.BAD_REQUEST);
    ResponseEntity<?> actualResponse = routeController.dropStudent("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void dropStudentFromCourseNotFoundTest() {
    String responseBody = "Course Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.dropStudent("COMS", 1001);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void dropStudentFromCourseDeptNotFoundTest() {
    String responseBody = "Department Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.dropStudent("PSYC", 1001);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void dropStudentFromCourseErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.dropStudent("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void setEnrollmentOkTest() {
    String responseBody = "Attributed was updated successfully.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.setEnrollmentCount(
        "COMS", 3134, 1);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void setEnrollmentNotFoundTest() {
    String responseBody = "Course Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.setEnrollmentCount(
        "COMS", 1001, 19);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }
  @Test
  public void setEnrollmentDeptNotFoundTest() {
    String responseBody = "Department Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.setEnrollmentCount(
        "PSYC", 1001, 102);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void setEnrollmentErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.setEnrollmentCount(
        "COMS", 1004, 3);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseTimeOkTest() {
    String responseBody = "Attributed was updated successfully.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    assertEquals("The course meets at: 4:10-5:25",
        routeController.findCourseLocation("COMS", 3134).getBody());
    ResponseEntity<?> actualResponse = routeController.changeCourseTime(
        "COMS", 3134, "8:00-14:00");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals("The course meets at: 8:00-14:00",
        routeController.findCourseLocation("COMS", 3134).getBody());
  }

  @Test
  public void changeCourseTimeNotFoundTest() {
    String responseBody = "Course Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.changeCourseTime(
        "COMS", 1001, "11:00-12:00");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseTimeDeptNotFoundTest() {
    String responseBody = "Department Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.changeCourseTime(
        "PSYC", 1001, "10:00-12:00");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseTimeErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.changeCourseTime(
        "COMS", 1004, "13:00-15:00");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseTeacherOkTest() {
    String responseBody = "Attributed was updated successfully.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    assertEquals("Brian Borowski is the instructor for the course.",
        routeController.findCourseInstructor("COMS", 3134).getBody());
    ResponseEntity<?> actualResponse = routeController.changeCourseTeacher(
        "COMS", 3134, "Junfeng Yang");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals("Junfeng Yang is the instructor for the course.",
        routeController.findCourseInstructor("COMS", 3134).getBody());
  }

  @Test
  public void changeCourseTeacherNotFoundTest() {
    String responseBody = "Course Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.changeCourseTeacher(
        "COMS", 1001, "Carl Vondrick");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseTeacherDeptNotFoundTest() {
    String responseBody = "Department Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.changeCourseTeacher(
        "PSYC", 1001, "Shuran Song");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseTeacherErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.changeCourseTeacher(
        "COMS", 1004, "Gail Kaiser");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseLocationOkTest() {
    String responseBody = "Attributed was updated successfully.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    assertEquals("301 URIS is where the course is located.",
        routeController.findCourseLocation("COMS", 3134).getBody());
    ResponseEntity<?> actualResponse = routeController.changeCourseLocation(
        "COMS", 3134, "12-100");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals("12-100 is where the course is located.",
        routeController.findCourseLocation("COMS", 3134).getBody());
  }

  @Test
  public void changeCourseLocationNotFoundTest() {
    String responseBody = "Course Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.changeCourseLocation(
        "COMS", 1001, "201 DAV");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseLocationDeptNotFoundTest() {
    String responseBody = "Department Not Found";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody,
        HttpStatus.NOT_FOUND);
    ResponseEntity<?> actualResponse = routeController.changeCourseLocation(
        "PSYC", 1001, "L201 SCI");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseLocationErrorTest() {
    IndividualProjectApplication.myFileDatabase = null;
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = routeController.changeCourseLocation(
        "COMS", 1004, "Zoom");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }
}
