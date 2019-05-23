<?php
	$con = mysqli_connect("localhost", "skh2929209", "tlsrlgns1234", "skh2929209");

	$userId = $_POST["userId"];
	$userPassword = $_POST["userPassword"];

	$statement = mysqli_prepare($con, "SELECT * FROM USER WHERE userId = ? AND userPassword = ?");

	mysqli_stmt_bind_param($statement, "ss", $userId, $userPassword);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $num, $userId, $userPassword, $userNick);

	$response = array();
	$response["success"] = false;

	while(mysqli_stmt_fetch($statement)){
		$response["success"] = true;
		$response["num"] = $num;
		$response["userId"] = $userId;
		$response["userPassword"] = $userPassword;
		$response["userNick"] = $userNick;
	}

	echo json_encode($response);
?>
