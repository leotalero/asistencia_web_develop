package co.sistemcobro.asistencia.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.octo.captcha.module.servlet.image.SimpleImageCaptchaServlet;

import co.sistemcobro.all.constante.EstadoEnum;
import co.sistemcobro.all.exception.DirectivaContrasenaException;
import co.sistemcobro.all.exception.LogicaException;
import co.sistemcobro.all.exception.SessionException;
import co.sistemcobro.hermes.bean.AplicacionBean;
import co.sistemcobro.hermes.bean.IdentificacionTipo;
import co.sistemcobro.hermes.bean.PreguntasSeguraBean;
import co.sistemcobro.hermes.bean.UsuarioBean;
import co.sistemcobro.hermes.bean.UsuarioPreguntaSeguraBean;
import co.sistemcobro.hermes.constante.AplicacionEnum;
import co.sistemcobro.hermes.constante.ClaveCambioEnum;
import co.sistemcobro.hermes.constante.Constante;
import co.sistemcobro.hermes.ejb.UsuarioEJB;

/**
 * 
 * @author Leonardo Talero
 * 
 */
@WebServlet(name = "HermesSeguridadServlet", urlPatterns = { "/portal/hermesseguridad" })
public class HermesSeguridadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(HermesSeguridadServlet.class);

	//ResourceBundle config = ResourceBundle.getBundle(Constante.FILE_CONFIG_HERMES);

	@EJB
	private UsuarioEJB usuarioEJB;

	public HermesSeguridadServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");
		action = action == null ? "" : action;
		try {
			if (action.equals("ver_preguntas")) {
				verpreguntas(request, response);
			} else if (action.equals("guardarpreguntas")) {
				guardarpreguntas(request, response);
			} else if (action.equals("recordar_contrasena")) {
				recordarcontrasena(request, response);
			}
			 else if (action.equals("validaridentificacion")) {
				 recordar_validaridentificacion(request, response);
			}
			 else if (action.equals("refreshpregunta")) {
				 recordar_refreshpregunta(request, response);
			}
			 else if (action.equals("validarpregunta")) {
				 recordar_validarpregunta(request, response);
			}
			else if (action.equals("usuario_contrasena_editar")) {
				actualizarUsuarioContrasena(request, response);
			}
			
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}



	
	public void verpreguntas(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			String idusuario = (String)request.getParameter("idusuario");			
			UsuarioBean usuario = null;
			
			if(idusuario!=null){
				usuario=usuarioEJB.getUsuario(Integer.valueOf(idusuario));
				List<UsuarioPreguntaSeguraBean> preguntasusuario = usuarioEJB.getPreguntasPoridUsuario(Long.valueOf(idusuario));
				if(preguntasusuario.size()==0){
					UsuarioPreguntaSeguraBean preguntasusuariovacio=new UsuarioPreguntaSeguraBean();
				
					request.setAttribute("preguntasusuario1", preguntasusuariovacio);
					request.setAttribute("preguntasusuario2", preguntasusuariovacio);
					request.setAttribute("preguntasusuario3", preguntasusuariovacio);	
				}else{
					for(UsuarioPreguntaSeguraBean x:preguntasusuario){
						x.setPregunta(usuarioEJB.getPreguntasdeSeguridadporId(x.getIdpreguntasegura()));
						
					}
					request.setAttribute("preguntasusuario1", preguntasusuario.get(0));
					request.setAttribute("preguntasusuario2", preguntasusuario.get(1));
					request.setAttribute("preguntasusuario3", preguntasusuario.get(2));	
				}
				
			}
			
			request.setAttribute("usuario", usuario);
			request.setAttribute("edicion", "si");
			request.setAttribute("preguntas", usuarioEJB.getPreguntasdeSeguridad());
			request.getRequestDispatcher("../portales/hermesseguridad/usuario_preguntas_seguridad.jsp").forward(request, response);
			} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}
	
	public void guardarpreguntas(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NumberFormatException, LogicaException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		
		AplicacionBean aplicacion = (AplicacionBean) request.getServletContext().getAttribute(Constante.APLICACION_SESSION);
		String idusuario=(String) request.getParameter("idusuario");
		UsuarioBean usuario=new UsuarioBean();
		if(idusuario==null || idusuario.equals("")){
			usuario=user;
			
		}else{
			usuario=usuarioEJB.getUsuario(Integer.valueOf(idusuario));
		}
		try {
			String pregunta1 = request.getParameter("idpreguntanumero1");
			String pregunta2 = request.getParameter("idpreguntanumero2");
			String pregunta3 = request.getParameter("idpreguntanumero3");
			String respuesta1 = request.getParameter("respuesta1");
			String respuesta2 = request.getParameter("respuesta2");
			String respuesta3 = request.getParameter("respuesta3");
			respuesta1 = respuesta1 == null ? "" : respuesta1;
			respuesta2 = respuesta2 == null ? "" : respuesta2;
			respuesta3 = respuesta3 == null ? "" : respuesta3;
		if(!respuesta1.equals("") && !respuesta2.equals("") && !respuesta3.equals("") && respuesta1.length()>3 && respuesta2.length()>3 && respuesta3.length()>3){
			if(pregunta1.equals(pregunta2) || pregunta2.equals(pregunta3) || pregunta1.equals(pregunta3)){
				throw new Exception("Las preguntas deben ser diferentes");
			}else{
		List<UsuarioPreguntaSeguraBean> usuariopreguntas=new ArrayList<UsuarioPreguntaSeguraBean>();	
			UsuarioPreguntaSeguraBean usuariopregunta1 = new UsuarioPreguntaSeguraBean();
				usuariopregunta1.setIdusuario(Long.valueOf(usuario.getIdusuario()));
				usuariopregunta1.setIdpreguntasegura(Long.valueOf(pregunta1));
				usuariopregunta1.setRespuesta(respuesta1);
				usuariopregunta1.setIdusuariocrea(usuario.getCodusuario());
				usuariopregunta1.setIdusuariomod(usuario.getCodusuario());
				usuariopregunta1.setEstado(EstadoEnum.ACTIVO.getIndex());
			usuariopreguntas.add(usuariopregunta1);
			UsuarioPreguntaSeguraBean usuariopregunta2 = new UsuarioPreguntaSeguraBean();
				usuariopregunta2.setIdusuario(Long.valueOf(usuario.getIdusuario()));
				usuariopregunta2.setIdpreguntasegura(Long.valueOf(pregunta2));
				usuariopregunta2.setRespuesta(respuesta2);
				usuariopregunta2.setIdusuariocrea(usuario.getCodusuario());
				usuariopregunta2.setIdusuariomod(usuario.getCodusuario());
				usuariopregunta2.setEstado(EstadoEnum.ACTIVO.getIndex());
			usuariopreguntas.add(usuariopregunta2);
			UsuarioPreguntaSeguraBean usuariopregunta3 = new UsuarioPreguntaSeguraBean();
				usuariopregunta3.setIdusuario(Long.valueOf(usuario.getIdusuario()));
				usuariopregunta3.setIdpreguntasegura(Long.valueOf(pregunta3));
				usuariopregunta3.setRespuesta(respuesta3);
				usuariopregunta3.setIdusuariocrea(usuario.getCodusuario());
				usuariopregunta3.setIdusuariomod(usuario.getCodusuario());
				usuariopregunta3.setEstado(EstadoEnum.ACTIVO.getIndex());
			usuariopreguntas.add(usuariopregunta3);
			
			List<UsuarioPreguntaSeguraBean> preguntas = usuarioEJB.getPreguntasPoridUsuario(Long.valueOf(usuario.getIdusuario()));
			
				if(preguntas!=null && preguntas.size()>0 ){
					for(UsuarioPreguntaSeguraBean x:preguntas){
								 x.setEstado(EstadoEnum.DESHABILITADO.getIndex());
								 usuarioEJB.actualizarPreguntasSeguridad(x);
						}
					usuarioEJB.insertarUsuarioPreguntaseguridad(usuariopreguntas);
				}else{
					 usuarioEJB.insertarUsuarioPreguntaseguridad(usuariopreguntas);
				}
		
			}
		}else{
			logger.error("Las respuestas tienen que tener longitud mayor a 3 caracteres");
			response.sendError(1,"Las respuestas tienen que tener longitud mayor a 3 caracteres");
		}
			
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}

	
	public void recordarcontrasena(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setAttribute("tiposidentifiaccion", usuarioEJB.getIdentifiacionTipo() );
			request.getRequestDispatcher("../portales/hermesseguridad/recordar_contrasena_identificacion.jsp").forward(request, response);
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}
	
	
	public void recordar_validaridentificacion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION_RECUPERA);	try {
			final String captcha = req.getParameter("captcha");
			final String ididentificaciontipo = req.getParameter("ididentificaciontipo");
			final String numeroidentificacion = req.getParameter("numeroidentificacion");
			if (captcha == null) {
				//logger.info("[usuario:" + u + ", idaplicacion:" + aplicacion.getIdaplicacion() + "] Digite el texto de la imagen (captcha).");
				throw new Exception("Digite el texto de la imagen.");
			} else {
				boolean captchaPassed = SimpleImageCaptchaServlet.validateResponse(req, captcha);
				if (!captchaPassed) {
					throw new Exception("El texto de la imagen es incorrecto.");
				}else{
					
					if(numeroidentificacion!=null && ididentificaciontipo!=null){
					UsuarioBean usuario = usuarioEJB.getUsuarioPorIdentificacionyTipo(numeroidentificacion,Long.valueOf(ididentificaciontipo));	
						
						
						if(usuario!=null && usuario.getEstado()==EstadoEnum.ACTIVO.getIndex()){
							
							List<UsuarioPreguntaSeguraBean> preguntas = usuarioEJB.getPreguntasPoridUsuario(Long.valueOf(usuario.getIdusuario()));
							if(preguntas.size()==0){
								session.removeAttribute(Constante.USUARIO_SESSION_RECUPERA);
								//request.setAttribute("preguntas", usuarioEJB.getPreguntasdeSeguridad());
							//	request.getRequestDispatcher("../portales/hermesseguridad/exceptionMsg.jsp").forward(request, response);
							
								throw new Exception("Sus preguntas de seguridad no han sido completadas recuerde contactarse con su soporte técnico.");
								 
							}else{
								Random random = new Random();
								int preguntanumero = random.nextInt((preguntas.size()-1) - 0 +1 ) + 0;
								
								PreguntasSeguraBean preguntaseleccionada=usuarioEJB.getPreguntasdeSeguridadporId(preguntas.get(preguntanumero).getIdpreguntasegura());
								request.setAttribute("preguntas", preguntas);
								request.setAttribute("preguntarandom", preguntaseleccionada);
								request.setAttribute("idpregunta", preguntaseleccionada.getIdpreguntasegura());
							
								session.setAttribute(Constante.USUARIO_SESSION_RECUPERA,usuario);
								request.getRequestDispatcher("../portales/hermesseguridad/recordar_contrasena_pregunta.jsp").forward(request, response);
							 
							}
							
							
						}else{
							throw new Exception("El número de identificación no se encuentra registrado, se ecuentra bloqueado o el tipo de identificación no es correcto.");
						}
					}
						
				}
				
				
				
			}
			
		}catch (SessionException e) {
				logger.error(e.getMessage());
				response.sendError(1, e.getMessage());
				
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.sendError(1, e.getMessage());
	
			}
	}
	
	public void recordar_refreshpregunta(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		UsuarioBean usuario = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION_RECUPERA);
		Integer intento = (Integer) session.getAttribute("numerointento");
		if(intento==null){
			intento=1;
		}
		try {
			//final String captcha = req.getParameter("captcha");
			final String idpregunta = req.getParameter("idpregunta");
			
					
					if(idpregunta!=null){
						//UsuarioBean usuario = usuarioEJB.getUsuarioPorNumIdentificacion(numeroidentificacion);	
					PreguntasSeguraBean preguntaanterior = usuarioEJB.getPreguntasdeSeguridadporId(Long.valueOf(idpregunta));
					
					if(intento<=3){//valida numero d intentos
						intento+=1;
						session.setAttribute("numerointento", intento);
						
						if(preguntaanterior!=null){
							
							List<UsuarioPreguntaSeguraBean> preguntas = usuarioEJB.getPreguntasPoridUsuario(Long.valueOf(usuario.getIdusuario()));
							Random random = new Random();
							int  preguntanumero = random.nextInt((preguntas.size()-1) - 0 +1 ) + 0;
							boolean val = true;
							while(val){
								if(Long.valueOf(idpregunta)==preguntas.get(preguntanumero).getIdpreguntasegura()){
									preguntanumero = random.nextInt((preguntas.size()-1) - 0 +1 ) + 0;
									val=true;
								}else{
									val=false;
								}
							}
							PreguntasSeguraBean preguntaseleccionada=usuarioEJB.getPreguntasdeSeguridadporId(preguntas.get(preguntanumero).getIdpreguntasegura());
							
							request.setAttribute("preguntas", preguntas);
							request.setAttribute("preguntarandom", preguntaseleccionada);
							request.setAttribute("idpregunta", preguntaseleccionada.getIdpreguntasegura());
							
						//throw new Exception("Intento número: "+ (intento-1));
					}
							
						
							
						}else{
							
							session.removeAttribute(Constante.USUARIO_SESSION_RECUPERA);
							session.removeAttribute("numerointento");
							String mensaje="Excedió el número de intentos";
							throw new Exception(mensaje);	
						}
					
						
				
				
					request.getRequestDispatcher("../portales/hermesseguridad/recordar_contrasena_pregunta.jsp").forward(request, response);
					
					
				
			}else{
				throw new Exception("no tiene id de pregunta.");
			}
			
		}catch (SessionException e) {
				logger.error(e.getMessage());
				response.sendError(1, e.getMessage());
				
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.sendError(1, e.getMessage());
	
			}
	}
	
	public void recordar_validarpregunta(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		UsuarioBean usuario = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION_RECUPERA);
		Integer intento = (Integer) session.getAttribute("numerointento");
		if(intento==null){
			intento=1;
		}
		try {
			//final String captcha = req.getParameter("captcha");
			final String respuesta = req.getParameter("respuesta");
			final String idpregunta = req.getParameter("idpregunta");
			
					if(respuesta!=null){
						PreguntasSeguraBean pregunta = usuarioEJB.getPreguntasdeSeguridadporId(Long.valueOf(idpregunta));	
						
						if(pregunta!=null){
							String respuestaendb="";
							List<UsuarioPreguntaSeguraBean> preguntas = usuarioEJB.getPreguntasPoridUsuario(Long.valueOf(usuario.getIdusuario()));
							
							for(UsuarioPreguntaSeguraBean x:preguntas){
								//busca respuesta para compararla
								if(Long.valueOf(idpregunta)==x.getIdpreguntasegura() || Long.valueOf(idpregunta).equals(x.getIdpreguntasegura())){
									respuestaendb=x.getRespuesta();
										
									
								}
							}
							if(intento<=3){//valida numero d intentos
								intento+=1;
								session.setAttribute("numerointento", intento);
								
								if(respuesta.equals(respuestaendb)){
									UsuarioBean usuariofinal = usuarioEJB.getUsuario(usuario.getIdusuario());
									request.setAttribute("directivacontrasena", usuarioEJB.getDirectivaContrasenaPorIdusuario(usuariofinal.getIdusuario()));
									request.setAttribute("usuario", usuariofinal);
									request.getRequestDispatcher("../portales/hermesseguridad/cambiar_contrasena.jsp").forward(request, response);
									 
								}else{
									request.setAttribute("numerointento", intento);
									throw new Exception("Respuesta Equivocada: Intento número: "+ (intento-1));	
								}
							}else{
								
								session.removeAttribute(Constante.USUARIO_SESSION_RECUPERA);
								session.removeAttribute("numerointento");
								String mensaje="Excedió el número de intentos";
								throw new Exception(mensaje);	
							}
								
							 
						}else{
							throw new Exception("No tiene pregunta.");
						}
					}
						
				
				
				
			
			
		}catch (SessionException e) {
		
				logger.error(e.getMessage());
				response.sendError(1, e.getMessage());
				
			
		} catch (Exception e) {
			
			logger.error(e.getMessage());
			response.sendError(1, e.getMessage());
			
			}
	}
	public void actualizarUsuarioContrasena(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean usuario = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION_RECUPERA);
		AplicacionBean aplicacion = (AplicacionBean) request.getServletContext().getAttribute(Constante.APLICACION_SESSION);
		try {

			String idusuario = request.getParameter("idusuario");
			String clave_new = request.getParameter("clave_new");
			String clave_new_r = request.getParameter("clave_new_r");
			

			idusuario = idusuario == null ? "" : idusuario;
			clave_new = clave_new == null ? "" : clave_new;
			clave_new_r = clave_new_r == null ? "" : clave_new_r;

			/* Se obtiene el usuario parcial solo para validar si es el usuario quien dice ser */
			
			if (null != usuario) {
				/* Se carga todos los datos del usario para proceder a cambiar la contraseña y luego actualizar la columna clavecambio */
				UsuarioBean usuariototal = usuarioEJB.getUsuario(usuario.getIdusuario());
				if (clave_new.equals(clave_new_r)) {
					final String clave = clave_new;
					final Integer idusuariot = usuario.getIdusuario();

					if (usuarioEJB.validarContrasenaBasadaEnLaDirectiva(idusuariot, clave)) {
						Integer resultado = usuarioEJB.updateContrasena(clave, idusuariot, usuario.getCodusuario(), ClaveCambioEnum.NO_SOLICITAR_CAMBIO_DE_CLAVE.getIndex());
						if (resultado > 0) {
							session.removeAttribute(Constante.USUARIO_SESSION_RECUPERA);
						} else {
							session.removeAttribute(Constante.USUARIO_SESSION_RECUPERA);
							throw new LogicaException("Error inesperado al cambiar la clave del usuario [" + usuario.getCodusuario() + "].");
						}
					}
				} else {
					//session.removeAttribute(Constante.USUARIO_SESSION_RECUPERA);
					throw new LogicaException("Los campos de la Nueva contraseña deben de coincidir.");
				}
			} else {
				throw new Exception("Contraseña actual incorrecta");
			}
		} catch (LogicaException e) {

			logger.error(e.getMessage());
			response.sendError(1, e.getMessage());
		} catch (Exception e) {

			logger.error(e.getMessage());
			response.sendError(1, e.getMessage());
		}

	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
