<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>

<c:set var="requests" value="${section.requests}"/>

<script>
$(function(){
	$(window).scroll(function(){
		$("#sidebar").css({"margin-top": ($(window).scrollTop()) + "px"});
	});
	
	$("#comment-wraper").hide();
	$("#content").css('overflow', 'auto');
	$("#users-table").find("tr").click(function(){
		getRequestContent($(this).find(":checkbox").attr("requestId"));
	});
	
	$(".selectAll").click(function(){
	    var checked = $(this).is(":checked");
    	$(this).parents("form").find(":checkbox[name='userId']").prop("checked",checked);
	});
	$("#email").click(function(){
	    if( $("#users-form").find(":checkbox[name='userId']:checked").length  > 0 )
	        $("#users-form").submit();
	    else
	        window.location.href = "<c:url value='/email/compose?backUrl=/department/${dept}/preRegistration?term=${term.code}' />";
	});
});

function getRequestContent( id ){
	var url = "<c:url value='/department/${dept}/offeredSection/getRequestContent' />";
	$.ajax({
		url: url,
		data: {
			id: id,
		},
		type : 'GET',
		success : function(data){
			data = JSON.parse(data);
			$("#sidebar-name").text(data.name);
			$("#sidebar-cin").text(data.cin);
			$("#sidebar-email").text(data.email);
			if(data.comment){
				$("#comment-wraper").show();
				$("#sidebar-comment").html(data.comment);
			}
		}
	});
}

function email( userId )
{
    var url = "<c:url value='/email/compose?userId=' />" + userId;
    url += "&backUrl=/department/" + "${dept}" + "/preRegistration?term="+${term.code};
    window.location.href=url;
}
</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration?term=${term.code}' />">Offered Sections</a></li>
<li>${section.sectionTitle}</li>
<li class="align_right"><a id="email" href="javascript:void(0)"><img title="Email Users"
   alt="[Email Users]" src="<c:url value='/img/icons/email_to_friend.png' />" /></a></li>
</ul>	

<c:if test="${fn:length(requests) == 0}">
	<p>No user has applied for this section.</p>
</c:if>

<c:if test="${fn:length(requests) > 0}">
<div class="left-content">
<form id="users-form" action="<c:url value='/email/compose' />" method="post">
<table class="viewtable" id="users-table">
  <tr>
  	<th><input class="selectAll" type="checkbox" /></th>
    <th>CIN</th>
    <th>Name</th>
    <th>Email</th>
    <th></th>
  </tr>
  
  <c:forEach items="${requests}" var="req" varStatus="status">
  	<tr>
  	<td class="center"><input type="checkbox" name="userId" value="${req.requester.id}" 
  		requestId="${req.id}"/></td>
  	<td>${req.requester.cin}</td>
  	<td>${req.requester.name}</td>
  	<td><a href="javascript:email(${req.requester.id})">${req.requester.primaryEmail}</a></td>
	<td class="center"><a href="<c:url value='/department/${dept}/preRegistration/edit?term=${term.code}&studentId=${req.requester.id}' />">
		<img src="<c:url value='/img/icons/script_edit.png' />" alt="[Edit]" title="Edit" /></a></td>
  	</tr>
  </c:forEach>
</table>
</form>
</div>

<div id="sidebar" class="bordered sidebar">
	<div class="content">
	<label class="title">Request Details</label>
	</div>
	<div class="details">
	<div class="row"><div class="label">Name:</div><label id="sidebar-name"></label></div>
	<div class="row"><div class="label">CIN:</div><label id="sidebar-cin"></label></div>
	<div class="row"><div class="label">Email:</div><label><a id="sidebar-email" href="javascript:email(${req.requester.id})"></a></label></div>
	<div class="row"><div class="label">Comment:</div></div>
	<div class="row" id="comment-wraper"><div class="textbox"><label id="sidebar-comment"></label></div></div>
	</div>
</div>

</c:if>


