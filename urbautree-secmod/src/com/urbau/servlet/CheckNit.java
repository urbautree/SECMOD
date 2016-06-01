package com.urbau.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.urbau.feeders.ClientesMain;


/**
 * Servlet implementation class 
 */
@WebServlet( 
		urlPatterns = { "/bin/CheckNit" } 
		)

public class CheckNit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckNit() {
        super();
    }

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String nit = request.getParameter( "nit" );
		
		ClientesMain cm = new ClientesMain();
		if( cm.existeNit( nit ) ){
			
			response.getOutputStream().write( "false".getBytes() );
			
		}  else {
			//response.getOutputStream().write( "success".getBytes() );
			response.getOutputStream().write( "true".getBytes() );
		}
			response.getOutputStream().flush();
			response.getOutputStream().close();
		
	}

}
