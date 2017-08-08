package test.trip;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
@WebAppConfiguration
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
		
		this.tourist = touristRepository.save(new Tourist(userEmail, "nickName"));
		this.tripList.add(tripRepository.save(new Trip(tourist, "title1", "description1")));
		this.tripList.add(tripRepository.save(new Trip(tourist, "title2", "description2")));
		System.out.println("test tripList="+this.tripList);
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
	public void getSingleTrip() throws Exception{
		System.out.println("test SingleTrip getId="+this.tripList.get(0).getId());
		mockMvc.perform(get("/"+userEmail+"/tripList/"
				+this.tripList.get(0).getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(mediaType))
				.andExpect(jsonPath("$.trip.id", is(this.tripList.get(0).getId().intValue())))
				.andExpect(jsonPath("$.trip.title", is("title1")))
				.andExpect(jsonPath("$.trip.description", is("description1")));
				//response body에 jsonPath expression를 이용해 접근하여 하위 subset에 json경로에 있는 값과 일치하는 값이 있는지 확인 
				//is(value)는 equalTo(value)를 위한 wrapper
	}

	
	protected String json(Object obj) throws IOException{
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		//HttpOutputMessage의 모의 구현 
		this.httpMessageConverter.write(obj, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		//mockHttpOutputMessage에 JSON 형태로 object를 기입
		return mockHttpOutputMessage.getBodyAsString();
	}
}