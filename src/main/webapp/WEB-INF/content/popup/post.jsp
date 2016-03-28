<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="<c:url value='/css/popup.css' />">

<script>
$(function(){
	var targetUsers = {};
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
    
    $("div.help").dialog({
        autoOpen: false,
        modal: true
    });

    $("#check-setnews").change(function() {
    	$("#categories").css( 'display', this.checked ? '' :'none');
    });
    $("#check-setnews").is(':checked') ? $("#categories").css( 'display', '') : $("#categories").css( 'display', 'none');
    
    $("#add-button").click(function() {
    	$("#targetUserId").val($("#input-user").attr('user-id'));
    });
    
    $(".add").each(function(){
        $(this).autocomplete({
            source: "<c:url value='/autocomplete/user' />",
            select: function(event, ui) {
                if( ui.item ){
                	$(this).attr('user-id', ui.item.id);
                }
            }
        });
    });
    
    $(".clear").each(function(){
       $(this).click(function(event){
           event.preventDefault();
           $("input[name='userId']").remove();
           $(".add").each(function(){
              $(this).val("");
           });
       });
    });
});

function help( name )
{
    $("#help-"+name).dialog("open");
}

function removeUser( id ) {
	$("#removeUserId").val(id);
}

</script>

<ul id="title">
  <li><a class="bc" href="current">Popup</a></li>
  <li>New</li>
</ul>

<form:form modelAttribute="popup" id="popup-form">
<table class="general">
  <tr>
    <th>Subject</th>
    <td>
      <form:input path="subject" cssClass="leftinput" cssStyle="width: 99%;" />
      <div class="error"><form:errors path="subject" /></div>
    </td>
  </tr>
  <tr>
    <th>Content</th>
    <td>
      <form:textarea id="textcontent" path="content" cssStyle="width: 99%;" rows="15" cols="80" />
      <div class="error"><form:errors path="content" /></div>
    </td>
  </tr>
  
  <tr>
	<th>Target Roles</th>
	<td>
		<table style="width:70%;">
		<tr>
		<td style="width:50%;">
		<c:forEach items="${roles}" var="role" varStatus="var">
			<c:if test="${var.index < fn:length(roles)/2}">
			<form:checkbox path="targetRoles" value="${role}" id="role-checkbox${var.index}"/> <Label for="role-checkbox${var.index}">${rolesMap[role]} </Label><br />
			</c:if>
		</c:forEach>
		</td>
		<td>
		<c:forEach items="${roles}" var="role" varStatus="var">
			<c:if test="${var.index ge fn:length(roles)/2}">
			<form:checkbox path="targetRoles" value="${role}" id="role-checkbox${var.index}"/> <Label for="role-checkbox${var.index}">${rolesMap[role]}</Label> <br />
			</c:if>
		</c:forEach>
		</td>
		</tr>
		</table>
	</td>
  </tr>
 
  <tr>
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
  </tr>
  
  <tr>
	<th>Additional Target Users</th>
	<td>	
		<p>
		<input type="text" class="forminput add" name="name" size="40" 
		    placeholder="Search for users to add" id="input-user" />
		<input type="submit" class="subbutton" name="add" value="Add" id="add-button" />
		<button class="subbutton clear">Clear</button>
		</p>
		<input type="hidden" id="targetUserId" name="targetUserId" />
		<input type="hidden" id="removeUserId" name="removeUserId" /> 
		<c:if test="${fn:length(popup.individualTargetUsers) > 0}">
	    <table class="target-users-table">
			<tr class="text-left">
			  <th style="padding-left:3px">CIN</th><th>Name</th><th>Primary Email</th>
			  <th class=""></th>
			</tr>
			<c:forEach items="${popup.individualTargetUsers}" var="user">
			<tr>
			  <td>${user.cin}</td>
			  <td>${user.name}</td>
			  <td>${user.primaryEmail}</td>
			  <td class="text-top"><input alt="[Remove]" title="Remove" type="image" src="<c:url value='/img/icons/delete.png' />" onclick="removeUser(${user.id})" />
			  </td>
			</tr>
			</c:forEach>
		</table>
		</c:if>
	</td>
  </tr>

  <tr>
    <th><csns:help name="pubdate">Publish Date</csns:help></th>
    <td>
      <form:input path="publishDate" cssClass="smallinput" size="10" maxlength="10" />
    </td>
  </tr>
    
  <tr>
    <th><csns:help name="expdate">Expiration Date</csns:help></th>
    <td>
      <form:input path="expireDate" cssClass="smallinput" size="10" maxlength="10" />
    </td>
  </tr>
  
  <tr>
	<th>Set As News</th>
	<td>
		<form:checkbox id="check-setnews" path="setAsNews"/><br />
	</td>
  </tr>
  
  <tr id="categories" style="display:none;">
  	  <th>News Category</th>
  	  <td>
	    <select name="forumId" size="1" class="leftinput">
          <c:forEach items="${popup.department.forums}" var="forum">
            <option value="${forum.id}">${forum.name}</option>
          </c:forEach>
        </select>
	  </td>
  </tr>
  
  <tr>
    <th></th>
    <td>
      <input type="submit" class="subbutton" name="submit" value="Submit" />
    </td>
  </tr>
</table>

</form:form>


<div id="help-expdate" class="help">
Each popup entry should have an <em>expiration date</em>, after which the
entry will be removed automatically from the front page.</div>
<div id="help-pubdate" class="help">
Each popup entry should have a <em>publish date</em>, after which the
entry will be published automatically to target users.</div>
