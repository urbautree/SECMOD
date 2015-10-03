package com.urbau.feeders;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.urbau._abstract.AbstractMain;
import com.urbau.beans.PresupuestoProyeccionBean;
import com.urbau.db.ConnectionManager;
import com.urbau.misc.Constants;
import com.urbau.misc.Util;

public class PresupuestosProyeccionesMain extends AbstractMain {
		
	private String allColumnNames = " PP.ID, PP.ID_PRESUPUESTO, PP.ANIO, PP.MES, PP.TIPO_RUBRO, PP.MONTO, TR.DESCRIPCION ";
	public static int getProgramId(){
		return -2;
	}
	public ArrayList<PresupuestoProyeccionBean> get( String q, int from, int idPresupuesto ){
		return get( q, from, -1 , idPresupuesto);
	}
	
	public ArrayList<PresupuestoProyeccionBean> get( String q, int from, int limit,int idPresupuesto ){
		
		int items = limit > 0 ? limit : Constants.ITEMS_PER_PAGE;
		ArrayList<PresupuestoProyeccionBean> list = new ArrayList<PresupuestoProyeccionBean>();
		Connection con  = null;
		Statement  stmt = null;
		ResultSet  rs   = null;
		int total_regs = -1;
		try{
			con = ConnectionManager.getConnection();
			stmt = con.createStatement();
			if( q == null || "null".equalsIgnoreCase( q ) || "".equals( q.trim() )){
				String sql = "SELECT " +allColumnNames+" FROM PRESUPUESTO_PROYECCION AS PP INNER JOIN TIPOS_RUBRO AS TR ON PP.TIPO_RUBRO=TR.ID  WHERE PP.ID_PRESUPUESTO='"+idPresupuesto+"' LIMIT " + from + "," + items;
				System.out.println("sql: " + sql);
				rs = stmt.executeQuery( sql );
				total_regs = Util.getTotalRegs( "PRESUPUESTO_PROYECCION", "" );
			} else {
				String rem_where = Util.getPresupuestoWhere( q );
				String sql =  "SELECT "+allColumnNames+
						" FROM PRESUPUESTO_PROYECCION AS PP INNER JOIN TIPOS_RUBRO AS TR ON PP.TIPO_RUBRO=TR.ID LIMIT " + rem_where  + from + "," + items ;
				System.out.println("sql: " + sql);
				rs = stmt.executeQuery(sql);
				total_regs = Util.getTotalRegs( "PRESUPUESTO_PROYECCION", rem_where );
			}
			while( rs.next() ){
				PresupuestoProyeccionBean bean = new PresupuestoProyeccionBean();
				bean.setId( rs.getInt( 1  ));
				bean.setIdPresupuesto( rs.getInt( 2  ));
				bean.setAnio(rs.getInt(3));
				bean.setMes(rs.getInt(4));
				bean.setTipoRublo(rs.getInt(5));
				bean.setMonto(rs.getDouble(6));
				bean.setRubloDescripcion( Util.trimString( rs.getString( 7 )));
				bean.setTotal_regs( total_regs );
				list.add( bean );
			}
		} catch( Exception e ){
			e.printStackTrace();
		} finally {
			ConnectionManager.close( con, stmt, rs );
		}
		return list;
	}
		
