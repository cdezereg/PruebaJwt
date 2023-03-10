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
import cl.dp.signup.payload.response.SignupResponse;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SignUpControllerTests {


	@Value(value="${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void con_error() throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    JSONObject signUPJsonObject = new JSONObject();
	    signUPJsonObject.put("password","aaAsdfd11");

		HttpEntity<String> requestEntity = new HttpEntity<>(signUPJsonObject.toString(),headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/sign-up",requestEntity,
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

		HttpEntity<String> requestEntity = new HttpEntity<>(signUPJsonObject.toString(),headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/sign-up",requestEntity,
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
		HttpEntity<String> requestEntity = new HttpEntity<>(signUPJsonObject.toString(),headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/sign-up",requestEntity,
				String.class);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		String sRep = response.getBody();
		SignupResponse signupRes = mapper.readValue(sRep, SignupResponse.class);
		assertEquals(name,signupRes.getName());
		assertEquals(email,signupRes.getEmail());
		assertEquals(password,signupRes.getPassword());
		for(int i = 0; i<phones.size();i++)
		{
			assertEquals(phones.get(i).getNumber(),signupRes.getPhones().get(i).getNumber());
			assertEquals(phones.get(i).getCitycode(),signupRes.getPhones().get(i).getCitycode());
			assertEquals(phones.get(i).getCountrycode(),signupRes.getPhones().get(i).getCountrycode());
		}		
		assertNotNull(signupRes.getId());
		assertNotNull(signupRes.getCreated());
		assertNotNull(signupRes.getToken());
		assertTrue(signupRes.isActive());

	}

}
