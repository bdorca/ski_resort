package management.lift;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name="type")
public enum LiftType {
	small, medium, large, extra_large
}
