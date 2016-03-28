<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul id="title">
<li><a class="bc" href="<c:url value='' />">PreRegistration</a></li>
<li>${department.name}</li>
</ul>

<c:if test="${fn:length(quarters) == 0}">
<p>No pre-registration forms yet.</p>
</c:if>

<c:if test="${fn:length(quarters) > 0}">
	<c:forEach items="${quarters}" var="quarter">
		<a href="<c:url value='/department/${dept}/preRegistration/request?quarter=${quarter.code}' />">
			<span style="text-transform: capitalize;"><b>Quarter ${quarter}</b></span></a>
	</c:forEach>
</c:if>
