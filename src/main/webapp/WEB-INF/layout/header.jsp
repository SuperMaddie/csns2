<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<security:authorize access="authenticated">
<c:if test="${not empty dept}">
	<c:import url="/WEB-INF/layout/popup.jsp" />
</c:if>
</security:authorize>

<div id="csns_header">
<div class="wrap">
  <a href="<c:url value='/' />"><img style="border: 0;" id="csns_logo" alt="csns"
    src="<c:url value='/img/layout/csns_logo.png' />" /></a>
  <tiles:insertAttribute name="lpanel" defaultValue="lpanel.jsp" defaultValueType="template" />
</div>
</div>
