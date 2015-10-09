<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="resourcesApp" var="resource" />
<fieldset>
	<legend><c:out value="${errorTitle}" /></legend>
	<div class="msgWarning1"><c:out value="${errorMsg}" /> <a href="<fmt:message bundle="${resource}" key="urlIntranet" />">link</a></div>
</fieldset>