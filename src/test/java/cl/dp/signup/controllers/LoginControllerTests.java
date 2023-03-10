package cl.dp.signup.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.dp.signup.models.Phone;
import cl.dp.signup.payload.response.LoginResponse;
import cl.dp.signup.payload.response.SignupResponse;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginControllerTests {


	@Value(value="${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void con_error() throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    JSONObject loginJsonObject = new JSONObject();
	    loginJsonObject.put("token","aaAsdfd11");

	    
		HttpEntity<String> requestEntity = new HttpEntity<>(loginJsonObject.toString(),headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/login",requestEntity,
				String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	
	
	@Test
	public void correcto() throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    JSONObject signUPJsonObject = new JSONObject();
	    signUPJsonObject.put("name","Krishna");
	    signUPJsonObject.put("email","a@a.cl");
	    signUPJsonObject.put("password","aaAsdfd11");
	
		HttpEntity<String> signupRequestEntity = new HttpEntity<>(signUPJsonObject.toString(),headers);
		ResponseEntity<String> signupResponse = restTemplate.postForEntity("http://localhost:" + port + "/sign-up",signupRequestEntity,
				String.class);
		assertNotNull(signupResponse);
		assertEquals(signupResponse.getStatusCode(), HttpStatus.OK);

		String sRep = signupResponse.getBody();
		ObjectMapper mapper = new ObjectMapper();
		SignupResponse signupRes = mapper.readValue(sRep, SignupResponse.class);
	    JSONObject loginJsonObject = new JSONObject();
	    loginJsonObject.put("token",signupRes.getToken());

	    
		HttpEntity<String> requestEntity = new HttpEntity<>(loginJsonObject.toString(),headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/login",requestEntity,
				String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void completo() throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    JSONObject signUPJsonObject = new JSONObject();
		String name = "Juan Perez";
	    signUPJsonObject.put("name",name);
		String email = "m@d.com";
	    signUPJsonObject.put("email",email);
		String password = "aaa12Maaa";	
	    signUPJsonObject.put("password",password);
		Phone phone1 = new Phone(Long.valueOf(1),2,"3");
		Phone phone2 = new Phone(Long.valueOf(3),4,"5");
		List<Phone> phones = new LinkedList<Phone>();
		phones.add(phone1);
		phones.add(phone2);
		ObjectMapper mapper = new ObjectMapper();

		JsonNode listNode = mapper.valueToTree(phones);
		JSONArray request = new JSONArray(listNode.toString());
	    signUPJsonObject.put("phones",request);
		HttpEntity<String> signupRequestEntity = new HttpEntity<>(signUPJsonObject.toString(),headers);
		ResponseEntity<String> signUpResponse = restTemplate.postForEntity("http://localhost:" + port + "/sign-up",signupRequestEntity,
				String.class);
		assertNotNull(signUpResponse);
		assertEquals(signUpResponse.getStatusCode(), HttpStatus.OK);
		String sRep = signUpResponse.getBody();
		SignupResponse signupRes = mapper.readValue(sRep, SignupResponse.class);
	    JSONObject loginJsonObject = new JSONObject();
	    loginJsonObject.put("token",signupRes.getToken());

		HttpEntity<String> requestEntity = new HttpEntity<>(loginJsonObject.toString(),headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/login",requestEntity,
				String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		String lRep = response.getBody();
		LoginResponse loginRes = mapper.readValue(lRep, LoginResponse.class);

		
		assertEquals(name,loginRes.getName());
		assertEquals(email,loginRes.getEmail());
		assertEquals(password,loginRes.getPassword());
		for(int i = 0; i<phones.size();i++)
		{
			assertEquals(phones.get(i).getNumber(),loginRes.getPhones().get(i).getNumber());
			assertEquals(phones.get(i).getCitycode(),loginRes.getPhones().get(i).getCitycode());
			assertEquals(phones.get(i).getCountrycode(),loginRes.getPhones().get(i).getCountrycode());
		}		
		assertNotNull(loginRes.getId());
		assertNotNull(loginRes.getCreated());
		assertNotNull(loginRes.getLastLogin());
		assertNotNull(loginRes.getToken());
		assertTrue(loginRes.isActive());

	}


}
