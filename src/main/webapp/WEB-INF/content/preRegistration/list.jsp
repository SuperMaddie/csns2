<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script>
$(function(){
    $("table").tablesorter();
    $("select[name='term'] option").each(function(){
       if( $(this).val() == ${term.code}) 
           $(this).attr('selected', true);
    });
    $("select[name='term']").change(function(){
        var term = $("select[name='term'] option:selected").val();
        window.location.href = "preRegistration?term=" + term;
    });
});

</script>

<ul id="title">
<li><a class="bc" href="<c:url value='' />">Offered Sections</a></li>
<li>${department.name}</li>

<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="t" items="${terms}"><option value="${t.code}">${t}</option></c:forEach>
  </select>
</li>

<security:authorize access="authenticated and (principal.isAdmin('${dept}') or principal.isFaculty('${dept}'))">
<li class="align_right"><a href="preRegistration/manage?term=${term.code}"><b>Manage Schedules</b></a></li>
</security:authorize>
</ul>

<c:if test="${fn:length(schedule.sections) == 0}">
	<p>No schedules yet.</p>
</c:if>

<c:if test="${fn:length(schedule.sections) > 0}">
<table class="viewtable">
<thead>
<tr>
  <th>Code</th><th>Name</th><th>Location</th><th>Time</th>
</tr>
</thead>
<tbody>
<c:forEach items="${sections}" var="section">
<tr>
  <td>
    <a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}' />">${section.course.code}
    <c:if test="${section.number != 1}">(${section.number})</c:if></a>
  </td>
  <td>
	<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}' />">${section.course.name}</a>
  </td>
  <td>
	<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}' />">${section.location}</a>
  </td>
  <td>
	<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}' />">${section.startTime}-${section.endTime}</a>
  </td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>
