<%@ include file="/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    	               "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" type="image/x-icon" href="${ctx}/imagen/${favicon}" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><c:if test="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno!=''}"><c:out value="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno}"/> - </c:if>Sistemcobro</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/sc.css">

	<link type="text/css" href="${ctx}/css/jquery/jquery-ui-1.8.21.custom.css" rel="Stylesheet" />	
	<script type="text/javascript" src="${ctx}/js/jquery-1.7.2.min.js"></script>	
	<script type="text/javascript" src="${ctx}/js/jquery-ui-1.8.21.custom.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.validate.js"></script>
	
	<script type="text/javascript">
	function getHTMLLoaging30(){
		return "<img width='30' height='30'  alt='Cargando...' src='${ctx}/imagen/loading_30x30.gif'/>";
	}
		$(document).ready(function() {	
			
			$.ajax(
					{jsonp: 'jsonp',
					  dataType: 'jsonp',
					  url: 'http://myexternalip.com/json',
					  success: function(myip) {
						 // alert(myip);
						  $("#publicip").val(myip);
						  }
					});
			
			$("#btnIngresar").button();
			
			$( "#dmHermesrecordarContrasena" ).dialog({   				
				width: 680,
				height: 450,   				
				modal: true,
				autoOpen: false,
				resizable: true
			});	
		
			
			
			$("#fLogin").validate({
				errorLabelContainer: "#msnLogin",
				errorClass: "invalid",
				rules: {
					<c:if test="${applicationScope[consHermes.APLICACION_SESSION].directivaacceso.validarcaptchaenlogin==1 || sessionScope[consHermes.CAPTCHA_ACTIVADO]==1}">		
						captcha:{
							required: true
						},
					</c:if>
					usuario:{
						required: true
					},
					clave:{
						required: true
					}
				},
				messages: {
					<c:if test="${applicationScope[consHermes.APLICACION_SESSION].directivaacceso.validarcaptchaenlogin==1 || sessionScope[consHermes.CAPTCHA_ACTIVADO]==1}">	
						captcha:{
							required: "Digite el texto de la imagen."
						},
					</c:if>
					usuario: {
						required: "Ingrese un usuario."
					},
					clave:{
						required: "Ingrese una clave."
					}
				}
			});

			$("#j_usuario").focus();
	
		});	
		
		
		function hermesRecordarContrasena(){
			
			$("#dmHermesrecordarContrasena").dialog("open");  
			$("#dmHermesrecordarContrasena").html(getHTMLLoaging30());
			$.ajax({
				cache: false,
				contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
	            type: 'POST',
	            url: "${ctx}/portal/hermesseguridad?action=recordar_contrasena",
	            dataType: "text",
	            error: function(jqXHR, textStatus, errorThrown) {
	                alert(jqXHR.statusText);
		        },
	            success: function(data) {
	            //	alert("regresa");
	               	$("#dmHermesrecordarContrasena").html(data);     
	            }
	        });
		}
		
		function generateCaptcha() {

			 
		    var timestamp = (new Date()).getTime();

		    requestMappingCaptcha = "${ctx}/page/inicio?action=ingresar";

		    jQuery.get(requestMappingCaptcha, timestamp, function(data) {
		                $("#captchaimglogin").slideUp("fast");
  
		                var newSrc = $("#captchaimglogin").attr("src").split("?");
		                newSrc = newSrc[0] + "?" + timestamp;
		                $("#captchaimglogin").attr("src", newSrc);
		                $("#captchaimglogin").slideDown("fast");
		        });
		}
	</script>
  </head>
  <body>
  
	<div style="padding: 20px;"></div>
    <div align="center">
    	<img alt="Sistemcobro" src="${ctx}/imagen/logo_sistemcobro_login.png">
    	<div style="padding: 10px;"></div>
	    <form name="fLogin" id="fLogin" action="${ctx}/page/inicio?action=ingresar" method="post" autocomplete="off">
	   	<input type="hidden" name="publicip" id="publicip"  value="">
	    <table>
	    <c:if test="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno!=''}">
	    <tr>
	    <th colspan="2">
	    <div style="padding: 4px; font-size: 15px;">
	    <c:out value="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno}"/>
	    </div></th>	       
	    </tr>
	    </c:if>
	    
	    <tr>
	    <td colspan="2" style="border: 1px solid #ccc">
	    <div style="padding: 12px;">
	    
	    <table style="border-collapse: separate;">
	    <tr>
	    <tr>
	    <td align="right"><b>Usuario: </b></td>
	    <td><input type="text" name="usuario" tabindex="1" id="j_usuario" style="width: 200px;" autocorrect="off"/></td>    
	    </tr>
	    <tr>
	    <td align="right"><b>Contraseña: </b></td>
	    <td><input type="password" name="clave" tabindex="2" id="j_clave" style="width: 200px;" autocomplete="off"/> </td>    
	    </tr>
	    
	    <c:if test="${applicationScope[consHermes.APLICACION_SESSION].directivaacceso.validarcaptchaenlogin==1 || sessionScope[consHermes.CAPTCHA_ACTIVADO]==1}">	    
		   
		    
		   <tr>
			     <td rowspan="2"  align="right"><b>Imagen: </b></td>
			    <td><div style="border-style: solid; border-width: 1px; margin-bottom: 1px;"><img name="captchaimglogin" id="captchaimglogin" src="${ctx}/jcaptcha.jpg" /></div>
		 </tr>
		   <tr>
				 <td>
			   		No puedes leer esto?<span  class="enlace" onclick="generateCaptcha();"title="Cambiar imagen">							
							cambiar imagen	</span>.
				</td>
			</tr>
			<tr>
				<td align="right"><b>Digite el texto que se <br/>muestra en la imagen: </b></td>
				
				<td>	
			    <input type="text" name="captcha" value="" style="width: 250px;" autocomplete="off"/>
			    </td>   
			</tr>
	    </c:if>
	    
	    <c:if test="${applicationScope[consHermes.APLICACION_SESSION].directivaacceso.verdirectivaaccesologin==1}">	
	    <tr>
	    <td colspan="2"><div style="padding: 4px;" align="center">Por política de seguridad a <c:out value="${applicationScope[consHermes.APLICACION_SESSION].directivaacceso.numintentosbloqueo}"/>	 
	    intentos fallidos el <br/>sistema bloqueara su acceso por <c:out value="${applicationScope[consHermes.APLICACION_SESSION].directivaacceso.tiempomaximobloqueo}"/> minutos.</div></td>	   
	    </tr>
	    </c:if>	
	    </table>
	    
	    
	    <div  align="center" style="padding: 10px;">
	    <button type="submit" id="btnIngresar" name="btnIngresar">Ingresar</button> 
	    </div>
	    
	   
	    
	    <div style="padding: 5px;">
	    <div  align="center" id="msnLogin" >	    
	    <c:if test="${not empty errorMsg}">
	    	<label for="clave" generated="true" class="invalid"><c:out value="${errorMsg}"/></label>
	    </c:if>	    
	    </div>
	    </div>
	    
	    </div>
	    </td>	       
	    </tr>
	    <tr>
	    <td>
	     <span class="enlace" onclick="hermesRecordarContrasena();"
						title="Recuerda usuario y contraseña">Recordar Contraseña
						
						</span>	
	    </tr>
	    </table>
	    
	    
	    
	    
	    
	  
						
		
	    </form>
	    
    </div>
     <div id="dmHermesrecordarContrasena" title="Recordar Contraseña"></div> 
  </body>

</html> 
