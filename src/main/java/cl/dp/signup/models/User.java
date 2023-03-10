package cl.dp.signup.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
    })

public class User {
	  @Id
	  @Type(type = "uuid-char")
	  UUID userId = UUID.randomUUID();
	
	  private String name;

	  private String email;

	  private String password;
	  
	  @OneToMany(mappedBy="phoneuser", fetch=FetchType.LAZY, cascade = {CascadeType.ALL})
	  private List<Phone> phones; 

	  private LocalDateTime created;
	  
	  private LocalDateTime lastLogin;
	  
	  private boolean isActive;
	  
	  public User() {
	  }

	  public User(String name, String email, String password) {
	    this.name = name;
	    this.email = email;
	    this.password = password;
	  }

	  public UUID getUserId() {
	    return userId;
	  }

	  public void setId(UUID userId) {
	    this.userId = userId;
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

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
