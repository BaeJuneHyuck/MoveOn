<?php
	$con = mysqli_connect("localhost", "skh2929209", "tlsrlgns1234", "skh2929209");

	$name = $_POST["name"];
	$latitude = $_POST["latitude"];
	$longitude = $_POST["longitude"];
	$toilet = $_POST["toilet"];
  $type = $_POST["type"];
	$report = $_POST["report"];

	$statement = mysqli_prepare($con, "INSERT INTO BUILDING(name, latitude, longitude, toilet, type, report) VALUES (?, ?, ?, ?, ?, ?)");
	mysqli_stmt_bind_param($statement, "sddiii", $name, $latitude, $longitude, $toilet, $type, $report);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;

	echo json_encode($response);
?>
