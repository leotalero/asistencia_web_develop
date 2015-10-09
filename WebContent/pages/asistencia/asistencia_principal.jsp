<%@ include file="/taglibs.jsp"%>



<script type="text/javascript">

function getLocalIP(cb) {

    var RTCPeerConnection = window.webkitRTCPeerConnection || window.mozRTCPeerConnection || window.RTCPeerConnection,
    rtc = new RTCPeerConnection({
        iceServers: []
    });
    if (window.mozRTCPeerConnection) rtc.createDataChannel('', {reliable: false});
    rtc.onicecandidate = function(e) {
        if (cb && e.candidate) {
            cb(e.candidate.candidate.split(" ")[4]);
            cb = null;
        }
    }; 

    rtc.createOffer(function(resp) {
            var moz=resp.sdp && 
                   resp.sdp.indexOf("c=IN IP4") !==0 && 
                   resp.sdp.split("c=IN IP4")[1].trim().split(/\s/)[0];
            if(moz && moz!=="0.0.0.0"){
                 cb( moz );
            }else{
                 rtc.setLocalDescription(resp);
            }
    }, Boolean);
} /* end getIP() */



  function refresh() {
    
    	obtenerhora();
    
}
 
  setInterval(refresh, 60000);

var clock;

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

	
		
	
	$("#fasistencia").validate({
		errorLabelContainer: "#msnBuscarAsistencia",
		errorClass: "invalid",
		//wrapper: "li",
		rules: {
			captcha:{
				required: true
			}
		},
		messages: {
			captcha: {
				required: "Ingrese el texto de la imagen. "
			}
		},
		submitHandler: function(form) {	
			$("#btnMarcarSalida").click();
			//actualizarBusquedaDehistorico();
		}
		
	});
	
	$("#btnMarcarEntrada").button();
	$("#btnMarcarSalida").button();
	//$("#btnMarcarEntrada").focus();
	
	$("#btnMarcarEntrada").click(function(){
		//alert("marcar");
	//	$("#divasistenciaprincipal").dialog("open");
	//	$("#cajaHistoricoAsistencia").html(getHTMLLoaging30());
	var idhorarioasig=$("#idhorarioasignadovalido").val();
	var publicipt=$("#publicip").val();
		$.ajax({
			cache: false,
			contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
            type: 'POST',	            
            url: "${ctx}/page/asistencia?action=asistencia_marcarentrada&idhorarioasignadovalido="+idhorarioasig+"&publicip="+publicipt,
            dataType: "text",
            error: function(jqXHR, textStatus, errorThrown) {
                alert(jqXHR.statusText);
                
                asistenciprincipal();
	        },
            success: function(data) {		                    	                    	
               //	$("#cajaHistoricoAsistencia").html(data);  
               //	actualizarBusquedaDehistorico('#fasistencia');
              alert("Marco entrada correctamente");
            	asistenciprincipal();
            }
        });
	});
	$("#btnMarcarSalida").click(function(){
		//alert("marcar");
	var captchatxt=$("#captcha").val();
	var idhorarioasig=$("#idhorarioasignadovalido").val();
	//	alert("marcar"+captchatxt);
		 if ($("#fasistencia").valid()) {
				$.ajax({
					cache: false,
					contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
		            type: 'POST',	            
		            url: "${ctx}/page/asistencia?action=asistencia_marcarsalida&captcha="+captchatxt+"&idhorarioasignadovalido="+idhorarioasig,
		            dataType: "text",
		            error: function(jqXHR, textStatus, errorThrown) {
		                alert(jqXHR.statusText);
		              
		                asistenciprincipal();
		                generateCaptcha();
			        },
		            success: function(data) {		                    	                    	
		               	//$("#cajaHistoricoAsistencia").html(data); 
		            	//actualizarBusquedaDehistorico('#fasistencia');
		            	  
		            	asistenciprincipal();
		            	 alert("Marco salida correctamente");
		            }
		        });
        } else {
            
        }
	
	});
});



	function actualizarBusquedaDehistorico(){
		//alert("histo");
		$("#cajaHistoricoAsistencia").html(getHTMLLoaging30()); 
       	$("#btnMarcarEntrada").attr('disabled',true);
      // if ($('#fasistencia').valid()) {
            
        	$.ajax({
    			cache: false,
    			contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
                type: 'POST',
                url:"${ctx}/page/asistencia?action=asistencia_buscarhistorico",
                dataType: "text",
                error: function(jqXHR, textStatus, errorThrown) {
                    alert(jqXHR.statusText);
    	        },
                success: function(data) {
                    $("#cajaHistoricoAsistencia").html(data);
                   $("#btnMarcarEntrada").attr('disabled',false);	 
                }
            });
        	
      //  } else {
            
       // }
		
	}

	
	
	function asistenciprincipal(){
		//alert("vaa....");
		//$("#divasistenciaprincipal").dialog("open");
		$("#divasistenciaprincipal").html(getHTMLLoaging30());
		$.ajax({
			cache: false,
			contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
            type: 'POST',	            
            url: "${ctx}/page/asistencia?action=asistencia_principal",
            dataType: "text",
            error: function(jqXHR, textStatus, errorThrown) {
                alert(jqXHR.statusText);
               
	        },
            success: function(data) {	
           	//alert("regresa..");
               	$("#divasistenciaprincipal").html(data); 
               	obtenerhora();
            	//actualizarBusquedaDehistorico('#fasistencia');
            	
            }
        });
	}

	
	function obtenerhora(){
		//alert("busca hora");
	
		//$("#divhora").html(getHTMLLoaging30()); 
			verificabotones();
			$.ajax({
	            type: 'POST',
	            url: "${ctx}/page/asistencia?action=obtenerhora_servidor",
	            dataType: "text",
	            success: function(data) {	
	            //alert("data:"+data);
	            	var clock;
					clock = $('.clock').FlipClock({
	            		clockFace: 'HourlyCounter',
	            		
	            		//clockFace: 'TwelveHourClock',
	                    autoStart: false,
	                    language: 'es',
	                    callbacks: {
	                    	stop: function() {
	                    		$('.message').html('The clock has stopped!');
	                    	}
	                    }
	                });
					
					var segundos=parseInt(data);
				//alert(segundos);
	            	clock.setTime(segundos);
	            	clock.start();
	            	
	            	//actualizarBusquedaDehistorico('#fasistencia');
	            	
	            }
	        });
		
	}	
	function validarEntero(valor){ 
    	valor = parseInt(valor);
     	//Compruebo si es un valor numérico 
     	if (isNaN(valor)) { 
           	 //entonces (no es numero) devuelvo el valor cadena vacia 
           	 return false;
     	}else{ 
           	 //En caso contrario (Si era un número) devuelvo el valor 
           	 return true;
     	} 
	}
	
	function verificabotones(){ 
	//	alert("entra");
    	if('${entrada.valor}'=='visible'){
    		$('#btnMarcarEntrada').removeAttr('disabled','disabled');
    		$('#btnMarcarEntrada').removeAttr('visibility','visibility');
    	}else if( '${entrada.valor}'=='hidden'){
    		//alert("entrada hidden");
    		
    		 $('#btnMarcarEntrada').attr('disabled','disabled');
    		$('#btnMarcarEntrada').attr('visibility','visibility');
    	}
    	
    	if('${salida.valor}'=='visible'){
    		//alert("deshabilita");
    	
    		$('#btnMarcarSalida').removeAttr('disabled','disabled');
    		$('#btnMarcarEntrada').removeAttr('visibility','visibility');
    	}else if( '${salida.valor}'=='hidden'){
    	//	alert("salida hidden");
    		
    		 $('#btnMarcarSalida').attr('disabled','disabled');
    		 $('#btnMarcarEntrada').attr('visibility','visibility');
    	}
    	
	}
	function generateCaptcha() {
		obtenerhora();
		 
	    var timestamp = (new Date()).getTime();

	    //requestMappingCaptcha = "${ctx}/portal/hermesseguridad?action=recordar_contrasena";

	    //jQuery.get(requestMappingCaptcha, timestamp, function(data) {
	                $("#captchaimg").slideUp("fast");

	              

	              
	                var newSrc = $("#captchaimg").attr("src").split("?");
	                newSrc = newSrc[0] + "?" + timestamp;
	                $("#captchaimg").attr("src", newSrc);
	                $("#captchaimg").slideDown("fast");
	      //  });
	}
	
