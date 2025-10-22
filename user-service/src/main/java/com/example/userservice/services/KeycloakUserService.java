package com.example.userservice.services;

import org.springframework.stereotype.Service;
import com.example.userservice.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakUserService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getAdminToken() {
        String url = serverUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String body = "grant_type=password"
                + "&client_id=" + clientId
                + "&username=" + adminUsername
                + "&password=" + adminPassword;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public void createUser(RegisterRequest registerRequest) {
        String token = getAdminToken();
        String url = serverUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);


        // Check if user already exists by email
        String checkUrl = url + "?email=" + registerRequest.getEmail();
        ResponseEntity<List> existingUsers = restTemplate.exchange(
                checkUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );





        if (existingUsers.getBody() != null && !existingUsers.getBody().isEmpty()) {
            System.out.println(" User with email already exists: " + registerRequest.getEmail());
            throw new RuntimeException("User with this email already exists");
        }

        // If not exists, create new user





        Map<String, Object> user = Map.of(
                "username", registerRequest.getUsername(),
                "email", registerRequest.getEmail(),
                "enabled", true,
                "firstName", registerRequest.getFirstName(),
                "lastName", registerRequest.getLastName(),
                "credentials", new Object[]{
                        Map.of(
                                "type", "password",
                                "value", registerRequest.getPassword(),
                                "temporary", false
                        )
                }
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println("User creation response: " + response.getStatusCode());
    }











    public List<Map<String, Object>> getAllUsers() {
        String token = getAdminToken();
        String url = serverUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        return response.getBody();
    }






    public Map<String, Object> getUserById(String userId) {
        String token = getAdminToken();
        String url = serverUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
    public void updateUser(String userId, Map<String, Object> updates) {
        String token = getAdminToken();
        String url = serverUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }
    public void deleteUser(String userId) {
        String token = getAdminToken();
        String url = serverUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }





}
