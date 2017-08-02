package model.trip;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity 
public class Tourist {
	
	@Id 
	@GeneratedValue
	private Long id;

	private String userEmail;
	private String nickName;
	
	@OneToMany(mappedBy = "tourist") 
	private List<Trip> tripList = new ArrayList<Trip>(); 
	
	Tourist() {
	}
		
	public Tourist(String email, String nickName){
		this.userEmail = email;
		this.nickName = nickName;
	}

	public Long getId() {
		return id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getNickName() {
		return nickName;
	}

	public List<Trip> getTripList() {
		return tripList;
	}

}
