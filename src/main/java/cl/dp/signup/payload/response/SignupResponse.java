package cl.dp.signup.payload.response;

import java.util.List;
import java.util.UUID;

import cl.dp.signup.models.Phone;


public class SignupResponse {
		private String name;
		private String email;
    	private String password; 
		private List<Phone> phones;
		private UUID id;
		private String created;
		private String lastLogin;
		private String token;
		private boolean isActive;
		
		public UUID getId() {
			return id;
		}
		public void setId(UUID id) {
			this.id = id;
		}
		public String getCreated() {
			return created;
		}
		public void setCreated(String created) {
			this.created = created;
		}
		public String getLastLogin() {
			return lastLogin;
		}
		public void setLastLogin(String lastLogin) {
			this.lastLogin = lastLogin;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public boolean isActive() {
			return isActive;
		}
		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public List<Phone> getPhones() {
			return phones;
		}
		public void setPhones(List<Phone> phones) {
			this.phones = phones;
		}
		public SignupResponse()
		{}
		
		public SignupResponse(String name, String email, String password, List<Phone> phones, 
				UUID id, String created, String lastLogin, String token, boolean isActive)
		{
			this.name = name;
			this.email = email;
			this.password = password; 
			this.phones = phones;
			this.id = id;
			this.created = created;
			this.lastLogin = lastLogin;
			this.token = token;
			this.isActive = isActive;
		}


}
