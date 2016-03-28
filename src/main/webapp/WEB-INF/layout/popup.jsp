<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="tilesx"
	uri="http://tiles.apache.org/tags-tiles-extras"%>

<link rel="stylesheet" href="<c:url value='/css/magnific-popup.css' />">
<link rel="stylesheet" href="<c:url value='/css/bjqs.css' />">
<link rel="stylesheet" href="<c:url value='/css/popup.css' />">

<script type="text/javascript">
	var ajaxUrl = "<c:url value='/department/${dept}/popup/getpopups' />";
	var userId = null;

	<c:if test='${not empty user}'>
	userId = ${user.id};
	</c:if>
</script>

<script src="<c:url value='/js/bjqs-1.3.js' />"
	type="text/javascript"></script>
<script src="<c:url value='/js/magnific-popup.js' />"
	type="text/javascript"></script>
<script src="<c:url value='/js/popup.js' />" type="text/javascript"></script>

<div id="my-popup" class="mfp-hide white-popup-block" >
	<h3 id="popup-title"></h3>
	<div id="my-slideshow">
		<div id="popup-div">
			<ul class="bjqs" id="popup-ul">
			</ul>
		</div>
	</div>
</div>