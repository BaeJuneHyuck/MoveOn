<?php
  $servername = "localhost";
  $username = "skh2929209";
  $password = "tlsrlgns1234";
  $database = "skh2929209";

  $conn = mysqli_connect($servername, $username, $password, $database);

  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $building = array();

  $sql = "SELECT * FROM BUILDING";

  $stmt = $conn->prepare($sql);

  $stmt->execute();

  $stmt->bind_result($id, $name, $latitude, $longitude, $toilet, $type, $report);

  $temp = array();

  while($stmt->fetch()){
    $utf8string = html_entity_decode(preg_replace("/U\+([0-9A-F]{4})/", "&#x\\1;", $string), ENT_NOQUOTES, 'UTF-8');

  	$temp['id']=$id;
  	$temp['name']=$name;
  	$temp['latitude']=$latitude;
  	$temp['longitude']=$longitude;
  	$temp['toilet']=$toilet;
  	$temp['type']=$type;
  	$temp['report']=$report;

  	array_push($building, $temp);
  }

  echo json_encode($building);
?>
