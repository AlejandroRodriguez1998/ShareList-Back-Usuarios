package edu.uclm.esi.fakeaccountsbe.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.User;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("tokens") //Nombre publico de donde vamos a hacer las peticiones
@CrossOrigin(origins = { "https://localhost:4200" }, allowCredentials = "true" )
public class TokenController {
	
	@Autowired
	private UserDao userDao;
	
	@PutMapping("/validar")
	public boolean validar(@RequestBody String token) {
	    User user = this.userDao.findByToken(token);
	    
	    if (user == null) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
	    }
	    
	    return user.getPremium();
	}
	
	// Endpoint para validar un token y obtener el email y si es premium
    @GetMapping("/validarTokenYObtenerInfo")
    public Map<String, Object> validarTokenYObtenerInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.replace("Bearer ", "").trim();
        }
        
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no proporcionado");
        }
        
        User user = this.userDao.findByCookie(token);
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("email", user.getEmail());
        result.put("isPremium", user.getPremium());
        
        return result;
    }
}
















