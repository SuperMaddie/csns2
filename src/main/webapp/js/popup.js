/**
 * 
 */
var ids = [];
var seenIds = [];

$(function() {
	getPopups(userId, ajaxUrl);
});


function getPopups(id, url) {
	if($.magnificPopup.instance.isOpen) {
		return;
	}
	seenIds = [];
	$.ajax({
		url : url,
		data : {},
		type : 'GET',
		success : function(data) {
			var count = JSON.parse(data).count;
			if(count > 0){
				$('#popup-title').html(count + " New Message" + (count>1?"s":""));
				
				var popups = $.map(JSON.parse(data).popups, function(el) { return el });
				//always add first popup
				seenIds.push(popups[0].id);
				
				for(var i = 0; i<count; i++) {
					ids.push(popups[i].id);
					$('#popup-ul').append("<li style=\"	overflow:auto;\"><div id=\"popup-subject\">"+popups[i].subject+"<br />"
							+ "<span id=\"popup-created-by\">Created by: " + popups[i].author + " " + popups[i].date +"</span></div><br />"
							+"<div id=\"popup-content\">" + popups[i].content+"</div></li>");
							//+"<div class=\"popup-footer\">" + (i + 1) + "/" + popups.length +"</div></li>");
				}
				
				$('#my-slideshow').bjqs({
					'width' : '100%',
					'showmarkers' : false,
					onNext : onNext,
					onPrev : onPrev
				});
				
				$.magnificPopup.open({
					type : 'inline',
					preloader : false,
					items: {
					      src: '#my-popup',
					      type: 'inline'
					 },
	
					callbacks : {
						beforeClose : function(event, ui) {
							beforeClose(url);
						}
					}
				});
				
				$('#my-slideshow').css('height', '');
			}
		}
	});
	//retrieve popups from server every hour
	setInterval( function(){ getPopups(userId, ajaxUrl); } , 60*60*1000 );
};

var onNext = function(pos) {
	seenIds.push(ids[(pos + 1) % ids.length]);
	seenIds = uniq(seenIds);
}

var onPrev = function(pos) {
	seenIds.push(ids[(pos == 0) ? ids.length-1 : pos-1]);
	seenIds = uniq(seenIds);
}

function beforeClose(url) {
	var seenPopups = "[";
	for(var i = 0; i<seenIds.length ; i++) {
		seenPopups += ("\"" + seenIds[i] + "\"");
		if(i < seenIds.length-1) {
			seenPopups += " ,";
		}
	}
	seenPopups += "]";
	$.ajax({
		url : url,
		data : {
			seenPopups : "{ \"seenPopups\" : "+ seenPopups +"}"
		},
		type : 'POST',
		success : function(data) {
			//console.log("seen success!");
		}
	});
}

function uniq(a) {
    var seen = {};
    return a.filter(function(item) {
        return seen.hasOwnProperty(item) ? false : (seen[item] = true);
    });
}