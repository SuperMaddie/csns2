<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script>
$(function(){
	
	$("#loading").hide();
	
    $( "#accordion" ).accordion({
    	heightStyle: 'panel', 
    	collapsible: true
	});
    
    $("#tabs").tabs({
        cache: false
    });
    $("table").tablesorter({sortList: [[1, 0],[2,0]]});
    $("select[name='term'] option").each(function(){
       if( $(this).val() == ${term.code}) 
           $(this).attr('selected', true);
    });
    $("select[name='term']").change(function(){
        var term = $("select[name='term'] option:selected").val();
        window.location.href = "preRegistration?term=" + term;
    });
    
	$("#student-form").submit(function(event){
	    if( $("#student-form").find(":checkbox[name='sectionId']:checked").length  > 0 ){
	    	$("#loading").show();
	    	$("#submit").prop("disabled", true); 
	    }
	});
    try{
    	$("textarea").each(function(){
        	CKEDITOR.replace( $(this).attr("id"), {
          		toolbar : "Basic",
          		width : "70%"
        	});
    	});
    }catch(ex){
    }
    
    //set ids
    var ids = JSON.parse($("#student-form").attr("ids"));
    ids.forEach(function(entry){
    	$("#student-form input[type=checkbox]").each(function(){
    		if(parseInt($(this).attr("value")) == entry){
    			selectCourse( $(this), true);
    		}
    	});
    });
    
    $("#student-form input[type=checkbox]").click(function(){
    	var thisObj = $(this);
    	var limit = parseInt($("#student-form").attr("limit"));
    	
    	selectCourse( $(this), $(this).is(":checked") );
    	
    	//check or uncheck links    	
    	var links = JSON.parse(thisObj.attr("links"));
    	if(links.length > 0){
	  		if(links.length > 1 && !thisObj.is(":checked")){
	  			links.forEach(function(id){
	  				if($("#student-form input[type=checkbox][value='" + id + "']").is(":checked"))
						selectCourse( $("#student-form input[type=checkbox][value='" + id + "']"), false );
	 			});
	  		}else if(links.length > 1 && thisObj.is(":checked") && !$("#student-form input[type=checkbox][value='" + links[0] + "']").is(":checked")){
	  			selectCourse( $("#student-form input[type=checkbox][value='" + links[0] + "']"), true );
	  		}else if(links.length == 1 && thisObj.is(":checked") && !$("#student-form input[type=checkbox][value='" + links[0] + "']").is(":checked")){
	  			selectCourse( $("#student-form input[type=checkbox][value='" + links[0] + "']"), true);
	  		}else if(links.length == 1 && !thisObj.is(":checked") && $("#student-form input[type=checkbox][value='" + links[0] + "']").is(":checked")){
	  			selectCourse( $("#student-form input[type=checkbox][value='" + links[0] + "']"), false);
	  		}
	  		
	  		//handle checkbox for multiple lab/rec
	  		var parent = $("#student-form input[type=checkbox][value='" + links[0] + "']");
	  		var parentLinks = JSON.parse(parent.attr("links"));
	  		if(parentLinks.length > 1){
	 			parentLinks.forEach(function(id){
	 				//if this is checked, uncheck the other labs
	 				var checkbox = $("#student-form input[type=checkbox][value='" + id + "']");
	 				if(thisObj.attr("value") == id);
	 				else if(thisObj.is(":checked") && checkbox.is(":checked")){
	 					selectCourse( checkbox, false);
	  				}
	 			});
	  		}
    	}
    	
    });
});


function selectCourse( checkbox, selected ){
	if( selected ) {
		var clone = checkbox.closest('tr').clone();
		var removeButton = $('#remove-template').children().first().clone();

		clone.find('td:first').html( removeButton );
		clone.prop('id', 'selected-' + checkbox.val());
		
		removeButton.click(function(){
			checkbox.click();
		});
		
		$('#selected-courses').append( clone );
		console.log(clone);
		
		/* Disable all equivalent sections */
		var equivalents = JSON.parse(checkbox.attr("equivalents"));
		if(equivalents.length > 0){
			equivalents.forEach(function(id){
				var eqCheckbox = $("#student-form input[type=checkbox][value='" + id + "']");
				console.log($("#student-form input[type=checkbox][value='" + id + "']").val());
				eqCheckbox.attr("disabled", true);
			});
		}
	} else {
		$('#selected-' + checkbox.val()).remove();
		/* Enable all equivalent sections */
		var equivalents = JSON.parse(checkbox.attr("equivalents"));
		if(equivalents.length > 0){
			equivalents.forEach(function(id){
				var eqCheckbox = $("#student-form input[type=checkbox][value='" + id + "']");
				eqCheckbox.removeAttr("disabled");
			});
		}
	}
	checkbox.prop( "checked", selected );
}

</script>

<ul id="title">
<li>Offered Sections</li>

