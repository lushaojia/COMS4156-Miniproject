package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This class contains the unit tests for the IndividualProjectApplication class.
 */
public class IndividualProjectApplicationUnitTests {
  /**
   * IndividiaulProjectApplication instance used for testing.
   */
  private static IndividualProjectApplication app;

  /**
   * MyFileDatabase instance used for validation.
   */
  public static MyFileDatabase database;

  /**
   * Create a MyFileDatabase with the given data and
   * an IndividualProjectApplication instance used for testing.
   */
  @BeforeAll
  public static void setupForTesting() {
    app = new IndividualProjectApplication();
    database = new MyFileDatabase(0, "./data.txt");
  }

  @Test
  public void runStartUpTest() {
    String[] args = new String[0];
    app.run(args);
    assertEquals(database.toString(),
        IndividualProjectApplication.myFileDatabase.toString());
  }

  @Test
  public void runSetupTest() {
    String[] args = new String[1];
    args[0] = "setup";
    app.run(args);
    assertEquals(database.toString(),
        IndividualProjectApplication.myFileDatabase.toString());
  }

  @Test
  public void overrideDatabaseTest() {
    HashMap<String, Department> departmentMap = new HashMap<>();
    HashMap<String, Course> courses = new HashMap<>();
    Course cs111 = new Course(
        "Sohie Lee",
        "SCI 120",
        "2:20-3:35",
        30
    );
    courses.put("111", cs111);
    Course cs240 = new Course(
        "Alexa VanHattum",
        "SCI W309",
        "4:45-5",
        25
    );
    courses.put("240", cs240);
    Department csDepartment = new Department(
        "CS",
        courses,
        "Orit Shaer",
        46
    );
    departmentMap.put("CS", csDepartment);
    MyFileDatabase newDatabase = new MyFileDatabase(1, "");
    newDatabase.setMapping(departmentMap);

    String[] args = new String[0];
    app.run(args);
    assertEquals(
        database.toString(),
        IndividualProjectApplication.myFileDatabase.toString()
    );
    IndividualProjectApplication.overrideDatabase(newDatabase);
    assertEquals(
        newDatabase.toString(),
        IndividualProjectApplication.myFileDatabase.toString()
    );
  }
}
