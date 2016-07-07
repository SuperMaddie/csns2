<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>

<script>
$(function(){
	$("#courseCode").val("");
	$("#number").val("1");
	$("#classNumber").val("");
	$("#units").val("");
});
</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration?term=${term.code}' />">Offered Sections</a></li>
<li>New</li>
</ul>

<form:form modelAttribute="section">
<table class="general">
<tr>
  <th style="width:150px;">Term</th>
  <td> ${term}</td>
</tr>

<tr>
  <th>Subject</th>
  <td>
    ${department.abbreviation}
  </td>
</tr>

<tr>
  <th>Title</th>
  <td>
    <form:input path="sectionTitle" type="text" cssClass="forminput"/>
    <div class="error"><form:errors path="sectionTitle" /></div>
  </td>
</tr>

<tr>
  <th>Course Code</th>
  <td>
    <form:input path="courseCode" id="courseCode" cssClass="mediuminput" required="required" />
  </td>
</tr>

<tr>
  <th>Section Number</th>
  <td>
  	<form:input path="number" id="number" cssClass="mediuminput" required="required" />
  </td>
</tr>

<tr>
  <th>Class Number</th>
  <td>
  	<form:input path="classNumber" id="classNumber" cssClass="mediuminput" required="required" />
  </td>
</tr>

<tr>
  <th>Day(s)</th>
  <td>
    <form:input path="day" type="text" cssClass="mediuminput" />
  </td>
</tr>

<tr>
<th>Start Time</th>
  <td>
  	<form:input path="startTime" id="startTime" type="text" cssClass="mediuminput" />
  </td>
</tr>

<tr>
  <th>End Time</th>
  <td>
  	<form:input path="endTime" id="endTime" type="text" cssClass="mediuminput" />
  </td>
</tr>

<tr>
<th>Location</th>
<td>
	<form:input path="location" id="location" type="text" cssClass="mediuminput" />
</td>
</tr>

<tr>
  <th>Type</th>
  <td>
  	<form:input path="type" id="type" type="text" cssClass="mediuminput" />
  </td>
</tr>

<tr>
  <th>Faculty</th>
  <td>
    <form:select path="instructors" multiple="false" cssClass="mediuminput">
        <form:option  value="">Select</form:option>
    	<form:options items="${department.faculty}" itemLabel="name" itemValue="id"/>
    </form:select>
  </td>
</tr>

<tr>
  <th>Units</th>
  <td>
    <form:input path="units" id="units" cssClass="mediuminput" required="required" />
  </td>
</tr>

<tr>
  <th>Notes</th>
  <td>
    <form:input path="notes" type="text" cssClass="mediuminput" />
  </td>
</tr>

<tr>
  <th>Capacity</th>
  <td>
    <form:input path="capacity" cssClass="mediuminput" required="required" />
  </td>
</tr>


<tr>
<th></th>
<td><input type="submit" name="submit" value="Add" class="subbutton"></td>
</tr>

</table>
</form:form>

