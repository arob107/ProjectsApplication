package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;

/*
 * This class is a service layer in the overall application. 
 * It essentially acts as a pass-through between the main application 
 * file that runs the menu (ProjectsApp.java) and the DAO file 
 * in the data layer (ProjectDao.java)
 * 
 * The service class in this small application doesn't do very much, but it allows
 * us to properly separate concerns of input/output, business logic, and database 
 * reads and writes. If you always structure your code like this, it will be much
 * easier to understand and make changes if needed
 */
public class ProjectService {

	private ProjectDao projectDao = new ProjectDao();

	// add a new project
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	// return the results of the method call to the DAO class
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	/*
	 * Call the DAO to retrieve a single Project object with all details, including
	 * materials, steps, and categories. This method will throw an exception if the
	 * project with the given ID does not exist.
	 */
	public Project fetchProjectById(Integer projectId) {
		/*
		 * Note that you will temporarily assign the results of a method call to the DAO
		 * to an Optional<Project> object. This will cause Eclipse to create the return
		 * type on the DAO method as Optional<Project>. Once the method has been
		 * created, you can delete the assignment and return the Project, if successful.
		 * If not successful, the method will throw a NoSuchElementException.
		 * 
		 * Temporarily assign a variable of type Optional<Project> to the results of
		 * calling projectDao.fetchProjectById(). Pass the project ID to the method.
		 * 
		 * This temporary assignment will cause Eclipse to create the correct return
		 * value (Optional<Project>) in ProjectService.java.
		 * 
		 * Let Eclipse create the method in ProjectDao for you.
		 * 
		 * Replace the variable and assignment with a return statement.
		 * 
		 * Add a method call to .orElseThrow() just inside the semicolon at the end of
		 * the method call to projectDao.fetchProjectById(). Use a zero-argument Lambda
		 * expression inside the call to .orElseThrow() to create and return a new
		 * NoSuchElementException with the custom message, "Project with project ID=" +
		 * projectId + " does not exist."
		 */
		return projectDao.fetchProjectById(projectId).orElseThrow(
				() -> new NoSuchElementException("Project with project ID=" + projectId + "does not exist."));

	}

}
