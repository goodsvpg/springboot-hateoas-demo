package test.trip;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import model.trip.Application;
import model.trip.Tourist;
import model.trip.TouristRepository;
import model.trip.Trip;
import model.trip.TripRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ConditionalOnNotWebApplication
public class TripRestControllerTest {

	private MockMvc mockMvc;
	
	private MediaType mediaType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));
	//MimeType의 서브클래스
	//HTTP specification에 정의된 MediaType을 JSON형태로 지정  
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	//web application의 설정을 제공하는 interface
	
	private HttpMessageConverter httpMessageConverter;
	//HTTP request와 HTTP response를 변환하기 위한 인터페이스 
	//HTTP 요청 메세지 body와 HTTP 응답 메세지 body를 메세지로 다룸
	//@requestBody와 @responseBody를 사용함
	
	private String userEmail = "aa@helloMail.com";
	
	private Tourist tourist;
	
	private List<Trip> tripList = new ArrayList<Trip>(); 
	
	@Autowired
	private TouristRepository touristRepository;
	
	@Autowired
	private TripRepository tripRepository;
	
	@Before
	public void setup() throws Exception{
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		//MockMvc instance를 완전히 초기화된 주어진 WebApplicationContext를 이용해 빌드.
		this.tripRepository.deleteAllInBatch();
		//batch call안의 모든 entity를 지움
		this.touristRepository.deleteAllInBatch();
		
		this.tourist = touristRepository.save(new Tourist(userEmail, "aa"));
		this.tripList.add(tripRepository.save(new Trip(tourist, "http://hello-demo-hateoas/1/", "title1")));
		this.tripList.add(tripRepository.save(new Trip(tourist, "http://hello-demo-hateoas/2/", "title2")));
	}

	@Autowired
	public void setConverters(HttpMessageConverter<?>[] messageConverter) {
		this.httpMessageConverter = Arrays.asList(messageConverter).stream()
								.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
								.findAny().orElse(null);
		//messageConverter 중에 MappingJackson2HttpMessageConverter인 것중 아무거나 httpMessageConverter에 저장(없는경우 null 저장)
		assertNotNull("the JSON message converter must not be null", this.httpMessageConverter);
	}
	
	@Test
	public void touristNotFound() throws Exception{
		mockMvc.perform(post("/aa@helloMail.com/tripList/")
				.content(this.json(new Trip()))
				.contentType(mediaType))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void getSingleTrip() throws Exception{
		mockMvc.perform(get("/"+userEmail+"/tripList"
				+this.tripList.get(0).getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(mediaType))
				.andExpect(jsonPath("$.id", is(this.tripList.get(0).getId().intValue())))
				.andExpect(jsonPath("$.uri", is("http://hello-demo-hateoas/1/"+userEmail)))
				.andExpect(jsonPath("$.title", is("title1")));
				//response body에 jsonPath expression를 이용해 접근하여 하위 subset에 json경로에 있는 값과 일치하는 값이 있는지 확인 
				//is(value)는 equalTo(value)를 위한 wrapper
	}
	
	@Test
	public void getSeveralTrip() throws Exception{
		mockMvc.perform(get("/" + userEmail + "/tripList"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(mediaType))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(this.tripList.get(0).getId().intValue())))
				.andExpect(jsonPath("$[0].uri", is("http://hello-demo-hateoas/1/"+userEmail)))
				.andExpect(jsonPath("$[0].title", is("title1")))
				.andExpect(jsonPath("$[1].id", is(this.tripList.get(1).getId().intValue())))
				.andExpect(jsonPath("$[1].uri", is("http://hello-demo-hateoas/2/"+userEmail)))
				.andExpect(jsonPath("$[1].title", is("title2")));
	}

	@Test
	public void addTour() throws Exception{
		String tourJson = json(new Trip(this.tourist, "http://hello-demo-hateoas/3"+userEmail, "happy ChiangMai!"));
		
		System.out.println("tourJson" + tourJson); 
		this.mockMvc.perform(post("/"+ userEmail + "/tripList")
				.contentType(mediaType)
				.content(tourJson))
				.andExpect(status().isCreated());
	}
	
	protected String json(Object obj) throws IOException{
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		//HttpOutputMessage의 모의 구현 
		this.httpMessageConverter.write(obj, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		//mockHttpOutputMessage에 JSON 형태로 object를 기입
		return mockHttpOutputMessage.getBodyAsString();
	}
}