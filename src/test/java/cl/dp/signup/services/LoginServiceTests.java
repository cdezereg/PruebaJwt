package cl.dp.signup.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.dp.signup.models.Phone;
import cl.dp.signup.models.User;
import cl.dp.signup.payload.request.LoginRequest;
import cl.dp.signup.payload.request.SignupRequest;
import cl.dp.signup.payload.response.LoginResponse;
import cl.dp.signup.payload.response.ServiceResponse;
import cl.dp.signup.payload.response.SignupResponse;
import cl.dp.signup.repository.UserRepository;

@SpringBootTest
public class LoginServiceTests {

	@Autowired
	private AuthService service;
	
	@Autowired
	private UserRepository userRepo;

	@Test
	public void vacio() throws Exception {

		LoginRequest loginreq = new LoginRequest();
		ServiceResponse response = service.login(loginreq);
		assertTrue(response.isHasError());
		assertTrue(response.getResponse().contains("El token se encuentra malformado o expirado"));
	}
	
	@Test
	public void token_incorrecto() throws Exception {

		LoginRequest loginreq = new LoginRequest();
		loginreq.setToken("a");
		ServiceResponse response = service.login(loginreq);
		assertTrue(response.isHasError());
		assertTrue(response.getResponse().contains("El token se encuentra malformado o expirado"));
	}


	public void token_correcto() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("b@a.com");
		signupreq.setPassword("aaa12Maaa");
		ServiceResponse signupResponse = service.signUp(signupreq);
		assertFalse(signupResponse.isHasError());
		assertThat(signupResponse.getResponse().length()>0);
		ObjectMapper mapper = new ObjectMapper();
		SignupResponse signupRes = mapper.readValue(signupResponse.getResponse(), SignupResponse.class);
		LoginRequest loginreq = new LoginRequest();
		loginreq.setToken(signupRes.getToken());
		ServiceResponse response = service.login(loginreq);
		assertFalse(response.isHasError());
	}
	
	@Test
	@Transactional
	public void graba() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		String name = "Juan Perez";
		signupreq.setName(name);
		String email = "k@d.com";		
		signupreq.setEmail(email);
		signupreq.setPassword("aaa12Maaa");
		Phone phone1 = new Phone(Long.valueOf(1),2,"3");
		Phone phone2 = new Phone(Long.valueOf(3),4,"5");
		List<Phone> phones = new LinkedList<Phone>();
		phones.add(phone1);
		phones.add(phone2);
		signupreq.setPhones(phones);
		ServiceResponse signupResponse = service.signUp(signupreq);
		assertFalse(signupResponse.isHasError());
		ObjectMapper mapper = new ObjectMapper();
		SignupResponse signupRes = mapper.readValue(signupResponse.getResponse(), SignupResponse.class);
		LoginRequest loginreq = new LoginRequest();
		loginreq.setToken(signupRes.getToken());
		ServiceResponse response = service.login(loginreq);
		assertFalse(response.isHasError());
		User usuario = userRepo.findByEmail(email).orElse(null);
		assertNotNull(usuario);
		assertNotNull(usuario.getUserId());
		assertThat(usuario.getName().equals(name));
		assertThat(usuario.getEmail().equals(email));
		assertNotNull(usuario.getPassword());
		assertNotNull(usuario.getCreated());
		assertNotNull(usuario.getLastLogin());
		assertTrue(usuario.isActive());
		for(int i = 0; i<phones.size();i++)
		{
			assertEquals(phones.get(i).getNumber(),usuario.getPhones().get(i).getNumber());
			assertEquals(phones.get(i).getCitycode(),usuario.getPhones().get(i).getCitycode());
			assertEquals(phones.get(i).getCountrycode(),usuario.getPhones().get(i).getCountrycode());
		}
	}

@Test
@Transactional
public void retorna_los_datos() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		String name = "Juan Perez";
		signupreq.setName(name);
		String email = "m@d.com";		
		signupreq.setEmail(email);
		String password = "aaa12Maaa";	
		signupreq.setPassword("aaa12Maaa");
		Phone phone1 = new Phone(Long.valueOf(1),2,"3");
		Phone phone2 = new Phone(Long.valueOf(3),4,"5");
		List<Phone> phones = new LinkedList<Phone>();
		phones.add(phone1);
		phones.add(phone2);
		signupreq.setPhones(phones);
		ServiceResponse signupResponse = service.signUp(signupreq);
		assertFalse(signupResponse.isHasError());
		ObjectMapper mapper = new ObjectMapper();
		SignupResponse signupRes = mapper.readValue(signupResponse.getResponse(), SignupResponse.class);
		LoginRequest loginreq = new LoginRequest();
		loginreq.setToken(signupRes.getToken());
		Thread.sleep(1000);
		ServiceResponse response = service.login(loginreq);
		assertNotNull(response);
		assertFalse(response.isHasError());
		assertNotNull(response.getResponse());
		assertThat(response.getResponse().length()>0);
		LoginResponse loginRes = mapper.readValue(response.getResponse(), LoginResponse.class);
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
		assertNotEquals(signupRes.getToken(), loginRes.getToken());
		assertTrue(loginRes.isActive());
		
	}
}