<c:if test="${fn:length(terms) > 0}">
<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="t" items="${terms}"><option value="${t.code}">${t}</option></c:forEach>
  </select>
</li>
</c:if>
</ul>

<c:if test="${fn:length(terms) == 0}">
	<p>Currently there are no schedules available.</p>
</c:if>

<c:if test="${fn:length(terms) > 0}">
<c:choose>
<c:when test="${schedule.closed}">
	<p>Past Due Date</p>
</c:when>

<c:otherwise>
<c:if test="${fn:length(schedule.sections) == 0 or not schedule.published}">
	<p>Currently there are no offered sections in <b>${term}</b>.</p>
</c:if>


<c:if test="${fn:length(schedule.sections) > 0 and schedule.published}">

<c:if test="${not empty publishDateFormatted or not empty expireDateFormatted }">
<div class="dates ui-widget">
	<div class="bordered left-date" id="pubDate">Published at:  ${publishDateFormatted}</div>
	<div class="bordered right-date" id="expireDate">Expires at:  ${expireDateFormatted}</div>
</div>
</c:if>

<form id="student-form" action="<c:url value='/department/${dept}/preRegistration/edit?term=${term.code}&studentId=${user.id}'/>" 
	method="post" ids="${ids}">
<div id=accordion>
	<h3>Selected Courses</h3>
	<div>
		<table class="viewtable small-font">
			<thead>
			<tr>
			  <th></th><th>Code</th><th>Section</th><th>Name</th><th>Type</th><th>Units</th><th>Instructor</th>
			  <th>Location</th><th>Time</th><th>Open</th>
			</tr>
			</thead>
			<tbody id="selected-courses">
			</tbody>
		</table>
	</div><!-- end of selected courses -->
	
	<h3>Undergraduate Courses</h3>
	<div>
		<table class="viewtable small-font">
			<thead>
			<tr>
			  <th></th><th>Code</th><th>Section</th><th>Name</th><th>Type</th><th>Units</th><th>Instructor</th>
			  <th>Location</th><th>Time</th><th>Open</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${schedule.sections}" var="section">
			<c:if test="${ not section.isGraduate()}">
			<tr>
			  <td class="center"><input type="checkbox" name="sectionId" value="${section.id}" 
			  	links="${section.linkedSectionIds}" equivalents="${section.equivalentSectionIds}" 
			  	units="${section.units}" sectionType="${section.type}" /></td>
			  <td>
			    ${section.subject} ${section.courseCode}
			  </td>
			  <td>
			  	${section.number}
			  </td>
			  <td>
				${section.sectionTitle}
			  </td>
			  <td>
			  	${section.type}
			  </td>
			  <td>
			  	${section.units}
		  	  </td>
			  <td>
				${section.instructors[0].name}
			  </td>
			  <td>
				${section.location}
			  </td>
			  <td>
				${section.day} ${section.startTime}
					<c:if test="${not empty section.startTime}"> - </c:if>${section.endTime}
			  </td>
			  <td>${section.capacity - section.requests.size()}</td>
			</tr>
			</c:if>
			</c:forEach>
			</tbody>
		</table>
	</div><!-- end of undergraduate -->
	
	<h3>Graduate Courses</h3>
	<div>
	<table class="viewtable small-font">
		<thead>
		<tr>
		  <th></th><th>Code</th><th>Section</th><th>Name</th><th>Type</th><th>Units</th><th>Instructor</th>
		  <th>Location</th><th>Time</th><th>Open</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${schedule.sections}" var="section">
		<c:if test="${ section.isGraduate()}">
		<tr>
		  <td class="center"><input type="checkbox" name="sectionId" value="${section.id}" 
		  	links="${section.linkedSectionIds}" units="${section.units}" sectionType="${section.type}"/></td>
		  <td>
		    ${section.subject} ${section.courseCode}
		  </td>
		  <td>
		  	${section.number}
		  </td>
		  <td>
			${section.sectionTitle}
		  </td>
		  <td>
		  	${section.type}
		  </td>
		  <td>
		  	${section.units}
	  	  </td>
		  <td>
			${section.instructors[0].name}
		  </td>
		  <td>
			${section.location}
		  </td>
		  <td>
			${section.day} ${section.startTime}
				<c:if test="${not empty section.startTime}"> - </c:if>${section.endTime}
		  </td>
		  <td>${section.capacity - section.requests.size()}</td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table>
	</div><!-- end of graduate -->
</div>
<br><input type="submit" id="submit" value="Submit" class="subbutton">
</form>

</c:if>
</c:otherwise>
</c:choose>

</c:if><!-- End of not empty schedule -->

<div id="loading" class="loading">
	<img src="<c:url value='/img/style/loading.gif' />">
</div>

<div id="remove-template" style="display: none;">
<a href="javascript:void(0)">
	<img src="<c:url value='/img/icons/delete.png' />" alt="Remove" title="Remove" />
</a>
</div>