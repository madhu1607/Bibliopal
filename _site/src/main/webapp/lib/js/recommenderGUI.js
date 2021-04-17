// when build model button is clicked, it will first become
// unclickable in order to prevent multiple model being 
// generated. Then it will prompt that it is processing and
// when it is done, it will prompts again.
$('#build').click(function(event) {
	$('#build').prop("disabled", true);
	$('#recommend').prop("disabled", true);

	//$('#prompt').html("<p class=\"lead\">Loading model...</p>");
	$('#prompt').html("<img src=\"resources/ajax-loader.gif\">");

	$.get('servlet', {
		run : ''
	}, function(response) {
		$('#prompt').hide();
		$('#prompt').html(response);
		$('#prompt').slideDown();
		$('#recommend').prop("disabled", false);
	})

});

// bind enter key to recommend button.
$(document).keypress(function(e) {
	if (e.which == 13) {
		$('#recommend').click();
	}
});

// when recommend button is clicked, clear the prompt and
// write the results, including recommendations and appliaction
// history of the user.
$('#recommend').click(function(event) {
	//$('#prompt').html("<p class=\"lead\">Loading recommendations...</p>");
	$('#prompt').html("<img src=\"resources/ajax-loader.gif\">");

	var id = $('#userID').val();
	$.get('servlet', {
		userID : id
	}, function(response) {
		$('#prompt').hide();
		$('#prompt').html(response);
		$('#prompt').slideDown();
	})
});
