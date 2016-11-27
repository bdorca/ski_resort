package management.lift;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="events")
@XmlAccessorType(XmlAccessType.FIELD)
public class Events {
	@XmlElement
	private float failure;
	@XmlElement
	private float add_people;

	public Events(float failure, float add_people) {
		super();
		this.failure = failure;
		this.add_people = add_people;
	}
	public Events(){
		
	}
	public float getFailure() {
		return failure;
	}
	public float getAdd_people() {
		return add_people;
	}

}
