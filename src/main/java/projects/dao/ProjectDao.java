package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

/*
 * This class will read and write to the MySQL database. This class will
 * write the values that are collected from the user in ProjectsApp and are 
 * contained in a Project object to the project table using JDBC method calls
*/

public class ProjectDao extends DaoBase {

	// create constants for the table names by using static final

	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	/*
	 * To save the project details, first create the SQL statement. Then obtain a
	 * Connection and start a transaction. Next, obtain a PreparedStatement
	 * (prevents SQL injection) and set the parameter values from the Project
	 * object. Finally, save the data and commit the transaction.
	 */

	public Project insertProject(Project project) {

		/*
		 * write the SQL statement that will insert the values from the Project object
		 * to the insertProject() method. Use question marks as placeholder values for
		 * the parameters passed to the PreparedStatement.
		 */

		// @formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on

		/*
		 * Obtain a connection from DbConnection.getConnection(). Assign it a variable
		 * of type connection named conn in a try-with-resource statement.
		 * 
		 * Start a transaction. Inside the try block, start a transaction by calling
		 * startTransaction() and passing the Connection object. startTransaction() is a
		 * method in the base class, DaoBase.
		 * 
		 * Below startTransaction,
		 */
		try (Connection conn = DbConnection.getConnection();) {
			startTransaction(conn);

			/*
			 * Obtain a PreparedStatement object from the Connection object. Add another
			 * try-with-resource statement to obtain a PreparedStatement from the Connection
			 * object.
			 */

			// Pass the SQL statement as a parameter to conn.prepareStatement()
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {

				/*
				 * Set the project details as parameters in the PreParedStatement object. Use
				 * the convenience method in DaoBase setParameter(). This method handles null
				 * values correctly.
				 */
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				/*
				 * Save the project details. Perform the insert by calling executeUpdate() on
				 * the PreparedStatment object. Do not pass any parameters to executeUpdate() or
				 * it will reset all the parameters leading to an obscure error.
				 */
				stmt.executeUpdate();

				/*
				 * Obtain the project ID (primary key) by calling the convenience method in
				 * DaoBase, getLastInsertId(). Pass the Connection object and the constant
				 * PROJECT_TABLE to getLastInsertId().
				 */
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);

				// Commit the transaction
				commitTransaction(conn);

				// Set the projectId on the Project object that was passed into insertProject
				// and return it
				project.setProjectId(projectId);
				return project;

				/*
				 * Add a catch block to the inner try block that catches Exception. In the catch
				 * block, roll back the transaction and throw a DbException initialized with the
				 * Exception object passed into the catch block. This will ensure that the
				 * transaction is rolled back when an exception is thrown.
				 */
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

			/*
			 * Catch the SQLException in a catch block added to the try- with-resouce. From
			 * within the catch block, throw a new DbException. The DbException constructor
			 * should take the SQLException object passed into the catch block.
			 */
		} catch (SQLException e) {
			throw new DbException(e);
		}

	} // end of insertProject

