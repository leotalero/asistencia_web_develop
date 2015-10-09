<%@ include file="/taglibs.jsp"%>

<link href='${ctx}/fullcalendar-2.2.6/fullcalendar.css' rel='stylesheet' />
<script src='${ctx}/fullcalendar-2.2.6/lib/moment.min.js'></script>
<script src='${ctx}/fullcalendar-2.2.6/fullcalendar.min.js'></script>
<script src='${ctx}/fullcalendar-2.2.6/lang-all.js'></script>
<script type="text/javascript">

	$(document).ready(function() {
		
		var idcontratotemp=document.getElementById("idcontrato").value;
		//alert("idcontrato"+idcontratotemp);
		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},

			defaultView: 'agendaWeek',
			editable: false,
			lang: 'es',
			selectable: false,
			selectHelper: true,
			select: function(start, end) {
				var title = prompt('Event Title:');
				var eventData;
				if (title) {
					eventData = {
							title: title,
							start: start,
							end: end,
						    rendering:reindering,
		                     constraint:constraint,
		                     description:description,
		                     color:color
						};
					$('#calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
				}
				$('#calendar').fullCalendar('unselect');
			},
			editable: false,
			eventLimit: true, // allow "more" link when too many events
			eventLimit: true, // allow "more" link when too many events
		 eventSources: [
			                {
			events: function(start, end, timezone, callback,description) {
		        $.ajax({
		        	cache: false,
				    url: "${ctx}/page/asistencia?action=mostrar_eventoensession&idcontrato="+idcontratotemp,
		            data: {
		                // our hypothetical feed requires UNIX timestamps
		            	start: 'start',
                        end: 'end',
                        id: 'id',
                        title: 'title',
                        rendering:'reindering',
                        constraint:'constraint',
                        description:'description',
                        color:'color'
                        
		            },
		            error: function(jqXHR, textStatus, errorThrown) {
		                alert(jqXHR.statusText);
			        },
		            success: function(doc) {
		            	
		            
		                var events = [];
		                $(doc).each(function() {
		                
		                    events.push({
		                    	 title: $(this).attr('title'),
			                        start: $(this).attr('start'), // will be parsed
			                        end: $(this).attr('end'), // will be parsed
			                        rendering: $(this).attr('rendering'),
			                        constraint: $(this).attr('constraint'), // will be parsed
			                        description: $(this).attr('description'), // will be parsed
			                        color: $(this).attr('color') // will be parsed
		                        
		                    });
		                });
		                callback(events);
		            }
		      
			
			
		        });
		    } ,
			//color: 'blue',   // an option!
           // textColor: 'black' // an option!
			
                   
			                }
			      ],
			eventRender: function(event, element) {
		        element.qtip({
		        	 /* style: {
		                widget: false, // Use the jQuery UI widget classes
		                def: false, // Remove the default styling (usually a good idea, see below)
		               
		            },  
		            style: { classes: 'qtip-jtools' },*/
		          
		            style: { classes: 'qtip-youtube' },
		            content: event.description,
		            position: {
		            	 at: 'center'
		            },
		            show: {
		               /*  effect: function() {
		                    $(this).show('slide', 500);
		                } */
		            },
		            hide: {
		            /*     effect: function() {
		                    $(this).hide('puff', 500);
		                } */
		            }
		        });
		    }
		});
		
	});

</script>

<style>

	

	#calendar {
		max-width: 900px;
		margin: 0 auto;
	}

</style>



<div align="left">

<div>

</div>

<form name="fCalendario" id="fCalendario" action="${ctx}/page/horarios" method="post">
<input type="hidden" name="idcontrato" id="idcontrato" value="${idcontrato}"/>
	<div id='calendar'></div>
</form>
</div>



<div style="padding: 30px 0px;"></div>