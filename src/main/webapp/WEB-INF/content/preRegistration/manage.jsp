<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script>
$(function(){
   $("#tabs").tabs({
       cache: false
   });
   $("#publishDate").datepicker({
		inline : true
	});
	$('#expireDate').datepicker({
	    inline: true
	});
    $("table").tablesorter({sortList: [[0,0]] });
    $("select[name='term'] option").each(function(){
       if( $(this).val() == ${term.code}) 
           $(this).attr('selected', true);
    });
    $("select[name='term']").change(function(){
        var term = $("select[name='term'] option:selected").val();
        window.location.href = "manage?term=" + term;
    });

});

function remove( id ) {
	var msg = "Do you want to remove this section?";
	if( confirm(msg) ){
		window.location.href = "<c:url value='/department/${dept}/offeredSection/delete?id=" + id + "&term=${term.code}' />"; 
	}
}

</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration' />">Offered Sections</a></li>
<li>${department.name}</li>
<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="t" items="${terms}"><option value="${t.code}">${t}</option></c:forEach>
  </select>
</li>
<c:if test="${not empty schedule}">
<li class="align_right"><a href="<c:url value='/department/${dept}/offeredSection/offer?term=${term.code}' />" ><img alt="[Add Section]"
  title="Add Section" src="<c:url value='/img/icons/page_add.png' />" /></a></li>
<li class="align_right"><a href="<c:url value='/department/${dept}/offeredSection/import?term=${term.code}' />" ><img alt="[Import Sections]"
  title="Import Sections" src="<c:url value='/img/icons/table_import.png' />" /></a></li>
</c:if>
</ul>

<c:if test="${fn:length(schedule.sections) == 0}">
	<p>Currently there are no offered sections in <b>${term}</b>.</p>
</c:if>

<c:if test="${fn:length(schedule.sections) > 0}">

<form:form modelAttribute="schedule">
	<div style="padding-top:10px;padding-bottom:10px;">
	<form:input path="publishDate" cssClass="mediuminput" placeholder="Publish Date"/>
	<form:input path="expireDate" cssClass="mediuminput" placeholder="Expire Date"/>
	<input type="submit" name="submit" class="subbutton" value="Apply" />
	</div>
</form:form>

<div id="tabs">
	<ul>
	  <li><a href="#undergraduate">Undergraduate Courses</a></li>
	  <li><a href="#graduate">Graduate Courses</a></li>
	</ul>
	
	<div id="undergraduate">
	<table class="viewtable">
		<thead>
		<tr>
		  <th>Code</th><th>Name</th><th>Type</th><th>Instructor</th><th>Location</th><th>Time</th>
		  <th>Applied Users</th><th>Open</th><th></th><th></th>
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
			<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}&term=${term.code}' />">${section.requests.size()} users applied</a>
		  </td>
		  <td>
		  	${section.capacity - section.requests.size()}
		  </td>
		  <td class="center">
		  	<a href="<c:url value='/department/${dept}/offeredSection/edit?id=${section.id}&term=${term.code}' />">
		  		<img src="<c:url value='/img/icons/script_edit.png' />" alt="[Edit]" title="Edit" /></a>
		  </td>
		  <td class="center">
		  	<a href="javascript:remove(${section.id})">
		  	 <img src="<c:url value='/img/icons/delete.png' />" alt="Remove" title="Remove" /> </a>
		  </td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table>
	</div><!-- end of undergraduate -->
	
	<div id="graduate">
	<table class="viewtable">
		<thead>
		<tr>
		  <th>Code</th><th>Name</th><th>Type</th><th>Instructor</th><th>Location</th><th>Time</th>
		  <th>Applied Users</th><th>Open</th><th></th><th></th>
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
			<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}&term=${term.code}' />">${section.requests.size()} users applied</a>
		  </td>
		  <td>
		  	${section.capacity - section.requests.size()}
		  </td>
		  <td class="center">
		  	<a href="<c:url value='/department/${dept}/offeredSection/edit?id=${section.id}&term=${term.code}' />">
		  		<img src="<c:url value='/img/icons/script_edit.png' />" alt="[Edit]" title="Edit" /></a>
		  </td>
		  <td class="center">
		  	<a href="javascript:remove(${section.id})">
		  	 <img src="<c:url value='/img/icons/delete.png' />" alt="Remove" title="Remove" /> </a>
		  </td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table>
	</div><!-- end of graduate -->
</div><!-- end of tabs -->

</c:if>
