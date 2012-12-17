package controller;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import model.MovieListModel;

import org.primefaces.event.DashboardReorderEvent;

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

	public void handleReorderDashboard(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: "
				+ event.getSenderColumnIndex());

		addMessage(message);
	}

	/** loads the movies */
	public void loadAllMovies() {
		this.movieListModel.setMovies(impl.loadAllMovies());
	}

	/** Setter, required for injected bean */
	public void setMovieListModel(final MovieListModel movieListModel) {
		this.movieListModel = movieListModel;
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}
