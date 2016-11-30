package user;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class UserData {

	@Id
	private String username;
	private String password;
	private String token;
	
	
	private long validUntil;
	UserData(){}
	
	UserData(String username, String password){
		this.username=username;
		this.password=password;
		
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(long validUntil) {
		this.validUntil = validUntil;
	}
	
	
}
