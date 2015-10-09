package co.sistemcobro.asistencia.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import co.sistemcobro.all.constante.EstadoEnum;
import co.sistemcobro.all.exception.DirectivaContrasenaException;
import co.sistemcobro.all.exception.LogicaException;
import co.sistemcobro.all.exception.SessionException;
import co.sistemcobro.hermes.bean.AplicacionBean;
import co.sistemcobro.hermes.bean.UsuarioBean;
import co.sistemcobro.hermes.bean.UsuarioPreguntaSeguraBean;
import co.sistemcobro.hermes.constante.AplicacionEnum;
import co.sistemcobro.hermes.constante.ClaveCambioEnum;
import co.sistemcobro.asistencia.constante.Constante;
import co.sistemcobro.hermes.ejb.UsuarioEJB;

/**
 * 
 * @author Leonardo talero
 * 
 */
@WebServlet(name = "HermesServlet", urlPatterns = { "/page/hermes" })
public class HermesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(HermesServlet.class);

	ResourceBundle config = ResourceBundle.getBundle(Constante.FILE_CONFIG_ASISTENCIA);

	@EJB
	private UsuarioEJB usuarioEJB;

	public HermesServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");
		action = action == null ? "" : action;
		try {
			if (action.equals("ver_cambiar_contrasena")) {
				verCambiarContrasena(request, response);
			} else if (action.equals("cambiar_contrasena")) {
				cambiarContrasena(request, response);
			} else if (action.equals("cambiar_contrasena_vencimiento_edicion")) {
				editarCambiarcontrasenaPorVencimiento(request, response);
			} else if (action.equals("ver_preguntas")) {
				verpreguntas(request, response);
			} else if (action.equals("guardarpreguntas")) {
				guardarpreguntas(request, response);
			}
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}

	public void editarCambiarcontrasenaPorVencimiento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.getRequestDispatcher("../pages/hermes/cambiar_contrasena_vencimiento.jsp").forward(request, response);
		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}

	public void verCambiarContrasena(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		try {
			UsuarioBean usuariosession = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
			request.setAttribute("directivacontrasena", usuarioEJB.getDirectivaContrasenaPorIdusuario(usuariosession.getIdusuario()));
			UsuarioBean usuario = usuarioEJB.getUsuario(usuariosession.getIdusuario());
			request.setAttribute("usuario", usuario);
			request.getRequestDispatcher("../pages/hermes/cambiar_contrasena.jsp").forward(request, response);

		} catch (Exception e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, e.getMessage());
		}
	}

	public void cambiarContrasena(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UsuarioBean user = (UsuarioBean) session.getAttribute(Constante.USUARIO_SESSION);
		AplicacionBean aplicacion = (AplicacionBean) request.getServletContext().getAttribute(Constante.APLICACION_SESSION);
		try {
			String clave_old = request.getParameter("clave_old");
			String clave_new = request.getParameter("clave_new");
			String clave_new_r = request.getParameter("clave_new_r");

			clave_old = clave_old == null ? "" : clave_old;
			clave_new = clave_new == null ? "" : clave_new;
			clave_new_r = clave_new_r == null ? "" : clave_new_r;

			/* Se obtiene el usuario parcial solo para validar si es el usuario quien dice ser */
			UsuarioBean usuario = usuarioEJB.isUsuario(user.getUsuario(), clave_old);
			if (null != usuario) {
				/* Se carga todos los datos del usario para proceder a cambiar la contraseña y luego actualizar la columna clavecambio */
				UsuarioBean usuariototal = usuarioEJB.getUsuario(usuario.getIdusuario());
				if (clave_new.equals(clave_new_r)) {
					final String clave = clave_new;
					final Integer idusuario = user.getIdusuario();

					if (usuarioEJB.validarContrasenaBasadaEnLaDirectiva(idusuario, clave)) {
						Integer resultado = usuarioEJB.updateContrasena(clave, idusuario, user.getCodusuario(), ClaveCambioEnum.NO_SOLICITAR_CAMBIO_DE_CLAVE.getIndex());
						if (resultado > 0) {
							/* Se procede a volver a cargar el usuario en sessión */
							UsuarioBean existe = usuarioEJB.tieneAccesoAplicacionConInit(user.getIdusuario(), AplicacionEnum.get(aplicacion.getIdaplicacion()));
							if (null != existe) {
								session.setAttribute(Constante.USUARIO_SESSION, existe);
								logger.info("Usuario recargado a Hermes [idusuario:" + usuariototal.getIdusuario() + ":" + usuariototal.getUsuario() + "]");
							} else {
								logger.info("Usuario no cuenta con permisos para la aplicación [idusuario:" + usuariototal.getUsuario() + ",idaplicacion:" + aplicacion.getIdaplicacion() + " ]");
								throw new SessionException("[" + usuariototal.getUsuario() + "] No cuenta con permisos para el uso de esta aplicación.");
							}
						} else {
							throw new LogicaException("Error inesperado al cambiar la clave del usuario [" + usuario.getCodusuario() + "].");
						}
					}
				} else {
					throw new LogicaException("Los campos de la Nueva contraseña deben de coincidir.");
				}
			} else {
				throw new Exception("Contraseña actual incorrecta");
			}

		} catch (DirectivaContrasenaException e) {
			logger.error(e.toString(), e.fillInStackTrace());
			response.sendError(1, "Nueva contraseña: "+e.getMessage());
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
			request.getRequestDispatcher("../pages/hermes/cambiar_preguntas_seguridad.jsp").forward(request, response);
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


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
