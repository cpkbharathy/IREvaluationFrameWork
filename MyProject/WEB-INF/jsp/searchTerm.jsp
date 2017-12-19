<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
	function submitForm(methodName) {
		window.location=methodName + ".mcs?searchTerm=" + document.getElementById("searchTerm").value;
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Search Term</title>
</head>
<body>
  <form action = "searchTerm.mcs" method="post">
  Text to Search : <input type="text" id="searchTerm" name="searchTerm"/>
  
  <input type="button" value="Query" onclick="submitForm('searchTerm')"/>
  <input type="button" value="AddToStats" onclick="submitForm('addToStats')"/>
  </form>
  
  <c:if test="${queryDataList != null && queryDataList.size() >0}">
    Found ${queryDataList.size()} appls
  	<table>
  	<th>Application ID </th>
  		<c:forEach items="${queryDataList}" var="appl">
  		<tr><td>${appl.key }</td><td>${appl.value }</td></tr>
  		</c:forEach>
  		
  	</table>
  </c:if>
</body>
</html>