package management.lift;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="lifts")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfLifts {
	@XmlElement(name="lift")
	private List<LiftModel> lifts;

	public ListOfLifts(List<LiftModel> lifts) {
		super();
		this.lifts = lifts;
	}
	
	public ListOfLifts(){
		
	}
}
