package resource.trip;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import model.trip.Trip;
import rest.trip.TripRestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlRootElement;

public class TripResource extends ResourceSupport{
	//link를 수집하는 DTO( Data Transfer Object) Base Class
	//trip collection과 개별 self link를 삽입하려고 사용
	//resource는 "self" 하나라도 링크를 가지고 있음
	//change to HAL(Hypertext Application Language)
	private final Trip trip;
	
	public TripResource(Trip trip) {
		String userEmail = trip.getTourist().getUserEmail();
		this.trip = trip;
		this.add(new Link(trip.getTitle(), "trip-title"));
		//resource에 링크를 저장
		//"_links"에서 확인 가능
		this.add(linkTo(TripRestController.class, userEmail).withRel(userEmail));
		//userEmail을 가지고 builder instance가 Link를 생성하고 resource에 저장
		//this.add(linkTo(methodOn(TripRestController.class, userEmail).getTrip(userEmail, trip.getId())).withSelfRel());
		//methodOn:컨트롤러 클래스의 프록시를 생성시킴
		//dummy method invocation
		//withSelRel:getTrip실행시 builder instance가 default self rel로 Link를 생성
		
		//다른방식
		try {
			Method method = TripRestController.class.getMethod("getTrip", String.class, Long.class);
			Link link = linkTo(method, userEmail, trip.getId()).withSelfRel();
			this.add(link);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Trip getTrip() {
		return trip;
	}
}
