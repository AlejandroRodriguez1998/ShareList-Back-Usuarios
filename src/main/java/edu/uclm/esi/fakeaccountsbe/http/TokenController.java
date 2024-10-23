package edu.uclm.esi.fakeaccountsbe.http;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("tokens") //Nombre publico de donde vamos a hacer las peticiones
@CrossOrigin("*") //Sirve para que el servidor o controlador que permita perticiones de cualquier lado
public class TokenController {
	
	@PutMapping("/validar")
	public void validar(@RequestBody String token) {
		if (new Random().nextBoolean()) 
			throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED);
	}
}
















