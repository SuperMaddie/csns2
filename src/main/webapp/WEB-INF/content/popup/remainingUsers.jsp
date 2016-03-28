<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>

<link rel="stylesheet" href="<c:url value='/css/popup.css' />">

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
	        window.location.href = "<c:url value='/email/compose?backUrl=/department/${dept}/popup/current' />";
	});
});

function email( userId )
{
    var url = "<c:url value='/email/compose?userId=' />" + userId;
    url += "&backUrl=/department/" + "${dept}" + "/popup/current";
    window.location.href=url;
}
</script>

<ul id="title">
  <li><a class="bc" href="current">Popup</a></li>
  <li>${popup.subject}</li>
  <li class="align_right"><a id="email" href="javascript:void(0)"><img title="Email Users"
    alt="[Email Users]" src="<c:url value='/img/icons/email_to_friend.png' />" /></a></li>
</ul>

<c:if test="${fn:length(popup.targetUsers) > 0 }">
<form id="users-form" action="<c:url value='/email/compose' />" method="post">
  <table class="viewtable">
  <tr>
  	<th><input class="selectAll" type="checkbox" /></th>
    <th>CIN</th>
    <th>Name</th>
    <th>Email</th>
  </tr>
  
  <c:forEach items="${popup.targetUsers}" var="user">
  	<tr>
  	<td class="center"><input type="checkbox" name="userId" value="${user.id}" /></td>
  	<td>${user.cin}</td>
  	<td>${user.name}</td>
  	<td><a href="javascript:email(${user.id})">${user.primaryEmail}</a></td>
  	</tr>
  </c:forEach>
  </table>
  <input type="hidden" name="backUrl" value="/department/${dept}/popup/current" />
  <input type="hidden" name="subject" value="${popup.subject}" />
  <input type="hidden" name="content" value="${popup.content}" />
</form>
</c:if>

