package user;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDataModel {
	@XmlElement
	String password;
	@XmlElement
	String username;
	public String getUserName() {
		// TODO Auto-generated method stub
		return username;
	}
	public String getPassword(){
		return password;
	}
}
