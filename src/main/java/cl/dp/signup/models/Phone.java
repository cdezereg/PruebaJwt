package cl.dp.signup.models;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "phones")
public class Phone {
	@Id
	@Type(type = "uuid-char")
	@JsonIgnore
	UUID id = UUID.randomUUID();
	  
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
	@JoinColumn(name="phoneuser", referencedColumnName = "userId", nullable = false)
	@JsonIgnore
	private User phoneuser;

	private Long number;
	private Integer citycode;
	private String countrycode;

	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public User getPhoneuser() {
		return phoneuser;
	}
	public void setPhoneuser(User phoneuser) {
		this.phoneuser = phoneuser;
	}
	public Long getNumber() {
		return number;
	}
	public void setNumber(Long number) {
		this.number = number;
	}
	public Integer getCitycode() {
		return citycode;
	}
	public void setCitycode(Integer citycode) {
		this.citycode = citycode;
	}
	public String getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(String contrycode) {
		this.countrycode = contrycode;
	}
	public Phone () {
	}
	
	public Phone (Long number, Integer citycode, String countrycode) {
		this.number = number;
		this.citycode = citycode;
		this.countrycode = countrycode;
	}
}
