<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>

<script>
$(function(){
    $("#publishDate").datepicker({
		inline : true
	});
	$('#expireDate').datepicker({
	    inline: true
	});
});
</script>
	
<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration?term=${term.code}' />">Offered Sections</a></li>
<li>Edit Schedule</li>
</ul>

<form:form modelAttribute="schedule">
<table class="general">
<tr>
  <th style="width:150px;">Publish Date</th>
  <td><form:input path="publishDate" cssClass="mediuminput" placeholder="Publish Date" /></td>
</tr>
<tr>
 	<th>Close Date</th>
	<td><form:input path="expireDate" cssClass="mediuminput" placeholder="Expire Date" /></td>
</tr>
<tr>
<th></th>
<td><input type="submit" name="submit" class="subbutton" value="Apply" /></td>
</tr>
</table>
</form:form>

