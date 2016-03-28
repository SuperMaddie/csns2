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
    try{
    	$("textarea").each(function(){
        	CKEDITOR.replace( $(this).attr("id"), {
          		toolbar : "Default"
        	});
    	});
    }catch(ex){
    }
});


</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration/list' />">Pre-Registration</a></li>
<li>${department.name}</li>
<li>${quarter.code}</li>
<li>Request</li>
</ul>

<form:form modelAttribute="request">
<table class="general">
<tr>
  <th>Sections</th>
  <td>
  	<c:forEach items="${sections}" var="section" varStatus="status">
			<form:checkbox path="sections" value="${section}" id="sec-checkbox${status.index}"/> 
				<Label for="sec-checkbox${status.index}">${section.course.name} </Label><br />
		</c:forEach>
  </td>
</tr>

<tr>
  <th>Comment</th>
  <td>
    <form:textarea id="textcontent" path="comment" cssStyle="width: 99%;" rows="15" cols="80" />
  </td>
</tr>

<tr>
<th></th>
<td><input type="submit" name="submit" value="Send" class="subbutton"></td>
</tr>

</table>
</form:form>

