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
	//@Id : Entity의 	PK
	
	private String userEmail;
	private String nickName;
	
	@OneToMany(mappedBy = "tourist") 
	private List<Trip> tripList = new ArrayList<Trip>(); 
	//일대일 다중성을 갖는 다른 엔티티에 대한 단일 값 연결을 지정
	
	public Tourist() {
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

	@Override
	public String toString() {
		return "Tourist [id=" + id + ", userEmail=" + userEmail + ", nickName=" + nickName + ", tripList=" + tripList
				+ "]";
	}

}
