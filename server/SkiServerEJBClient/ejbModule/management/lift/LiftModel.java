package management.lift;


public class LiftModel{
	
	private String Id;
	
	private String name;
	
	private LiftType type;

	public String getId() {
		return Id;
	}

	public String getName() {
		return name;
	}

	public LiftModel(String id, String name, int size) {
		super();
		Id = id;
		this.name = name;
		this.type=LiftType.values()[size];
	}
	
	public LiftModel(){
		
	}

	public LiftType getType() {
		// TODO Auto-generated method stub
		return type;
	}
	
}