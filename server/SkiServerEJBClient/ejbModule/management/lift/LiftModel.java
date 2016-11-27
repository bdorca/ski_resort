package management.lift;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="lift")
@XmlAccessorType(XmlAccessType.FIELD)
public class LiftModel{
	@XmlElement
	private String id;

	@XmlElement
	private String name;

	@XmlElement
	private LiftType type;

	@XmlElement
	private float speed;

	@XmlElement
    private float customers;

	@XmlElement
    private float resource;
	@XmlElement
    private float consumption;
	@XmlElement
    private Events events;
	public LiftModel(){		
	}
	public LiftModel(String id, String name, LiftType type, float speed, float customers, float resource,
			float consumption, Events events) {
		super();
		this.id = id;
		this.name = name;
		this.type=type;
		this.speed = speed;
		this.customers = customers;
		this.resource = resource;
		this.consumption = consumption;
		this.events = events;
	}
	public float getConsumption() {
		return consumption;
	}
	public float getCustomers() {
		return customers;
	}
	public Events getEvents() {
		return events;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public float getResource() {
		return resource;
	}

	public float getSpeed() {
		return speed;
	}

	public LiftType getType() {
		return type;
	}
	
	

	
}