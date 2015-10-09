<%@ include file="/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    	               "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" type="image/x-icon" href="${ctx}/imagen/${favicon}" />
<style>

.container {
  width: 500px;
  margin: 150px auto 10px auto;
}

.launcher {
  position: relative;
  text-align: center;
  color: auto;

}



.app-launcher {
  position: absolute;
  left: -320px;
  top: 30px;
display:none;
  z-index: 1;
}

.apps {
  position: relative;
  border: 1px solid #ccc;
  border-color: rgba(0,0,0,.2);
  box-shadow: 0 2px 10px rgba(0,0,0,.2);
  -webkit-transition: height .2s ease-in-out;
  transition: height .2s ease-in-out;
  min-height: 196px;
  overflow-y: auto;
  overflow-x: hidden;
  width: 320px;
  height: auto;
  margin-bottom: 30px;
}

.launcher .button {
  cursor: pointer;
  width: 32px;
  margin: 0 auto;
}

.hide { display: none; }

.apps ul {
  background: #fff;
  margin: 0;
  padding: 28px;
  width: 264px;
  overflow: hidden;
  list-style: none;
}

.apps ul li {
  float: left;
  height: 64px;
  width: 88px;
  color: black;
  padding: 18px 0;
  text-align: center;
}

.apps .more {
  line-height: 40px;
  text-align: center;
  display: block;
  width: 320px;
  background: #f5f5f5;
  cursor: pointer;
  height: 40px;
  overflow: hidden;
  position: absolute;
  text-decoration: none;
  color: #282828;
}

