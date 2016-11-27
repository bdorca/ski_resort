package management.lift;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name="command")
public enum Command {
	speed, change_speed, resource, increased_pop, decreased_pop, customer, report, online, offline, exit, id, name
}
