<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul id="title">
<li>Popups</li>
<security:authorize access="authenticated and principal.isFaculty('${dept}')">
<li class="align_right"><a href="post"><img alt="[Post Popup]"
  title="Post Popup" src="<c:url value='/img/icons/newspaper_add.png' />" /></a></li>
</security:authorize>
</ul>

<c:if test="${fn:length(popups) == 0}">
<p>No popups yet.</p>
</c:if>

<c:if test="${fn:length(popups) > 0}">
<div id="blk">
<c:forEach items="${popups}" var="popup">
<div class="blk_wrap"  style="margin-top: 15px;">
  <div class="post_subject">
    
    <h3>${popup.subject}</h3>
    
    <h4>Posted by ${popup.author.name}
    
    <fmt:formatDate value="${popup.createDate}" pattern="MM/dd/yyyy" />

    
  	[<a href="<c:url value='/department/${dept}/popup/edit?id=${popup.id}' />">  Edit </a>]
  	
  	</h4>
  </div>
  
  <div class="post_content">
    ${popup.content}   
  </div>
  
  <div class="post_content">
  	<p><a class="popup-detail" href="<c:url value='/department/${dept}/popup/seenUsers?id=${popup.id}' />">Read by ${fn:length(popup.readUsers)} Users</a>
 	<a class="popup-detail" href="<c:url value='/department/${dept}/popup/remainingUsers?id=${popup.id}' />">${fn:length(popup.targetUsers)} Remaining Users</a></p>   
  </div>
  
</div>
</c:forEach>
</div>
</c:if>
