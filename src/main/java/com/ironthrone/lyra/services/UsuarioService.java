package com.ironthrone.lyra.services;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ironthrone.lyra.security.IronPasswordEncryption;
import com.google.common.collect.Iterables;
import com.ironthrone.lyra.contracts.UsuarioRequest;
import com.ironthrone.lyra.ejb.Alumno;
import com.ironthrone.lyra.ejb.Institucion;
import com.ironthrone.lyra.ejb.Periodo;
import com.ironthrone.lyra.ejb.Rol;
import com.ironthrone.lyra.ejb.Usuario;
import com.ironthrone.lyra.pojo.InstitucionPOJO;
import com.ironthrone.lyra.pojo.RolPOJO;
import com.ironthrone.lyra.pojo.TareaPOJO;
import com.ironthrone.lyra.pojo.UsuarioPOJO;
import com.ironthrone.lyra.repositories.InstitucionRepository;
import com.ironthrone.lyra.repositories.PeriodoRepository;
import com.ironthrone.lyra.repositories.RolRepository;
import com.ironthrone.lyra.repositories.UsuarioRepository;

import java.util.Date;
import java.util.Iterator;


/**
 * Clase que proporciona los servicios de usuario al controlador Usuario.
 * @author jeanpaul
 *
 */

@Service
public class UsuarioService implements UsuarioServiceInterface {

	@Autowired private UsuarioRepository usersRepository;
	@Autowired private RolRepository rolRepository;
	@Autowired private InstitucionRepository instituteRepository;
	@Autowired private PeriodoRepository periodoRepository;
	@Autowired private IronPasswordEncryption encryptor;

	/**
	 * Genera POJOs a partir de una lista EJB.
	 * @param users representa una lista de usuarios tipo ejb
	 * @return UserInterfaceUsers, lista de usuarios POJO.
	 */
	private List<UsuarioPOJO> generateUserDtos(List<Usuario> users){
		
		List<UsuarioPOJO> uiUsers = new ArrayList<UsuarioPOJO>();
		
		users.stream().forEach(u -> {
			
			boolean periodoActual = false;
			Iterator<Periodo> iteratorList = u.getPeriodos().stream().iterator();
			
			while (iteratorList.hasNext()) {
				Periodo p = iteratorList.next();
				
				if(p.getIsActivePr()){
					periodoActual = true;
					break;
				}
			};	

			if(periodoActual){
				
				UsuarioPOJO dto = new UsuarioPOJO();
				BeanUtils.copyProperties(u,dto);
				dto.setActiveUs(u.getIsActiveUs());
				dto.setRols(generateRolDto(u));
				dto.setTareas(generateTareaDto(u));
				dto.setListaInstituciones(generateInstitutionDtos(u));
				dto.setPeriodo(null);
				dto.setMaterias(null);
				dto.setSeccions(null);
				dto.setAlumnos(null);
				uiUsers.add(dto);
			}
		});	
		
		return uiUsers;
	};
	
	/**
	 * Genera POJOs a partir de una lista EJB.
	 * @param users representa una lista de usuarios tipo ejb
	 * @return UserInterfaceInts, lista de intituciones POJO.
	 */
	private List<InstitucionPOJO> generateInstitutionDtos(Usuario users){
		
		List<InstitucionPOJO> uiInts = new ArrayList<InstitucionPOJO>();

		
		users.getInstitucions().stream().forEach(i -> {
			
			InstitucionPOJO dto = new InstitucionPOJO();
			dto.setIdInstitucion(i.getIdInstitucion());
			dto.setNombreInstitucion(i.getNombreInstitucion());
			dto.setLogoInstitucion(i.getLogoInstitucion());
			dto.setHasSuscripcion(i.getHasSuscripcion());
			
			uiInts.add(dto);
		});	
		
		return uiInts;
	};
	
	/**
	 * Retorna una lista de usuarios.
	 * @param user request de la capa frontend.
	 * @return lista de usuarios POJO
	 */
	@Override
	@Transactional
	public List<UsuarioPOJO> getAll(UsuarioRequest ur) {

		int idInst = ur.getUsuario().getIdInstitucion();
		Institucion ints = instituteRepository.findOne(idInst);
		
		List<Usuario> users =  usersRepository.findByInstitucionsIn(ints);
		return generateUserDtos(users);
	}
	
