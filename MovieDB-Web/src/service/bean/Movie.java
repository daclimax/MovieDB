package service.bean;

import java.util.Date;

public class Movie {

	private String actors;

	private String genres;

	private int id;

	private String imdbId;

	private String imdbRating;

	private String plot;

	private String posterPath;

	private String title;

	private boolean warning;

	private int year;

	private Date created;

	public String getActors() {
		return actors;
	}

	public Date getCreated() {
		return created;
	}

	public String getGenres() {
		return genres;
	}

	public int getId() {
		return id;
	}

	public String getImdbId() {
		return imdbId;
	}

	public String getImdbRating() {
		return imdbRating;
	}

	public String getPlot() {
		return plot;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public String getTitle() {
		return title;
	}

	public int getYear() {
		return year;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public void setImdbRating(String imdbRating) {
		this.imdbRating = imdbRating;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