.apps.overflow .more {
  border-bottom: 1px solid #ebebeb;
  left: 28px;
  width: 264px;
  height: 0;
  cursor: default;
  height: 0;
  outline: none;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title><c:if test="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno!=''}"><c:out value="${applicationScope[consHermes.APLICACION_SESSION].nombreexterno}" escapeXml="true"/> - </c:if>Sistemcobro</title>

	<link type="text/css" href="${ctx}/css/sc.css" rel="stylesheet" />			
	<link type="text/css" href="${ctx}/css/jquery/jquery-ui-1.8.21.custom.css" rel="Stylesheet" />
<%-- 	<link type="text/css" href="${ctx}/css/jquery/jqui1.11.1.custom.sc/jquery-ui.min.css" rel="Stylesheet" /> --%>
	
	<script type="text/javascript" src="${ctx}/js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery-ui-1.8.21.custom.min.js"></script>
<%-- 	<script type="text/javascript" src="${ctx}/js/jquery-ui.1.11.1.custom.min.js"></script> --%>
<%-- 	<script type="text/javascript" src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script> --%>
<%-- 	<script type="text/javascript" src="${ctx}/js/jquery-ui-1.10.4.custom.min.js"></script> --%>
	<script type="text/javascript" src="${ctx}/js/jquery.validate.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.tablesorter.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.fixedtableheader.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.fixedtable.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.hotkeys-0.7.9.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.dateformat.js"></script>	
	<script type="text/javascript" src="${ctx}/js/graft/highcharts-custom.js"></script>
	<script type="text/javascript" src="${ctx}/js/cvi_text_lib.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.flipv.js"></script>
	
<link href='${ctx}/js/qtip/jquery.qtip.css' rel='stylesheet' />
<script src='${ctx}/js/qtip/jquery.qtip.js'></script>

<link href='${ctx}/fullcalendar-2.2.6/fullcalendar.css' rel='stylesheet' />
<script src='${ctx}/fullcalendar-2.2.6/lib/moment.min.js'></script>
<script src='${ctx}/fullcalendar-2.2.6/fullcalendar.min.js'></script>
<script src='${ctx}/fullcalendar-2.2.6/lang-all.js'></script>

<link type="text/css" href="${ctx}/css/jquery/jquery.multiselect.css" rel="Stylesheet" />
<script type="text/javascript" src="${ctx}/js/ui.dropdownchecklist-1.4-min.js"></script>
<script type="text/javascript" src="${ctx}/js/jquery.multiselect.js"></script>
<script type="text/javascript" src="${ctx}/js/jquery.multiselect.filter.js"></script>
<script type="text/javascript" src="http://public.tableausoftware.com/javascripts/api/tableau_v8.js"></script>



<link rel="stylesheet" href="${ctx}/FlipClock-master/compiled/flipclock.css">

<script src="${ctx}/FlipClock-master/compiled/flipclock.js"></script>


<script type="text/javascript">
	function campoTrim(obj){
		$(obj).val($.trim($(obj).val()));
	}
	
	function getHTMLLoaging14(txt){
		if(txt=='' || txt=='undefined'){
			return "<span class='img1'><img width='14' height='14' alt='Cargando...' src='${ctx}/imagen/loading_14x14.gif'/></span>";
		}else{
			return "<span class='img1'><img width='14' height='14' alt='Cargando...' src='${ctx}/imagen/loading_14x14.gif'/></span> "+txt;
		}		
	}
		
	function getHTMLLoaging16(txt){
		if(txt=='' || txt=='undefined'){
			return "<span class='img1'><img width='16' height='16' alt='Cargando...' src='${ctx}/imagen/loading_16x16.gif'/></span>";
		} else {
			return "<span class='img1'><img width='16' height='16' alt='Cargando...' src='${ctx}/imagen/loading_16x16.gif'/></span> "+txt;
		}
	}
	
	function getHTMLLoaging30(){
		return "<img width='30' height='30'  alt='Cargando...' src='${ctx}/imagen/loading_30x30.gif'/>";
	}
	
	function getMsn(msn){
		return "<div class='msgInfo2'>"+msn+"</div>";
	}
	
	$(document).ready(function() {
		
		

		
		$(document).bind('keydown', 'alt+s', function() {
			$('#salir_sistema').trigger('click');			
		});	

		$('#salir_sistema').click(function(){
			window.location = $('#salir_sistema').attr('href');			
		});	

		
		$("#tabsOpciones").tabs({
			cache: true,
			spinner: ' '+getHTMLLoaging14(''),			
			ajaxOptions: {
				cache: false,
				error: function( xhr, status, index, anchor ) {
					$( anchor.hash ).html(
						"No se pudo cargar esta pestaña. Informe a su área de tecnología." );
				}
			}
		});

		
		$( "#dmHermesContrasena" ).dialog({   				
			width: 500,
			height: 390,   				
			modal: true,
			autoOpen: false,
			resizable: true
		});			
		
		
		
		
		
	});

	function recargarTabOpcion(index){
		$("#tabsOpciones").tabs("load", index );
	}
	
	function hermesContrasena(){
		$("#dmHermesContrasena").dialog("open");  
		$("#dmHermesContrasena").html(getHTMLLoaging30());
		$.ajax({
			cache: false,
			contentType: 'application/x-www-form-urlencoded; charset=iso-8859-1;', 
            type: 'POST',
            url: "${ctx}/page/hermes?action=ver_cambiar_contrasena",
            dataType: "text",
            error: function(jqXHR, textStatus, errorThrown) {
                alert(jqXHR.statusText);
	        },
            success: function(data) {		                    	                    	
               	$("#dmHermesContrasena").html(data);     
            }
        });
	}
	

	$(document).ready(function(){

		  var scroll = false;
		  var launcherMaxHeight = 396;
		  var launcherMinHeight = 296;
		  
		  // Mousewheel event handler to detect whether user has scrolled over the container
		  $('.apps').bind('mousewheel', function(e){
				if(e.originalEvent.wheelDelta /120 > 0) {
				  // Scrolling up
				}
				else{
					// Scrolling down
					if(!scroll){
						$(".second-set").show();
						$('.apps').css({height: launcherMinHeight}).addClass('overflow');
						scroll =true; 
						$(this).scrollTop(e.originalEvent.wheelDelta);
					}
				}
			});
		  
		  // Scroll event to detect that scrollbar reached top of the container
		  $('.apps').scroll(function(){
			var pos=$(this).scrollTop();
			if(pos == 0){
				scroll =false;
				$('.apps').css({height: launcherMaxHeight}).removeClass('overflow');
				$(".second-set").hide();
			}
		  });
		  
		  // Click event handler to show more apps
		  $('.apps .more').click(function(){
			$(".second-set").show();
			$(".apps").animate({ scrollTop: $('.apps')[0].scrollHeight}).css({height: 296}).addClass('overflow');
		  });
		  
		  // Click event handler to toggle dropdown
		  $(".button").click(function(event){
			event.stopPropagation();
			$(".app-launcher").toggle();
		  });
		  
		  $(document).click(function() {
			//Hide the launcher if visible
			$('.app-launcher').hide();
			});
			
			// Prevent hiding on click inside app launcher
			$('.app-launcher').click(function(event){
				event.stopPropagation();
			});
	  
	});
	// Resize event handler to maintain the max-height of the app launcher
	$(window).resize(function(){
			$('.apps').css({maxHeight: $(window).height() - $('.apps').offset().top});
	});
	
</script>
</head>
<body>
<div id="dmHermesContrasena" title="Módulo de seguridad"></div> 
<table border="0"  width="100%">
<tr height="30">
<td valign="middle" align="center" width="400"><img alt="" src="${ctx}/imagen/logo_sistemcobro.png"></td>
<td align="right" valign="top">
<!--<span style="font-size: 24px; font-weight: bolder; padding-left: 40px;">Repotes Konfigura</span>-->
<div  style="float: right; padding: 5px;">
[<b><c:out value="${sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.grupo.nombre}" /></b>]
<b><c:out value="${sessionScope[consHermes.USUARIO_SESSION].nombre}"/></b> (<b><c:out value="${sessionScope[consHermes.USUARIO_SESSION].usuario}"/>)</b>, 
<span class="enlace" onclick="hermesContrasena();" title="<c:if test="${sessionScope[consHermes.USUARIO_SESSION].usuarioini.diasrestacambiarcontrasena>0}">En <c:out value="${sessionScope[consHermes.USUARIO_SESSION].usuarioini.diasrestacambiarcontrasena}"/> día(s) expira su contraseña.</c:if>">Cambiar contraseña<c:if test="${sessionScope[consHermes.USUARIO_SESSION].usuarioini.diasrestacambiarcontrasena>0}"> (<c:out value="${sessionScope[consHermes.USUARIO_SESSION].usuarioini.diasrestacambiarcontrasena}"/>d)</c:if></span> | <a href="${ctx}/page/inicio?action=salir" id="salir_sistema">Salir</a>  <span class="texto1">(Alt+s)</span>
<br>
<span class="texto1">[Mis aplicaciones]</span>
<div class="launcher"  title="[Mis aplicaciones]" style="float: right; padding: 1px;">

    <div class="button"><img alt="Mis aplicaciones" src="${ctx}/imagen/applaunchergoogle40x40.png"></div>
    <div class="app-launcher">
      <div class="apps">
          <ul class="first-set">
	          <c:forEach items="${usuarioaplicaciones}" var="aplicacion" varStatus="loop">
		      
			     
			     <c:if test="${aplicacion.aplicacion.icono != null}">
			     <li><a href="${aplicacion.aplicacion.linkproduccion}/page/inicio?action=ingresar&ahui=${idaccesohistorico}" target="_blank"><img alt="${aplicacion.aplicacion.nombre}" src="http://ftp.sistemcobro.com/imagen/${aplicacion.aplicacion.icono}"><br/>${aplicacion.aplicacion.nombre}</a></li>	
				 
			     </c:if>
			     <c:if test="${aplicacion.aplicacion.icono == null}">
			      <li><a href="${aplicacion.aplicacion.linkproduccion}/page/inicio?action=ingresar&ahui=${idaccesohistorico}" target="_blank"><img alt="${aplicacion.aplicacion.nombre}" src="http://ftp.sistemcobro.com/imagen/app-default-icon.png"><br/>${aplicacion.aplicacion.nombre}</a></li>	
				 
			     </c:if>
				
			  </c:forEach>
		  </ul>	
			<!--  <a href="#" class="more">More</a>
        <ul class="second-set hide">
        
				 <li><a href="#"><i class="fa fa-dribbble fa-4x"></i></a> </li>
       
        </ul> -->
     <a href="#" class="more">More</a>
      </div>
    </div>
  </div>
<br>
<br>
<div class="displayavatarpequeno" align="left">
		<c:if test="${fotourlencode !=null }">
		<img class="avatarpequeno" src="${fotourlencode}">
		</c:if>
		<c:if test="${fotourlencode ==null }">
		<img class="avatarpequeno" src="http://www.boursematch.com/assets/images/avatar_default.gif">
		</c:if>
		    
</div>
</div>
</td>

<td align="center" valign="top">

  
  

</td>



</tr>

<tr>
<td>

</td>
</tr>
</table>


<div style="padding: 10px;">
<div id="tabsOpciones">
	<ul>
	<li><a href="${ctx}/page/asistencia?action=asistencia_principal">&nbsp;Asistencia<span>&nbsp;</span></a></li>	
		<%-- <c:if test="${sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 1 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 219 || sessionScope[consHermes.USUARIO_SESSION].usuarioaplicacion.idgrupo == 220 }">
		<li><a href="${ctx}/page/asistencia?action=asistencia_principal">&nbsp;Asistencia<span>&nbsp;</span></a></li>	
		</c:if>	 --%>
		
	</ul>
</div>
</div>
<div style="padding: 50px 0px;" ></div>

</body>
</html>
