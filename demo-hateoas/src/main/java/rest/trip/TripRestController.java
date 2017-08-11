package rest.trip;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

import model.trip.Tourist;
import model.trip.TouristRepository;
import model.trip.Trip;
import model.trip.TripRepository;
import resource.trip.TripResource;

@RestController
@RequestMapping("/{userEmail}/tripList")
public class TripRestController{
	
	private final TripRepository tripRepository;
	private final TouristRepository touristRepository;
	
	@Autowired
	public TripRestController(TripRepository tripRepository, TouristRepository touristRepository) {
		this.tripRepository = tripRepository;
		this.touristRepository = touristRepository;
		System.out.println("TripRestController constructor");
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
	//content-type을 application/hal+json로 변경
	public Resources<TripResource> getTripList(@PathVariable String userEmail){
		this.validateTourist(userEmail);

		//userEmail로 작성된 trip 리스트를 모두 return
		Collection<Trip> tripCollection = this.tripRepository.findByTouristUserEmail(userEmail);
		List<TripResource> tripResourceList = new ArrayList<>(); 
		if(!tripCollection.isEmpty()){
			for(Trip t : tripCollection){
				tripResourceList.add(new TripResource(t));
			}
		}
		
		//after java 8
//		List<TripResource> tripResourceList = this.tripRepository.findByTouristUserEmail(userEmail)
//												.stream().map(TripResource::new)
//												//userMail과 일치하는 Trip을 이용하여 TripResource 인스턴스 생성
//												.collect(Collectors.toList());
//												//TripResource 인스턴스들을 List에 담음
		
		System.out.println("resource="+new Resources<>(tripResourceList).getContent());
		return new Resources<>(tripResourceList);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@PathVariable String userEmail, @RequestBody Trip input){
		this.validateTourist(userEmail);
		
		Optional<Tourist> tourist = this.touristRepository.findByUserEmail(userEmail);
		//Optional : 값이 있거나 null인 컨테이너 value
		
		if(tourist.isPresent()){
			//userEmail과 일치하는 tourist가 있다면 
			Trip result = tripRepository.save(new Trip(tourist.get(), input.getTitle(), input.getDescription()));
			
//			URI location = ServletUriComponentsBuilder //Servlet 요청에서 사용 가능한 URL정보를 복사하는 정적 팩토리 메소드를 제공
//							.fromCurrentRequest().path("/{id}") //지정된 경로를 builder의 기존 경로에 추가
//							//pk
//							.buildAndExpand(result.getId()).toUri();
//							//uri template 변수를 array의 변수로 변경 ->uri template로 변경
//			return ResponseEntity.created(location).build();
			Link oneTrip = new TripResource(result).getLink("self");
			return ResponseEntity.created(URI.create(oneTrip.getHref())).build();
			//ResponseEntity - Model의 type에 따라 Link or LinkBuilder 조회
			
		}else{
			return ResponseEntity.noContent().build();
		}
		
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/{tripId}")
	public TripResource getTrip(@PathVariable String userEmail, @PathVariable Long tripId){
		System.out.println("getTrip userEmail=" + userEmail + ", tripId="+tripId);
		this.validateTourist(userEmail);
		//{"trip":{"id":1,"title":"title1","description":"description1"},
		//"_links":{"trip-title":{"href":"title1"},"aa@helloMail.com":{"href":"http://localhost:8090/aa%40helloMail.com/tripList"},
		//"self":{"href":"http://localhost:8090/aa@helloMail.com/tripList/1"}}}
		return new TripResource(this.tripRepository.findOne(tripId));
		//userEmail을 기준으로 엔티티 검색
	}
	
	private void validateTourist(String userEmail){
		try {
			this.touristRepository.findByUserEmail(userEmail);
			//tourRepository에 userEmail을 가진 tour가 있는지 확인
		} catch (TouristNotFoundException e) {
			// TODO: handle exception
			e.getStackTrace();
		}
	}
	
}
