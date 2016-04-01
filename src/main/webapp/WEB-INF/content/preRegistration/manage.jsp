<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script>
$(function(){
   $("#publishDate").datepicker({
		inline : true
	});
	$('#expireDate').datepicker({
	    inline: true
	});
    $("table").tablesorter();
    $("select[name='term'] option").each(function(){
       if( $(this).val() == ${term.code}) 
           $(this).attr('selected', true);
    });
    $("select[name='term']").change(function(){
        var term = $("select[name='term'] option:selected").val();
        window.location.href = "offeredSections?term=" + term;
    });
    $("input[name='publishDate']").change(function(){
    	var date = $("input[name='publishDate']").val();
    	window.location.href = "TentativeSchedule/edit?publishDate="+date+"&id="+${schedule.id}
    });
    $("input[name='expireDate']").change(function(){
    	var date = $("input[name='expireDate']").val();
    	window.location.href = "TentativeSchedule/edit?expireDate="+date+"&id="+${schedule.id}
    });
});

function remove( id ) {
	var msg = "Do you want to remove this section?";
	if( confirm(msg) )
		window.location.href = "../offeredSection/delete?id=" + id + "&term=" + ${term.code}; 
}

function help( name )
{
    $("#help-"+name).dialog("open");
}
</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/offeredSection/search' />">Offered Sections</a></li>
<li>${department.name}</li>
<li class="align_right">
  <select class="formselect" name="term">
    <c:forEach var="t" items="${terms}"><option value="${t.code}">${t}</option></c:forEach>
  </select>
</li>
<c:if test="${not empty schedule}">
<li class="align_right"><a href="<c:url value='/department/${dept}/offeredSection/offer?term=${term.code}' />" ><img alt="[Offer Section]"
  title="Offer Section" src="<c:url value='/img/icons/table_import.png' />" /></a></li>
</c:if>
</ul>

<c:choose>
<c:when test="${empty schedule}">
	<a href="../tentativeSchedule/create?term=${term.code}">Create tentative schedule</a>
</c:when>

<c:otherwise>
<c:if test="${fn:length(schedule.sections) == 0}">
	<p>No sections offered yet.</p>
</c:if>

<c:if test="${fn:length(schedule.sections) > 0}">

<form:form modelAttribute="schedule">
	<div style="padding:10px;">
	<form:input path="publishDate" cssClass="mediuminput" placeholder="Publish Date"/>
	<form:input path="expireDate" cssClass="mediuminput" placeholder="Expire Date"/>
	<input type="submit" name="submit" class="subbutton" value="Submit" />
	</div>
</form:form>

<table class="viewtable">
<thead>
<tr>
  <th>Code</th><th>Name</th><th>Applied Users</th>
  <th></th>
  <th></th>
</tr>
</thead>
<tbody>
<c:forEach items="${schedule.sections}" var="section">
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
  <td>
  	<a href="<c:url value='/department/${dept}/offeredSection/edit?id=${section.id}&term=${term.code}' />"> Edit </a>
  </td>
  <td>
  	<a href="javascript:remove(${section.id})">
  	 <img src="<c:url value='/img/icons/delete.png' />" alt="Remove" title="Remove" /> </a>
  </td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>

</c:otherwise>
</c:choose>


<!-- <div id="help-expdate" class="help">
Each section should have an <em>expiration date</em>, after which
	students are not able to change it.
</div>
<div id="help-pubdate" class="help">
Each section should have a <em>publish date</em>, after which the
entry will be published automatically to users.</div>	 -->
