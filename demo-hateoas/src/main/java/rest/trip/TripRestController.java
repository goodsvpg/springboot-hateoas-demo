package rest.trip;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(method = RequestMethod.GET)
	public Collection<Trip> getTripList(@PathVariable String userEmail){
		System.out.println("get!");
		this.validateTourist(userEmail);
		return this.tripRepository.findByTouristUserEmail(userEmail);
		//userEmail로 작성된 trip 리스트를 모두 return
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@PathVariable String userEmail, @RequestBody Trip input){
		this.validateTourist(userEmail);
		
		Optional<Tourist> tourist = this.touristRepository.findByUserEmail(userEmail);
		//Optional : 값이 있거나 null인 컨테이너 value
		
		if(tourist.isPresent()){
			Trip result = tripRepository.save(new Trip(tourist.get(), input.getTitle(), input.getDescription()));
			
			URI location = ServletUriComponentsBuilder //Servlet 요청에서 사용 가능한 URL정보를 복사하는 정적 팩토리 메소드를 제공
							.fromCurrentRequest().path("/{id}") //지정된 경로를 builder의 기존 경로에 추가
							.buildAndExpand(result.getId()).toUri();
							//uri template 변수를 array의 변수로 변경 ->uri로 변경
			System.out.println("uri!"+location.getPath());	
			return ResponseEntity.created(location).build();
		}else{
			return ResponseEntity.noContent().build();
		}
		
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/{tripId}")
	public TripResource getTrip(@PathVariable String userEmail, @PathVariable Long tripId){
		System.out.println("getTrip userEmail=" + userEmail + ", tripId="+tripId);
		this.validateTourist(userEmail);
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
