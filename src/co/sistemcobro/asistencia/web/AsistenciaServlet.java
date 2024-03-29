package co.sistemcobro.asistencia.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.net.ntp.TimeStamp;
import org.apache.log4j.Logger;
import org.springframework.format.datetime.joda.MillisecondInstantPrinter;

import com.google.gson.Gson;
import com.octo.captcha.module.servlet.image.SimpleImageCaptchaServlet;

import co.sistemcobro.all.constante.EstadoEnum;
import co.sistemcobro.all.exception.DirectivaContrasenaException;
import co.sistemcobro.all.exception.LogicaException;
import co.sistemcobro.all.exception.SessionException;
import co.sistemcobro.all.util.Util;
import co.sistemcobro.hermes.bean.AplicacionBean;
import co.sistemcobro.hermes.bean.UsuarioBean;
import co.sistemcobro.hermes.bean.UsuarioPreguntaSeguraBean;
import co.sistemcobro.hermes.constante.AplicacionEnum;
import co.sistemcobro.hermes.constante.ClaveCambioEnum;
import co.sistemcobro.hermes.constante.UsuarioTipoBusquedaEnum;
import co.sistemcobro.asistencia.bean.Asistencia;
import co.sistemcobro.asistencia.bean.BotonEntrada;
import co.sistemcobro.asistencia.bean.BotonSalida;
import co.sistemcobro.asistencia.bean.Botones;
import co.sistemcobro.asistencia.constante.AsistenciaClasificacionEnum;
import co.sistemcobro.asistencia.constante.AsistenciaFuenteEnum;
import co.sistemcobro.asistencia.constante.AsistenciaTipoEnum;
import co.sistemcobro.asistencia.constante.Constante;
import co.sistemcobro.asistencia.ejb.AsistenciaEJB;
import co.sistemcobro.hermes.ejb.UsuarioEJB;
import co.sistemcobro.rrhh.bean.Contrato;
import co.sistemcobro.rrhh.bean.EmpleadoBean;
import co.sistemcobro.rrhh.bean.EmpleadoPropiedad;
import co.sistemcobro.rrhh.bean.Evento;
import co.sistemcobro.rrhh.bean.Horario;
import co.sistemcobro.rrhh.bean.HorarioAsignado;
import co.sistemcobro.rrhh.constante.FrecuenciasHorariosEnum;
import co.sistemcobro.rrhh.ejb.ContratoEJB;
import co.sistemcobro.rrhh.ejb.EmpleadoEJB;
import co.sistemcobro.rrhh.ejb.HorarioEJB;


/**
 * 
 * @author Leonardo talero
 * 
 */
