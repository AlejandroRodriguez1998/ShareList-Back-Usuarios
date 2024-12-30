package edu.uclm.esi.fakeaccountsbe.services;

import java.time.Instant;
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
public class PasswordResetService {

    @Autowired
    private UserDao userDao;



    public void sendResetPasswordEmail(String email) {
        // Localiza el usuario y guarda el token
        User usuario = userDao.findById(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setTokenReset(token);
        usuario.setTokenResetExpiracion(Instant.now().plusSeconds(3600).toEpochMilli()); // Expira en 1 hora
        userDao.save(usuario);

        // Construimos la URL y el contenido HTML a enviar
        String urlReset = "https://localhost:4200/reset-password?token=" + token;
        String cuerpoHtml = "<p>Haz clic en el siguiente enlace para restablecer tu contraseña:</p>"
                + "<a href='" + urlReset + "'>Restablecer Contraseña</a>";

        // Construimos el JSON con org.json
        // Estructura mínima: sender, to, subject, htmlContent
        JSONObject sender = new JSONObject()
                .put("email", "joselaranavarro10@gmail.com")  // Remitente
                .put("name", "ShareList");                    // Nombre remitente

        JSONObject destinatario = new JSONObject()
                .put("email", email)
                .put("name", "Usuario");

        JSONArray toList = new JSONArray();
        toList.put(destinatario);

        JSONObject root = new JSONObject()
                .put("sender", sender)
                .put("to", toList)
                .put("subject", "Restablece tu contraseña")
                .put("htmlContent", cuerpoHtml);

        // Construir la petición HTTP con la api-key en el header
        String apiKey = "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";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
            .header("accept", "application/json")
            .header("content-type", "application/json")
            .header("api-key", apiKey)
            // POST con el body en JSON
            .POST(HttpRequest.BodyPublishers.ofString(root.toString()))
            .build();

        // Mandamos la request y gestionamos la respuesta
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            int statusCode = response.statusCode();
            String respBody = response.body();
            
            // Si 2xx, consideramos éxito
            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Correo enviado con éxito. Respuesta de Brevo: " + respBody);
            } else {
                // Si no, lanzamos excepción con info
                throw new RuntimeException("Error enviando correo. Status=" + statusCode + ". Respuesta=" + respBody);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo", e);
        }
    }

    // Método que resetea la contraseña de un usuario
    public void resetPassword(String tokenReset, String nuevaPassword) {
        User usuario = userDao.findByTokenReset(tokenReset);
        if (!isResetTokenValid(tokenReset)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        usuario.setPwd(nuevaPassword);
        usuario.setTokenReset(null);
        usuario.setTokenResetExpiracion(0);
        userDao.save(usuario);
    }
    
    // Método que comprueba si un token de reset es válido
    public boolean isResetTokenValid(String token) {
        User usuario = userDao.findByTokenReset(token);
        if (usuario == null) {
            return false;
        }
        // Comprobar fecha de expiración
        if (usuario.getTokenResetExpiracion() < System.currentTimeMillis()) {
            return false;
        }
        return true;
    }
}