	/*
	 * Now you need to write the code to retrieve all the projects from the
	 * database. It is structured similarly to the insertProject() method, but it
	 * will also incorporate a ResultSet to retrieve the project row(s).
	 * 
	 * To implement this method, first you will write the SQL statement that
	 * instructs MySQL to return all project rows without any materials, steps, or
	 * categories. Then, you will obtain a Connection and start a transaction. Next,
	 * you will obtain a PreparedStatement from the Connection object. Then, you
	 * will get a ResultSet from the PreparedStatement. Finally, you will iterate
	 * over the ResultSet to create a Project object for each row returned.
	 */
	public List<Project> fetchAllProjects() {
		// @formatter:off
		String sql = "SELECT * FROM " + PROJECT_TABLE
				+ " ORDER BY project_name";				
		// @formatter:on

		try (Connection conn = DbConnection.getConnection();) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				/*
				 * Add a try-with-resource statement to obtain a ResultSet from the
				 * PreparedStatement. Include the import statement for ResultSet. It is in the
				 * java.sql package.
				 */
				try (ResultSet rs = stmt.executeQuery()) {
					/*
					 * create and return a List of Projects. Loop through the result set, create and
					 * assign each result row to a new Project object. Add the Project object to the
					 * List of Projects. You can do this by calling the extract method.
					 */
					List<Project> projects = new LinkedList<>();
					while (rs.next()) {
						projects.add(extract(rs, Project.class));
					}

					return projects;
				}

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}

	} // end of fetchAllProjects

	public Optional<Project> fetchProjectById(Integer projectId) {
		/*
		 * SQL statement to return all columns from the project table in the row that
		 * matches the given projectId. Use the "?" placeholder
		 */
		// @formatter:off
		String sql = "SELECT * FROM " + PROJECT_TABLE
				+ " WHERE project_id = ?";
		// @formatter:on

		/*
		 * Obtain a Connection object in a try-with-resource statement. Add the catch
		 * block to handle the SQLException. In the catch block throw a new DbException
		 * passing the SQLException object as a parameter.
		 */
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			/*
			 * Inside the try block, create a variable of type Project and set it to null.
			 * Return the Project object as an Optional object using Optional.ofNullable().
			 * Save the file. You should have no compilation errors at this point but you
			 * may see some warnings. This is OK.
			 */
			try {
				Project project = null;

				/*
				 * Inside the inner try block, obtain a PreparedStatement from the Connection
				 * object in a try-with-resource statement. Pass the SQL statement in the method
				 * call to prepareStatement().
				 */
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					// Add the projectId method parameter as a parameter to the PreparedStatement.
					setParameter(stmt, 1, projectId, Integer.class);
					/*
					 * Obtain a ResultSet in a try-with-resource statement. If the ResultSet has a
					 * row in it (rs.next()) set the Project variable to a new Project object and
					 * set all fields from values in the ResultSet. You can call the extract()
					 * method for this.
					 */
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				/*
				 * Below the try-with-resource statement that obtains the PreparedStatement but
				 * inside the try block that manages the rollback, add three method calls to
				 * obtain the list of materials, steps, and categories. Since each method
				 * returns a List of the appropriate type, you can call addAll() to add the
				 * entire List to the List in the Project object
				 */
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}

				commitTransaction(conn);
				return Optional.ofNullable(project);

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	/*
	 * Write the three methods to return materials, steps, and categories. Each
	 * method should return a List of the appropriate type. Each method should take
	 * the Connection and the project ID as parameters.
	 * 
	 * Each method is written in the same way as the other query methods with the
	 * exception that the Connection is passed as a parameter, so you don't need to
	 * call DbConnection.getConnection() to obtain it.
	 * 
	 * Each method can add throws SQLException to the method declaration. This is
	 * because the method call to each method is within a try/catch block.
	 */
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = "SELECT m.* FROM " + MATERIAL_TABLE + " m " 
				+ "JOIN " + PROJECT_TABLE 
				+ " p USING (project_id) "
				+ "WHERE project_id = ?";
		// @formatter:on

		// prepare the sql statement
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			// set the parameter (?) within the sql statement
			setParameter(stmt, 1, projectId, Integer.class);

			// execute the query
			try (ResultSet rs = stmt.executeQuery()) {
				// instantiate the list of materials (or steps/categories)
				List<Material> materials = new LinkedList<>();

				// loop through all of the rows and add them to the list
				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}

				return materials;
			}
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = "SELECT s.* FROM " + STEP_TABLE + " s " 
				+ "JOIN " + PROJECT_TABLE 
				+ " p USING (project_id) "
				+ "WHERE project_id = ?";
		// @formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}

				return steps;
			}
		}
	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c " 
				+ "JOIN " + PROJECT_CATEGORY_TABLE
				+ " pc USING (category_id) " 
				+ "WHERE project_id = ?";
		// @formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}

				return categories;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		// this method has a similar structure to insertProject
		// @formatter:off
		String sql = ""
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ? ";
		// @formatter:on

		try (Connection conn = DbConnection.getConnection();) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {

				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);

				boolean updated = stmt.executeUpdate() == 1;

				commitTransaction(conn);

				return updated;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		// @formatter:off
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ? ";
		// @formatter:on

		try (Connection conn = DbConnection.getConnection();) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {

				setParameter(stmt, 1, projectId, Integer.class);

				boolean deleted = stmt.executeUpdate() == 1;

				commitTransaction(conn);

				return deleted;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

}
