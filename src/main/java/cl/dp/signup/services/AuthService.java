package cl.dp.signup.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.dp.signup.models.Phone;
import cl.dp.signup.models.User;
import cl.dp.signup.payload.request.LoginRequest;
import cl.dp.signup.payload.request.SignupRequest;
import cl.dp.signup.payload.response.ErrorResponse;
import cl.dp.signup.payload.response.LoginResponse;
import cl.dp.signup.payload.response.ServiceResponse;
import cl.dp.signup.payload.response.SignupResponse;
import cl.dp.signup.payload.response.ErrorMessage;
import cl.dp.signup.repository.UserRepository;
import cl.dp.signup.security.jwt.JwtUtils;

@Service
public class AuthService {
	
	@Autowired
	UserRepository userRepository;
		
	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");
	
	public ServiceResponse signUp(SignupRequest signUpRequest) throws JsonProcessingException{
		List<ErrorMessage> errorList = new LinkedList<ErrorMessage>();
		String response="";
		boolean hasError = false;
		//Validaciones de nombre de usuario
		if (signUpRequest.getName()!=null &&userRepository.existsByName(signUpRequest.getName())) {
			errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()),400, "El Nombre de usuario ya existe en la base de datos."));
		}
		//Validaciones de email
		if (signUpRequest.getEmail()==null)
		{
			errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 400, "Se debe incluir el mail."));			
		}
		else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
				errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()),400, "El Email ya existe en la base de datos."));
		}
		//Validaciones de formato de email
		else if (!Pattern.compile("^(.+)@(\\S+)$").matcher(signUpRequest.getEmail()).matches()) {
			errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 400, "El Email debe tener el formato correcto."));
		}		
		//Validaciones de Password
		if (signUpRequest.getPassword()==null)
		{
			errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 400, "\"Se debe incluir el password."));			
		}
		else
		{
			//Validaciones de formato de password
			if (!Pattern.compile("^.{8,12}$").matcher(signUpRequest.getPassword()).matches()) {
				errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 400, "El Password debe contener entre 8 o a 12 caracteres."));
			}		
			if (!Pattern.compile("^[0-9a-zA-Z]+$").matcher(signUpRequest.getPassword()).matches()) {
				errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 400, "El Password debe contener solo letras y numeros."));
			}		
			if (!Pattern.compile("^[a-z0-9]*[A-Z][a-z0-9]*$").matcher(signUpRequest.getPassword()).matches()) {
				errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 400, "El Password debe contener precisamente una mayuscula."));
			}		
			if (!Pattern.compile("^[a-zA-Z]*[0-9][a-zaA-Z]*[0-9][a-zaA-Z]*$").matcher(signUpRequest.getPassword()).matches()) {
				errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 400, "El Password debe contener precisamente dos digitos."));
			}
		}
		//Si no encontró errores comienza la insercion
		if (errorList.size()==0)
		{
			User user = new User(signUpRequest.getName(), 
								 signUpRequest.getEmail(),
								 encoder.encode(signUpRequest.getPassword()));
	
			List<Phone> strPhones = signUpRequest.getPhones();		
			user.setActive(true);
			user.setCreated(LocalDateTime.now());
			if (strPhones!=null)
			{
				for(Phone phone: strPhones)
				{
					phone.setPhoneuser(user);
				}
			}
			user.setPhones(strPhones);
			//Inserta los objetos en la base
			userRepository.save(user);
			try {
				//Valida que el objeto se haya creado correctamente
				User dBUser = userRepository.findByUserId(user.getUserId())
					.orElseThrow(()->new NotFoundException());

				//Crea el token con el UserId y el password			
				String jwt = jwtUtils.generateJwtToken(user.getUserId().toString(),signUpRequest.getPassword());
				ObjectMapper mapper = new ObjectMapper();
				response = mapper.writeValueAsString(new SignupResponse(signUpRequest.getName(), signUpRequest.getEmail(), 
						signUpRequest.getPassword(), signUpRequest.getPhones(),	dBUser.getUserId(),	
						customFormatter.format(dBUser.getCreated()), null, jwt, dBUser.isActive()));
			} catch (NotFoundException x)
			{			
				errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 500 , "Error Interno"));
			}
		}
		if (!errorList.isEmpty())
		{
			//Si existen errores retorna el objeto de errores
			ErrorResponse error = new ErrorResponse();
			error.setError(errorList);
			ObjectMapper mapper = new ObjectMapper();
			response = mapper.writeValueAsString(error);
			hasError = true;
		}
		//Si no existen errores retorna el objeto de respuesta
		return new ServiceResponse(response,hasError);
	}

	public ServiceResponse login(LoginRequest loginRequest) throws JsonProcessingException{
		List<ErrorMessage> errorList = new LinkedList<ErrorMessage>();
		String response="";
		boolean hasError = false;

		//Valida que el token venga bien formado
		if (loginRequest.getToken() != null && jwtUtils.validateJwtToken(loginRequest.getToken())) {

			//Obtiene el userId y el password del Token
			String pair = jwtUtils.getCoordFromJwtToken(loginRequest.getToken());
			int sep = pair.indexOf(":");
			String userId = pair.substring(0,sep);
			String password = pair.substring(sep+1);
			try {
				//Va a buscar el usuario a la base de datos
				User dBUser = userRepository.findByUserId(UUID.fromString(userId)).orElseThrow(() -> new NotFoundException());
				//Actualiza lastLogin en la base de datos
				dBUser.setLastLogin(LocalDateTime.now());
				userRepository.save(dBUser);
				//Genera un nuevo token
				String jwt = jwtUtils.generateJwtToken(userId,password);
				ObjectMapper mapper = new ObjectMapper();
				
				response = mapper.writeValueAsString(new LoginResponse(dBUser.getUserId(),customFormatter.format(dBUser.getCreated()),customFormatter.format(dBUser.getLastLogin()), jwt,
						dBUser.isActive(),dBUser.getName(), dBUser.getEmail(), password, dBUser.getPhones()));
			} catch (NotFoundException x)
			{			
				errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()), 500 , "Error Interno"));
			}
		}
		else 
		{
			//Si el token no es correcto genera un error
			errorList.add(new ErrorMessage(customFormatter.format(LocalDateTime.now()),400, "El token se encuentra malformado o expirado"));			
		}
		if (!errorList.isEmpty())
		{
			//Si existen errores retorna el objeto de errores
			ErrorResponse error = new ErrorResponse();
			error.setError(errorList);
			ObjectMapper mapper = new ObjectMapper();
			response = mapper.writeValueAsString(error);
			hasError = true;
		}
		//Si no existen errores retorna el objeto de respuesta
		return new ServiceResponse(response, hasError);

	}

}