@WebServlet(name = "AsistenciaServlet", urlPatterns = { "/page/asistencia" })
public class AsistenciaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(AsistenciaServlet.class);

	ResourceBundle config = ResourceBundle.getBundle(Constante.FILE_CONFIG_ASISTENCIA);

	@EJB
	private UsuarioEJB usuarioEJB;
	@EJB
	private AsistenciaEJB asistenciaEJB;

	@EJB
	private EmpleadoEJB empleadoEJB;

	@EJB
	private HorarioEJB horarioEJB;
	@EJB
	private ContratoEJB contratoEJB;
	
	public AsistenciaServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");
		action = action == null ? "" : action;
		try {
			if (action.equals("asistencia_principal")) {
				asistencia_principal(request, response);
			} else if (action.equals("asistencia_buscarhistorico")) {
				asistencia_buscarhistorico(request, response);
			} else if (action.equals("obtenerhora_servidor")) {
				obtenerhora_servidor(request, response);
			} else if (action.equals("asistencia_marcarentrada")) {
				asistencia_marcarentrada(request, response);
			} else if (action.equals("asistencia_marcarsalida")) {
				asistencia_marcarsalida(request, response);
			}else if(action.equals("mostrar_calendario")){
				mostrar_calendario(request, response);
			}else if(action.equals("mostrar_evento")){
				mostrar_evento(request, response);
			}
			else if(action.equals("mostrar_eventoensession")){
				mostrar_eventoensession(request, response);
			}
			else if(action.equals("mostrar_dashboard")){
				mostrar_dashboard(request, response);
			}
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}

	public void asistencia_principal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		BotonEntrada entrada=new BotonEntrada();
		BotonSalida salida=new BotonSalida();
		try {
			
			String codigoidentificacion = user.getCodigoidentificacion();
			List<EmpleadoBean> empleados = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(codigoidentificacion);
			EmpleadoBean empleado=null;
			if(empleados.size()!=0){
				 empleado = empleados.get(0);
			
			Long idempleado = empleado.getIdempleado();
			 Contrato contrato = contratoEJB.getUltimoContratosporIdEmpleado(idempleado);
			
			 if(contrato!=null){
				 
			
			 
			 
			List<HorarioAsignado> horarios = horarioEJB.getHorariosAsignadosporContratoActivos(contrato.getIdcontrato());
			if(horarios.size()!=0){
				Timestamp dateservidor = asistenciaEJB.Obtenerhora_servidor();
				Horario horariovalido=null;
				HorarioAsignado horarioasignadovalido=new HorarioAsignado();
				String flag = "";	
				for(HorarioAsignado ho:horarios){
						 List<HashMap<String, Object>> datafechashorario = retornafechasdehorario(dateservidor,ho,contrato);
							
						
						for(HashMap<String, Object> fechas:datafechashorario){
							 GregorianCalendar fechaentrada=new GregorianCalendar(); 
							 GregorianCalendar fechasalida=new GregorianCalendar(); 
							 fechaentrada .setTime((Date)fechas.get("fechaeventoentrada"));
							 fechasalida .setTime((Date)fechas.get("fechaeventosalida"));
							 String fechaservidor = Util.timestampToString(dateservidor, "dd/MM/yyyy");
							 //int a = dateservidor.get(Calendar.DAY_OF_YEAR);
							
							String fechaentradastring = Util.timestampToString(new Timestamp(fechaentrada.getTimeInMillis()), "dd/MM/yyyy");
							String fechacompara="";
							if(ho.getFrecuenciaasignacion().getNocturno()==1){
								fechacompara=fechaentradastring;
							}else{
								String fechasalidastring = Util.timestampToString(new Timestamp(fechasalida.getTimeInMillis()), "dd/MM/yyyy");
								
								fechacompara=fechasalidastring;
							}
							//String fechasalidastring = Util.timestampToString(new Timestamp(fechasalida.getTimeInMillis()), "dd/MM/yyyy");
							 
							if(fechaservidor.equals(fechacompara) ){
							
								 horariovalido=ho.getHorario();
									horarioasignadovalido=ho;
									flag="ok";
									break;
								}
							 
							
						}
						if(flag.equals("ok")){
							break;
						}
						//Timestamp fechainicio = ho.getValidezinicio();
						//Timestamp fechafin = ho.getValidezfin();
						//if(dateservidor.after(fechainicio) && dateservidor.before(fechafin)){
						//	horariovalido=ho.getHorario();
						//	horarioasignadovalido=ho;
						//	break;
						//}
						
					}
					


				
					if(horarioasignadovalido.getValidezfin()!=null || horarioasignadovalido.getValidezfin()!=null){
						List<Asistencia> asistencias= asistenciaEJB.getAsistenciaHistorico(contrato.getIdcontrato());
						Asistencia ultimaasistencia=new Asistencia();


///////////////////////////////
						if(asistencias.size()!=0){
							ultimaasistencia=asistencias.get(0);
							 List<HashMap<String, Object>> datafechashorario = retornafechasdehorario(dateservidor,horarioasignadovalido,contrato);
							 GregorianCalendar fechaentrada=new GregorianCalendar(); 
							 GregorianCalendar fechasalida=new GregorianCalendar(); 
							 GregorianCalendar dateserver=new GregorianCalendar(); 
							 dateserver.setTime(dateservidor);
							 Date fechahorarioinicio=null;
							 Date fechahorariofin=null;
							for(HashMap<String, Object> datafechas:datafechashorario){
								 fechaentrada .setTime((Date)datafechas.get("fechaeventoentrada"));
								 fechasalida .setTime((Date)datafechas.get("fechaeventosalida"));
								
								
								if(dateserver.get(Calendar.DAY_OF_YEAR)==fechaentrada.get(Calendar.DAY_OF_YEAR)){
									fechahorarioinicio=(Date)datafechas.get("fechaeventoentrada");
									fechahorariofin=(Date)datafechas.get("fechaeventosalida");
									break;
								}
							}
							
							
						Botones botones = evaluacondicion(dateservidor, horarioasignadovalido, contrato ,fechahorarioinicio,fechahorariofin,ultimaasistencia);	
							
						
						entrada.setValor(botones.getBotonentrada().getValor());
						
						salida.setValor((botones.getBotonsalida().getValor()));
						//entrada.setValor("visible");
						
						//salida.setValor("visible");
						request.setAttribute("dateservidor", dateservidor);
						request.setAttribute("entrada", entrada);
						request.setAttribute("salida", salida);
						request.setAttribute("idhorarioasignadovalido", horarioasignadovalido.getIdhorarioasignado());
						request.setAttribute("mensajeinfo", botones.getMensaje());
						request.getRequestDispatcher("../pages/asistencia/asistencia_principal.jsp").forward(request, response);
					
						}else{
							//throw new LogicaException("El empleado no tiene registrado ninguna asistencia");
							entrada.setValor("visible");
							salida.setValor("hidden");
							
							request.setAttribute("entrada", entrada);
							request.setAttribute("salida", salida);
							request.setAttribute("idhorarioasignadovalido", horarioasignadovalido.getIdhorarioasignado());
							//request.setAttribute("mensaje", "El empleado no tiene registrado ninguna asistencia");
							request.getRequestDispatcher("../pages/asistencia/asistencia_principal.jsp").forward(request, response);
						
						}
				}else{//si horario asignado no tiene validez final
						
						entrada.setValor("hidden");
						salida.setValor("hidden");
						
						request.setAttribute("entrada", entrada);
						request.setAttribute("salida", salida);
						request.setAttribute("mensaje", "El usuario no tiene un horario valido para este momento ");
						request.getRequestDispatcher("../pages/asistencia/asistencia_principal.jsp").forward(request, response);
						
					}
					
				}else{
					
					entrada.setValor("hidden");
					salida.setValor("hidden");
					
					request.setAttribute("entrada", entrada);
					request.setAttribute("salida", salida);
					
					request.setAttribute("mensaje", "El usuario no tiene un horario asignado actualmente");
					request.getRequestDispatcher("../pages/asistencia/asistencia_principal.jsp").forward(request, response);
				}
			
			 }else{
				 
					entrada.setValor("hidden");
					salida.setValor("hidden");
					
					request.setAttribute("entrada", entrada);
					request.setAttribute("salida", salida);
					
					request.setAttribute("mensaje", "No se encuentra un contrato activo registrado en el aplicativo RRHH");
					request.getRequestDispatcher("../pages/asistencia/asistencia_principal.jsp").forward(request, response);
			 }
			
			}else{
				
				entrada.setValor("hidden");
				salida.setValor("hidden");
				
				request.setAttribute("entrada", entrada);
				request.setAttribute("salida", salida);
				
				request.setAttribute("mensaje", "No se encuentra el empleado registrado en el aplicativo RRHH");
				request.getRequestDispatcher("../pages/asistencia/asistencia_principal.jsp").forward(request, response);
			
			}
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}


	public void asistencia_buscarhistorico(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
	try {
		
		
		String codigoidentificacion = user.getCodigoidentificacion();
		List<EmpleadoBean> empleados = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(codigoidentificacion);
		EmpleadoBean empleado=null;
		if(empleados.size()!=0){
			 empleado = empleados.get(0);
			
			 Long idempleado = empleado.getIdempleado();
			 Contrato contrato = contratoEJB.getUltimoContratosporIdEmpleado(idempleado);
			 if(contrato!=null){
				 List<Asistencia> asistencias= asistenciaEJB.getAsistenciaHistorico(contrato.getIdcontrato());
				 request.setAttribute("asistencias", asistencias);
				 request.setAttribute("contrato", contrato); 
			 }else{
				 
			 }
			 
		}
////////////////////////////////////////tableu
	try {
	//String idempleado = request.getParameter("idempleado");
	
	List<EmpleadoBean> empleadost = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(user.getCodigoidentificacion());
	EmpleadoBean empleadot = new EmpleadoBean();
			if(empleadost!=null && empleadost.size()>0){
			empleadot=empleadost.get(0);
			//List<Area> areas = contratoEJB.getAreas();
			//request.setAttribute("areas", areas);
			//request.setAttribute("estadoActivo", "ACTIVO");
			//request.setAttribute("estadoDeshabilitado", "DESHABILITADO");
			TableauServlet ts = new TableauServlet();
			//final String user = "SISTEMCOBRO"+"\\"+"dtroncoso";
			//final String user ="appsg";
			final String usert=config.getString("rrhh.tableau.userG");
			final String wgserver=config.getString("rrhh.tableau.server");
			//final String wgserver = "172.16.1.63:8081";
			String iplocal=Inet4Address.getLocalHost().getHostAddress();
			//String ticket= ts.getTrustedTicket(wgserver, usert, iplocal);
			//String parametros="numeroidentificacionP=";
			//parametros+=empleadot.getEmpleadoidentificacion().getNumeroidentificacion();
			//request.setAttribute("ticket", ticket);
			//request.setAttribute("server", wgserver);
			//request.setAttribute("parametros", parametros);
			
			}else{
			
			}


	} catch (Exception e) {
	logger.error(e.toString(), e.fillInStackTrace());
	response.sendError(1, e.getMessage());
	}
			request.getRequestDispatcher("../pages/asistencia/asistencia_lista.jsp").forward(request, response);
		

	} catch (Exception e) {
		logger.error(e.toString(), e.fillInStackTrace());
		response.sendError(1, e.getMessage());
	}

	}


	public void obtenerhora_servidor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
	try {
		
		Timestamp dateservidor = asistenciaEJB.Obtenerhora_servidor();

		int horas = dateservidor.getHours()*3600;
		int minutos = dateservidor.getMinutes()*60;
		int segundos = dateservidor.getSeconds();
		
		int horaservidor = horas+minutos+segundos+1;
		PrintWriter out = response.getWriter();
		out.println(horaservidor);
		out.close();
		request.setAttribute("dateservidor", dateservidor);
	} catch (Exception e) {
		logger.error(e.toString(), e.fillInStackTrace());
		response.sendError(1, e.getMessage());
	}

	}

	
	public void asistencia_marcarentrada(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		try {
		if(user!=null){
			String ipAddress = getClientIpAddr(request);
			String publicip = request.getParameter("publicip");
			
				String codigoidentificacion = user.getCodigoidentificacion();
			List<EmpleadoBean> empleados = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(codigoidentificacion);
			EmpleadoBean empleado=null;
			if(empleados.size()!=0){
				 empleado = empleados.get(0);
			}
			Long idempleado = empleado.getIdempleado();
			 Contrato contrato = contratoEJB.getUltimoContratosporIdEmpleado(idempleado);
			
			List<HorarioAsignado> horarios = horarioEJB.getHorariosAsignadosporContratoActivos(contrato.getIdcontrato());
			Timestamp dateservidor = asistenciaEJB.Obtenerhora_servidor();
			Horario horariovalido=null;
			HorarioAsignado horarioasignadovalido=new HorarioAsignado();
			String idhorariovalido=request.getParameter("idhorarioasignadovalido");
			   horarioasignadovalido = horarioEJB.getHorariosAsignadosporId(Long.valueOf(idhorariovalido));
			   horariovalido=horarioEJB.getHorariosporId(horarioasignadovalido.getIdhorario());
			   /*
				for(HorarioAsignado ho:horarios){
					Timestamp fechainicio = ho.getValidezinicio();
					Timestamp fechafin = ho.getValidezfin();
					if(dateservidor.after(fechainicio) && dateservidor.before(fechafin)){
						horariovalido=ho.getHorario();
						horarioasignadovalido=ho;
						break;
					}
					
				}*/
						
				List<Asistencia> asistencias= asistenciaEJB.getAsistenciaHistorico(contrato.getIdcontrato());
				request.setAttribute("asistencias", asistencias);
			
				 List<HashMap<String, Object>> datafechashorario = retornafechasdehorario(dateservidor,horarioasignadovalido,contrato);
				 GregorianCalendar fechaentrada=new GregorianCalendar(); 
				 GregorianCalendar fechasalida=new GregorianCalendar(); 
				 GregorianCalendar dateserver=new GregorianCalendar(); 
				 dateserver.setTime(dateservidor);
				 Date fechahorarioinicio=null;
				 Date fechahorariofin=null;
				for(HashMap<String, Object> datafechas:datafechashorario){
					 fechaentrada .setTime((Date)datafechas.get("fechaeventoentrada"));
					 Date fechaentradat = fechaentrada.getTime();
					 fechasalida .setTime((Date)datafechas.get("fechaeventosalida"));
					Date fechasalidat = fechasalida.getTime();
					
					if(dateserver.get(Calendar.DAY_OF_YEAR)==fechaentrada.get(Calendar.DAY_OF_YEAR)){
						fechahorarioinicio=(Date)datafechas.get("fechaeventoentrada");
						fechahorariofin=(Date)datafechas.get("fechaeventosalida");
						break;
					}
				}
				
				
				Asistencia asistencia=new Asistencia();
				 
				asistencia.setIdasistenciafuente(Long.valueOf(AsistenciaFuenteEnum.MODULOASISTENCIA.getIndex()));
				asistencia.setIdasistenciatipo(Long.valueOf(AsistenciaTipoEnum.HORARIO.getIndex()));
				asistencia.setIdcontrato(contrato.getIdcontrato());
				asistencia.setHoraentradaprogramada(horariovalido.getHoraentrada());
				asistencia.setHorasalidaprogramada(horariovalido.getHorasalida());
				asistencia.setFechainicioprogramado(new Timestamp(fechahorarioinicio.getTime()));
				asistencia.setFechafinprogramado(new Timestamp(fechahorariofin.getTime()));
				asistencia.setFechainiciomarcado(dateservidor);
				asistencia.setMinutosflexibilidad(horariovalido.getMinutosflex());
				asistencia.setIp(ipAddress);
				GregorianCalendar fechahorainiciocalendar=new GregorianCalendar();
				fechahorainiciocalendar.setTime(fechahorarioinicio);
				
				Long minutosdiferencia = dateserver.getTime().getTime()-fechahorainiciocalendar.getTime().getTime();
				Long minutosdiferenciamin=minutosdiferencia/60000;
				Long minutosdiferenciaconflexi = (dateserver.getTime().getTime()-(fechahorainiciocalendar.getTime().getTime()+(asistencia.getMinutosflexibilidad()*1000*60)))/60000;
				if(minutosdiferenciamin>0 && minutosdiferenciaconflexi<=0){
					asistencia.setIdclasificacion(AsistenciaClasificacionEnum.A_TIEMPO_TOLERANCIA.getIndex());
					
				}else if(minutosdiferenciamin<=0){
					asistencia.setIdclasificacion(AsistenciaClasificacionEnum.A_TIEMPO.getIndex());
				} else if(minutosdiferenciamin>0){
					asistencia.setIdclasificacion(AsistenciaClasificacionEnum.TARDE.getIndex());
				}
				
				
				if(publicip==null){
					asistencia.setPublicip(ipAddress);
				}else{
					asistencia.setPublicip(publicip);
				}
		
				asistencia.setIdusuariocrea(user.getCodusuario());
				asistencia.setEstado(EstadoEnum.ACTIVO.getIndex());
				
				
			Integer resultado = asistenciaEJB.insertaMaracionentrada(asistencia);
			if (resultado >0) {
				PrintWriter out = response.getWriter();
				out.println(asistencia.getIdcontrato());
				out.close();

			} else {
				throw new LogicaException("Error al insertar la asistencia");
			}
		}else{
			throw new LogicaException("Existen valores en blanco y no se puede guardar el registro");
		}
			
				
			// else {
			//	throw new LogicaException("El campo Clave debe conincidir con el campo Clave (repetir)");
			//}

		} catch (LogicaException e) {

			response.sendError(1, e.toString());
		} catch (Exception e) {

			response.sendError(1, e.toString());
		}
	}
	
	public void asistencia_marcarsalida(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		Asistencia asistencia=new Asistencia();
		try {
		if(user!=null){
			String ipAddress = getClientIpAddr(request);
			   
			final String captcha = request.getParameter("captcha");
			
			boolean captchaPassed = SimpleImageCaptchaServlet.validateResponse(request, captcha);
			if (!captchaPassed) {
				throw new Exception("El texto de la imagen es incorrecto.");
			}else{
				
			
			String codigoidentificacion = user.getCodigoidentificacion();
			List<EmpleadoBean> empleados = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(codigoidentificacion);
			EmpleadoBean empleado=null;
			if(empleados.size()!=0){
				 empleado = empleados.get(0);
			}
			Long idempleado = empleado.getIdempleado();
			 Contrato contrato = contratoEJB.getUltimoContratosporIdEmpleado(idempleado);
			
			List<HorarioAsignado> horarios = horarioEJB.getHorariosAsignadosporContratoActivos(contrato.getIdcontrato());
			Timestamp dateservidor = asistenciaEJB.Obtenerhora_servidor();
			Horario horariovalido=null;
			HorarioAsignado horarioasignadovalido=new HorarioAsignado();
			String idhorariovalido=request.getParameter("idhorarioasignadovalido");
			   horarioasignadovalido = horarioEJB.getHorariosAsignadosporId(Long.valueOf(idhorariovalido));
			   horariovalido=horarioEJB.getHorariosporId(horarioasignadovalido.getIdhorario());
				/*for(HorarioAsignado ho:horarios){
					Timestamp fechainicio = ho.getValidezinicio();
					Timestamp fechafin = ho.getValidezfin();
					if(dateservidor.after(fechainicio) && dateservidor.before(fechafin)){
						horariovalido=ho.getHorario();
						horarioasignadovalido=ho;
					}
					
				}
					*/	
				List<Asistencia> asistencias= asistenciaEJB.getAsistenciaHistorico(contrato.getIdcontrato());
				request.setAttribute("asistencias", asistencias);
				
				 List<HashMap<String, Object>> datafechashorario = retornafechasdehorario(dateservidor,horarioasignadovalido,contrato);
				 GregorianCalendar fechaentrada=new GregorianCalendar(); 
				 GregorianCalendar fechasalida=new GregorianCalendar(); 
				 GregorianCalendar dateserver=new GregorianCalendar(); 
				 dateserver.setTime(dateservidor);
				 Date fechahorarioinicio=null;
				 Date fechahorariofin=null;
				for(HashMap<String, Object> datafechas:datafechashorario){
					 fechaentrada .setTime((Date)datafechas.get("fechaeventoentrada"));
					 fechasalida .setTime((Date)datafechas.get("fechaeventosalida"));
					
					
					if(dateserver.get(Calendar.DAY_OF_YEAR)==fechaentrada.get(Calendar.DAY_OF_YEAR)){
						fechahorarioinicio=(Date)datafechas.get("fechaeventoentrada");
						fechahorariofin=(Date)datafechas.get("fechaeventosalida");
						break;
					}
				}
				
				
				
				if(asistencias.size()!=0){
					asistencia=asistencias.get(0);
					
					//Timestamp temp = ;
					if(asistencia.getFechafinmarcado()==null){
						asistencia.setFechafinmarcado(dateservidor);
						
						asistencia.setIdusuariomod(user.getCodusuario());
						asistencia.setEstado(EstadoEnum.ACTIVO.getIndex());
						
						
					Integer resultado = asistenciaEJB.editarMaracionentradaconmarcacionSalida(asistencia);
						if (resultado >0) {
							PrintWriter out = response.getWriter();
							out.println(asistencia.getIdcontrato());
							out.close();
		
						} else {
							throw new LogicaException("Error al editar los datos de la asistencia");
						}	
					}else{
						
						Date fech=new Date(asistencia.getFechafinmarcado().getTime());
						throw new LogicaException("ya marco salida no puede marcar nuevemente"+fech);
					}
						

			
				}
				
			
			
			}		 
			
		}else{
			throw new LogicaException("Existen valores en blanco y no se puede guardar el registro");
		}
			
				
			
		

		} catch (LogicaException e) {

			response.sendError(1, e.toString());
		} catch (Exception e) {

			response.sendError(1, e.toString());
		}
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	private List<Integer> diadelasemana(int frecuencia){
		List<Integer> dia=new ArrayList<Integer>();
		if(frecuencia==FrecuenciasHorariosEnum.LUNES.getIndex() || frecuencia==FrecuenciasHorariosEnum.LUNES_NOCTURNO.getIndex()){
			dia.add(2);
		}else if(frecuencia==FrecuenciasHorariosEnum.MARTES.getIndex() || frecuencia==FrecuenciasHorariosEnum.MARTES_NOCTURNO.getIndex() ){
			dia.add(3);
		}
		else if(frecuencia==FrecuenciasHorariosEnum.MIERCOLES.getIndex() || frecuencia==FrecuenciasHorariosEnum.MIERCOLES_NOCTURNO.getIndex() ){
			dia.add(4);
		}
		else if(frecuencia==FrecuenciasHorariosEnum.JUEVES.getIndex() || frecuencia==FrecuenciasHorariosEnum.JUEVES_NOCTURNO.getIndex() ){
			dia.add(5);
		}
		else if(frecuencia==FrecuenciasHorariosEnum.VIERNES.getIndex() || frecuencia==FrecuenciasHorariosEnum.VIERNES_NOCTURNO.getIndex() ){
			dia.add(6);
		}else if(frecuencia==FrecuenciasHorariosEnum.SABADO.getIndex() || frecuencia==FrecuenciasHorariosEnum.SABADO_NOCTURNO.getIndex() ){
			dia.add(7);
		}
		else if(frecuencia==FrecuenciasHorariosEnum.DOMINGO.getIndex() || frecuencia==FrecuenciasHorariosEnum.DOMINGO_NOCTURNO.getIndex() ){
			dia.add(1);
		}else if(frecuencia==FrecuenciasHorariosEnum.LUNES_VIERNES.getIndex() || frecuencia==FrecuenciasHorariosEnum.LUNES_VIERNES_NOCTURNO.getIndex() ){
			dia.add(2);dia.add(3);dia.add(4);dia.add(5);dia.add(6);
			
		}
		else if(frecuencia==FrecuenciasHorariosEnum.lUNES_SABADO.getIndex() || frecuencia==FrecuenciasHorariosEnum.LUNES_SABADO_NOCTURNO.getIndex() ){
			dia.add(2);dia.add(3);dia.add(4);dia.add(5);dia.add(6);dia.add(7);
			
		}
		else if(frecuencia==FrecuenciasHorariosEnum.FIN_DE_SEMANA.getIndex() || frecuencia==FrecuenciasHorariosEnum.FIN_DE_SEMANA_NOCTURNO.getIndex() ){
			dia.add(7);	dia.add(1);
			
		}
		
		return dia;
		
	}
	
	
	private Botones evaluacondicion(Date dateservidor,HorarioAsignado ho,Contrato contrato,Date fechahorarioinicio,Date fechahorariofin,Asistencia ultimaasistencia) throws LogicaException, ParseException{
		Botones botones =new Botones();
		Date date=null;
		int flag=0;
		 GregorianCalendar fechaentradaultimaaistencia=new GregorianCalendar(); 
		 GregorianCalendar fechasalidaultimaasistencia=new GregorianCalendar(); 
		 GregorianCalendar dateserver=new GregorianCalendar(); 
		 dateserver.setTime(dateservidor);
		
		if(ho!=null){
			 Date dateservidort = Util.stringToDate( Util.dateToString(dateservidor, "dd/MM/yyyy"), "dd/MM/yyyy");
				Date valiniciot =Util.stringToDate(Util.timestampToString(ho.getValidezinicio(), "dd/MM/yyyy"), "dd/MM/yyyy") ;
				Date valfin = Util.stringToDate(Util.timestampToString(ho.getValidezfin(), "dd/MM/yyyy"), "dd/MM/yyyy") ;
				
					if ((dateservidort.after(valiniciot)||dateservidort.equals(valiniciot)) && (dateservidort.before(valfin)||dateservidort.equals(valfin))){
			//	if (dateservidor.after(ho.getValidezinicio()) && dateservidor.before(ho.getValidezfin())){
					
					if(ultimaasistencia!=null && ultimaasistencia.getFechainiciomarcado()==null ){
						Integer extras = ho.getHorario().getMinutospermitidos();
						Timestamp hafun = ultimaasistencia.getFechafinprogramado();
						GregorianCalendar hafuncalendar=new GregorianCalendar();
						hafuncalendar.setTime(hafun);
						hafuncalendar.set(Calendar.MINUTE,extras);
						Date tm = hafuncalendar.getTime();
						String tmformat = Util.dateToString(tm, "dd/MM/yyyy HH:mm");
						botones.setMensaje("El usuario ya termin� su ciclo de asistencia, el siguiente estar� habilitado desde el "+tmformat+".");
						
						BotonEntrada botonentrada=new BotonEntrada();
						botonentrada.setValor("hidden");
						BotonSalida botonsalida=new BotonSalida();
						botonsalida.setValor("hidden");
						botones.setBotonentrada(botonentrada);
						botones.setBotonsalida(botonsalida);
					}else{
						
					
							Date ultimamarcacion = new Date(ultimaasistencia.getFechainiciomarcado().getTime());
							GregorianCalendar gcultimamarcaion=new GregorianCalendar();
							gcultimamarcaion.setTime(ultimamarcacion);
								if(ho.getFrecuenciaasignacion().getNocturno()==0){
									
								}else{
									gcultimamarcaion.add(Calendar.DAY_OF_YEAR, 1);
								}
								
								Integer extrast = ho.getHorario().getMinutospermitidos();
								Timestamp hafunt = ultimaasistencia.getFechafinprogramado();
								GregorianCalendar hafuncalendart=new GregorianCalendar();
								hafuncalendart.setTime(hafunt);
								hafuncalendart.set(Calendar.MINUTE,extrast);
								Date tmt = hafuncalendart.getTime();
							
							if(dateserver.after(hafuncalendart)){
								Timestamp f = ultimaasistencia.getFechafinprogramado();
								Date hafuDate=new Date(f.getTime());
								//int dianow = dateserver.get(Calendar.DAY_OF_YEAR);
								fechasalidaultimaasistencia .setTime(hafuDate);
														
								
								if(dateserver.get(Calendar.DAY_OF_YEAR)==fechasalidaultimaasistencia.get(Calendar.DAY_OF_YEAR)){
									
									if(ultimaasistencia.getFechafinmarcado()==null){
										Integer extras = ho.getHorario().getMinutospermitidos();
										Timestamp hafun = ultimaasistencia.getFechafinprogramado();
										GregorianCalendar hafuncalendar=new GregorianCalendar();
										hafuncalendar.setTime(hafun);
										hafuncalendar.set(Calendar.MINUTE,extras);
										
										if(dateserver.getTime().before(hafuncalendar.getTime())){
											BotonEntrada botonentrada=new BotonEntrada();
											botonentrada.setValor("hidden");
											BotonSalida botonsalida=new BotonSalida();
											botonsalida.setValor("visible");
											botones.setBotonentrada(botonentrada);
											botones.setBotonsalida(botonsalida);
										}else{
											
											
											BotonEntrada botonentrada=new BotonEntrada();
											botonentrada.setValor("visible");
											BotonSalida botonsalida=new BotonSalida();
											botonsalida.setValor("hidden");
											botones.setBotonentrada(botonentrada);
											botones.setBotonsalida(botonsalida);
										}
										
										
									}else{
										BotonEntrada botonentrada=new BotonEntrada();
										botonentrada.setValor("visible");
										BotonSalida botonsalida=new BotonSalida();
										botonsalida.setValor("hidden");
										botones.setBotonentrada(botonentrada);
										botones.setBotonsalida(botonsalida);
									}
									
									
								}else{
									BotonEntrada botonentrada=new BotonEntrada();
									botonentrada.setValor("visible");
									BotonSalida botonsalida=new BotonSalida();
									botonsalida.setValor("hidden");
									botones.setBotonentrada(botonentrada);
									botones.setBotonsalida(botonsalida);
								}
								
								
								
								
							}else{
								Integer extras = ho.getHorario().getMinutospermitidos();
								Timestamp hafun = ultimaasistencia.getFechafinprogramado();
								GregorianCalendar hafuncalendar=new GregorianCalendar();
								hafuncalendar.setTime(hafun);
								hafuncalendar.set(Calendar.MINUTE,extras);
								Date tm = hafuncalendar.getTime();
								if(dateservidor.after(hafuncalendar.getTime())){
									BotonEntrada botonentrada=new BotonEntrada();
									botonentrada.setValor("visible");
									BotonSalida botonsalida=new BotonSalida();
									botonsalida.setValor("hidden");
									botones.setBotonentrada(botonentrada);
									botones.setBotonsalida(botonsalida);
								}else{
									
									if(ultimaasistencia.getFechafinmarcado()==null){
										BotonEntrada botonentrada=new BotonEntrada();
										botonentrada.setValor("hidden");
										BotonSalida botonsalida=new BotonSalida();
										botonsalida.setValor("visible");
										botones.setBotonentrada(botonentrada);
										botones.setBotonsalida(botonsalida);
									}else{
										String tmformat = Util.dateToString(tm, "dd/MM/yyyy HH:mm");
										botones.setMensaje("El usuario ya termin� su ciclo de asistencia, el siguiente estar� habilitado desde el "+tmformat+".");
										
										BotonEntrada botonentrada=new BotonEntrada();
										botonentrada.setValor("hidden");
										BotonSalida botonsalida=new BotonSalida();
										botonsalida.setValor("hidden");
										botones.setBotonentrada(botonentrada);
										botones.setBotonsalida(botonsalida);
									}
									
									
									
									
								}
								
							}
					
					}
					
				}else{
					BotonEntrada botonentrada=new BotonEntrada();
					botonentrada.setValor("hidden");
					BotonSalida botonsalida=new BotonSalida();
					botonsalida.setValor("hidden");
					botones.setBotonentrada(botonentrada);
					botones.setBotonsalida(botonsalida);
				}
		
				
			
				
		}
		return botones;
	}
	
	
	

	private List<HashMap<String, Object>> retornafechasdehorario(Date dateservidor,HorarioAsignado ho,Contrato contrato) throws LogicaException, ParseException{
		Botones botones =new Botones();
		Date date=null;
		int flag=0;
		
		 List<HashMap<String, Object>> datahorariofechas=new ArrayList<HashMap<String,Object>>();
		if(ho!=null && ho.getValidezinicio()!=null && ho.getValidezfin()!=null){
			 Date dateservidort = Util.stringToDate( Util.dateToString(dateservidor, "dd/MM/yyyy"), "dd/MM/yyyy");
			Date valiniciot =Util.stringToDate(Util.timestampToString(ho.getValidezinicio(), "dd/MM/yyyy"), "dd/MM/yyyy") ;
			
			
			
				Date valfin;
			if(ho.getFrecuenciaasignacion().getNocturno()==1){
				Timestamp fechatemp = Util.addDays(ho.getValidezfin(), 1);
				ho.setValidezfin(fechatemp);
				valfin = Util.stringToDate(Util.timestampToString(ho.getValidezfin(), "dd/MM/yyyy"), "dd/MM/yyyy") ;
				
			}else{
				 valfin = Util.stringToDate(Util.timestampToString(ho.getValidezfin(), "dd/MM/yyyy"), "dd/MM/yyyy") ;
					
			}
			
				if ((dateservidort.after(valiniciot)||dateservidort.equals(valiniciot)) && (dateservidort.before(valfin)||dateservidort.equals(valfin))){
				//si esta en el rango de dias	para el horario
					GregorianCalendar start =new  GregorianCalendar();
				    Timestamp Validezinicio = ho.getValidezinicio();
				     date = new Date(Validezinicio.getTime());
				    Time horaentrada = ho.getHorario().getHoraentrada();
				    date.setHours(horaentrada.getHours());
				    date.setMinutes(horaentrada.getMinutes());
				    date.setSeconds(horaentrada.getSeconds());
				    
		            start.setTime(date);
		            
		            GregorianCalendar end;
					Timestamp Validezfin;
					Date datefin;
					//si tiene fecha finalizacion
											if(ho.getValidezfin()==null){
								            	// Calendar calendarEnd=GregorianCalendar.getInstance();
								            	Date dte=new Date();
								            	
								            	GregorianCalendar calendarEnd=new GregorianCalendar();
								            	calendarEnd.setTime(dte);
								            	    calendarEnd.set(calendarEnd.MONTH,11);
								            	    calendarEnd.set(calendarEnd.DAY_OF_MONTH,31);
					
								            	    // returning the last date
								            	    Date endDate=calendarEnd.getTime();
								            	    Timestamp findea�o = new Timestamp(endDate.getTime());
								            	    
								            	ho.setValidezfin(findea�o);
								            	  end = new GregorianCalendar();
												     Validezfin = ho.getValidezfin();
												   datefin = new Date(Validezfin.getTime());
												    Time horasalida = ho.getHorario().getHorasalida();
												    datefin.setHours(horasalida.getHours());
												    datefin.setMinutes(horasalida.getMinutes());
												    datefin.setSeconds(horasalida.getSeconds());
												    
										            end.setTime(datefin);
										            
										          
								            	
								            }else{
								            	
								            	
								            	  end =new  GregorianCalendar();
												     Validezfin = ho.getValidezfin();
												     
												    datefin = new Date(Validezfin.getTime());
												    Time horasalida = ho.getHorario().getHorasalida();
												    datefin.setHours(horasalida.getHours());
												    datefin.setMinutes(horasalida.getMinutes());
												    datefin.setSeconds(horasalida.getSeconds());
												    
										            end.setTime(datefin);
										            
										          
										          
										            
							                     	
								            }
								            
								            
		            
		            
					int dias = Util.diferenciaEnDias(start, end);
					int dia=0;
					
					 int tempfrec = ho.getHorario().getIdfrecuenciaasignacion().intValue();
					 List<Integer> diadelasemana = diadelasemana(tempfrec);
					 //data tiene la informacion de los dias incluidos en el horario
					 List<HashMap<String, Object>> data = Util.diferenciaEnDiasconFrecuencia(start, end,diadelasemana,tempfrec);
					 Date fechaeventoentrada = null;
					 Date fechaeventosalida =null;
					
					for(HashMap<String, Object> datainfo:data){
						 HashMap<String, Object> horariofechas=new HashMap<String, Object>();
						GregorianCalendar fecha = (GregorianCalendar)datainfo.get("fecha");
						
						 fechaeventoentrada = fecha.getTime();
						
					
					
						 
						 if( ho.getFrecuenciaasignacion().getNocturno()==1){//si es nocturno
								
								long m = datefin.getTime();
								long k = date.getTime();
								Time m1 = new Time(m);
								Time k1 = new Time(k);
								Date temp = fecha.getTime();
								temp.setDate(fecha.getTime().getDate()+1);
								temp.setHours(m1.getHours());
								temp.setMinutes(m1.getMinutes());
								temp.setSeconds(m1.getSeconds());
								
								 fechaeventosalida =temp;
							
							}else{
								 fechaeventosalida =fecha.getTime();
								 fechaeventosalida.setHours( ho.getHorario().getHorasalida().getHours());
								 fechaeventosalida.setMinutes( ho.getHorario().getHorasalida().getMinutes());
								 fechaeventosalida.setSeconds( ho.getHorario().getHorasalida().getSeconds());
								   
							}
						 
					 
						horariofechas.put("fechaeventoentrada", fechaeventoentrada);
						horariofechas.put("fechaeventosalida", fechaeventosalida) ;
						 datahorariofechas.add(horariofechas);
					}//termina for hash map data
					
				
					
					
					
					

					
				}
				
			
				
		}
		return datahorariofechas;
	}
@SuppressWarnings("unchecked")
public void mostrar_calendario(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			String idcontrato = request.getParameter("idcontrato");
			if(idcontrato!=null && !idcontrato.equals("") ){
			request.setAttribute("idcontrato", idcontrato);
		
			List<Evento> l=(List<Evento>) request.getSession().getAttribute("eventos");
				if(l==null){
					mostrar_evento(request,response);
				}else{
					
				}
			
			}
			else{
				
			}
			request.getRequestDispatcher("../pages/horarios/horario_nuevo.jsp").forward(request, response);
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}

	}

