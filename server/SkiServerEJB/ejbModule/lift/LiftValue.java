package lift;

import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import management.lift.LiftType;

@Table(name = "LiftValue")
@Entity
@NamedQuery(name = "LiftValue.findAll", query = "SELECT a FROM LiftValue a")
public class LiftValue {
	@Min(0)
	@Max(3)
	@Id
	LiftType type;
	float minResource;
	float maxResource;
	String name;
	
	public float getInitResource() {
		return new Random().nextFloat()*(maxResource-minResource)+minResource;
	}

	public LiftType getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	
}
