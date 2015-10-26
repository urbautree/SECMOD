package com.urbau.servlet.entity;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.urbau._abstract.entity.Entity;
import com.urbau.beans.PuntoDeVentaBean;
import com.urbau.feeders.PuntosDeVentasMain;

import static com.urbau.misc.Constants.ADD;
import static com.urbau.misc.Constants.EDIT;
import static com.urbau.misc.Constants.REMOVE;
import static com.urbau.misc.Constants.MODE_PARAMETER;
import static com.urbau.misc.Constants.ID_PARAMETER;
import static com.urbau.misc.Constants.NOMBRE_PARAMETER;
import static com.urbau.misc.Constants.DIRECCION_PARAMETER;
import static com.urbau.misc.Constants.TELEFONO_PARAMETER;

@WebServlet("/PuntosDeVentas")
public class PuntosDeVentas extends Entity {
	private static final long serialVersionUID = 1L;
       
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{					
			HttpSession session = request.getSession();
			validateRequest( session );
			String mode = request.getParameter( MODE_PARAMETER );
							
			String message = "";
			if( request.getParameter( ID_PARAMETER ) != null || ADD.equals( request.getParameter( MODE_PARAMETER ))  ){
				
				PuntoDeVentaBean bean = new PuntoDeVentaBean();
														
				bean.setNombre( request.getParameter( NOMBRE_PARAMETER ) );					
				bean.setDireccion( request.getParameter( DIRECCION_PARAMETER ) );
				bean.setTelefono( request.getParameter( TELEFONO_PARAMETER ));					
																																																													
				if( !ADD.equals( request.getParameter( MODE_PARAMETER ) ) ){
					bean.setId( Integer.parseInt( request.getParameter( ID_PARAMETER )));
				}
					
				PuntosDeVentasMain main = new PuntosDeVentasMain();
				
				if( ADD.equals( mode )){
					if ( main.add( bean ) ){
						message = "Registro creado con exito.";
					} else {
						showMessage( "No se pudo crear el registro" , response );
					}
				} else if( EDIT.equals( mode )){
					if ( main.mod( bean ) ){
						message = "Registro modificado con exito.";
					} else {
						showMessage( "No se pudo modificar el registro", response  );
					}
				} else if( REMOVE.equals( mode )){
					if ( main.del( bean ) ){
						message = "Registro eliminado con exito.";
					} else {
						showMessage( "No se pudo eliminar el registro" , response );
					}
				}
				response.getOutputStream().write( message.getBytes() );
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}
			
		} catch( UserNotAuthenticatedException exception ){
			System.out.println( "Error: " + exception.getMessage() );
			exception.printStackTrace();
			response.getOutputStream().write( exception.getMessage().getBytes() );
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}
	
}
		