	/**
	 * Retorna los detalles de un Usuario..
	 * @param idUsuario, identificador unico de usuario.
	 * @return Usuario de tipo UsuarioPOJO.
	 */
	@Override
	@Transactional
	public UsuarioPOJO getUserById(int idUsuario) {

		Usuario user =  usersRepository.findOne(idUsuario);
		UsuarioPOJO dto = new UsuarioPOJO();
		BeanUtils.copyProperties(user, dto);
		dto.setActiveUs(user.getIsActiveUs());
		dto.setDateOfJoin(user.getDateOfJoin());
		dto.setRols(generateRolDto(user));
		dto.setTareas(generateTareaDto(user));

		return dto;
	}
	
	/**
	 * Genera una lista de tareas POJO.
	 * @param user al que se le genera la lista.
	 * @return lista de tareas pojo
	 */
	
	private List<TareaPOJO> generateTareaDto(Usuario user) {
		
		List<TareaPOJO> tareas = new ArrayList<TareaPOJO>();	
		
		user.getTareas().stream().forEach(t -> {
			TareaPOJO tarea = new TareaPOJO();
			BeanUtils.copyProperties(t, tarea);			
			
			tarea.setUsuarios(null);
			tarea.setActiveTa(t.getIsActiveTa());
			tarea.setRols(null);
			tareas.add(tarea);
		});	
		
		return tareas;
	}

	/**
	 * Genera una lista de roles POJO.
	 * @param user al que se le genera la lista.
	 * @return lista de roles pojo
	 */
	
	public List<RolPOJO> generateRolDto(Usuario user){
		
		List<RolPOJO> roles = new ArrayList<RolPOJO>();	
		
		user.getRols().stream().forEach(r -> {
			RolPOJO rol = new RolPOJO();
			BeanUtils.copyProperties(r, rol);			
			rol.setTareas(null);
			rol.setUsuarios(null);
			rol.setActiveRol(r.getIsActiveRol());
			roles.add(rol);
		});	
		
//		roles.stream().forEach(r -> {
//			System.out.print("ROL: " + r.getNombreRol() + " ");
//		});	
		
		return roles;	
	}
	
	/**
	 * Retorna una lista de usuarios activos.
	 * @return Lista de Usuario tipo POJO
	 */
	@Override
	@Transactional
	public List<UsuarioPOJO> getActiveUsers() {

		List<Usuario> users =  usersRepository.findByisActiveUsTrue();
		
		return generateUserDtos(users);
	}
	
	/**
	 * Retorna una lista de usuarios inactivos.
	 * @return Lista de Usuario tipo POJO
	 */
	@Override
	@Transactional
	public List<UsuarioPOJO> getInactiveUsers() {

		List<Usuario> users =  usersRepository.findByisActiveUsFalse();
		
		return generateUserDtos(users);
	}


	/**
	 * Retorna un unico usuario por ID.
	 * @param idUsuario, identificador unico de usuario.
	 * @return Usuario de tipo UsuarioEJB.
	 */
	@Override
	@Transactional
	public Usuario findById(int idUsuario) {

		return usersRepository.findOne(idUsuario);
	}

