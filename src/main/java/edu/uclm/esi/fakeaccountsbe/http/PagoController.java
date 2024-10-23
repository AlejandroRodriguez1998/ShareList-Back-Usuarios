package edu.uclm.esi.fakeaccountsbe.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.fakeaccountsbe.services.PagosService;

@RestController
@RequestMapping("pagos") //Nombre publico de donde vamos a hacer las peticiones
@CrossOrigin("*") //Sirve para que el servidor o controlador que permita perticiones de cualquier lado
public class PagoController {
	
	@Autowired
	private PagosService service;
	
	@PutMapping("/prepararTransaccion")
	public String prepararTransaccion(@RequestBody float importe) {
		return this.service.prepararTransaccion((long) (importe * 100));
	}
}
















