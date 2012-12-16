package controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import model.MovieListModel;
import service.impl.DataServiceImpl;

@ManagedBean
@RequestScoped
public class MovieListController {

	private final DataServiceImpl impl;

	@ManagedProperty(value = "#{movieListModel}")
	private MovieListModel movieListModel;

	public MovieListController() {
		impl = new DataServiceImpl();
	}

	/** loads the movies */
	public void loadAllMovies() {
		this.movieListModel.setMovies(impl.loadAllMovies());
	}

	/** Setter, required for injected bean */
	public void setMovieListModel(final MovieListModel movieListModel) {
		this.movieListModel = movieListModel;
	}
}
