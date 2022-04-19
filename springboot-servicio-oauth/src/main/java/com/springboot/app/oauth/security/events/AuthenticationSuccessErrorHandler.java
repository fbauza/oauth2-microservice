package com.springboot.app.oauth.security.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.springboot.app.commons.usuarios.models.entity.Usuario;
import com.springboot.app.oauth.services.IUsuarioService;

import brave.Tracer;
import feign.FeignException;

@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher{

	private Logger log = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private Tracer tracer;	
	
	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		
		if (authentication.getDetails() instanceof WebAuthenticationDetails) {
		//if (authentication.getName().equalsIgnoreCase("frontendapp"))
			return;
		}
		
		UserDetails user = (UserDetails) authentication.getPrincipal();
		String messageLog = "Success Login: " + user.getUsername();
		System.out.println(messageLog);
		log.info(messageLog);
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		
		if (usuario.getAttemps() != null && usuario.getAttemps() > 0) {
			usuario.setAttemps(0);
			
			usuarioService.update(usuario, usuario.getId());
		}
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		String messageLog = "Login Error: " + exception.getMessage();
		System.out.println(messageLog);
		log.error(messageLog);
		
		try {
			
			StringBuilder errMessage = new StringBuilder();
			errMessage.append(messageLog);
			
			Usuario usuario = usuarioService.findByUsername(authentication.getName());
			if (usuario.getAttemps() == null) {
				usuario.setAttemps(0);
			}
			
			usuario.setAttemps(usuario.getAttemps()+1);
			log.info(String.format("Intentos usuario: %s ==> %d ", usuario.getUsername(), usuario.getAttemps()));
			
			errMessage.append(" - Intentos usuario: " + usuario.getUsername() + " ==> " + usuario.getAttemps());

			
			if (usuario.getAttemps() >= 3) {
				String errAttemps = String.format("usuario %s inhabilitado, intentos superados, intentos generados %d",
						usuario.getUsername(),
						usuario.getAttemps());
				log.error(errAttemps);
				errMessage.append(" - " + errAttemps);
				
				usuario.setEnabled(false);
			}
			
			usuarioService.update(usuario, usuario.getId());
			
			tracer.currentSpan().tag("error.message", errMessage.toString());
			
		} catch (FeignException e) {
			log.error(String.format("Usuario inexistente %s", authentication.getName()));
		}
		
	}

}
