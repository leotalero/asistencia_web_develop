<%@ include file="/taglibs.jsp"%>

<script type="text/javascript">
$(document)
		.ready(
				function() {
					$('#tableauViz').html('');
					initializeViz();
				});
					
					

	function initializeViz() {
		var placeholderDiv = document.getElementById("tableauViz");
		//var url2 = "http://${server}/trusted/${ticket}/views/ProyectoIntranet/Tendencias?:embed=y&:display_count=no&${parametros}";
		var url2 = "http://${server}/trusted/${ticket}/views/Dashboard_Asistencia_Individual/DashboardAsistencia?:embed=y&:display_count=no&${parametros}&:refresh=si&:toolbar=no";
		viz = new tableauSoftware.Viz(placeholderDiv, url2);
		
		 //alert(url2);
	};				
</script>


 <div align="center">
 <fieldset><legend class="e6">Reporte de Asistencia</legend>

         <div style="width: 100%"  id ="tableauViz"></div>
   
</fieldset>
</div>