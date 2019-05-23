<?php
	$con = mysqli_connect("localhost", "skh2929209", "tlsrlgns1234", "skh2929209");

	$userId = $_POST["userId"];
	$userPassword = $_POST["userPassword"];
	$userNick = $_POST["userNick"];

	$statement = mysqli_prepare($con, "INSERT INTO USER(userId, userPassword, userNick) VALUES (?, ?, ?)");
	mysqli_stmt_bind_param($statement, "sss", $userId, $userPassword, $userNick);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;

	echo json_encode($response);
?>
