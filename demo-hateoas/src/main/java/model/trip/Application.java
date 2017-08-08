package model.trip;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import rest.trip.TripRestController;


@SpringBootApplication
@ComponentScan(basePackageClasses = TripRestController.class)
//같은 패키지 내에 controller가 존재하지 않으면 써주어야 찾음
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	CommandLineRunner init(TouristRepository touristRepository, TripRepository tripRepository){
		return (evt) -> {
			System.out.println("hello init!");
			List<String> emailList = new ArrayList<String>();
			emailList.add("aa@helloMail.com");
			emailList.add("bb@helloMail.com");
			emailList.add("cc@helloMail.com");
			emailList.add("dd@helloMail.com");
			
			for(String email : emailList){
				Tourist tourist = touristRepository.save(new Tourist(email, "nickName"));
				System.out.println("tourist="+tourist.toString());
				tripRepository.save(new Trip(tourist, "title1", "description1"));
				Trip trip = tripRepository.save(new Trip(tourist, "title2", "description2"));
				System.out.println("trip="+trip.toString());
			}
			
			System.out.println("find?"+tripRepository.findByTouristUserEmail("aa@helloMail.com").size());
		};
	}
}
