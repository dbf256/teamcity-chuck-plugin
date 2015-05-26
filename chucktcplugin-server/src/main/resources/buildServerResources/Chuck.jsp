<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="happyImage" scope="request" type="java.lang.String"/>
<jsp:useBean id="sadImage" scope="request" type="java.lang.String"/>
<jsp:useBean id="chuckHappy" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="message" scope="request" type="java.lang.String"/>

<div class="chuck_div" style="padding: 5px; height: 80px;">
<c:choose>
      <c:when test="${chuckHappy}">
        <img style="float: left; padding-right: 10px; width: 70px;" src="<c:url value="${happyImage}"/>"/>
        ${message}
      </c:when>
      <c:otherwise>
        <img style="float: left; padding-right: 10px; width: 70px;" src="<c:url value="${sadImage}"/>"/>
        ${message}
      </c:otherwise>
</c:choose>
</div>
