package cl.dp.signup.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import cl.dp.signup.payload.request.SignupRequest;
import cl.dp.signup.payload.response.ServiceResponse;
import cl.dp.signup.payload.response.SignupResponse;
import cl.dp.signup.repository.UserRepository;

@SpringBootTest
public class SignUpServiceTests {

	@Autowired
	private AuthService service;
	
	@Autowired
	private UserRepository userRepo;

	@Test
	public void vacio() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("Se debe incluir el mail"));
		assertTrue(response.getResponse().contains("Se debe incluir el password"));
	}
	
	@Test
	public void sin_email() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setPassword("aaa12Maaa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("Se debe incluir el mail"));
		assertFalse(response.getResponse().contains("Se debe incluir el password"));
	}

	@Test
	public void sin_password() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("a@a.com");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertFalse(response.getResponse().contains("Se debe incluir el mail"));
		assertTrue(response.getResponse().contains("Se debe incluir el password"));
	}

	@Test
	public void email_y_password() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("b@a.com");
		signupreq.setPassword("aaa12Maaa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isFalse();
		assertFalse(response.getResponse().contains("Se debe incluir el mail"));
		assertFalse(response.getResponse().contains("Se debe incluir el password"));
	}
	@Test
	public void nombre_repetido() throws Exception {

		String name = "nombre";
		SignupRequest signupreq1 = new SignupRequest();
		signupreq1.setName(name);
		signupreq1.setEmail("c@b.com");
		signupreq1.setPassword("aaa12Maaa");
		ServiceResponse response1 = service.signUp(signupreq1);
		assertThat(response1.isHasError()).isFalse();
		SignupRequest signupreq = new SignupRequest();
		signupreq.setName(name);
		signupreq.setEmail("c@a.com");
		signupreq.setPassword("aaa12Maaa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Nombre de usuario ya existe en la base de datos"));
	}

	@Test
	public void email_repetido() throws Exception {

		String email = "x@x.com";
		SignupRequest signupreq1 = new SignupRequest();
		signupreq1.setEmail(email);
		signupreq1.setPassword("aaa12Maaa");
		ServiceResponse response1 = service.signUp(signupreq1);
		assertThat(response1.isHasError()).isFalse();
		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail(email);
		signupreq.setPassword("aaa12Maaa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Email ya existe en la base de datos"));
	}
	@Test
	public void email_malformado() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("a");
		signupreq.setPassword("aaa12Maaa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Email debe tener el formato correcto"));
	}
	
	@Test
	public void password_menor_a_8() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("d@a.com");
		signupreq.setPassword("12Maa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Password debe contener entre 8 o a 12 caracteres"));
	}
	
	@Test
	public void password_mayor_a_12() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("e@a.com");
		signupreq.setPassword("aaaaaaaaaa12Maa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Password debe contener entre 8 o a 12 caracteres"));
	}

	@Test
	public void password_sin_mayuscula() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("f@a.com");
		signupreq.setPassword("aaa12aaaa");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Password debe contener precisamente una mayuscula"));
	}
	
	@Test
	public void password_con_mas_de_1_mayuscula() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("g@a.com");
		signupreq.setPassword("aaa12ZZZZZZ");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Password debe contener precisamente una mayuscula"));
	}
	
	@Test
	public void password_con_menos_de_2_digitos() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("h@a.com");
		signupreq.setPassword("aaa1aaaaZ");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Password debe contener precisamente dos digitos"));
	}
	
	@Test
	public void password_con_mas_de_2_digitos() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("i@a.com");
		signupreq.setPassword("0aaa1aaaaZ9");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Password debe contener precisamente dos digitos"));
	}
	
	@Test
	public void password_con_caracter_extrano() throws Exception {

		SignupRequest signupreq = new SignupRequest();
		signupreq.setEmail("j@a.com");
		signupreq.setPassword("0aa#a1aaaaZ9");
		ServiceResponse response = service.signUp(signupreq);
		assertThat(response.isHasError()).isTrue();
		assertTrue(response.getResponse().contains("El Password debe contener solo letras y numeros"));
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
		ServiceResponse response = service.signUp(signupreq);
		assertFalse(response.isHasError());
		User usuario = userRepo.findByEmail(email).orElse(null);
		assertNotNull(usuario);
		assertNotNull(usuario.getUserId());
		assertThat(usuario.getName().equals(name));
		assertThat(usuario.getEmail().equals(email));
		assertNotNull(usuario.getPassword());
		assertNotNull(usuario.getCreated());
		assertTrue(usuario.isActive());
		for(int i = 0; i<phones.size();i++)
		{
			assertEquals(phones.get(i).getNumber(),usuario.getPhones().get(i).getNumber());
			assertEquals(phones.get(i).getCitycode(),usuario.getPhones().get(i).getCitycode());
			assertEquals(phones.get(i).getCountrycode(),usuario.getPhones().get(i).getCountrycode());
		}
	}

@Test
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
		ServiceResponse response = service.signUp(signupreq);
		assertNotNull(response);
		assertFalse(response.isHasError());
		assertNotNull(response.getResponse());
		assertThat(response.getResponse().length()>0);
		ObjectMapper mapper = new ObjectMapper();
		SignupResponse signupRes = mapper.readValue(response.getResponse(), SignupResponse.class);
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
