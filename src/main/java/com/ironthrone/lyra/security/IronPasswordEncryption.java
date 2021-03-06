package com.ironthrone.lyra.security;

import java.security.SecureRandom;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.stereotype.Component;

@Component
public class IronPasswordEncryption {

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();
	
	/**
	 * Encripcion basica, sencilla pero rapida.
	 * @param password contraseña a encriptar.
	 * @return hash del string dado
	 */
	
	public String ironEncryption(String password){
		
		BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
		return encryptor.encryptPassword(password);
		
	}
	
	/**
	 * Encripcion avanzada, fuerte pero mas lenta.
	 * @param password contraseña a encriptar.
	 * @return hash del string dado
	 */
	public String valyrianEncryption(String password){
		
		StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
		return encryptor.encryptPassword(password);
		
	}

	 
	/**
	 * Encripcion custumizada, se le puede cambiar el algoritmo de encripcion a i.e SHA-512
	 * @param password contraseña a encriptar.
	 * @return hash del string dado
	 */
	public String obsidianEncryption(String password){
		
		ConfigurablePasswordEncryptor encryptor = new ConfigurablePasswordEncryptor();
		encryptor.setAlgorithm("SHA-512");
		
		return encryptor.encryptPassword(password);
	}


	// matches password with hash
	
	public boolean clashSteel(String password, String hash){
		
		BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
		boolean isOkay = encryptor.checkPassword(password, hash);
		return isOkay;
	}
	


	public String randomHilt( int len ){
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}


	 

	
}
