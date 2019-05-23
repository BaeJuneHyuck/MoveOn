<?php
  $servername = "localhost";
  $username = "skh2929209";
  $password = "tlsrlgns1234";
  $database = "skh2929209";

  $id = $_POST["id"];
  $mysqli = mysqli_connect($servername, $username, $password, $database);

  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  $query = "UPDATE BUILDING SET report = report + 1 WHERE id = '".$id."';";
  $query .= "DELETE FROM BUILDING WHERE report >= 5";

  /* execute multi query */
  if ($mysqli->multi_query($query)) {
      do {
          /* store first result set */
          $stmt = $mysqli->prepare($query);

          $stmt->execute();
      } while ($mysqli->next_result());
  }

  /* close connection */
  $mysqli->close();


?>
