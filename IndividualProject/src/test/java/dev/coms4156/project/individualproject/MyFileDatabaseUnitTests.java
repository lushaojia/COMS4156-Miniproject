package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This class contains the unit tests for the MyFileDatabase class.
 */
public class MyFileDatabaseUnitTests {
  /**
   * MyFileDatabase instances used for testing.
   */
  public static MyFileDatabase fileDatabase;
  public static MyFileDatabase invalidObjectFileDatabase;
  public static MyFileDatabase emptyFileDatabase;

  /**
   * Create the following kinds of MyFileDatabase instances for testing:
   * a regular one, one with a file containing an invalid object (i.e. not a hash map),
   * and one with an empty file path.
   */
  @BeforeAll
  public static void setUpForTesting() {
    // Create a file database with empty file path for testing
    emptyFileDatabase = new MyFileDatabase(1, "");

    // Create a file that stores an invalid (not-hashmap) object type
    String invalidObjectFilePath = "./invalidObject.txt";
    List<String> invalidObject = new ArrayList<>();
    invalidObject.add("gudetama");
    invalidObject.add("kuromi");
    invalidObject.add("keroppi");
    try {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(invalidObjectFilePath));
      out.writeObject(invalidObject);
      System.out.println("Invalid object for testing has been serialized to file.");
    } catch (IOException e) {
      e.printStackTrace();
    }
    invalidObjectFileDatabase = new MyFileDatabase(1, invalidObjectFilePath);

    // Set up
    fileDatabase = new MyFileDatabase(1, "./testMyFileDatabase.txt");
    HashMap<String, Department> departmentMap = new HashMap<>();
    HashMap<String, Course> courses = new HashMap<String, Course>();
    Course econ3211 = new Course(
        "Murat Yilmaz",
        "310 FAY",
        "4:10-5:25",
        96);
    econ3211.setEnrolledStudentCount(11);

    courses.put("3211", econ3211);
    Department econDepartment = new Department(
        "ECON",
        courses,
        "Michael Woodford",
        0
    );
    departmentMap.put("ECON", econDepartment);
    fileDatabase.setMapping(departmentMap);
  }

  @Test
  public void setMappingTest() {
    HashMap<String, Department> departmentMap = new HashMap<>();
    HashMap<String, Course> courses = new HashMap<>();
    Course econ3211 = new Course(
        "Murat Yilmaz",
        "310 FAY",
        "4:10-5:25",
        96);
    econ3211.setEnrolledStudentCount(11);
    courses.put("3211", econ3211);
    Department econDepartment = new Department(
        "ECON",
        courses,
        "Michael Woodford",
        0
    );
    departmentMap.put("ECON", econDepartment);
    MyFileDatabase database = new MyFileDatabase(1, "");
    database.setMapping(departmentMap);
    assertEquals("For the ECON department: \n"
            + "ECON 3211: \n"
            + "Instructor: Murat Yilmaz; Location: 310 FAY; Time: 4:10-5:25\n",
        database.toString());
  }

  @Test
  public void getDepartmentMappingTest() {
    HashMap<String, Department> expectedDepartmentMap = new HashMap<>();
    HashMap<String, Course> courses = new HashMap<>();
    Course econ3211 = new Course(
        "Murat Yilmaz",
        "310 FAY",
        "4:10-5:25",
        96);
    econ3211.setEnrolledStudentCount(11);

    courses.put("3211", econ3211);
    Department econDepartment = new Department(
        "ECON",
        courses,
        "Michael Woodford",
        0
    );
    expectedDepartmentMap.put("ECON", econDepartment);

    assertEquals(expectedDepartmentMap.get("ECON").toString(),
        fileDatabase.getDepartmentMapping().get("ECON").toString());
  }

  @Test
  public void toStringTest() {
    assertEquals("For the ECON department: \n"
            + "ECON 3211: \n"
            + "Instructor: Murat Yilmaz; Location: 310 FAY; Time: 4:10-5:25\n",
        fileDatabase.toString());
  }

  @Test
  public void serializeAndDeSerializeObjectTest() {
    HashMap<String, Department> expectedDepartmentMap = new HashMap<>();
    HashMap<String, Course> courses = new HashMap<>();
    Course econ3211 = new Course(
        "Murat Yilmaz",
        "310 FAY",
        "4:10-5:25",
        96);
    econ3211.setEnrolledStudentCount(11);

    courses.put("3211", econ3211);
    Department econDepartment = new Department(
        "ECON",
        courses,
        "Michael Woodford",
        0
    );
    expectedDepartmentMap.put("ECON", econDepartment);

    fileDatabase.saveContentsToFile();
    assertEquals(expectedDepartmentMap.get("ECON").toString(),
        fileDatabase.deSerializeObjectFromFile().get("ECON").toString());
  }

  @Test
  public void deSerializeObjectFromFileInvalidObjectTypeTest() {
    try {
      HashMap<String, Department> data = invalidObjectFileDatabase.deSerializeObjectFromFile();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
      return;
    }
    fail();
  }

  @Test
  public void deSerializeObjectFromFileThrowsExceptionTest() {
    assertNull(emptyFileDatabase.deSerializeObjectFromFile());
  }

}