	public PresupuestoProyeccionBean get( int id ){
		if( id < 0 ){
			return getBlankBean();
		}
		PresupuestoProyeccionBean bean = null;
		Connection con  = null;
		Statement  stmt = null;
		ResultSet  rs   = null;
		try{
			con  = ConnectionManager.getConnection();
			stmt = con.createStatement();
			String sql = "SELECT "+allColumnNames+" FROM PRESUPUESTO_PROYECCION AS PP INNER JOIN TIPOS_RUBRO AS TR ON PP.TIPO_RUBRO=TR.ID WHERE PP.ID=" + id ;
			System.out.println("sql: "+ sql);
			rs = stmt.executeQuery( sql);
			while( rs.next() ){
			    bean = new PresupuestoProyeccionBean();
			    bean.setId( rs.getInt( 1  ));
				bean.setIdPresupuesto( rs.getInt( 2  ));
				bean.setAnio(rs.getInt(3));
				bean.setMes(rs.getInt(4));
				bean.setTipoRublo(rs.getInt(5));
				bean.setMonto(rs.getDouble(6));
				bean.setRubloDescripcion( Util.trimString( rs.getString( 7 )));								
			}
		} catch( Exception e ){
			e.printStackTrace();
		} finally {
			ConnectionManager.close( con, stmt, rs );
		}
		return bean;
	}
	public PresupuestoProyeccionBean getBlankBean(){
		PresupuestoProyeccionBean bean = new PresupuestoProyeccionBean();
		bean.setId(-1);
		bean.setIdPresupuesto(-1);
		bean.setAnio(-1);
		bean.setMes(-1);
		bean.setTipoRublo(-1);
		bean.setMonto(00.00);
		bean.setRubloDescripcion( "");		
		return bean;
	}
	public boolean add( PresupuestoProyeccionBean bean ){
		Connection con = null;
		Statement  stmt= null;
		try {
			con = ConnectionManager.getConnection();
			stmt= con.createStatement();
			String sql = "INSERT INTO PRESUPUESTO_PROYECCION " +
					"(ID_PRESUPUESTO,ANIO,MES,TIPO_RUBRO,MONTO) " +
						"VALUES " +
					"("+ bean.getIdPresupuesto()+","+bean.getAnio()+","+bean.getMes()+","+bean.getTipoRublo()+","+bean.getMonto()+")";
			int total = stmt.executeUpdate( sql );
			
			String sqlProyectado = "UPDATE PRESUPUESTO SET PROYECTADO = (SELECT SUM(MONTO) from PRESUPUESTO_PROYECCION WHERE ID_PRESUPUESTO="+bean.getIdPresupuesto()+") WHERE ID="+bean.getIdPresupuesto()+"";	
			stmt.executeUpdate( sqlProyectado );
			
			return total>0;
			 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectionManager.close( con, stmt, null );
		}
	}
	public boolean mod( PresupuestoProyeccionBean bean ){
		if ( bean.getId() <= 0 ){
			return false;
		}
		Connection con = null;
		Statement  stmt= null;
		try {
			con = ConnectionManager.getConnection();
			stmt= con.createStatement();
			String sql = "UPDATE PRESUPUESTO_PROYECCION SET " +
					"ID_PRESUPUESTO = " + bean.getIdPresupuesto() + ", " +
					"ANIO = " + bean.getAnio() + ", " +
					"MES = " + bean.getMes() + ", " +
					"TIPO_RUBRO = " + bean.getTipoRublo() + ", " +
					"MONTO = " + bean.getMonto()+ " " +
					"WHERE ID = " + bean.getId();
			System.out.println("sql[" + sql+"]");
			int total = stmt.executeUpdate( sql );
			
			String sqlProyectado = "UPDATE PRESUPUESTO SET PROYECTADO = (SELECT SUM(MONTO) from PRESUPUESTO_PROYECCION WHERE ID_PRESUPUESTO="+bean.getIdPresupuesto()+") WHERE ID="+bean.getIdPresupuesto();			
			stmt.executeUpdate( sqlProyectado );
			
			return total>0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectionManager.close( con, stmt, null );
		}
	}
	
	public boolean del( PresupuestoProyeccionBean bean ){
		if ( bean.getId() <= 0 ){
			return false;
		}
		Connection con = null;
		Statement  stmt= null;
		try {
			con = ConnectionManager.getConnection();
			stmt= con.createStatement();
			String sql = "DELETE FROM PRESUPUESTO_PROYECCION WHERE ID = " + bean.getId();
			int total = stmt.executeUpdate( sql );
			
			String sqlProyectado = "UPDATE PRESUPUESTO SET PROYECTADO = (SELECT SUM(MONTO) from PRESUPUESTO_PROYECCION WHERE ID_PRESUPUESTO="+bean.getIdPresupuesto()+") WHERE ID="+bean.getIdPresupuesto();			
			stmt.executeUpdate( sqlProyectado );
			
			return total>0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectionManager.close( con, stmt, null );
		}
	}
		
	
}
