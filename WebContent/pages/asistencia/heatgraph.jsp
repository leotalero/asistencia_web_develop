<%@ include file="/taglibs.jsp"%>

<script src="${ctx}/js/graft/themes/dark-green.js"></script>
		<script type="text/javascript">
		var reportePatronGrafica;
		$(document).ready(function() {
			var fechastem= $("#fechas").val();
			 $('#reportePatronGrafica').highcharts({
				 data: {
			            table: 'datatable'
			        },
			        chart: {
			            type: 'column'
			        },
			        title: {
			            text: 'Data extracted from a HTML table in the page'
			        },
			        yAxis: {
			            allowDecimals: false,
			            title: {
			                text: 'Units'
			            }
			        },
			        tooltip: {
			            formatter: function () {
			                return '<b>' + this.series.name + '</b><br/>' +
			                    this.point.y + ' ' + this.point.name.toLowerCase();
			            }
			        }
			        
			    });
		
	});
		</script>
	

<input type="hidden" name="reporteaasistencia" value="${reporteaasistencia}"/>
<input type="hidden" name="fechas" id="fechas" value="${fechas}"/>

<div id="reportePatronGrafica" ></div>







