<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script>
$(function(){
    $("#tabs").tabs({
        cache: false
    });
    $("table").tablesorter({sortList: [[0,0], [1,0]] });
    $("select[name='term'] option").each(function(){
       if( $(this).val() == ${term.code}) 
           $(this).attr('selected', true);
    });
    $("select[name='term']").change(function(){
        var term = $("select[name='term'] option:selected").val();
        window.location.href = "<c:url value='/department/${dept}/preRegistration?term=" + term + "'/>";
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

function remove( id ) {
	var msg = "Do you want to remove this section?";
	if( confirm(msg) ){
		window.location.href = "<c:url value='/department/${dept}/offeredSection/delete?id=" + id + "&term=${term.code}' />"; 
	}
}
</script>

<ul id="title">
<li>Offered Sections</li>

<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="t" items="${terms}"><option value="${t.code}">${t}</option></c:forEach>
  </select>
</li>

<security:authorize access="authenticated and principal.isAdmin('${dept}')">
<c:if test="${not empty schedule}">
	<li class="align_right"><a href="<c:url value='/department/${dept}/offeredSection/offer?term=${term.code}' />" ><img alt="[Add Section]"
	  title="Add Section" src="<c:url value='/img/icons/page_add.png' />" /></a></li>
	<li class="align_right"><a href="<c:url value='/department/${dept}/offeredSection/import?term=${term.code}' />" ><img alt="[Import Sections]"
	  title="Import Sections" src="<c:url value='/img/icons/table_import.png' />" /></a></li>
	<c:if test="${fn:length(schedule.sections) > 0 }">
  	<li class="align_right"><a href="<c:url value='/department/${dept}/tentativeSchedule/edit?term=${term.code}' />" ><img alt="[Edit Section]"
	  title="Edit Schedule" src="<c:url value='/img/icons/page_edit.png' />" /></a></li>
	</c:if>
</c:if>
</security:authorize>
</ul>

<c:if test="${empty schedule}">
<p>There is no schedule available for <b>${term}</b>.</p>
<security:authorize access="authenticated and principal.isAdmin('${dept}')">
	<p> Do you want to create a schedule?</p>
	<form action="<c:url value='/department/${dept}/preRegistration/createSchedule?term=${term.code}' />" method="POST">
		<input type="submit" name="cancel" id="cancel" value="Cancel" class="subbutton"/>
		<input type="submit" name="create" id="create" value="Create" class="subbutton"/>
	</form>
</security:authorize>
</c:if>

<c:if test="${not empty schedule}">
<c:choose>
<c:when test="${fn:length(schedule.sections) == 0}">
	<p>Currently there are no offered sections in <b>${term}</b>.</p>
	<security:authorize access="authenticated and principal.isAdmin('${dept}')">
		<p>You can import a list of sections to this term or create them individually.</p>
	</security:authorize>
</c:when>

<c:otherwise>
<div id="tabs">
	<ul>
	  <li><a href="#undergraduate">Undergraduate Courses</a></li>
	  <li><a href="#graduate">Graduate Courses</a></li>
	</ul>
	
	<div id="undergraduate">
	<table class="viewtable small-font">
		<thead>
		<tr>
		  <th>Code</th><th>Section</th><th>Name</th><th>Type</th><th>Instructor</th><th>Location</th><th>Time</th>
		  <th>Open</th><th>Notes</th>
		  <security:authorize access="authenticated and principal.isAdmin('${dept}')">
		  <th>Applied Users</th>
		  <th></th><th></th>
		  </security:authorize>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${schedule.sections}" var="section">
		<c:if test="${ not section.isGraduate()}">
		<tr>
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
		  	${section.capacity - section.requests.size()}
		  </td>
  		  <td>
		  	${section.notes}
		  </td>
		  <security:authorize access="authenticated and principal.isAdmin('${dept}')">
	  		  <td>
				<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}&term=${term.code}' />">${section.requests.size()} user(s) applied</a>
		  	  </td>
			  <td class="center">
			  	<a href="<c:url value='/department/${dept}/offeredSection/edit?id=${section.id}&term=${term.code}' />">
			  		<img src="<c:url value='/img/icons/script_edit.png' />" alt="[Edit]" title="Edit" /></a>
			  </td>
			  <td class="center">
			  	<a href="javascript:remove(${section.id})">
			  	 <img src="<c:url value='/img/icons/delete.png' />" alt="Remove" title="Remove" /> </a>
			  </td>
		  </security:authorize>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table>
	</div><!-- end of undergraduate -->
	
	<div id="graduate">
	<table class="viewtable small-font">
		<thead>
		<tr>
		  <th>Code</th><th>Section</th><th>Name</th><th>Type</th><th>Instructor</th><th>Location</th><th>Time</th>
		  <th>Open</th><th>Notes</th>
		  <security:authorize access="authenticated and principal.isAdmin('${dept}')">
		  <th>Applied Users</th>
		  <th></th><th></th>
		  </security:authorize>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${schedule.sections}" var="section">
		<c:if test="${ section.isGraduate()}">
		<tr>
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
		  	${section.capacity - section.requests.size()}
		  </td>
   		  <td>
		  	${section.notes}
		  </td>
		  <security:authorize access="authenticated and principal.isAdmin('${dept}')">
			  <td>
				<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}&term=${term.code}' />">${section.requests.size()} user(s) applied</a>
			  </td>
			  <td class="center">
			  	<a href="<c:url value='/department/${dept}/offeredSection/edit?id=${section.id}&term=${term.code}' />">
			  		<img src="<c:url value='/img/icons/script_edit.png' />" alt="[Edit]" title="Edit" /></a>
			  </td>
			  <td class="center">
			  	<a href="javascript:remove(${section.id})">
			  	 <img src="<c:url value='/img/icons/delete.png' />" alt="Remove" title="Remove" /> </a>
			  </td>
		  </security:authorize>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table>
	</div><!-- end of graduate -->
	
</div><!-- end of tabs -->
</c:otherwise>
</c:choose>

</c:if>
