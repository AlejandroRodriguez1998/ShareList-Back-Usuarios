package edu.uclm.esi.fakeaccountsbe.http;

import java.util.Collection;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.CredencialesRegistro;
import edu.uclm.esi.fakeaccountsbe.model.User;
import edu.uclm.esi.fakeaccountsbe.services.UserService;

@RestController
@RequestMapping("users") //Nombre publico de donde vamos a hacer las peticiones
@CrossOrigin(origins = { "https://localhost:4200" }, allowCredentials = "true" )  //Sirve para que el servidor o controlador que permita perticiones de cualquier lado

public class UserController {
	@Autowired //instanciar este objeto sin llamar al constructor
	private UserService userService;
	
	@Autowired
	private UserDao userDao;
	
	@GetMapping("/checkCookie")
	public String checkCookie(HttpServletRequest request) {
		String fakeUserId = this.findCookie(request, "fakeUserId");
		if (fakeUserId!=null) {
			User user = this.userDao.findByCookie(fakeUserId);
			if (user!=null) {
				user.setToken(UUID.randomUUID().toString());
				this.userDao.save(user);
				return user.getToken();
			}
		}
		return null;
	}
	
	@PostMapping("/registrar1")
	public void registrar1(HttpServletRequest req, @RequestBody CredencialesRegistro cr) {
		cr.comprobar();
		User user = new User();
		user.setEmail(cr.getEmail());
		user.setPwd(cr.getPwd1());
		
		this.userService.registrar(req.getRemoteAddr(), user);
	}
	
	@PutMapping("/login1")
	public void login1(HttpServletResponse response, HttpServletRequest request, @RequestBody(required = false) User user) {
	    User authenticatedUser = this.userService.find(user.getEmail(), user.getPwd());

	    // Generar nuevo token
	    String token = UUID.randomUUID().toString();
	    authenticatedUser.setToken(token);
	    this.userDao.save(authenticatedUser);

	    // Establecer la cookie del token
	    Cookie cookie = new Cookie("token", token);
	    cookie.setMaxAge(3600 * 24 * 365);
	    cookie.setPath("/");
	    cookie.setHttpOnly(true); // Importante para seguridad
	    cookie.setSecure(true);
	    cookie.setAttribute("SameSite", "None");
	    response.addCookie(cookie);
	}

	
	private String findCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies==null)
			return null;
		for (int i=0; i<cookies.length; i++)
			if (cookies[i].getName().equals(cookieName))
				return cookies[i].getValue();
		return null;
	}
	
	@GetMapping("/logout")
	public void logout(HttpServletResponse response, HttpServletRequest request) {
	    String token = null;
	    if (request.getCookies() != null) {
	        for (Cookie cookie : request.getCookies()) {
	            if ("token".equals(cookie.getName())) {
	                token = cookie.getValue();
	                break;
	            }
	        }
	    }

	    if (token != null) {
	        User user = this.userDao.findByToken(token);
	        if (user != null) {
	            // Eliminar el token del usuario
	            user.setToken(null);
	            this.userDao.save(user);
	        }

	        // Eliminar la cookie
	        Cookie cookie = new Cookie("token", null);
	        cookie.setMaxAge(0);
	        cookie.setPath("/");
	        cookie.setHttpOnly(true);
	        cookie.setSecure(true);
	        cookie.setAttribute("SameSite", "None");
	        response.addCookie(cookie);
	    }
	}

	
	@GetMapping("/premium")
	public void premium(HttpServletRequest request) {
		//String token = request.getHeader("Authorization").replace("Bearer ", "").trim();
		//User user = this.userDao.findByToken(token);
		
		User user = (User) request.getAttribute("user");
	    if (user == null) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene permisos para esta acción.");
	    }
		user.setPremium(true);
	
		this.userDao.save(user);
	}
	
	@GetMapping("/registrar2")
	public void registrar2(HttpServletRequest req, @RequestParam String email, @RequestParam String pwd1, @RequestParam String pwd2) {
		CredencialesRegistro cr = new CredencialesRegistro();
		cr.setEmail(email);
		cr.setPwd1(pwd1);
		cr.setPwd2(pwd2);
		cr.comprobar();
		User user = new User();
		user.setEmail(cr.getEmail());
		user.setPwd(cr.getPwd1());
		
		this.userService.registrar(req.getRemoteAddr(), user);
	}
	
	@GetMapping("/registrarMuchos")
	public void registrarMuchos(HttpServletRequest req, @RequestParam String name, @RequestParam Integer n) {
		for (int i=0; i<n; i++)
			this.registrar2(req, name + i + "@pepe.com", "Pepe1234", "Pepe1234");
	}

	@GetMapping("/login2")
	public User login2(HttpServletResponse response, @RequestParam String email, @RequestParam String pwd) {
		pwd = org.apache.commons.codec.digest.DigestUtils.sha512Hex(pwd);
		User user = this.userService.find(email, pwd);
		user.setToken(UUID.randomUUID().toString());
		response.setHeader("token", user.getToken());
		return user;
	}
	
	@GetMapping("/login3/{email}")
	public User login3(HttpServletResponse response, @PathVariable String email, @RequestParam String pwd) {
		return this.login2(response, email, pwd);
	}
	
	@GetMapping("/getAllUsers")
	public Collection<User> getAllUsers() {
		return (Collection<User>) this.userService.getAllUsers();
	}
	
	@DeleteMapping("/delete")
	public void delete(HttpServletRequest request, @RequestParam String email, @RequestParam String pwd) {
		User user = this.userService.find(email, pwd);
		
		String token = request.getHeader("token");
		if (!token.equals(user.getToken()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token " + token + " inválido");
		
		this.userService.delete(email);
	}
	
	@DeleteMapping("/clearAll")
	public void clearAll(HttpServletRequest request) {
		String sToken = request.getHeader("prime");
		Integer token = Integer.parseInt(sToken);
		if (!isPrime(token.intValue()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Debes pasar un número primo en la cabecera");
		if (sToken.length()!=3)
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El nº primo debe tener tres cifras");
		this.userService.clearAll();
	}
	
	private boolean isPrime(int n) {
	    if (n <= 1) return false;
	    for (int i = 2; i <= Math.sqrt(n); i++) {
	        if (n % i == 0) return false;
	    }
	    return true;
	}
}
