public void mostrar_evento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
	try {
		String idcontrato = request.getParameter("idcontrato");
		if(idcontrato!=null){
			List<HorarioAsignado> horariosasignados=horarioEJB.getHorariosAsignadosporContrato(Long.valueOf( idcontrato));
			
				List<Evento> l = new ArrayList<Evento>();
				////////////////////////hoy
				Evento d = new Evento();
				Date actual=new Date(); 
				String actualt = Util.dateToString(actual, "yyyy-MM-dd");
				d.setId(999);
				 
				 
				 d.setStart(actualt);
				//d.setEnd(actualt);
				 d.setColor("#F4DD12");
				// d.setTitle("hoy");
				d.setRendering("background");
				d.setDescription("hoy");
				l.add(d);
				 ////////////////////////fin hoy
				 
				int i=0;
				Date date=null,datefin=null;
				for(HorarioAsignado x:horariosasignados){
					
					
						    GregorianCalendar start =new  GregorianCalendar();
							    Timestamp Validezinicio = x.getValidezinicio();
							     date = new Date(Validezinicio.getTime());
							    Time horaentrada = x.getHorario().getHoraentrada();
							    date.setHours(horaentrada.getHours());
							    date.setMinutes(horaentrada.getMinutes());
							    date.setSeconds(horaentrada.getSeconds());
							    
					            start.setTime(date);
					            
					           // start.add(Calendar.MINUTE, 60);
					            String endDatestart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
					            endDatestart = endDatestart.replace(" ", "T");
					            
					            String  endDateend = null;
					            Timestamp Validezfin=null;
					         	 GregorianCalendar end = new GregorianCalendar();
					         	 
					            if(x.getValidezfin()==null){
					            	// Calendar calendarEnd=GregorianCalendar.getInstance();
					            	Date dte=new Date();
					            	
					            	GregorianCalendar calendarEnd=new GregorianCalendar();
					            	calendarEnd.setTime(dte);
					            	    calendarEnd.set(calendarEnd.MONTH,11);
					            	    calendarEnd.set(calendarEnd.DAY_OF_MONTH,31);
		
					            	    // returning the last date
					            	    Date endDate=calendarEnd.getTime();
					            	    Timestamp findea�o = new Timestamp(endDate.getTime());
					            	    
					            	x.setValidezfin(findea�o);
					            	  end = new GregorianCalendar();
									     Validezfin = x.getValidezfin();
									   datefin = new Date(Validezfin.getTime());
									    Time horasalida = x.getHorario().getHorasalida();
									    datefin.setHours(horasalida.getHours());
									    datefin.setMinutes(horasalida.getMinutes());
									    datefin.setSeconds(horasalida.getSeconds());
									    
							            end.setTime(datefin);
							            
							           // start.add(Calendar.MINUTE, 60);
							            endDateend = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(datefin);
							            endDateend = endDateend.replace(" ", "T");
					            	
					            }else{
					            	
					            	
					            	  end =new  GregorianCalendar();
									     Validezfin = x.getValidezfin();
									    datefin = new Date(Validezfin.getTime());
									    Time horasalida = x.getHorario().getHorasalida();
									    datefin.setHours(horasalida.getHours());
									    datefin.setMinutes(horasalida.getMinutes());
									    datefin.setSeconds(horasalida.getSeconds());
									    
							            end.setTime(datefin);
							            
							           // start.add(Calendar.MINUTE, 60);
							            endDateend = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(datefin);
							            endDateend = endDateend.replace(" ", "T");
							            
				                     	
					            }
					           
					            
							Evento evento = new Evento();
							evento.setId(i);
							evento.setTitle(x.getHorario().getNombrehorario()+" [ "+x.getHorario().getHoraentrada()+"-"+x.getHorario().getHorasalida()+"]");
							evento.setStart(endDatestart+"-05:00");
							EmpleadoBean empleado = empleadoEJB.buscarEmpleadosporId(x.getIdempleado());
							x.setHorario(horarioEJB.getHorariosporId(x.getIdhorario()));
							evento.setDescription("Empleado:"+empleado.getNombres()+""+empleado.getApellidos()+" - Identificacion: "+empleado.getEmpleadoidentificacion().getNumeroidentificacion()+" - "
									+ " Horario:"+x.getHorario().getNombrehorario()+"["+x.getHorario().getHoraentrada()+"-"+x.getHorario().getHorasalida()+"]-[frecuencia:"+x.getFrecuenciaasignacion().getNombrefrecuencia()+"]"
									+"- [Validez Inicial: "+x.getValidezinicio()+" Validez Final: "+x.getValidezfin()+"]");
							evento.setEnd(endDateend+"-05:00");
							if(x.getEstado()==EstadoEnum.DESHABILITADO.getIndex()){
								evento.setColor("#AE3B50");
							}
							
							int dias = Util.diferenciaEnDias(start, end);
							int dia=0;
							
							 int tempfrec = x.getHorario().getIdfrecuenciaasignacion().intValue();
							 List<Integer> diadelasemana = diadelasemana(tempfrec);
							 List<HashMap<String, Object>> data = Util.diferenciaEnDiasconFrecuencia(start, end,diadelasemana,tempfrec);
							 HashMap<String, Object> firsthashmap=null;
							 GregorianCalendar primerafecha=null;
							 Date primerafechatemp=null;
							 Integer dianumero =null;
							 GregorianCalendar fecha=null;
							 if(data.size()!=0){
								firsthashmap = data.get(0);
								  primerafecha = (GregorianCalendar) firsthashmap.get("fecha");
								 primerafechatemp=primerafecha.getTime();
								 dianumero = (Integer) firsthashmap.get("dia");
								
								 fecha = primerafecha;
							}
							
							 
							//for(int j=0;j<=data.size();j++){
								int j=1;
							for(HashMap<String, Object> p:data){
								fecha=(GregorianCalendar)p.get("fecha");
								 Date fechaevento = fecha.getTime();
								 Date fechaeventosalida =null;
							
								 if( x.getHorario().getIdfrecuenciaasignacion()>=11){//si es nocturno
										
										long m = datefin.getTime();
										long k = date.getTime();
										Time m1 = new Time(m);
										Time k1 = new Time(k);
										Date temp = fecha.getTime();
										temp.setDate(fecha.getTime().getDate()+1);
										temp.setHours(m1.getHours());
										temp.setMinutes(m1.getMinutes());
										temp.setSeconds(m1.getSeconds());
										
										 fechaeventosalida =temp;
									
									}else{
										 fechaeventosalida =fecha.getTime();
										 fechaeventosalida.setHours( x.getHorario().getHorasalida().getHours());
										 fechaeventosalida.setMinutes( x.getHorario().getHorasalida().getMinutes());
										 fechaeventosalida.setSeconds( x.getHorario().getHorasalida().getSeconds());
										   
									}
								
								 
								 
								 //fechaeventosalida =fecha.getTime(); ;
								 
								 String formatoini = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaevento);
						            String fechaparaeventoinicio = formatoini.replace(" ", "T");
						           
						          String formatofin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaeventosalida);
						            String fechaparaeventofin = formatofin.replace(" ", "T");
						        
						          
						            
								Evento event=new Evento();
								event.setId(j);
								
								event.setStart(fechaparaeventoinicio+"-05:00");
								event.setEnd(fechaparaeventofin+"-05:00");
								event.setTitle(evento.getTitle());
								event.setDescription(evento.getDescription());
								
								
								
								if(x.getEstado()==EstadoEnum.DESHABILITADO.getIndex()){
									event.setColor("#AE3B50");
								}	
										l.add(event);
								// fecha.add(Calendar.DAY_OF_YEAR, 1);
										j++;
							}
							i++;
							
						
				}
				
		        
				
				
				
				
				
				
				
			
