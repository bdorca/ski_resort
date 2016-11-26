package lift;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import management.lift.LiftType;


@Table(name="Lift")
@Entity
@NamedQuery(name="Lift.findAll", query="SELECT a FROM Lift a")
public class Lift {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String liftId;
	
	private String name;
	
	private int size;
	
	
	
	public Lift(String liftId, String name, int size) {
		super();
		this.liftId = liftId;
		this.name = name;
		this.size = size;
	}

	public Lift(){
		
	}
	
	public Lift(String liftId2) {
		liftId=liftId2;
	}

	public Lift(String liftId, String name, LiftType type) {
		this.liftId = liftId;
		this.name = name;
		this.size = type.ordinal();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}
	
	public String getLiftId() {
		return liftId;
	}

	public void setName(String name) {
		this.name = name;
	}


	public void setSize(int size2) {
		size=size2;
	}
}
