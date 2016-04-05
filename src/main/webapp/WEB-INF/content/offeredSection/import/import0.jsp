<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration?term=${term.code}' />">Offered Sections</a></li>
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration/manage?term=${term.code}' />">Manage Offered Sections</a></li>
<li>Import Section</li>
</ul>

<div id="import">
	<p>Important Notes:<br></p>
	<ul>
		<li>The file must be in excel format.</li>
		<li>The first row must contain the titles.</li>
	</ul>
	
	<form action="<c:url value='/department/${dept}/offeredSection/import' />" method="post" enctype="multipart/form-data">
		<b>Select File: </b>
	  	<input type="file" name="file" style="width: 25%;" class="leftinput" required
	  		accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
		<br><div class="error">${message}</div>
	<p><input type="hidden" name="term" value="${term.code}" />
	<input type="hidden" name="_page" value="0" />
	<input type="submit" name="_target1" value="Next" class="subbutton" /></p>
	</form>
</div>