///////////////////////////////////////////cumplea�os/////////////////////////////////////////////
				Timestamp dateservidor = asistenciaEJB.Obtenerhora_servidor();
				Date datserver=new Date(dateservidor.getTime());
				String dateserverstring = Util.dateToString(dateservidor, "dd/MM/yyyy");
				List<EmpleadoBean> empleados = empleadoEJB.getEmpleadosporfechacumple(Util.addDays("dd/MM/yyyy", dateserverstring, 7));
				int p=1;
				 List<HashMap<String, Object>> fechascumple=new ArrayList<HashMap<String,Object>>();
				String fechacumple = "";
				Evento eventorepetido=new Evento();
				eventorepetido.setDescription("Cumplea�os de ");
				Evento eventonuevo=new Evento();
				eventonuevo.setDescription("Cumplea�os de ");
				int eventosnuevos=0;
				List<Evento> listadoeventosnuevos =new ArrayList<Evento>();
				 Date actual1 = new Date();
				 GregorianCalendar cal=new GregorianCalendar();
				 cal.setTime(actual1);
				cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-5);
				Date tem = cal.getTime();
				 GregorianCalendar findemes=new GregorianCalendar();
				 findemes.setTime(actual);
				 findemes.set(Calendar.MONTH, findemes.get(Calendar.MONTH)+1);
				Date findemest = findemes.getTime();
				 for(EmpleadoBean f:empleados){
					
					Contrato contrato = contratoEJB.getUltimoContratosporIdEmpleado(f.getIdempleado());
							if(f.getFechanacimiento()!=null){
								Timestamp fechanacimiento = f.getFechanacimiento();
							
							Date fechanacimientod= new Date(fechanacimiento.getTime());
							fechanacimientod.setYear((new Date().getYear()));
							 fechacumple = Util.dateToString(fechanacimientod, "yyyy-MM-dd");
							
							
							if(fechanacimientod.after(tem)){
									HashMap<String, Object> fechamap=new HashMap<String, Object>();
									fechamap.put("fecha", fechacumple);
									fechamap.put("idempleado", f.getIdempleado());
									fechamap.put("nombre", f.getNombres()+" "+f.getApellidos());
									if(contrato!=null && contrato.getArea()!=null && contrato.getCargo()!=null){
										fechamap.put("area_cargo", " Area:"+contrato.getArea().getNombrearea()+" Cargo :" + contrato.getCargo().getNombrecargo());
										
									}else{
										fechamap.put("area_cargo", "Area: Cargo:");
										
									}
									//fechamap.put("area", f.get+" "+f.getApellidos());
									
									if(verificasiestaenlista(fechacumple, fechascumple)){
										 
										 
									for(Evento g:listadoeventosnuevos){
										if(g.getStart().equals(fechacumple+"T00:00:00-05:00")){
											g.setDescription(g.getDescription()+" y ["+fechamap.get("nombre") +" - "+fechamap.get("area_cargo").toString().toLowerCase()+"]");
											break;
										}
									}
										
									}else{
									 eventonuevo=new Evento();
										eventonuevo.setId(100+p);
										eventonuevo.setStart(fechacumple+"T00:00:00-05:00");
										eventonuevo.setEnd(fechacumple+"T24:00:00-05:00");
										//eventonuevo.setStart(fechacumple);
										//eventonuevo.setEnd(fechacumple);
										//eventonuevo.setAllday(true);
										eventonuevo.setColor("#257e4a");
										eventonuevo.setTitle("Cumplea�os");
										eventonuevo.setRendering("background");
										eventonuevo.setDescription(eventorepetido.getDescription()+"["+fechamap.get("nombre") +" - "+fechamap.get("area_cargo").toString().toLowerCase()+"]");
										listadoeventosnuevos.add(eventonuevo);
									}
									fechascumple.add(fechamap);
									
									
									
									
											p++;
									
									
							}		
						}else{
						
					}
				}
				
				
			
					 
				 for(Evento r:listadoeventosnuevos){
					 l.add(r);
				 }
					 
				 request.getSession().setAttribute("eventos", l);
					// l.add(d);
		         /*  
				 response.setContentType("application/json");
				 response.setCharacterEncoding("UTF-8");
				 PrintWriter out = response.getWriter();
				 String objeto = new Gson().toJson(l);
				 out.write(objeto);*/
				
		}
		
	} catch (Exception e) {
		logger.error(e.toString(), e.fillInStackTrace());
		response.sendError(1, e.getMessage());
	}

}
private Boolean verificasiestaenlista(String valor, List<HashMap<String, Object>> listado){
	String fechatemp = valor;
	boolean resu = false;
	for(HashMap<String, Object> lis:listado){
		String fecha = (String)lis.get("fecha");
		Long idempleado = (Long)lis.get("idempleado");
		
		if(fechatemp.equals(fecha)){
			
			resu= true;
			break;
		}else{
			resu= false;
		}
	}
	
	return resu;
	
}
public void mostrar_eventoensession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	try {
		
		String idcontrato = request.getParameter("idcontrato");
		List<Evento> l=(List<Evento>) request.getSession().getAttribute("eventos");
		 response.setContentType("application/json");
		 response.setCharacterEncoding("UTF-8");
		 PrintWriter out = response.getWriter();
		 String objeto = new Gson().toJson(l);
		 out.write(objeto);
		
	} catch (Exception e) {
		logger.error(e.toString(), e.fillInStackTrace());
		response.sendError(1, e.getMessage());
	}

}



