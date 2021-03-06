package com.ironthrone.lyra.contracts;


import com.ironthrone.lyra.pojo.UsuarioPOJO;

public class UsuarioRequest extends BaseRequest{
	
	private UsuarioPOJO usuario;
	private int idUsuario;
	

	public UsuarioPOJO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioPOJO usuario) {
		this.usuario = usuario;
	}
	
	@Override
	public String toString() {
		return "Request de Usuario [user=" + usuario + "]";
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}


}
