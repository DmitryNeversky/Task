jQuery(document).ready(function($) {

	"use strict";

	[].slice.call( document.querySelectorAll( 'select.cs-select' ) ).forEach( function(el) {
		new SelectFx(el);
	});

	jQuery('.selectpicker').selectpicker;

	$('.notify').on('click', function(event) {

		const formData = new FormData();
		formData.append("look", $('.notify').data("id"));

		$.ajax ({
			url: location,
			type: "POST",
			data: formData,
			dataType: "html",
			processData: false,
			contentType: false,
			success: function() {
				$(".notification-button").load(" .notification-counter");
			}
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

	// var chartsheight = $('.flotRealtime2').height();
	// $('.traffic-chart').css('height', chartsheight-122);


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
	$('#menuToggle').on('click', function(event) {
		var windowWidth = $(window).width();   		 
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
			var $temp_text = $(this).children('.dropdown-toggle').html();
			$(this).children('.sub-menu').prepend('<li class="subtitle">' + $temp_text + '</li>'); 
		});
	});


	// Load Resize 
	$(window).on("load resize", function(event) { 
		var windowWidth = $(window).width();  		 
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

	$( "#valStatus" ).change(function(event) {
		event.preventDefault()

		$('.ideas').fadeOut('slow','linear', function(){
			let status = $('#valStatus').val()
			let value = $('#valSort').val()
			let direction
			let property
			switch (value){
				case "Популярное":
					direction = "DESC"
					property = "likes"
					break
				case "Менее популярное":
					direction = "ASC"
					property = "likes"
					break
				case "Сначала свежее":
					direction = "DESC"
					property = "id"
					break
				case "Сначала старое":
					direction = "ASC"
					property = "id"
					break
				default:
					direction = "DESC"
					property = "id"
					break
			}

			const formData = new FormData();
			formData.append("direction", direction);
			formData.append("property", property);
			formData.append("status", status);

			send(formData, window.location, function(res) {
				$(".ideas").html($('.ideas', res).html()).ready(function (){
					$('.ideas').fadeIn('slow','linear')
				})
			})
		});
	});

	$( "#valSort" ).change(function(event) {
		event.preventDefault()

		$('.ideas').fadeOut('slow','linear', function(){
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