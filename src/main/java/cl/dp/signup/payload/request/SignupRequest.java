package cl.dp.signup.payload.request;

import java.util.List;

import javax.validation.constraints.*;

import cl.dp.signup.models.Phone;
 
public class SignupRequest {
    private String name;
 
    @Email
    private String email;
    
    private List<Phone> phones;
    
    private String password;
  
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
      return this.phones;
    }
    
    public void setPhones(List<Phone> phones) {
      this.phones = phones;
    }
}
