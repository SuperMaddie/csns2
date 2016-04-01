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

</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/offeredSection/search' />">Offered Sections</a></li>
<li><a class="bc" href="<c:url value='/department/${dept}/offeredSections?term=${term.code}' />">${department.name}</a></li>
<li>New</li>
</ul>

<form:form modelAttribute="section">
<table class="general">
<tr>
  <th>Term</th>
  <td> ${term}</td>
</tr>

<tr>
  <th>Course</th>
  <td>
    <form:select path="course" items="${department.courses}"
      itemLabel="code" itemValue="id" />
  </td>
</tr>

<tr>
  <th>Faculty</th>
  <td>
    <form:select path="instructors" itemLabel="name" itemValue="id" multiple="false">
        <form:option  value="">Select Instructor</form:option>
    	<form:options items="${department.faculty}" />
    </form:select>
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
  	<form:input path="number" />
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
<th></th>
<td><input type="submit" name="submit" value="Add" class="subbutton"></td>
</tr>

</table>
</form:form>

