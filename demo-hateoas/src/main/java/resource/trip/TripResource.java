package resource.trip;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import model.trip.Trip;
import rest.trip.TripRestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class TripResource extends ResourceSupport{
	//link를 수집하는 DTO( Data Transfer Object) Base Class
	//trip collection과 개별 self link를 삽입하려고 사용
	private final Trip trip;

	
	public TripResource(Trip trip) {
		String userEmail = trip.getTourist().getUserEmail();
		this.trip = trip;
		this.add(new Link(trip.getTitle(), "trip-title"));
		//resource에 링크를 저장
		this.add(linkTo(TripRestController.class, userEmail).withRel(userEmail));
		//userEmail을 가지고 builder instance가 Link를 생성하고 resource에 저장
		this.add(linkTo(methodOn(TripRestController.class, userEmail).getTrip(userEmail, trip.getId())).withSelfRel());
	}


	public Trip getTrip() {
		return trip;
	}
}
