package rest.trip;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TouristNotFoundException extends RuntimeException{
	public TouristNotFoundException(String userEmail) {
		// TODO Auto-generated constructor stub
		super("could not find tourist"+userEmail+".");
	}
}
