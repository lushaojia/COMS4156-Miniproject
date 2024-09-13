package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

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

  /**
   * Create a file database and a route controller for testing.
   */
  @BeforeEach
  public void setupForTesting() {
    routeController = new RouteController();

    // Set up a database for testing
    HashMap<String, Department> departmentMap;
    departmentMap = new HashMap<>();
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
    departmentMap.put("COMS", comsDepartment);

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
    departmentMap.put("ECON", econDepartment);

    IndividualProjectApplication.myFileDatabase = new MyFileDatabase(1, "");
    IndividualProjectApplication.myFileDatabase.setMapping(departmentMap);
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
        "Welcome, "
            + "in order to make an API call direct your browser or Postman to an endpoint "
            + "\n\n This can be done using the following format: \n\n http:127.0.0"
            + ".1:8080/endpoint?arg=value",
        routeController.index());
  }

  @Test
  public void retrieveDepartmentOkTest() {
    String expected1 = """
            COMS 1004: \

            Instructor: Adam Cannon; Location: 417 IAB; Time: 11:40-12:55
            COMS 3134: \

            Instructor: Brian Borowski; Location: 301 URIS; Time: 4:10-5:25
            """;
    String expected2 = """
        COMS 3134: \

        Instructor: Brian Borowski; Location: 301 URIS; Time: 4:10-5:25
        COMS 1004: \

        Instructor: Adam Cannon; Location: 417 IAB; Time: 11:40-12:55
        """;
    ResponseEntity<?> actualResponse = routeController.retrieveDepartment("COMS");
    String actualResponseBody = (String) actualResponse.getBody();
    assertTrue(expected1.equals(actualResponseBody) || expected2.equals(actualResponseBody));
    assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
  }

  @Test
  public void retrieveDepartmentNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "Department Not Found",
        HttpStatus.NOT_FOUND
    );
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
    String responseBody = "\nInstructor: Murat Yilmaz; Location: 310 FAY; Time: 4:10-5:25";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    ResponseEntity<?> actualResponse = routeController.retrieveCourse("ECON", 3211);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveCourseNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "Course Not Found",
        HttpStatus.NOT_FOUND
    );
    ResponseEntity<?> actualResponse = routeController.retrieveCourse("ECON", 3212);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void retrieveCourseDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "Department Not Found",
        HttpStatus.NOT_FOUND
    );
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );

    ResponseEntity<?> actualResponse = controller.isCourseFull("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void getMajorCountFromDeptOkTest() {
    ResponseEntity<?> expectedResponse = new ResponseEntity<>(
        "There are: 2700 majors in the department",
        HttpStatus.OK
    );
    ResponseEntity<?> actualResponse = routeController.getMajorCtFromDept("COMS");
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveDepartment("COMS");

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.getMajorCtFromDept("COMS");
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveDepartment("COMS");

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.identifyDeptChair("COMS");
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.findCourseLocation("COMS", 1004);
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.findCourseInstructor("COMS", 1004);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void findCourseTimeOkTest() {
    String responseBody = "The course meets at: 11:40-12:55";
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.findCourseTime("COMS", 1004);
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
    assertEquals("There are: 2701 majors in the department",
        routeController.getMajorCtFromDept("COMS").getBody()
    );
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveDepartment("COMS");

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.addMajorToDept("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void removeMajorFromDeptOkTest() {
    assertEquals(
        "There are: 2700 majors in the department",
        routeController.getMajorCtFromDept("COMS").getBody()
    );

    ResponseEntity<?> expectedResponse = new ResponseEntity<>(
        "Attribute was updated or is at minimum",
        HttpStatus.OK
    );
    ResponseEntity<?> actualResponse = routeController.removeMajorFromDept("COMS");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals(
        "There are: 2699 majors in the department",
        routeController.getMajorCtFromDept("COMS").getBody()
    );
  }

  @Test
  public void removeMajorFromEmptyDeptOkTest() {
    ResponseEntity<?> expectedResponse = new ResponseEntity<>(
        "Attribute was updated or is at minimum", HttpStatus.OK
    );
    assertEquals(
        "There are: 0 majors in the department",
        routeController.getMajorCtFromDept("ECON").getBody()
    );
    ResponseEntity<?> actualResponse = routeController.removeMajorFromDept("ECON");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals(
        "There are: 0 majors in the department",
        routeController.getMajorCtFromDept("ECON").getBody()
    );
  }

  @Test
  public void removeMajorFromDeptNotFoundTest() {
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "Department Not Found",
        HttpStatus.NOT_FOUND
    );
    ResponseEntity<?> actualResponse = routeController.removeMajorFromDept("PSYC");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void removeMajorFromDeptErrorTest() {

    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveDepartment("COMS");

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.removeMajorFromDept("COMS");
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.dropStudent("COMS", 1004);
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);
    
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.setEnrollmentCount(
        "COMS", 1004, 3);
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }

  @Test
  public void changeCourseTimeOkTest() {
    String responseBody = "Attributed was updated successfully.";
    ResponseEntity<String> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
    assertEquals("The course meets at: 4:10-5:25",
        routeController.findCourseTime("COMS", 3134).getBody());
    ResponseEntity<?> actualResponse = routeController.changeCourseTime(
        "COMS", 3134, "8:00-14:00");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    assertEquals("The course meets at: 8:00-14:00",
        routeController.findCourseTime("COMS", 3134).getBody());
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.changeCourseTime(
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.changeCourseTeacher(
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
    RouteController controller = Mockito.spy(RouteController.class);
    doThrow(new RuntimeException("Test Exception")).when(controller).retrieveCourse("COMS", 1004);

    ResponseEntity<String> expectedResponse = new ResponseEntity<>(
        "An Error has occurred",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    ResponseEntity<?> actualResponse = controller.changeCourseLocation(
        "COMS", 1004, "Zoom");
    assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
  }
}