public void mostrar_dashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	HttpSession session = request.getSession(false);
	UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		
////////////////////////////////////////tableu
try {
			//String idempleado = request.getParameter("idempleado");
			
			List<EmpleadoBean> empleadost = empleadoEJB.buscarEmpleadosporCodigoIdentificacion(user.getCodigoidentificacion());
			EmpleadoBean empleadot = new EmpleadoBean();
			if(empleadost!=null){
				empleadot=empleadost.get(0);
				//List<Area> areas = contratoEJB.getAreas();
				//request.setAttribute("areas", areas);
				//request.setAttribute("estadoActivo", "ACTIVO");
				//request.setAttribute("estadoDeshabilitado", "DESHABILITADO");
				TableauServlet ts = new TableauServlet();
				//final String user = "SISTEMCOBRO"+"\\"+"dtroncoso";
				//final String user ="appsg";
				final String usert=config.getString("rrhh.tableau.userG");
				final String wgserver=config.getString("rrhh.tableau.server");
				//final String wgserver = "172.16.1.63:8081";
				String iplocal=Inet4Address.getLocalHost().getHostAddress();
				String ticket= ts.getTrustedTicket(wgserver, usert, iplocal);
				String parametros="numeroidentificacionP=";
				parametros+=empleadot.getEmpleadoidentificacion().getNumeroidentificacion();
				request.setAttribute("ticket", ticket);
				request.setAttribute("server", wgserver);
				request.setAttribute("parametros", parametros);
			
			}else{
			
			}
			request.getRequestDispatcher("../pages/asistencia/dashboardindividual.jsp").forward(request, response);



	} catch (Exception e) {
		logger.error(e.toString(), e.fillInStackTrace());
		response.sendError(1, e.getMessage());
	}


}



		public static String getClientIpAddr(HttpServletRequest request) {  
		    String ip = request.getHeader("X-Forwarded-For");  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("Proxy-Client-IP");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("WL-Proxy-Client-IP");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("HTTP_X_FORWARDED");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("HTTP_CLIENT_IP");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("HTTP_FORWARDED_FOR");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("HTTP_FORWARDED");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("HTTP_VIA");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getHeader("REMOTE_ADDR");  
		    }  
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
		        ip = request.getRemoteAddr();  
		    }  
		    return ip; 
		}
}
