package model.trip;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	CommandLineRunner init(TouristRepository touristRepository, TripRepository tripRepository){
		return (evt) -> {
			List<String> emailList = new ArrayList<String>();
			emailList.add("aa@helloMail.com");
			emailList.add("bb@helloMail.com");
			emailList.add("cc@helloMail.com");
			emailList.add("dd@helloMail.com");
			
			for(String email : emailList){
				Tourist tourist = touristRepository.save(new Tourist(email, "nickName"));
				tripRepository.save(new Trip(tourist, "http://hello-demo-hateoas/1/", "title1"));
				tripRepository.save(new Trip(tourist, "http://hello-demo-hateoas/2/", "title2"));
			}
		};
	}
}