obtenerhora();
actualizarBusquedaDehistorico();
</script>

<div id="dmDetalleEmpleado" title="Detalle Empleado"></div>
<div id="dmMensajeEmpleado" title="Mensaje"></div>
<div id="dmMensajeContrato" title="Contrato"></div>
<div id="dmNuevoEmpleado" title="Nuevo Empleado"></div>
<div id="dmEditarEmpleado" title="Editar Empleado"></div>
<div id="dmEditarEmpleadoContrasena" title="Cambiar Contraseña"></div>
<div id="dmEditarEmpleadoAplicacion" title="Editar Grupo"></div>
<div id="dmEditarEmpleadoCuenta" title="Editar Cuenta"></div>
<div id="dmNuevoContrato" title="Nuevo Contrato"></div>
<div id="dmEditarContrato" title="Editar Contrato"></div>
<div id="dmNuevaPropiedad" title="Nueva Propiedad"></div>
<div id="dmEditarPropiedad" title="Editar Propiedad"></div>
<div id="dmEditarHorarioAsignado" title="Editar Horario Asignado"></div>


<div id="divasistenciaprincipal">

	
	<form name="fasistencia" id="fasistencia" action="${ctx}/page/asistencia">

	<input type="hidden" name="idhorarioasignadovalido" id="idhorarioasignadovalido"  value="${idhorarioasignadovalido}">
	<input type="hidden" name="publicip" id="publicip"  value="">
	<fieldset ><legend align="left" class="e6" >Módulo asistencia</legend>
	       
	        	
	   
      	<table style="width:40em" border="0"  align="center"   >
               
        <tr align="center">
        <td  valign="middle" align="center">
       
			
			<div class="message"></div>
	
		</td>
         </tr>
         <tr align="center">
	       
			 
			 <td colspan="1"> 
			 <div align="center" >  
	          <div   align="center" class="clock"  style="width: 42em"></div> 
	          <div class="texto1"><fmt:formatDate value="${dateservidor}" pattern="dd/MM/yyyy"/></div> 
	          <div class="texto1">Horario sincronizado con la hora oficial mundial basado en NTP(Network Time Protocol)</div>  
			 </div> 
			
		 </tr>
		<tr>
			<td>&nbsp;
			</td>
		</tr>
		 <tr  align="center">
		
		 <td>
	  <c:if test='${not empty mensaje && mensaje!=""}'>
	  <div  class="msgInfo1">
		 <c:out value="${mensaje}"></c:out>
	 </div>
	  </c:if>
	  <c:if test='${not empty mensajeinfo && mensajeinfo!=""}'>
	   <div  class="msgInfo1">
		 <c:out value="${mensajeinfo}"></c:out>
		 </div>
	  </c:if>
		
		 </td>
		 
         </tr>
       </table>
       
       <table style="width:100%" border="0"  >
		
	      <tr>
	        	<td align="center">
	        	<c:choose>
	        	<c:when test="${salida.valor=='visible'}">
	         
	         <div style="width: 50em">
				
				
			         <table class="caja" style="width:100%" border="0"  >
					          <tr>
					     <th rowspan="2" nowrap="nowrap" style="text-align: right;">Imagen:
					       <div class="texto2">Le mostramos esta imagen 
					       <br> para evitar que no marque 
					       <br>involuntariamente su salida.</div> 
					     </th>
						   
						    <td><div style="border-style: solid; border-width: 1px; margin-bottom: 1px;"><img name="captchaimg" id="captchaimg" src="${ctx}/jcaptcha.jpg" /></div>
						   
						</tr>
						<tr>
							 <td>
						   		No puede leer esto?<span  class="enlace" onclick="generateCaptcha();"title="Cambiar imagen">							
										cambiar imagen	</span>.
							</td>
						</tr>
							<tr>
							<th nowrap="nowrap" style="text-align: right;">Digite el texto que se muestra en la imagen:</th>
							
							<td>	
						    <input type="text" required="required" id="captcha" name="captcha" value="" style="width: 250px;" autocomplete="off"/>
						    </td>    
						  </tr>
						 </table>
						 
						  <div  align="center" id="msnBuscarAsistencia" ></div>
						 <div style="padding: 10px;">
						   <button type="button"   id="btnMarcarSalida" name="btnMarcarSalida"><span style="font-size: 16px"> Marcar salida </span></button>
	        			 </div>
			
	          </div>
	         
	        	</c:when>
	        <c:otherwise>
	        	
	        	<c:if test="${not empty entrada && entrada.valor=='visible'}"> 
	        	 <div style="padding: 10px;">
	           		<button   type="button" id="btnMarcarEntrada" name="btnMarcarEntrada"><span style="font-size: 16px">Marcar ingreso</span></button> 
	      		</div>
			</c:if>
	        </c:otherwise>
	        	</c:choose>
	        	
	          
	          </td>
	      </tr>

	    </table> 
       </fieldset>
     
       </form> 







<div  align="center" id="cajaHistoricoAsistencia">
</div> 


</div>

