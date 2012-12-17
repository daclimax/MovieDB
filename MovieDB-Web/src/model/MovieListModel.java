package model;

import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import service.bean.Movie;
import constants.MovieListConstants;

/**
 * 
 * Managed Bean for the Movies List.
 * 
 */
@ManagedBean(name = "movieListModel")
@SessionScoped
public class MovieListModel {

	private List<Movie> movies;

	private final DashboardModel dashboardModel;

	public MovieListModel() {
		this.movies = new LinkedList<Movie>();

		this.dashboardModel = new DefaultDashboardModel();
		DashboardColumn columnYear = new DefaultDashboardColumn();
		DashboardColumn columnGenre = new DefaultDashboardColumn();
		DashboardColumn columnActors = new DefaultDashboardColumn();
		DashboardColumn columnPlot = new DefaultDashboardColumn();
		DashboardColumn columnImdbId = new DefaultDashboardColumn();
		DashboardColumn columnRating = new DefaultDashboardColumn();

		columnYear.addWidget(MovieListConstants.YEAR);
		columnGenre.addWidget(MovieListConstants.GENRE);
		columnActors.addWidget(MovieListConstants.ACTORS);
		columnPlot.addWidget(MovieListConstants.PLOT);
		columnImdbId.addWidget(MovieListConstants.IMDB_ID);
		columnRating.addWidget(MovieListConstants.RATING);

		dashboardModel.addColumn(columnYear);
		dashboardModel.addColumn(columnGenre);
		dashboardModel.addColumn(columnActors);
		dashboardModel.addColumn(columnPlot);
		dashboardModel.addColumn(columnImdbId);
		dashboardModel.addColumn(columnRating);
	}

	public DashboardModel getDashboardModel() {
		return dashboardModel;
	}

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
}
