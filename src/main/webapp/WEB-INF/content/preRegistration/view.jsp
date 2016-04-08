<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script>
$(function(){
    $( "#accordion" ).accordion();
    $("#tabs").tabs({
        cache: false
    });
    $("table").tablesorter({sortList: [[0,0]] });
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
			alert("you did not select any sections");
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
});

</script>

<ul id="title">
<li>Offered Sections</li>

<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="t" items="${terms}"><option value="${t.code}">${t}</option></c:forEach>
  </select>
</li>

<security:authorize access="authenticated and principal.isFaculty('${dept}')">
<li class="align_right"><a href="preRegistration/manage?term=${term.code}"><img src="<c:url value='/img/icons/table_edit.png' />" alt="[Manage Sections]" title="ManageSections"/></a></li>
</security:authorize>
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

<!-- Faculty View -->

<div id="tabs">
	<ul>
	  <li><a href="#undergraduate">Undergraduate Courses</a></li>
	  <li><a href="#graduate">Graduate Courses</a></li>
	</ul>
	
	<div id="undergraduate">
	<table class="viewtable small-font">
		<thead>
		<tr>
		  <th>Code</th><th>Name</th><th>Type</th><th>Units</th><th>Instructor</th><th>Location</th><th>Time</th>
		  <th>Applied Users</th><th>Open</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${schedule.sections}" var="section">
		<c:if test="${ not section.isGraduate()}">
		<tr>
		  <td>
		    ${section.subject} ${section.courseCode}
		    <c:if test="${section.number != 1}">(${section.number})</c:if>
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
		  <td>
		  	<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}&term=${term.code}' />">${section.requests.size()} user(s) applied</a>
		  </td>
		  <td>${section.capacity - section.requests.size()}</td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table>
	</div><!-- end of undergrad -->
	
	<div id="graduate">
	<table class="viewtable small-font">
		<thead>
		<tr>
		  <th>Code</th><th>Name</th><th>Type</th><th>Units</th><th>Instructor</th><th>Location</th><th>Time</th>
		  <th>Applied Users</th><th>Open</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${schedule.sections}" var="section">
		<c:if test="${ section.isGraduate()}">
		<tr>
		  <td>
		    ${section.subject} ${section.courseCode}
		    <c:if test="${section.number != 1}">(${section.number})</c:if>
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
 			  <td>
		  	<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}&term=${term.code}' />">${section.requests.size()} user(s) applied</a>
		  </td>
		  <td>${section.capacity - section.requests.size()}</td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table>
	</div><!-- end of graduate -->
	
</div><!-- end of tabs -->

</c:if>
</c:otherwise>
</c:choose>
