<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>

<script>
$(function(){
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
	if( confirm(msg) )
		window.location.href = "delete?id=" + id + "&term=" + ${term.code}; 
}
</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration?term=${term.code}' />">Offered Sections</a></li>
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration/manage?term=${term.code}' />">Manage Offered Sections</a></li>
<li>Edit</li>
<li class="align_right"><a href="javascript:remove(${section.id})"><img title="Delete Section"
 alt="[Delete Section]" src="<c:url value='/img/icons/table_delete.png' />" /></a></li>
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
    <form:input path="sectionTitle" type="text" cssClass="forminput" />
  </td>
</tr>

<tr>
  <th>Course Code</th>
  <td>
    <form:input path="courseCode" type="number" cssClass="mediuminput" required="required" />
  </td>
</tr>

<tr>
  <th>Section Number</th>
  <td>
  	<form:input path="number" type="number" cssClass="mediuminput" required="required" />
  </td>
</tr>

<tr>
  <th>Class Number</th>
  <td>
  	<form:input path="classNumber" type="number" cssClass="mediuminput" />
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
    <form:input path="units" type="number" cssClass="mediuminput" />
  </td>
</tr>

<tr>
  <th>Notes</th>
  <td>
    <form:textarea path="notes" cssStyle="width: 60%;" rows="15" cols="50" />
  </td>
</tr>

<tr>
  <th>Capacity</th>
  <td>
    <form:input path="capacity" type="number" cssClass="mediuminput" />
  </td>
</tr>


<tr>
<th></th>
<td><input type="submit" name="submit" value="Edit" class="subbutton"></td>
</tr>

</table>
</form:form>

