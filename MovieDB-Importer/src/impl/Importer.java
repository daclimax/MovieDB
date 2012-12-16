package impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Importer {

	final static String ACTORS = "ACTORS";

	final static String API_URL = "http://www.omdbapi.com/";

	final static String DBPASS_KEY = "database_password";

	final static String DBUSER_KEY = "database_user";

	final static String ERROR = "ERROR";

	final static String EXPORT_FILE_KEY = "export_movies_file";

	final static String GENRE = "GENRE";

	final static String IMDB_ID = "IMDBID";

	final static String IMDB_RATING = "IMDBRATING";

	final static String MOVIEDB_KEY = "database_name";

	final static String OUTPUT_FORMAT = "xml";

	final static String PLOT = "PLOT";

	final static String PLOT_FORMAT = "full";

	final static String POSTER_URL = "POSTER";

	final static String TITLE = "TITLE";

	final static String WARNING = "WARNING";

	final static String YEAR = "YEAR";

	private static Connection connect;

	final private static Logger LOG = Logger.getLogger(Importer.class);

	final static ResourceBundle props = ResourceBundle.getBundle("moviedb");

	// final String API_URL = "http://imdbapi.org/";
	// final String API_URL = "http://cattweasel.net/search/";

	/**
	 * checks the Document with searchResults, whether the wanted movie is in
	 * there
	 * 
	 * @param doc
	 * @param movieName
	 * @return imdbID
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> checkResultList(final Document doc, final String movieName) {
		final Map<String, String> resultMap = new HashMap<String, String>();
		final Element root = doc.getRootElement();

		String failReason = "";
		boolean successful = true;

		Map<Integer, Map<String, String>> moviesMap = new HashMap<Integer, Map<String, String>>();
		int movieCounter = 0;

		for (final Iterator<Element> parentIterator = root.elements().iterator(); parentIterator.hasNext();) {
			final Element parentElement = parentIterator.next();

			if (parentElement.getName().toUpperCase().equals(Importer.ERROR)) {
				successful = false;
				failReason = String.valueOf(parentElement.getData());
				resultMap.put(Importer.ERROR, failReason);
			}

			if (successful) {
				Map<String, String> movieDetailMap = new HashMap<String, String>();

				for (final Iterator<Attribute> parentAttributesIterator = parentElement.attributes().iterator(); parentAttributesIterator
						.hasNext();) {
					final Attribute parentAttribute = parentAttributesIterator.next();
					// TITLE
					if (parentAttribute.getName().toUpperCase().equals(Importer.TITLE)) {
						movieDetailMap.put(Importer.TITLE, String.valueOf(parentAttribute.getData()));
					}

					// YEAR
					if (parentAttribute.getName().toUpperCase().equals(Importer.YEAR)) {
						movieDetailMap.put(Importer.YEAR, String.valueOf(parentAttribute.getData()));
					}

					// IMDB_ID
					if (parentAttribute.getName().toUpperCase().equals(Importer.IMDB_ID)) {
						movieDetailMap.put(Importer.IMDB_ID, String.valueOf(parentAttribute.getData()));
					}

					moviesMap.put(movieCounter, movieDetailMap);
				}
			}
			movieCounter++;
		}

		int lowestDifference = 1000;
		int bestKeyMovieId = -1;

		for (Integer keyMovieId : moviesMap.keySet()) {
			for (String key : moviesMap.get(keyMovieId).keySet()) {

				if (key.equals(Importer.TITLE)) {
					int actualDifference = StringUtils.getLevenshteinDistance(movieName, moviesMap.get(keyMovieId).get(key));

					// there's another movie with this difference, give a
					// warning.
					if (actualDifference == lowestDifference) {
						moviesMap.get(bestKeyMovieId).put(Importer.WARNING, "more than one movie found, it may not the searched one");
					}

					// it could be the searched movie
					if (actualDifference < lowestDifference) {
						lowestDifference = actualDifference;
						bestKeyMovieId = keyMovieId;
					}
				}
			}
		}

		return moviesMap.get(bestKeyMovieId);
	}

	/**
	 * Checks given movieName in database to avoid duplicates
	 * 
	 * @param movieName
	 * @return true, if the movie is already in database
	 */
	private static boolean checkWhetherMovieIsAlreadyInDatabase(final String movieName) {
		boolean isAlreadyInDb = false;
		try {
			final String queryStr = "SELECT MOV_ID FROM movies WHERE MOV_TITLE=? ; ";

			PreparedStatement statement = connect.prepareStatement(queryStr);
			statement.setString(1, movieName);
			final ResultSet resultSet = statement.executeQuery();

			isAlreadyInDb = resultSet.next();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return isAlreadyInDb;
	}

	@SuppressWarnings("unchecked")
	private static void loadMovieDetails(final Map<String, String> resultMap) {
		// Forming a complete url ready to send
		final String dataurl = Importer.API_URL + "?i=" + resultMap.get(Importer.IMDB_ID) + "&r=" + Importer.OUTPUT_FORMAT + "&plot="
				+ Importer.PLOT_FORMAT;

		Importer.LOG.info("Getting details from service ...");

		InputStream inputStream = null;
		Document doc = null;
		try {
			final URL url = new URL(dataurl);
			inputStream = url.openStream();

			final SAXReader reader = new SAXReader();
			doc = reader.read(inputStream);

			final Element root = doc.getRootElement();

			for (final Iterator<Element> parentIterator = root.elements().iterator(); parentIterator.hasNext();) {
				final Element parentElement = parentIterator.next();

				for (final Iterator<Attribute> parentAttributesIterator = parentElement.attributes().iterator(); parentAttributesIterator
						.hasNext();) {
					final Attribute parentAttribute = parentAttributesIterator.next();

					// GENRE
					if ((resultMap.get(Importer.GENRE) == null) && parentAttribute.getName().toUpperCase().equals(Importer.GENRE)) {
						resultMap.put(Importer.GENRE, String.valueOf(parentAttribute.getData()));
					}

					// ACTORS
					if ((resultMap.get(Importer.ACTORS) == null) && parentAttribute.getName().toUpperCase().equals(Importer.ACTORS)) {
						resultMap.put(Importer.ACTORS, String.valueOf(parentAttribute.getData()));
					}

					// PLOT
					if ((resultMap.get(Importer.PLOT) == null) && parentAttribute.getName().toUpperCase().equals(Importer.PLOT)) {
						resultMap.put(Importer.PLOT, String.valueOf(parentAttribute.getData()));
					}

					// POSTER_URL
					if ((resultMap.get(Importer.POSTER_URL) == null) && parentAttribute.getName().toUpperCase().equals(Importer.POSTER_URL)) {
						resultMap.put(Importer.POSTER_URL, String.valueOf(parentAttribute.getData()));
					}

					// IMDB_RATING
					if ((resultMap.get(Importer.IMDB_RATING) == null)
							&& parentAttribute.getName().toUpperCase().equals(Importer.IMDB_RATING)) {
						resultMap.put(Importer.IMDB_RATING, String.valueOf(parentAttribute.getData()));
					}
				}
			}

		} catch (final IOException e) {
			e.printStackTrace();

		} catch (final DocumentException e2) {
			e2.printStackTrace();

		} finally {

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(final String[] args) {

		try {
			// load mysql jdbc driver
			Class.forName("com.mysql.jdbc.Driver");

			// Setup the connection with the DB
			Importer.connect = DriverManager.getConnection("jdbc:mysql://localhost/" + props.getString(Importer.MOVIEDB_KEY) + "?user="
					+ props.getString(Importer.DBUSER_KEY) + "&password=" + props.getString(Importer.DBPASS_KEY));

		} catch (final ClassNotFoundException e) {
			LOG.info("mysql driver not found");
			e.printStackTrace();
		} catch (final SQLException e2) {
			LOG.info("Connection could not established.");
			e2.printStackTrace();
		}

		BufferedReader br = null;
		try {
			LOG.info(props.getString(Importer.EXPORT_FILE_KEY));
			final File file = new File(props.getString(Importer.EXPORT_FILE_KEY));
			br = new BufferedReader(new FileReader(file));

			// count for successful inserts
			int successCount = 0;
			int successCountWithWarnings = 0;
			int errorCount = 0;
			int alreadyInDBCount = 0;

			while (br.ready()) {

				// remove some characters and parentFolders
				String movieName = br.readLine();
				movieName = movieName.replace("./", "");
				movieName = movieName.substring(movieName.indexOf("/") + 1, movieName.length());

				LOG.info("Processing movie \"" + movieName + "\"...");

				// Remove unwanted space from moviename by trimming it and
				// replace whitespaces with "+"
				final String movieNameForURL = movieName.trim().replace(" ", "+");

				final Document doc = Importer.searchForMovies(movieNameForURL);
				final Map<String, String> resultMap = Importer.checkResultList(doc, movieName);
				resultMap.put(TITLE, movieName);

				if (!Importer.checkWhetherMovieIsAlreadyInDatabase(resultMap.get(TITLE))) {
					if (resultMap.containsKey(Importer.ERROR)) {
						errorCount++;
						Importer.LOG.info("ERROR: " + resultMap.get(Importer.ERROR));

					} else {
						// load movie details with given ID
						Importer.loadMovieDetails(resultMap);

						// save details in database
						Importer.saveDetailsInDatabase(resultMap);

						if (resultMap.containsKey(Importer.WARNING)) {
							successCountWithWarnings++;
							Importer.LOG.info("WARNING: " + resultMap.get(Importer.WARNING));
						}

						successCount++;
						Importer.LOG.info("... load data for \"" + movieName + "\" was successful");
					}
				} else {
					alreadyInDBCount++;
					LOG.info("... the movie is already in database, no need to import it.");
				}
			}

			LOG.info("-----------------------------------------------------------------------------------");
			LOG.info("|	Summary:");
			LOG.info("|     successful movie inserts:           " + successCount);
			LOG.info("|	    successful movie with warnings:     " + successCountWithWarnings);
			LOG.info("|	    error count:                        " + errorCount);
			LOG.info("|	    already in db:                      " + alreadyInDBCount);
			LOG.info("-----------------------------------------------------------------------------------");

		} catch (final IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (br != null) {
					br.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves the loaded details of a movie in the local db
	 * 
	 * @param resultMap
	 */
	private static void saveDetailsInDatabase(final Map<String, String> resultMap) {
		try {
			final String queryStr = "INSERT INTO movies (MOV_TITLE, MOV_YEAR, MOV_GENRE, MOV_ACTORS, MOV_PLOT, "
					+ "MOV_POSTER_URL, MOV_RATING, MOV_WARNING, MOV_IMDB_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ; ";

			PreparedStatement statement = connect.prepareStatement(queryStr);
			statement.setString(1, resultMap.get(TITLE));
			statement.setInt(2, Integer.valueOf(resultMap.get(YEAR)));
			statement.setString(3, resultMap.get(GENRE));
			statement.setString(4, resultMap.get(ACTORS));
			statement.setString(5, resultMap.get(PLOT));
			statement.setString(6, resultMap.get(POSTER_URL));
			statement.setString(7, resultMap.get(IMDB_RATING));
			if (resultMap.containsKey(WARNING)) {
				statement.setInt(8, 1);
			} else {
				statement.setInt(8, 0);
			}
			statement.setString(9, resultMap.get(IMDB_ID));
			statement.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Searches for a movie with given movieName
	 * 
	 * @param movieName
	 * @return xmlDocument
	 */
	private static Document searchForMovies(final String movieName) {
		// Forming a complete url ready to send.
		final String dataurl = Importer.API_URL + "?s=" + movieName + "&r=" + Importer.OUTPUT_FORMAT;

		InputStream inputStream = null;
		Document doc = null;
		try {
			final URL url = new URL(dataurl);
			inputStream = url.openStream();

			final SAXReader reader = new SAXReader();
			doc = reader.read(inputStream);

		} catch (final IOException e) {
			e.printStackTrace();

		} catch (final DocumentException e2) {
			e2.printStackTrace();

		} finally {

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		return doc;
	}
}
