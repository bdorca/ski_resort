window.onload = init;
var socket = new WebSocket("ws://localhost:8080/SkiServerWeb/lift");
socket.onmessage = onMessage;

function onMessage(event) {
	var lift = JSON.parse(event.data);
	if (lift.action === "add") {
		printLiftElement(lift);
	}
	if (lift.action === "remove") {
		document.getElementById(lift.id).remove();
		// lift.parentNode.removeChild(lift);
	}
	if (lift.action === "toggle") {
		var node = document.getElementById(lift.id);
		var statusText = node.children[2];
		if (lift.status === "On") {
			statusText.innerHTML = "Status: " + lift.status
					+ " (<a href=\"#\" OnClick=toggleLift(" + lift.id
					+ ")>Turn off</a>)";
		} else if (lift.status === "Off") {
			statusText.innerHTML = "Status: " + lift.status
					+ " (<a href=\"#\" OnClick=toggleLift(" + lift.id
					+ ")>Turn on</a>)";
		}
	}
	if (lift.action == "error") {
		console.log(lift.message);
	}
}

function addLift(name, type) {
	var LiftAction = {
		action : "add",
		data : {
			name : name,
			type : type
		}
	};
	socket.send(JSON.stringify(LiftAction));
}

function removeLift(element) {
	var id = element;
	var LiftAction = {
		action : "remove",
		id : id
	};
	socket.send(JSON.stringify(LiftAction));
}

function toggleLift(element) {
	var id = element;
	var LiftAction = {
		action : "toggle",
		id : id
	};
	socket.send(JSON.stringify(LiftAction));
}

function printLiftElement(lift) {
	var content = document.getElementById("content");

	var liftDiv = document.createElement("div");
	liftDiv.setAttribute("id", lift.id);
	liftDiv.setAttribute("class", "lift " + lift.type);
	content.appendChild(liftDiv);

	var liftName = document.createElement("span");
	liftName.setAttribute("class", "liftName");
	liftName.innerHTML = lift.name;
	liftDiv.appendChild(liftName);

	var liftType = document.createElement("span");
	liftType.innerHTML = "<b>Type:</b> " + lift.type;
	liftDiv.appendChild(liftType);

	var liftStatus = document.createElement("span");
	if (lift.status === "On") {
		liftStatus.innerHTML = "<b>Status:</b> " + lift.status
				+ " (<a href=\"#\" OnClick=toggleLift(" + lift.id
				+ ")>Turn off</a>)";
	} else if (lift.status === "Off") {
		liftStatus.innerHTML = "<b>Status:</b> " + lift.status
				+ " (<a href=\"#\" OnClick=toggleLift(" + lift.id
				+ ")>Turn on</a>)";
		// liftDiv.setAttribute("class", "lift off");
	}
	liftDiv.appendChild(liftStatus);

	var removeLift = document.createElement("span");
	removeLift.setAttribute("class", "removeLift");
	removeLift.innerHTML = "<a href=\"#\" OnClick=removeLift(" + lift.id
			+ ")>Remove lift</a>";
	liftDiv.appendChild(removeLift);
}

function showForm() {
	document.getElementById("addLiftForm").style.display = '';
}

function hideForm() {
	document.getElementById("addLiftForm").style.display = "none";
}

function formSubmit() {
	var form = document.getElementById("addLiftForm");
	var name = form.elements["lift_name"].value;
	var type = form.elements["lift_type"].value;
	hideForm();
	document.getElementById("addLiftForm").reset();
	addLift(name, type);
}

function init() {
	hideForm();
}