<?php

	$dbh = 'mysql:host=localhost:8080; dbname=fourtothefloor';
	$username = 'root';
	$password = '';

	try {
		$pdo = new PDO($dbh,$username,$password);
	} catch (Exception $e) {
		echo "Connection Error";
		die();
	}
?>