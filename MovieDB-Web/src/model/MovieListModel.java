package model;

import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import service.bean.Movie;

/**
 * 
 * Managed Bean for the Movies List.
 * 
 */
@ManagedBean(name = "movieListModel")
@SessionScoped
public class MovieListModel {

	private List<Movie> movies;

	public MovieListModel() {
		this.movies = new LinkedList<Movie>();
	}

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}

}
