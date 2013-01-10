<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csns" uri="http://cs.calstatela.edu/csns" %>

<c:if test="${survey.type != 'NAMED'}">
<script>
$(function(){
    $(".viewtable").addClass("halfwidth");
});
</script>
</c:if>

<ul id="title">
<li><a class="bc" href="../list">Surveys</a></li>
<li><a class="bc" href="../results?id=${survey.id}"><csns:truncate
  value="${survey.name}" length="65" /></a></li>
<li>List of Responses</li>
</ul>

<h3>Number of responses: ${fn:length(answerSheets)}</h3>

<c:if test="${not empty question}">
<div>${question.description}</div>
<p>
<c:choose>
  <c:when test="${question.type == 'CHOICE' and question.singleSelection }">
    <c:forEach items="${question.choices}" var="choice" varStatus="choiceStatus">
      <input type="radio" 
        <c:if test="${choiceStatus.index == param.selection}">checked="checked"</c:if>
      /> ${choice} <br />
    </c:forEach>
  </c:when>
  <c:when test="${question.type == 'CHOICE' and not question.singleSelection }">
    <c:forEach items="${question.choices}" var="choice" varStatus="choiceStatus">
      <input type="checkbox"
        <c:if test="${choiceStatus.index == param.selection}">checked="checked"</c:if>
      /> ${choice} <br />
    </c:forEach>
  </c:when>
  <c:when test="${question.type == 'RATING'}">
    ${question.minRating}
    <c:forEach begin="${question.minRating}" end="${question.maxRating}" step="1" var="rating">
      <input type="radio" <c:if test="${rating == param.rating}">checked="checked"</c:if> />
    </c:forEach>
    ${question.maxRating}
  </c:when>
</c:choose>
</p>
</c:if>


<c:if test="${fn:length(answerSheets) > 0}">
<table class="viewtable">
  <tr>
<c:if test="${survey.type == 'NAMED'}">
    <th>CIN</th><th>Name</th>
</c:if>
    <th>Response ID</th><th>Timestamp</th>
  </tr>
<c:forEach items="${answerSheets}" var="answerSheet">
  <tr>
<c:if test="${survey.type == 'NAMED'}">
    <td>${answerSheet.author.cin}</td>
    <td>${answerSheet.author.name}</td>
</c:if>
    <td><a href="view?answerSheetId=${answerSheet.id}">${answerSheet.id}</a></td>
    <td class="datetime">
      <fmt:formatDate value="${answerSheet.date}" pattern="yyyy-MM-dd hh:mm:ss a" />
    </td>
  </tr>
</c:forEach>
</table>
</c:if>
