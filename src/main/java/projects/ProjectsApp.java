package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

//import projects.dao.DbConnection;

public class ProjectsApp {

	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
	// Display a list of options.
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project"
			);
	// @formatter:on

	// Use Scanner to get user input
	private Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		// DbConnection.getConnection();

		// create a method that processes the menu
		new ProjectsApp().processUserSelections();
	}

	// process the menu method
	private void processUserSelections() {
		boolean done = false;

		// loop will terminate when done == true
		while (!done) {
			try {
				int selection = getUserSelection();

				switch (selection) {
				case (-1):
					done = exitMenu();
					break;
				case (1):
					createProject();
					break;
				case (2):
					listProjects();
					break;
				case (3):
					selectProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + "Try again.");
			}

		}
	}

	private void selectProject() {
		// list the available projects
		listProjects();

		// collect a projectId from the user
		Integer projectId = getIntInput("Enter a project ID to select a project");

		// un-select any currently selected project
		curProject = null;

		/*
		 * Call a new method, fetchProjectById() on the projectService object. The
		 * method should take a single parameter, the project ID input by the user. It
		 * should return a Project object. Assign the returned Project object to the
		 * instance variable curProject. Note that if an invalid project ID is entered,
		 * projectService.fetchProjectById() will throw a NoSuchElementException, which
		 * is handled by the catch block in processUserSelections().
		 */
		curProject = projectService.fetchProjectById(projectId);
		
		if (Objects.isNull(curProject)) {
			System.out.println("\nInvalid project ID selected.");
		}
		
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");

		// for each project, print the ID and name of each project in projects
		projects.forEach(
				project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");

		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");

		// error handling if the difficulty is not 1-5
		boolean difficultyCorrect = false;

		while (!difficultyCorrect) {
			if (difficulty < 1 || difficulty > 5) {
				System.out.println("The difficulty level needs to be between 1 and 5. Please try again.");
				difficulty = getIntInput("Enter the project difficulty (1-5)");
			} else {
				difficultyCorrect = true;
			}

		}

		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created a project: " + dbProject);
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number. Try again.");
		}
	}

	private boolean exitMenu() {
		System.out.println("Exiting the menu. Goodbye.");
		return true;
	}

	private int getUserSelection() {
		// method that prints operation options to the screen for the user to choose
		// from
		printOperations();

		// call the getIntInput method
		Integer input = getIntInput("Enter a menu selection");

		// check to see if the value in input is null. If so, return -1 and input
		// otherwise
		return Objects.isNull(input) ? -1 : input;
	}

	// get integer input by the user method call. If the getStringInput method
	// returns null,
	// this method returns null. If not null, the String input is converted to
	// Integer, if possible,
	// or an error is thrown (ex. "abc" is entered into input
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.valueOf(input); // integer.valueOf(input) converts the String input to an Integer value
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number. Try again.");
		}
	}

	// get string input method call that takes the user's input, returns null if
	// blank, and returns the trimmed (no spaces) input value
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	}

	// print options method call that displays all available user options, line by
	// line
	private void printOperations() {
		System.out.println("\nBelow are the available options. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println("   " + line));
		
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
