<%@ include file="/taglibs.jsp"%>
<script src="${ctx}/js/graft/themes/grid.js"></script>


<script type="text/javascript">
var reportePatronGrafica;
$.tablesorter.addParser({ 
    id: 'datetime', 
    is: function(s) { 
        return false; 
    }, 
    format: function(s) { 
    	if (s.length > 0) {
    		return $.tablesorter.formatFloat(Date.parseExact(s,'dd/MM/yyyy H:mm').getTime());
    	} else {
    	    return 0;
    	}
    }, 
    type: 'numeric' 
});

$(document).ready(function() {
	$("#asistenciastable").tablesorter({ 
		debug: false,
        headers: {},
        cssHeader: "headerSort",
        cssAsc: "headerSortUp",
        cssDesc: "headerSortDown",
        sortMultiSortKey: "shiftKey"
    });

	$("#asistenciastable").fixedtableheader();
	
	
	$("#tabsHorarios").tabs({
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
	
	
	var fechastem= $("#fechas").val();
	 $('#reportePatronGrafica').highcharts({
		 data: {
		        table: 'asistenciastablegraph',
		            	 //startRow: 0,
		                 startColumn:0,
		                 endColumn: 1
		                // firstRowAsNames: false
		        },
		 plotOptions: {
	            column: {
	                dataLabels: {
	                    enabled: true
	                }
	               
	                
	            },
	            line: {
	                dataLabels: {
	                    enabled: true
	                }
	               
	                
	            },
	            area: {
	            	
	                dataLabels: {
	                    enabled: true
	                },
	                
	               
		            negativeColor: '#50B432',
		            color:'#DF5353'
	            }
	        },
		 
				chart: {
					type: 'area',
					 zoomType: 'xy',
	           
	        },

        	
		  credits: {
	            enabled: false
	        },
		 exporting: {
	            enabled: false
	        },
	        title: {
	            text: 'Asistencias',
	            x: -20 //center
	        },
	        subtitle: {
	            text: 'Tendencias',
	            x: -20
	        },
	        yAxis: {
	            allowDecimals: false,
	            title: {
	                text: 'Temprano-Minutos-Tarde'
	            }
	        },
	        xAxis: {
	        	   type: 'datetime',
	               dateTimeLabelFormats: {
	                   day: '%e of %b',
	                	   pointInterval: 12 * 3600 * 1000 // one day
	               },
	        	labels: {
	        		
	        		rotation: -60
	            }
		        
	        },
	        tooltip: {
	           
	            valueDecimals: 2
	        },
	        
	    });
	 
	 
	 
	 
	 
	 
	 
	 
	

});

		



</script>

<div id="tabsHorarios">
	<ul>		
		<li><a href="#idTabsHistoricoAsistencias">&nbsp;Histórico de asistencia<span>&nbsp;</span></a></li>
		<li><a href="${ctx}/page/asistencia?action=mostrar_calendario&idcontrato=${contrato.idcontrato}">&nbsp;Horario<span>&nbsp;</span></a></li>
		<li><a href="${ctx}/page/asistencia?action=mostrar_dashboard&idcontrato=${contrato.idcontrato}">&nbsp;Dashboard<span>&nbsp;</span></a></li>
	</ul>
	
	
	<div id="idTabsHistoricoAsistencias">
<div align="left" >
<fieldset ><legend class="e6" >Histórico</legend>

<c:choose>
		<c:when test="${fn:length(asistencias) eq 0}">
			<div class="msgInfo1" align="left">No se encontraron asistencias.</div>
		</c:when>		
		<c:otherwise>
		
			<div>
			<div id="usuario_acciones" style="padding: 5px 0px 0px 0px;">			
<%--			<b>Ventas selecionadas:</b>&nbsp;&nbsp;&nbsp;<span class="enlace" id="enviar_correo_usuario">enviar por correo</span>--%>
			
			<div style="float: right;"><span class="texto1">Ordenar múltiples columnas: (Shift+[clic columna])</span></div>
			</div>
			<div style="clear: both;"></div>
			<div id="divasistencias" >
			<table style="width:100%" border="0" id="asistenciastable" class="tExcel tRowSelect">
			  <col style="width: 20px;"/>
			  <col style="width: 20px;"/>
		 	 <col style="width: 20px;"/>
			  <col style="width: 30px;"/>
			  <col style="width: 50px;"/>
			  <col style="width: 50px;"/>
			 
			
			  <thead>
			  <tr class="td3">
			    <th>#</th>			    
				<th><span title="IdAsistencia">Idasistencia</span></th>
				<th><span title="Tipo">Tipo</span></th>
				<th><span title="Fuente">Fuente</span></th>
				<th><span title="Horario Entrada">Hora entrada </span></th>	
				<th><span title="Horario Entrada">Hora salida </span></th>	
			  				  				  	
			  	<th>Fecha inicio horario</th>
			  	<th>Fecha fin horario</th>
			  <%-- 	<th>Minutos Flex.</th> --%>
			  	<th>Fecha marcada Entrada</th>
			  	<th>Fecha marcada Salida</th>
			  	<th>Minutos de dif. ingreso</th>
			  	<th>Estado asistencia</th>
			  <%-- 	<th>Fecha crea.</th>
			  	<th>Usuario crea.</th>
			   	<th>Fechamod</th>	
			  	<th>Usuario mod.</th>			  	
			 	<th>ip</th> --%>		
			  </tr>
			  </thead>
			  <tbody>
			  <c:forEach items="${asistencias}" var="asistencia" varStatus="loop">
			  <tr style="color: ${asistencia.estado==2?'':'red'};">
			  	<td><c:out value="${loop.index+1}"/></td>			  		
			  	<td><c:out value="${asistencia.idasistencia}"/></td>
			   	<td><c:out value="${asistencia.asistenciatipo.nombreasistenciatipo}"/></td>  
			 	<td><c:out value="${asistencia.asistenciafuente.nombreasistenciafuente}"/></td>	
			  	  <td><fmt:formatDate value="${asistencia.horaentradaprogramada}" pattern="H:mm"/></td>
			  	<td><fmt:formatDate value="${asistencia.horasalidaprogramada}" pattern="H:mm"/></td>
			 
			    <td><fmt:formatDate value="${asistencia.fechainicioprogramado}" pattern="dd/MM/yyyy H:mm"/></td>
			  	<td><fmt:formatDate value="${asistencia.fechafinprogramado}" pattern="dd/MM/yyyy H:mm"/></td>
			  <%-- 	<td><c:out value="${asistencia.minutosflexibilidad}"/></td>	 --%>
			  	    <td><fmt:formatDate value="${asistencia.fechainiciomarcado}" pattern="dd/MM/yyyy H:mm"/></td>
			  	<td><fmt:formatDate value="${asistencia.fechafinmarcado}" pattern="dd/MM/yyyy H:mm"/></td>
			  	<td style="color: ${asistencia.calculoentrada<=0?'':'red'};"><c:out value="${asistencia.calculoentrada}"/></td>
			  <td style="color: ${asistencia.estadoasistencia!='Tarde'?'':'red'};"><c:out value="${asistencia.estadoasistencia}"/></td>
			 
			  	<%-- <td><fmt:formatDate value="${asistencia.fechacrea}" pattern="dd/MM/yyyy H:mm"/></td>
			  	<td><c:out value="${asistencia.idusuariocrea} "/></td>
			 	<td><fmt:formatDate value="${asistencia.fechamod}" pattern="dd/MM/yyyy H:mm"/></td>	
			  	<td><c:out value="${asistencia.idusuariomod} "/></td>		  		  	
			  <td><c:out value="${asistencia.ip} "/></td>	 --%>
			  </tr>			 
			  </c:forEach>			  
			  </tbody>
			</table>
			
			<!-- ///////////////////////////////////////////--------------------------------- -->
			
			<table hidden="hidden"  style="width:100%" border="0" id="asistenciastablegraph" class="tExcel tRowSelect" >
			  <col style="width: 20px;"/>
			  <col style="width: 20px;"/>
		 	 <col style="width: 20px;"/>
			  <col style="width: 30px;"/>
			  <col style="width: 50px;"/>
			  <col style="width: 50px;"/>
			 
			
			  <thead>
			  <tr class="td3">
			    <th>categorias</th>			    
					
			  				  				  	
			  	<th>Minutos de dif. ingreso</th>
			  	<th>Minutos de dif. Salida</th>
				
			  </tr>
			  </thead>
			  <tbody>
			  <c:forEach items="${asistencias}" var="asistencia" varStatus="loop">
			  
			  
				 <th><c:out value="${asistencia.fechastring}"/></th>
			
			 	<td><c:out value="${asistencia.calculoentrada}"/></td>
				<td><c:out value="${asistencia.calculosalida}"/></td>
			
			  	<%-- <td><fmt:formatDate value="${asistencia.fechacrea}" pattern="dd/MM/yyyy H:mm"/></td>
			  	<td><c:out value="${asistencia.idusuariocrea} "/></td>
			 	<td><fmt:formatDate value="${asistencia.fechamod}" pattern="dd/MM/yyyy H:mm"/></td>	
			  	<td><c:out value="${asistencia.idusuariomod} "/></td>		  		  	
			  <td><c:out value="${asistencia.ip} "/></td>	 --%>
			  </tr>			 
			  </c:forEach>			  
			  </tbody>
			</table>
			<!-- ---------------------------------------------------------------------- -->
			</div>
			</div>
			<div id="reportePatronGrafica" ></div>
			
			<!-- <div align="center">
 				<fieldset><legend class="e6">Reporte de Asistencia</legend>

        	 <div style="width: 100%"  id ="tableauViz"></div>
   
			</fieldset>
			</div> -->
	</c:otherwise>
</c:choose> 
</fieldset>
</div>
</div>
</div>