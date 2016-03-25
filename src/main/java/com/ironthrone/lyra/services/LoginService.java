package com.ironthrone.lyra.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.ironthrone.lyra.contracts.LoginRequest;
import com.ironthrone.lyra.contracts.LoginResponse;
import com.ironthrone.lyra.ejb.Institucion;
import com.ironthrone.lyra.ejb.Usuario;
import com.ironthrone.lyra.pojo.InstitucionPOJO;
import com.ironthrone.lyra.repositories.LoginRepository;
import com.ironthrone.lyra.security.IronPasswordEncryption;
import com.ironthrone.lyra.security.RavenMail;

/**
 * Proporciona los servicios de login y autenticacion al controlador login.
 * @author jeanpaul
 *
 */
@Service
public class LoginService implements LoginServiceInterface {

	@Autowired private LoginRepository loginRepository;
	@Autowired private IronPasswordEncryption encryptor;
	@Autowired private RavenMail raven;	
			   private boolean isActive;
			   private int randomLength = 5;
			   
			 
			   
	@Override
	@Transactional
	public void checkUser(LoginRequest lr, LoginResponse response, HttpSession currentSession) {
	
		Usuario loggedUser = loginRepository.findByEmail(lr.getEmail());
		isActive = loggedUser.getIsActiveUs();
		
		if (encryptor.clashSteel(lr.getPassword(), loggedUser.getPassword())) {
					
				if(isActive){
					response.setCode(200);
					response.setCodeMessage("User authorized");
					List<Integer> idInstitutions = new ArrayList<Integer>();
					List<Integer> idRoles = new ArrayList<Integer>();
					
					//CREATE AND SET THE VALUES FOR THE CONTRACT OBJECT
					response.setUserId(loggedUser.getIdUsuario());
					response.setFirstName(loggedUser.getNombre());
					response.setLastName(loggedUser.getApellido());
					
					loggedUser.getInstitucions().stream().forEach(i -> {
						idInstitutions.add(i.getIdInstitucion());

					});
					
					loggedUser.getRols().stream().forEach(r -> {
						idRoles.add(r.getIdRol());
					});
					
					response.setIdRoles(idRoles);
					response.setIdInstitucions(idInstitutions);
					response.setInstituciones(generateInstitucionDtos(loggedUser.getInstitucions()));
					
					//response.setIdInstitucion(loggedUser.getInstitucion().getIdInstitucion());
					
					currentSession.setAttribute("userId", loggedUser.getIdUsuario());
				}else{
					response.setCode(400);
					response.setErrorMessage("User is inactive, please contact your admin.");
					
				}
		
			} else {
				response.setCode(401);
				response.setErrorMessage("Unauthorized User");
			}
		}


	/**
	 * Cambia las credenciales de logeo de un usuario olvidadizo por una nueva contraseña aleatoria
	 * y le envia un correo con la misma.
	 * @param userMail el email del usuario a modificar.
	 * @return result, si noy hay problemas retorna true, false si el email es erroneo.
	 */
	@Override
	@Transactional
	public boolean getCredentials(String userMail) {
		

		Usuario user = loginRepository.findByEmail(userMail);
		boolean success = false;
		
		System.out.println("ID DEL USUARIO " + user.getIdUsuario());
		
		
		if(user.getIdUsuario() > 0){
			
			String password = encryptor.randomHilt(randomLength);
			String message  = " gracias por contactarnos. Su nueva contraseña es:" + password;
			
			user.setPassword(encryptor.ironEncryption(password));
			loginRepository.save(user);
			raven.SendRavenMessage(user.getEmail(), user.getNombre(), user.getApellido(), message);
			
			success = true;		
			
		}
			
		return success;
	}
	
	/**
	 * Genera POJOs a partir de una lista EJB.
	 * @param List<Institucion> representa una lista de instituciones tipo ejb
	 * @return List<InstitucionPOJO>
	 */
	private List<InstitucionPOJO> generateInstitucionDtos(List<Institucion> instituciones){
		
		List<InstitucionPOJO> institucionesPojo = new ArrayList<InstitucionPOJO>();
		
		instituciones.stream().forEach(i -> {
			InstitucionPOJO dto = new InstitucionPOJO();
			BeanUtils.copyProperties(i,dto);
			dto.setHasSuscripcion(i.getHasSuscripcion());
			dto.setAlumnos(null);
			dto.setBitacoras(null);
			dto.setGrados(null);
			dto.setMaterias(null);
			dto.setSubscripcions(null);
			dto.setUsuarios(null);
			institucionesPojo.add(dto);
		});	
		
		return institucionesPojo;
	};
}