	/**
	 * Guarda los datos de un Usuario.
	 * @param user request de la capa frontend.
	 * @return booleano, true = success, false = error.
	 */
	@Override
	@Transactional
	public Boolean saveUser(UsuarioRequest ur) {

		Usuario newUser = new Usuario();
		Usuario nuser = null;
		
		List<String> idRoles = ur.getUsuario().getIdRoles();
		List<Rol> rols = new ArrayList<Rol>();
		List<Institucion> listInts = new ArrayList<Institucion>();
		List<Alumno> aList = new ArrayList<Alumno>();
		
		boolean hasRoles = false;
		boolean newPass = false;
//		boolean hasInstitutes = false;
		
		int idInst = ur.getUsuario().getIdInstitucion();
		Institucion ints = instituteRepository.findOne(idInst);
		listInts.add(ints);
		
		/**Copia las propiedades del Request a el nuevo Usuario**/
		BeanUtils.copyProperties(ur.getUsuario(), newUser);	
		newUser.setPassword("12345");
		newUser.setPassword(encryptor.ironEncryption(newUser.getPassword()));

		/**Verifica si la lista de roles contiene algo **/
		
		if(!idRoles.isEmpty()){
			hasRoles = true;	
		}
		
			
		/**Si el Usuario tiene un ID de -1, crea uno nuevo, si no modifica uno existente. **/
		
		if(ur.getUsuario().getIdUsuario() <= -1){
			
			newUser.setIdUsuario(0);
			newUser.setIsActiveUs(true);
			newUser.setDateOfJoin(getCurrentDate());
			newUser.setInstitucions(listInts);
			newUser.setPeriodos(getPeriodo());
			newUser.setAlumnos(aList);
			
			nuser = usersRepository.save(newUser);
			
		/** Si hay roles por agregar, toma el usuario recien creado y le asigna los roles **/
			
			if(hasRoles){
				nuser = assignUserRoles(rols,idRoles,nuser);
			}
	 
		}else{		
			
			Usuario oldUser = findById(newUser.getIdUsuario());
		
			if(hasRoles){
				
				oldUser = removeRoles(oldUser);
				rols = getRoles(idRoles);			
				oldUser.setRols(rols);
			}
			
			oldUser = assignProperties(oldUser, ur.getUsuario(), newPass);
			oldUser.setInstitucions(listInts);
			nuser = usersRepository.save(oldUser);	
			
		}
		return (nuser == null) ? false : true;
	}
	
	
	/**
	 * Metodo similar al SaveUser, pero para que un usuario se modifique a si mismo.
	 * @param ur request del usuario
	 * @return true si se salvo exitosamente, false de lo contrario
	 */
	@Transactional
	@Override
	public Boolean editProfile(UsuarioRequest ur){
		
		Usuario nuser = null;
		boolean newPass = ur.getUsuario().isNewPass();
	
		
		Usuario oldUser = findById(ur.getUsuario().getIdUsuario());
		oldUser = assignProperties(oldUser, ur.getUsuario(), newPass);
		
		nuser = usersRepository.save(oldUser);	
		
		return (nuser == null) ? false : true;
	}
	
	
	/**
	 *  Copia los nuevos datos traidos de la IU a el objeto que se va a salvar en la base
	 * @param DbUser usuario que se salvara a la base
	 * @param UiUser usuarioPOJO de la Interfaz
	 * @return el usuario de base con los atributos actualizados.
	 */
	
	private Usuario assignProperties(Usuario DbUser, UsuarioPOJO UiUser, boolean newPass){
		
	//	String hash;
	//	int length = 5;
		

		
		DbUser.setNombre(UiUser.getNombre());
		DbUser.setApellido(UiUser.getApellido());
		DbUser.setCedula(UiUser.getCedula());
		DbUser.setEmail(UiUser.getEmail());
		DbUser.setTelefono(UiUser.getTelefono());
		DbUser.setMovil(UiUser.getMovil());
		DbUser.setIsActiveUs(UiUser.isActiveUs());
		
		Periodo p = Iterables.getLast(DbUser.getPeriodos());
		boolean periodoActual = p.getIsActivePr();

		
		if(!periodoActual){	
			List<Periodo> lista = DbUser.getPeriodos();
			Periodo periodo = periodoRepository.findByIsActivePrTrue();
			lista.add(periodo);
			DbUser.setPeriodos(lista);
		}
		
		if(newPass){
			String userHash = UiUser.getPassword();
			DbUser.setPassword(encryptor.ironEncryption(userHash));
		}

		
//		else{
//			hash = encryptor.randomHilt(length);
//			DbUser.setPassword(encryptor.ironEncryption(hash));
//		}
		
		return DbUser;
	}
	
