<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>

<c:set var="requests" value="${section.requests}"/>

<script>
$(function(){
	$(".selectAll").click(function(){
	    var checked = $(this).is(":checked");
    	$(this).parents("form").find(":checkbox[name='userId']").prop("checked",checked);
	});
	$("#email").click(function(){
	    if( $("#users-form").find(":checkbox[name='userId']:checked").length  > 0 )
	        $("#users-form").submit();
	    else
	        window.location.href = "<c:url value='/email/compose?backUrl=/department/${dept}/offeredSections' />";
	});
});

function email( userId )
{
    var url = "<c:url value='/email/compose?userId=' />" + userId;
    url += "&backUrl=/department/" + "${dept}" + "/offeredSections";
    window.location.href=url;
}
</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/offeredSections' />">Offered Sections</a></li>
<li>${section.course.name}</li>
<li class="align_right"><a id="email" href="javascript:void(0)"><img title="Email Users"
   alt="[Email Users]" src="<c:url value='/img/icons/email_to_friend.png' />" /></a></li>
</ul>

<c:if test="${fn:length(requests) > 0}">
<form id="users-form" action="<c:url value='/email/compose' />" method="post">
<table class="viewtable">
  <tr>
  	<th><input class="selectAll" type="checkbox" /></th>
    <th>CIN</th>
    <th>Name</th>
    <th>Email</th>
  </tr>
  
  <c:forEach items="${requests}" var="req" varStatus="status">
  	<tr>
  	<td class="center"><input type="checkbox" name="userId" value="${req.requester.id}" /></td>
  	<td>${req.requester.cin}</td>
  	<td>${req.requester.name}</td>
  	<td><a href="javascript:email(${req.requester.id})">${req.requester.primaryEmail}</a></td>
  	</tr>
  </c:forEach>
</table>
</form>

<c:if test="${fn:length(comments)>0 }">
	<h3>Comments</h3>
	
	<table class="general">
	<c:forEach items="${requests}" var="req">
		<c:if test="${not empty req.comment}">
			<tr>
			<th style="width:20%;">${req.requester.name}</th>
			<td>${req.comment}</td>
			</tr>
		</c:if>
	</c:forEach>
	</table>
</c:if>

</c:if>


