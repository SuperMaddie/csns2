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
        window.location.href = "offeredSections?term=" + term;
    });
});

function remove( id ) {
	var msg = "Do you want to remove this section?";
	if( confirm(msg) )
		window.location.href = "offeredSection/delete?id=" + id; 
}
</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/offeredSection/search' />">Offered Sections</a></li>
<li>${department.name}</li>
<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="q" items="${terms}"><option value="${q.code}">${q}</option></c:forEach>
  </select>
</li>
<security:authorize access="authenticated and principal.isAdmin('${dept}')">
<li class="align_right"><a href="offeredSection/offer?term=${term.code}"><img alt="[Offer Section]"
  title="Offer Section" src="<c:url value='/img/icons/table_import.png' />" /></a></li>
</security:authorize>
</ul>

<c:if test="${fn:length(sections) > 0}">
<table class="viewtable">
<thead>
<tr>
  <th>Code</th><th>Name</th><th>Applied Users</th>
  <security:authorize access="authenticated and principal.isAdmin('${dept}')">
  <th></th>
  <th></th>
  </security:authorize>
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
	<a href="<c:url value='/department/${dept}/offeredSection?id=${section.id}' />">${fn:length(section.requests)} user(s) applied</a>
  </td>
  <security:authorize access="authenticated and principal.isAdmin('${dept}')">
  <td>
  	<a href="<c:url value='/department/${dept}/offeredSection/edit?id=${section.id}' />"> Edit </a>
  </td>
  <td>
  	<a href="javascript:remove(${section.id})">
  	 <img src="<c:url value='/img/icons/delete.png' />" alt="Remove" title="Remove" /> </a>
  </td>
  </security:authorize>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>
