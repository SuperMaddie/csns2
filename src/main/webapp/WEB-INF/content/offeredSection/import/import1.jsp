<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration?term=${term.code}' />">Offered Sections</a></li>
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration/manage?term=${term.code}' />">Manage Offered Sections</a></li>
<li>Import Section</li>
</ul>

<div id="import">
	<p>Please check the information to make sure everything is right.<br></p>
	
	<form action="<c:url value='/department/${dept}/offeredSection/import' />" method="post" 
		enctype="multipart/form-data">
		<table class="viewtable">
			<thead>
				<tr>
					<th>Subject</th><th>Section</th><th>Class No.</th>
					<th>Title</th><th>Day</th><th>Start</th><th>End</th><th>Location</th>
					<th>Type</th><th>Instructor</th><th>Unit</th><th>Notes</th>
				</tr>
			</thead>
			<c:forEach items="${sections}" var="section">
				<tr>
					<td>${section.subject} ${section.courseCode}</td>
					<td>${section.number}</td><td>${section.classNumber}</td><td>${section.sectionTitle}</td>
					<td>${section.day}</td><td>${section.startTime}</td><td>${section.endTime}</td>
					<td>${section.location}</td><td>${section.type}</td><td>${section.instructors[0]}</td>
					<td>${section.units}</td><td>${section.notes}</td>
				</tr>
			</c:forEach>
		</table>
	
		<p><input type="hidden" name="term" value="${term.code}" />
		<input type="hidden" name="_page" value="1" />
		<input type="submit" name="_target0" value="Back" class="subbutton">
		<input type="submit" name="_finish" value="Finish" class="subbutton" /></p>
	</form>
</div>
