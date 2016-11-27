package management.lift;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="lifts")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfLifts {
	@XmlElement()
	private Map<String,LiftModel> lifts;

	public ListOfLifts(List<LiftModel> list) {
		super();
		this.lifts = new HashMap<>();
		for(LiftModel l:list){
			if(l.getType()!=null)
				lifts.put(l.getType().toString(), l);
		}
	}
	
	public ListOfLifts(){
		
	}
}
