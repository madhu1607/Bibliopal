<%@ page language="java" pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html>
<html lang="en">
<title>Book Recommender</title>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Recommender</title>

<!-- Bootstrap -->
<link href="lib/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!--  Effects -->
<link href="lib/css/recommenderGUI.css" rel="stylesheet">

<!-- Spinner doesn't seem to be working? -->
<link href="lib/css/ladda-themeless.min.css" rel="stylesheet">
<script src="lib/js/spin.min.js"></script>
<script src="lib/js/ladda.min.js"></script>

</head>
<body class="center">
	<h1>BiblioPal - Book for You</h1>
	<img src="resources/mahout-logo-poweredby-100.png" alt="Mahout">
	<p></p>
	<br>
	<div>
		<form method="GET" action="servlet" class="form-inline">
			<button type="submit" id="build" name="run" value="Build Model"
				class="btn btn-primary ladda-button" data-style="expand-right">
				<span class="ladda-label">Build Model</span>
			</button>
			<p></p>
			and
			<p></p>
			<input type="text" class="form-control" placeholder="Input user ID"
				name="Submit" id="userID">
			<button type="button" id="recommend" value="Recommend"
				class="btn btn-primary">Recommend</button>

		</form>
	</div>
	</br>
	<div id="prompt" class="active" align="center"></div>

	<div id="history"></div>

	<div id="recommendation"></div>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<script src="lib/js/recommenderGUI.js">
		
	</script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="lib/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>