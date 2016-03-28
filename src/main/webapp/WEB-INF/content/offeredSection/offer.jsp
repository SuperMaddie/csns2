<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>


<script>
$(function(){
   $("#section\\.course option[value='${section.course.id}']").attr("selected", "selected");
   $("#section\\.instructors option[value='${section.instructors[0].id}']").attr("selected", "selected"); 
   
   $("#publishDate").datepicker({
		inline : true
	});
	$('#expireDate').datepicker({
	    inline: true
	});
	
    $("div.help").dialog({
        autoOpen: false,
        modal: true
    });
});

function help( name )
{
    $("#help-"+name).dialog("open");
}

</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/offeredSection/search' />">Offered Sections</a></li>
<li><a class="bc" href="<c:url value='/department/${dept}/offeredSections?term=${section.term.code}' />">${department.name}</a></li>
<li>New</li>
</ul>

<form:form modelAttribute="section">
<table class="general">
<tr>
  <th>Term</th>
  <td> ${section.term}</td>
</tr>

<tr>
  <th>Course</th>
  <td>
    <form:select path="course" items="${department.courses}"
      itemLabel="code" itemValue="id" />
  </td>
</tr>

<tr>
  <th>Instructor</th>
  <td>
    <form:select path="instructors" items="${department.faculty}" 
      itemLabel="name" itemValue="id" multiple="false" />
  </td>
</tr>

<!-- <tr>
<th>Target Standings</th>
<td>	
	<table style="width:70%;">
	<tr>
	<td  style="width:50%;">
	<c:forEach items="${standings}" var="standing" varStatus="var">
		<c:if test="${var.index < fn:length(standings)/2}">
		<form:checkbox path="targetStandings" value="${standing}" id="standing-checkbox${var.index}"/> <Label for="standing-checkbox${var.index}">${standing.symbol} - ${standing.name}</Label> <br />
		</c:if>
	</c:forEach>
	</td>
	<td>
	<c:forEach items="${standings}" var="standing" varStatus="var">
		<c:if test="${var.index ge fn:length(standings)/2}">
		<form:checkbox path="targetStandings" value="${standing}" id="standing-checkbox${var.index}"/> <Label for="standing-checkbox${var.index}">${standing.symbol} - ${standing.name}</Label> <br />
		</c:if>
	</c:forEach>
	</td>
	</tr>
	</table>
</td>
</tr> -->

<tr>
  <th>Capacity</th>
  <td>
    <form:input path="capacity" type="number" cssClass="leftinput" cssStyle="width: 20%;" />
  </td>
</tr>

<tr>
  <th>Section Number</th>
  <td>
  	<form:select path="number" items="${numbers}" multiple="false" />
  </td>
</tr>

<tr>
  <th>Day</th>
  <td>
  	<form:select path="day" items="${days}" multiple="false" />
  </td>
</tr>

<!-- <tr>
  <th>Start Time</th>
  <td>
  	<form:input path="startTime" type="time" />
  </td>
</tr>

<tr>
  <th>End Time</th>
  <td>
  	<form:input path="endTime" type="time" />
  </td>
</tr> -->

<tr>
  <th>Publish Date</th>
  <td>
    <form:input path="publishDate" cssClass="smallinput" size="10" maxlength="10" />
  </td>
</tr>
  
<tr>
  <th>Expiration Date</th>
  <td>
    <form:input path="expireDate" cssClass="smallinput" size="10" maxlength="10" />
  </td>
</tr>
<tr>
<th></th>
<td><input type="submit" name="submit" value="Add" class="subbutton"></td>
</tr>

</table>
</form:form>

<!-- <div id="help-expdate" class="help">
Each section should have an <em>expiration date</em>, after which
	students are not able to change it.
</div>
<div id="help-pubdate" class="help">
Each section should have a <em>publish date</em>, after which the
entry will be published automatically to users.</div>	 -->
