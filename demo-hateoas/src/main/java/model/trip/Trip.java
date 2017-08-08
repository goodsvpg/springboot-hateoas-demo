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
	
	private String title;
	private String description;

	public Trip() {
		
	}
	
	public Trip(Tourist tourist, String title, String description) {
		this.tourist = tourist;
		this.title = title;
		this.description = description;
	}

	public Tourist getTourist() {
		return tourist;
	}
	
	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	
	@Override
	public String toString() {
		return "Trip [tourist=" + tourist + ", id=" + id + ", title=" + title + ", description=" + description + "]";
	}

}
