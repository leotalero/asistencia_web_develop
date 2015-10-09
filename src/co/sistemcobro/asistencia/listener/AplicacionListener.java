package co.sistemcobro.asistencia.listener;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import co.sistemcobro.all.constante.EstadoEnum;
import co.sistemcobro.all.exception.DatoException;
import co.sistemcobro.all.exception.LogicaException;
import co.sistemcobro.hermes.bean.AccesoHistorial;
import co.sistemcobro.hermes.bean.AplicacionBean;
import co.sistemcobro.hermes.constante.AplicacionEnum;
import co.sistemcobro.hermes.constante.Constante;
import co.sistemcobro.hermes.ejb.UsuarioEJB;

/**
 * 
 * @author Jony Hurtado
 * 
 */
@WebListener
public class AplicacionListener implements ServletContextListener, HttpSessionListener {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(AplicacionListener.class);

	

	@EJB
	private UsuarioEJB usuario;

	public void sessionCreated(HttpSessionEvent he) {
		logger.error("session creada");
		
	}

	public void sessionDestroyed(HttpSessionEvent he) {
			HttpSession session = he.getSession();
		String idaccesohistorico =(String) session.getAttribute(Constante.IDACCESOHISTORICO);
		AccesoHistorial accesohistorial;
		try {
			if(idaccesohistorico!=null){
				accesohistorial = usuario.getAccesoHistorialUltimosPorId(Long.valueOf(idaccesohistorico));
				accesohistorial.setEstadosession(EstadoEnum.DESHABILITADO.getIndex());
				Integer ah = usuario.actualizarAccesoHistorial(accesohistorial);
				logger.error("session terminada");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LogicaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/* Application Startup Event */
	public void contextInitialized(ServletContextEvent ce) {
		crearAplicacion(ce);
	}

	/* Application Shutdown Event */
	public void contextDestroyed(ServletContextEvent ce) {
		logger.error("context terminada");
	}

	private void crearAplicacion(ServletContextEvent ce) {
		AplicacionBean aplicacionactual = (AplicacionBean) ce.getServletContext().getAttribute(Constante.APLICACION_SESSION);
		if (aplicacionactual == null || aplicacionactual.getIdaplicacion() <= 0) {
			AplicacionBean aplicacion;
			try {
				aplicacion = usuario.getAplicacionPorIdaplicacion(AplicacionEnum.ASISTENCIA.getIndex());
				aplicacion.setDirectivaacceso(usuario.getDirectivaAccesoPorIdaplicacion(aplicacion.getIdaplicacion()));
				logger.info("Cargo aplicación: [" + aplicacion.getIdaplicacion() + "] " + aplicacion.getNombreexterno());
				ce.getServletContext().setAttribute(Constante.APLICACION_SESSION, aplicacion);
			} catch (LogicaException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
	}

}