	 /**
	  * 
	  * @param rols es una lista vacia de roles.
	  * @param idRoles es una lista de ID de roles.
	  * @param user el usuario cuyos roles seran asignados.
	  * @return usuario modificado.
	  */
	private Usuario assignUserRoles(List<Rol> rols, List<String> idRoles, Usuario user){
		
		rols = getRoles(idRoles);
		user.setRols(rols);
		user  = usersRepository.save(user);
		
		return user;
	}
	
	
	/**
	 * Metodo que mediante una lista de roles, itera sobre esta y los agrega a la tabla
	 * intermedia de RolesUsuario en la base de datos.
	 * @param listaRoles
	 */
	private List<Rol> getRoles(List<String> idRoles) {
		
		List<Rol> rols = new ArrayList<Rol>();
		
		idRoles.stream().forEach(r -> {	
			
			int id = Integer.parseInt(r);
			Rol rol = rolRepository.findOne(id);
			rols.add(rol);
		
		});
		
		System.out.println("Consigo los nuevos roles" + rols.get(0));
		
		return rols;
		
	}

	/**
	 * Remueve los roles de un usuario antes de ser modificado.
	 * @param listaRoles
	 */
	
	private Usuario removeRoles(Usuario user) {
		
		user.setRols(null);
		user = usersRepository.save(user);
		
		System.out.println("Remuevo roles del usuario viejo" + user.toString());
		
		return user;
 
	}
	
	/**Esta funcion representa el periodo actual, cada ves que se hace una carga masiva nueva, un nuevo periodo
	 * es creado y seteado a los alumnos y usuarios nuevos. El sistema solo listara y permitira acceso a los usuarios
	 * cuyo perido actual sea verdadero.		   
	 */
	private List<Periodo> getPeriodo(){
		
		List<Periodo> list = new ArrayList<Periodo>();	
				
//		if(isNew){

			Periodo p = periodoRepository.findByIsActivePrTrue();
			list.add(p);	
			
//		}else{
//			list = u.getPeriodos();
//			Periodo p = periodoRepository.findByIsActivePrTrue();
//			list.add(p);			
//		}

		
		return list;
	}

	/**
	 * Consigue la fecha actual.
	 * @return esta fecha.
	 */
	public Date getCurrentDate(){
		
		 //  DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		   //get current date time with Date()
		   Date date = new Date();
		   return date;
	}


	public Boolean prueba(String mail) {
	
		int id = usersRepository.getUserIdbyEmail(mail);
		System.out.println(id);
		if(id >= 1){
			return true;
		}
		
		return false;
	}
	
	 /**
	  * Recibe un correo y retorna un Usuario POJO
	  * @param String
	  * @return UsuarioPOJO
	  */
	@Override
	public UsuarioPOJO getUserByEmail(String email) {
		
		Usuario u =  usersRepository.findByEmail(email);
		UsuarioPOJO dto = new UsuarioPOJO();

			BeanUtils.copyProperties(u, dto);
			dto.setActiveUs(u.getIsActiveUs());
			dto.setDateOfJoin(u.getDateOfJoin());
			dto.setRols(generateRolDto(u));

		
		return dto;
	}

	/**
	  * Recibe una cedula y retorna un Usuario POJO
	  * @param String
	  * @return UsuarioPOJO
	  */
	@Override
	public UsuarioPOJO getUserByCedula(String cedula) {
		Usuario u =  usersRepository.findByCedula(cedula);
		UsuarioPOJO dto = new UsuarioPOJO();
		
		if(u != null){
			BeanUtils.copyProperties(u, dto);
			dto.setActiveUs(u.getIsActiveUs());
			dto.setDateOfJoin(u.getDateOfJoin());
			dto.setRols(generateRolDto(u));
			dto.setListaInstituciones(generateInstitutionDtos(u));
			dto.setTareas(generateTareaDto(u));	
		}

		return dto;
	}
	
	@Override
	public UsuarioPOJO getUserByEncargadoDelInstituto(UsuarioRequest ur){

		Usuario u =  usersRepository.findByCedula(ur.getUsuario().getCedula());
		UsuarioPOJO dto = new UsuarioPOJO();

			BeanUtils.copyProperties(u, dto);
			dto.setActiveUs(u.getIsActiveUs());
			dto.setDateOfJoin(u.getDateOfJoin());
			dto.setRols(generateRolDto(u));
			dto.setTareas(null);
			dto.setListaInstituciones(generateInstitutionDtos(u));
			dto.setPeriodo(null);
			dto.setMaterias(null);
			dto.setSeccions(null);
			dto.setAlumnos(null);

		
		return dto;
	}

}
