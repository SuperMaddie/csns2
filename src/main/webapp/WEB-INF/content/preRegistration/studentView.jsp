<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script>
$(function(){
    $( "#accordion" ).accordion({
    	heightStyle: 'panel', 
    	collapsible: true
	});
    $("#tabs").tabs({
        cache: false
    });
    $("table").tablesorter({sortList: [[1, 0]]});
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
	    	$("#student-form").submit();
	    	$("#submit").prop("disabled", true); 
	    }
	    else{
	    	event.preventDefault();
			alert("You did not select any sections.");
			return false;
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
    			$(this).prop("checked", true);
    		}
    	});
    });
    
    $("#student-form input[type=checkbox]").click(function(){
    	var thisObj = $(this);
    	var limit = parseInt($("#student-form").attr("limit"));

    	//check limit for number of units
    	var checked = 0;
    	if(thisObj.is(":checked")){
    		$("#student-form input[type=checkbox]").each(function(){
    			if($(this).is(":checked")){
    				if($(this).attr("sectionType").toUpperCase() == "LAB" 
    						|| $(this).attr("sectionType").toUpperCase() == "REC");
    				else
    					checked += parseInt($(this).attr("units"));
    			}
    		});
    		
    		if(checked > limit){
    			thisObj.prop("checked", false);
    			notify("limit");
    		}
    	}
    	
    	//check/uncheck links    	
    	var links = JSON.parse(thisObj.attr("links"));
    	if(links.length > 0){
	  		if(links.length > 1 && $(this).not(":checked")){
	  			links.forEach(function(id){
	 				//if lec is unchecked, uncheck all labs
					$("#student-form input[type=checkbox][value='" + id + "']").prop("checked", false);
	 			});
	  		}
	  		
	  		$("#student-form input[type=checkbox][value='" + links[0] + "']")
				.prop("checked", $(this).is(":checked"));
			
	  		
	  		//handle checkbox for multiple lab/rec
	  		var parent = $("#student-form input[type=checkbox][value='" + links[0] + "']");
	  		var parentLinks = JSON.parse(parent.attr("links"));
	  		if(parentLinks.length > 1){
	 			parentLinks.forEach(function(id){
	 				//if this is checked, uncheck the other labs
	 				if(thisObj.attr("value") == id);
	 				else if($("#student-form input[type=checkbox][value='" + id + "']").is(":checked")
	 						&& thisObj.is(":checked")){
	 					$("#student-form input[type=checkbox][value='" + id + "']").prop("checked", false);
	  				}
	 			});
	  		}
    	}
    	
    });
    
    $("div.notify").dialog({
        autoOpen: false,
        modal: true
    });
});

function notify(name){
	$("#notify-" + name).dialog("open");
}

</script>

<ul id="title">
<li>Offered Sections</li>

<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="t" items="${terms}"><option value="${t.code}">${t}</option></c:forEach>
  </select>
</li>
</ul>

<c:choose>
<c:when test="${schedule.closed}">
	<p>Past Due Date</p>
</c:when>

<c:otherwise>
<c:if test="${fn:length(schedule.sections) == 0 or not schedule.published}">
	<p>Currently there are no offered sections in <b>${term}</b>.</p>
</c:if>


<c:if test="${fn:length(schedule.sections) > 0 and schedule.published}">

<form id="student-form" action="<c:url value='/department/${dept}/preRegistration?term=${term.code}'/>" 
	method="post" limit="${limit}" ids="${ids}">
<div id=accordion>
	<h3>Undergraduate Courses</h3>
	<div>
		<table class="viewtable small-font">
			<thead>
			<tr>
			  <th></th><th>Code</th><th>Name</th><th>Units</th><th>Instructor</th>
			  <th>Location</th><th>Time</th><th>Open</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${schedule.sections}" var="section">
			<c:if test="${ not section.isGraduate()}">
			<tr>
			  <td class="center"><input type="checkbox" name="sectionId" value="${section.id}" 
			  	links="${section.linkedSectionIds}" units="${section.units}" sectionType="${section.type}" /></td>
			  <td>
			    ${section.subject} ${section.courseCode}
			    <c:if test="${section.number != 1}">(${section.number})</c:if>
			  </td>
			  <td>
				${section.sectionTitle} - ${section.type}
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
		  <th></th><th>Code</th><th>Name</th><th>Units</th><th>Instructor</th>
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
		    <c:if test="${section.number != 1}">(${section.number})</c:if>
		  </td>
		  <td>
			${section.sectionTitle} - ${section.type}
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

<div class="">
	<div class="ui-widget pre-reg-text"><h3>Add Comment</h3></div>
 	<textarea name="comment" id="comment" rows="15" cols="50"></textarea>
</div>

<br><input type="submit" id="submit" value="Submit" class="subbutton">
</form>

<div id="notify-limit" class="notify">
	<p>You are not allowed to take more than ${limit} units.</p>
</div>

</c:if>
</c:otherwise>
</c:choose>
