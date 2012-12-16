package service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import service.bean.Movie;
import constants.MovieListConstants;

/**
 * DataService
 */
public class DataServiceImpl {

	private static Connection connect;

	final private static Logger LOG = Logger.getLogger(DataServiceImpl.class);

	final static ResourceBundle props = ResourceBundle.getBundle("moviedb");

	public DataServiceImpl() {

		// Setup the connection with the DB
		try {
			// load mysql jdbc driver
			Class.forName("com.mysql.jdbc.Driver");

			connect = DriverManager.getConnection("jdbc:mysql://localhost/" + props.getString(MovieListConstants.MOVIEDB_KEY) + "?user="
					+ props.getString(MovieListConstants.DBUSER_KEY) + "&password=" + props.getString(MovieListConstants.DBPASS_KEY));

		} catch (SQLException e) {
			LOG.info("Could not establish DB-Connection.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOG.info("Could not load Mysql-Driver.");
			e.printStackTrace();
		}
	}

	/** find a movie by given id */
	public Movie findMovieById(int movieId) {
		Movie movie = new Movie();
		try {
			final String queryStr = "SELECT * FROM movies WHERE MOV_ID = ?; ";
			PreparedStatement statement = connect.prepareStatement(queryStr);
			statement.setInt(1, movieId);
			final ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				movie.setActors(resultSet.getString(MovieListConstants.COLUMN_ACTORS));
				movie.setCreated(resultSet.getDate(MovieListConstants.COLUMN_CREATED));
				movie.setGenres(resultSet.getString(MovieListConstants.COLUMN_GENRE));
				movie.setId(resultSet.getInt(MovieListConstants.COLUMN_ID));
				movie.setImdbId(resultSet.getString(MovieListConstants.COLUMN_IMDB_ID));
				movie.setImdbRating(resultSet.getString(MovieListConstants.COLUMN_RATING));
				movie.setPlot(resultSet.getString(MovieListConstants.COLUMN_PLOT));
				movie.setPosterPath(resultSet.getString(MovieListConstants.COLUMN_POSTER));
				movie.setTitle(resultSet.getString(MovieListConstants.COLUMN_TITLE));
				movie.setWarning(resultSet.getInt(MovieListConstants.COLUMN_WARNING) == 1);
				movie.setYear(resultSet.getInt(MovieListConstants.COLUMN_YEAR));
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load all movies from database.
	 * 
	 * @return List<Movie>
	 */
	public List<Movie> loadAllMovies() {
		List<Movie> movies = new LinkedList<Movie>();

		try {
			final String queryStr = "SELECT * FROM movies; ";
			PreparedStatement statement = connect.prepareStatement(queryStr);
			final ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Movie movie = new Movie();
				movie.setActors(resultSet.getString(MovieListConstants.COLUMN_ACTORS));
				movie.setCreated(resultSet.getDate(MovieListConstants.COLUMN_CREATED));
				movie.setGenres(resultSet.getString(MovieListConstants.COLUMN_GENRE));
				movie.setId(resultSet.getInt(MovieListConstants.COLUMN_ID));
				movie.setImdbId(resultSet.getString(MovieListConstants.COLUMN_IMDB_ID));
				movie.setImdbRating(resultSet.getString(MovieListConstants.COLUMN_RATING));
				movie.setPlot(resultSet.getString(MovieListConstants.COLUMN_PLOT));
				movie.setPosterPath(resultSet.getString(MovieListConstants.COLUMN_POSTER));
				movie.setTitle(resultSet.getString(MovieListConstants.COLUMN_TITLE));
				movie.setWarning(resultSet.getInt(MovieListConstants.COLUMN_WARNING) == 1);
				movie.setYear(resultSet.getInt(MovieListConstants.COLUMN_YEAR));

				movies.add(movie);
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return movies;
	}
}
