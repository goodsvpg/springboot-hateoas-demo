package model.trip;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.ManyToOne;


@Entity
public class Trip {
	
	@JsonIgnore
	@ManyToOne
	private Tourist tourist;
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String uri;
	private String title;
	
	public Trip() {
	}
	
	public Trip(Tourist tourist, String uri, String title){
		this.tourist = tourist;
		this.uri = uri;
		this.title = title;
	}

	public Tourist getTourist() {
		return tourist;
	}

	public Long getId() {
		return id;
	}

	public String getUri() {
		return uri;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "Trip [tourist=" + tourist + ", id=" + id + ", uri=" + uri + ", title=" + title + "]";
	}

}
