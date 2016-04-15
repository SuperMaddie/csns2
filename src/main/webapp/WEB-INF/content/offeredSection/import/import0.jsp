<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script>
	$(function(){
		$("#loading").hide();
		$("#file-form").submit(function(){
			$("#loading").show();
		});
	});
</script>

<ul id="title">
<li><a class="bc" href="<c:url value='/department/${dept}/preRegistration?term=${term.code}' />">Offered Sections</a></li>
<li>Import Section</li>

<li class="align_right"><a href="<c:url value='/department/${dept}/offeredSection/offer?term=${term.code}' />" ><img alt="[Add Section]"
  title="Add Section" src="<c:url value='/img/icons/page_add.png' />" /></a></li>
</ul>

<div id="import">
	<div>
		<p><b>Important Notes:</b><br></p>
		<ul>
			<li>The file must be in excel format(.xls or .xlsx).</li>
			<li>The first row must contain the titles.</li>
			<li>The titles must contain the followings:
				<p><i>Term, Subj, Cat, Sect, Class Nbt, Title, Day, Start, End, Bldg/Room, Type, Instructor, 
				Prgrss Unt, Available, Acad Group, Class Type, Mode</i></p>
			</li>
			<li>For now Available, Acad Group, Class Type and Mode are being ignored.</li>
			
		</ul>
		
		<form id="file-form" action="<c:url value='/department/${dept}/offeredSection/import' />" method="post" enctype="multipart/form-data">
			<b>Select File: </b>
		  	<input type="file" name="file" style="width: 25%;" class="leftinput" required
		  		accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
			<br><div class="error">${message}</div>
		<p><input type="hidden" name="term" value="${term.code}" />
		<input type="hidden" name="_page" value="0" />
		<input type="submit" name="_target1" id="next" value="Next" class="subbutton" /></p>
		</form>
	
	</div>
</div>

<div id="loading" class="loading">
	<img src="<c:url value='/img/style/loading.gif' />">
</div>
