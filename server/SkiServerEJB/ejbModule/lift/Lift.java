package lift;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import management.lift.Events;
import management.lift.LiftType;
/*
            "id": self.id,
            "name": self.name,
            "type": self.type.name,
            "speed": self.speed,
            "customers": self.queue,
            "resource": self.resource,
            "consumption": self.consumption,
            "events": self.event_rate

 */

@Table(name = "Lift")
@Entity
@NamedQuery(name = "Lift.findAll", query = "SELECT a FROM Lift a")
public class Lift {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String liftId;
	private String name;
	private LiftType type;
	private float speed;
	private float customers;
	private float resource;
	private float consumption;
	private float failure;
	private float add_people;
	@ManyToOne()
	private LiftValue values;
	
	public Lift() {

	}

	public Lift(String liftId2) {
		liftId = liftId2;
	}

	public Lift(String liftId, String name, LiftType type) {
		this.liftId = liftId;
		this.name = name;
		this.type = type;
	}

	public Lift(String id2, String name2, LiftType type, float speed2, float customers2, float resource2,
			float consumption2, Events events) {
		liftId=id2;
		name=name2;
		type=type;
		speed=speed2;
		customers=customers2;
		resource=resource2;
		consumption=consumption2;
		failure=events.getFailure();
		add_people=events.getAdd_people();
	}

	public float getConsumption() {
		return consumption;
	}

	public float getCustomers() {
		return customers;
	}

	public Events getEvents() {
		Events e=new Events(failure, add_people);
		return e;
	}

	public int getId() {
		return id;
	}

	public String getLiftId() {
		return liftId;
	}

	public String getName() {
		return name;
	}

	public float getResource() {
		return resource;
	}

	public LiftType getType() {
		return type;
	}

	public float getSpeed() {
		return speed;
	}

	public void setConsumption(float consumption) {
		this.consumption = consumption;
	}

	public void setCustomers(float customers) {
		this.customers = customers;
	}

	public void setEvents(Map<String, Float> events) {

		failure = events.get("failure");
		add_people = events.get("add_people");
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResource(float resource) {
		this.resource = resource;
	}

	public void settype(LiftType type2) {
		type = type2;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setData(String name2, LiftType type, float speed2, float customers2, float resource2,
			float consumption2, Events events) {
		name=name2;
		this.type=type;
		speed=speed2;
		customers=customers2;
		resource=resource2;
		consumption=consumption2;
		failure=events.getFailure();
		add_people=events.getAdd_people();		
	}

	public LiftValue getValues() {
		return values;
	}

	public void setValues(LiftValue values) {
		this.values=values;
	}

}
