jQuery(document).ready(function($) {

	let oldAvatar = $('#previewAvatar').attr('src');

	function readURL(input) {

		if (input.files && input.files[0]) {
			let reader = new FileReader();

			reader.onload = function(e) {
				$('#previewAvatar').attr('src', e.target.result);
			}

			reader.readAsDataURL(input.files[0]); // convert to base64 string

			$('#cancelLoad').css('visibility', 'visible')
		}
	}

	$('#input-file').change(function() {
		readURL(this);
	});

	$('#cancelLoad').click(function () {
		$('#previewAvatar').attr('src', oldAvatar);
		$('#input-file').val(null)
		$(this).css('visibility', 'hidden')
	});

	if( window.location.toString() === "http://localhost:8080/" )
		$('#home-href').addClass("active")
	if( window.location.toString().includes("/ideas") )
		$('#ideas-href').addClass("active")
	if( window.location.toString().includes("/profile") )
		$('#user-href').addClass("active")
	if( window.location.toString().includes("/login?error"))
		$('#error').text("Неправильный email или пароль.")

	let body = $('body')

	$('.notify').on('click', function() {

		const formData = new FormData();
		formData.append("look", $('.notify').data("id"));

		send(formData, location, function (){
			$(".notification-button").load(" .notification-counter");
		})
	});

	body.on('submit', '.removeNotify', function (event) {
		event.preventDefault()

		let location = $(this).attr("action")

		send(null, location, function(res) {
			$(".for-message").html($('.for-message', res).html())
		});
	});

	$('.search-trigger').on('click', function(event) {
		event.preventDefault();
		event.stopPropagation();
		$('.search-trigger').parent('.header-left').addClass('open');
	});

	$('.search-close').on('click', function(event) {
		event.preventDefault();
		event.stopPropagation();
		$('.search-trigger').parent('.header-left').removeClass('open');
	});

	$('.equal-height').matchHeight({
		property: 'max-height'
	});

	// Counter Number
	$('.count').each(function () {
		$(this).prop('Counter',0).animate({
			Counter: $(this).text()
		}, {
			duration: 3000,
			easing: 'swing',
			step: function (now) {
				$(this).text(Math.ceil(now));
			}
		});
	});

	// Menu Trigger
	$('#menuToggle').on('click', function() {
		let windowWidth = $(window).width();
		if (windowWidth<1010) { 
			$('body').removeClass('open'); 
			if (windowWidth<760){ 
				$('#left-panel').slideToggle(); 
			} else {
				$('#left-panel').toggleClass('open-menu');  
			} 
		} else {
			$('body').toggleClass('open');
			$('#left-panel').removeClass('open-menu');  
		} 
			 
	}); 

	$(".menu-item-has-children.dropdown").each(function() {
		$(this).on('click', function() {
			let $temp_text = $(this).children('.dropdown-toggle').html();
			$(this).children('.sub-menu').prepend('<li class="subtitle">' + $temp_text + '</li>'); 
		});
	});

	// Load Resize 
	$(window).on("load resize", function() {
		let windowWidth = $(window).width();
		if (windowWidth<1010) {
			$('body').addClass('small-device'); 
		} else {
			$('body').removeClass('small-device');  
		} 
		
	});

	function truncateText(selector, maxLength) {
		$(selector).text((i, txt) => txt.length > maxLength ? txt.substr(0,maxLength) + "..." : txt);
	}

	truncateText(".preDescription", 200);

	body.on('submit', '#likeForm', function(event) {
		event.preventDefault()

		let id = $(this).attr("name");

		const formData = new FormData();
		formData.append("flag", 'true')

		send(formData, "/ideas/setLike-" + id, function(res) {
			$(".ideas").html($('.ideas', res).html()).ready(function () {
				$('.ideas').fadeIn('slow', 'linear')
			});
		});
	});

	body.on('submit', '#unLikeForm', function(event) {
		event.preventDefault()

		let id = $(this).attr("name");

		const formData = new FormData();
		formData.append("flag", 'false')

		send(formData, "/ideas/setLike-" + id, function(res) {
			$(".ideas").html($('.ideas', res).html()).ready(function () {
				$('.ideas').fadeIn('slow', 'linear')
			});
		});
	});

	$('#filterButton').click(function (event) {
		event.preventDefault()

		$('.ideas').fadeOut('slow','linear', function(){
			let title = $('#valTitle').val()
			let status = $('#valStatus').val()
			let value = $('#valSort').val()
			let direction
			let property
			switch (value){
				case "Популярное":
					direction = "DESC"
					property = "likeCount"
					break
				case "Менее популярное":
					direction = "ASC"
					property = "likeCount"
					break
				case "Сначала свежее":
					direction = "DESC"
					property = "id"
					break
				case "Сначала старое":
					direction = "ASC"
					property = "id"
					break
			}

			const formData = new FormData();
			formData.append("direction", direction);
			formData.append("property", property);
			formData.append("status", status);
			formData.append("title", title);

			send(formData, window.location, function(res) {
				$(".ideas").html($('.ideas', res).html()).ready(function (){
					$('.ideas').fadeIn('slow','linear')
				})
			})
		});
	});

	function send(formData, url, func){
		$.ajax ({
			url: url,
			type: "POST",
			data: formData,
			dataType: "html",
			processData: false,
			contentType: false,
			async: false,
			success: func
		});
	}
});