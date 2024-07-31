package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.entity.Project;
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

}
