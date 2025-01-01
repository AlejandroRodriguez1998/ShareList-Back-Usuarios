package edu.uclm.esi.fakeaccountsbe.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uclm.esi.fakeaccountsbe.dao.UserDao;
import edu.uclm.esi.fakeaccountsbe.model.User;

@Service
public class ActivationService {

    @Autowired
    private UserDao userDao;
    
    
    // Envía un correo de activación al usuario con un enlace que contiene el token de activación
    public void sendActivationEmail(User user) {
        // Generar token activación
        String tokenActivacion = UUID.randomUUID().toString();
        user.setTokenActivacion(tokenActivacion);
        // activated = false se estableció al registrar
        this.userDao.save(user);

        // Construimos la URL y el contenido HTML a enviar
        // Cuando el usuario pinche, irá a la página de "inicio" con ?activation=tokenActivacion
        String urlActivacion = "https://localhost:4200/?activation=" + tokenActivacion;
        String cuerpoHtml = "<p>Gracias por registrarte en ShareList. Para activar tu cuenta, haz clic en el siguiente enlace:</p>"
                + "<a href='" + urlActivacion + "'>Activar cuenta</a>";

        // Construimos el JSON con org.json
        JSONObject sender = new JSONObject()
                .put("email", "joselaranavarro10@gmail.com")
                .put("name", "ShareList");

        JSONObject destinatario = new JSONObject()
                .put("email", user.getEmail())
                .put("name", "Usuario");

        JSONArray toList = new JSONArray();
        toList.put(destinatario);

        JSONObject root = new JSONObject()
                .put("sender", sender)
                .put("to", toList)
                .put("subject", "Activa tu cuenta en ShareList")
                .put("htmlContent", cuerpoHtml);

        // Construir la petición HTTP con la api-key en el header
        String apiKey = "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";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
            .header("accept", "application/json")
            .header("content-type", "application/json")
            .header("api-key", apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(root.toString()))
            .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String respBody = response.body();

            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Correo de activación enviado con éxito. Respuesta: " + respBody);
            } else {
                throw new RuntimeException("Error enviando correo de activación. Status=" + statusCode + ". Resp=" + respBody);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo de activación", e);
        }
    }

    // Método para activar la cuenta de un usuario
    public String activateUser(String tokenActivacion) {
        User user = userDao.findByTokenActivacion(tokenActivacion);
        if (user == null) {
            throw new RuntimeException("Token de activación inválido");
        }
        if (user.isActivated()) {
            return "alreadyActivated"; 
        }
        user.setActivated(true);
        // No consumimos el token porque 
        //user.setTokenActivacion(null); // Consumimos el token
        userDao.save(user);
        return "activated"; 
    }
}
