package edu.uclm.esi.fakeaccountsbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity @Table(name = "usuario")
public class User {
	@Id @Column(length = 60)
	private String email;
	private String pwd;
	
	@JsonIgnore @Column(length = 36) //Significa que cuando se recoge la informacion que no viaje al cliente
	private String token;
	
	@JsonIgnore
	private boolean premium;
	
	@JsonIgnore @Transient //para que no se guarde en la base de datos
	private long creationTime;
	
	@Transient //@JsonIgnore 
	private String ip;
	
	@Column(length = 36)
	private String cookie;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = org.apache.commons.codec.digest.DigestUtils.sha512Hex(pwd);;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public boolean getPremium() {
		return premium;
	}
	
	public void setPremium(Boolean premium) {
		this.premium = premium;
	}
	
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;		
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setCookie(String id) {
		this.cookie = id;
	}
	
	public String getCookie() {
		return this.cookie;
	}
}
