jQuery(document).ready(function($) {

	let zoom = false

	$('#mayZoom').click(function (){
		if(zoom){
			$(this).removeClass('block-center img-zoom z-5')
			zoom = false
		} else {
			$(this).addClass('block-center img-zoom z-5')
			zoom = true
		}
	})

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

	if( window.location.toString() === "http://82.146.35.210:8080/" )
		$('#home-href').addClass("active")
	if( window.location.toString().includes("/ideas") )
		$('#ideas-href').addClass("active")
	if( window.location.toString().includes("/profile") )
		$('#user-href').addClass("active")
	if( window.location.toString().includes("/login?error"))
		$('#error').text("Неправильный email или пароль.")

	let body = $('body')

	body.on('submit', '.removeNotify', function (event) {
		event.preventDefault()

		let loc = $(this).attr('action')

		$.post( loc, function( data ) {
			$(".for-message").html($('.for-message', data).html());
		});
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
			$('.left-panel').animate({width: "280px"}, 100)
			$('body').toggleClass('open');
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
		if(window.location.toString().includes("registration") && windowWidth<=575){
			$('.registration-form').removeClass('block-center')
			$('.overlay-image').css('height', '135%')
		}

	});

	function truncateText(selector, maxLength) {
		$(selector).text((i, txt) => txt.length > maxLength ? txt.substr(0,maxLength) + "..." : txt);
	}

	truncateText(".preDescription", 200);

	body.on('click', '#addLike', function(event) {
		event.preventDefault()

		let id = $(this).data('id')

		let formData = {'flag': true}

		setLike("/ideas/setLike-" + id, formData, id)
	});

	body.on('click', '#remLike', function(event) {
		event.preventDefault()

		let id = $(this).data('id')

		let formData = {'flag': false}

		setLike("/ideas/setLike-" + id, formData, id)
	});

	function setLike(url, formData, id){
		$.ajax ({
			url: url,
			method: "POST",
			data: formData,
			dataType: 'html',
			success: function (fragment) {
				$("#idea-" + id).html($('#idea-' + id, fragment).html())
				console.log(fragment)
			},
			error: function(e) {
				console.log(e);
			}
		});
	}

	body.on('click', '.my-page-item', function () {
		$('.my-page-item').removeClass('active')

		$(this).addClass('active')
	})

	body.on('click', '.page-link', function (event) {
		event.preventDefault()

		let url = $(this).attr('href')

		$('.ideas').fadeOut('slow','linear', function() {
			let title = $('#valTitle').val()
			let status = $('#valStatus').val()
			let value = $('#valSort').val()
			let direction
			let property
			switch (value) {
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

			let formData = {
				"direction": direction,
				"property": property,
				"status": status,
				"title": title
			}

			$.ajax({
				url: url,
				method: "GET",
				data: formData,
				dataType: 'html',
				success: function (fragment) {
					$(".ideas").html($('.ideas', fragment).html()).ready(function () {
						$('.ideas').fadeIn('slow', 'linear')
					});
				},
				error: function (e) {
					console.log(e);
				}
			});
		});
	})

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

			let formData = {
				"direction": direction,
				"property": property,
				"status": status,
				"title": title
			}

			$.ajax ({
				url: window.location,
				method: "GET",
				data: formData,
				dataType: 'html',
				success: function (fragment) {
					$(".ideas").html($('.ideas', fragment).html()).ready(function () {
						$('.ideas').fadeIn('slow', 'linear')
					});
				},
				error: function(e) {
					console.log(e);
				}
			});
		});
	});

	$(document).ajaxSend(function(e, xhr, options) {
		let token = $("meta[name='_csrf']").attr("content");
		let header = $("meta[name='_csrf_header']").attr("content");

		xhr.setRequestHeader(header, token);
	});
});