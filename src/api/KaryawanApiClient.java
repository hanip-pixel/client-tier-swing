package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.Karyawan;

public class KaryawanApiClient {
    private static final String BASE_URL = "http://localhost/application-tier-php/public/karyawan";
    private final HttpClient client;
    private final Gson gson;

    public KaryawanApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public List<Karyawan> findAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
        
        return parseListResponse(response.body());
    }

    public Karyawan create(Karyawan k) throws Exception {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("employee_id", k.getEmployeeId());
        requestBody.put("name", k.getName());
        requestBody.put("department", k.getDepartment());
        requestBody.put("position", k.getPosition());
        requestBody.put("salary", k.getSalary());
        requestBody.put("hire_date", k.getHireDate() != null ? k.getHireDate().toString() : null);
        requestBody.put("email", k.getEmail());
        requestBody.put("phone", k.getPhone());
        
        String json = gson.toJson(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // HANYA TAMPILKAN RESPONSE (tanpa log request)
        System.out.println("Raw response:");
        System.out.println(response.body());
        System.out.println();
        
        return parseSingleResponse(response);
    }

    public Karyawan update(Karyawan k) throws Exception {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("employee_id", k.getEmployeeId());
        requestBody.put("name", k.getName());
        requestBody.put("department", k.getDepartment());
        requestBody.put("position", k.getPosition());
        requestBody.put("salary", k.getSalary());
        requestBody.put("hire_date", k.getHireDate() != null ? k.getHireDate().toString() : null);
        requestBody.put("email", k.getEmail());
        requestBody.put("phone", k.getPhone());
        
        String json = gson.toJson(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + k.getId()))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // HANYA TAMPILKAN RESPONSE (tanpa log request)
        System.out.println("Raw response:");
        System.out.println(response.body());
        System.out.println();
        
        return parseSingleResponse(response);
    }

    public void delete(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .timeout(Duration.ofSeconds(15))
                .DELETE()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // HANYA TAMPILKAN RESPONSE (tanpa log request)
        System.out.println("Raw response:");
        System.out.println(response.body());
        System.out.println();
        
        parseVoidResponse(response);
    }
    
    public Karyawan findById(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return parseSingleResponse(response);
    }

    private List<Karyawan> parseListResponse(String jsonBody) throws Exception {
        ApiResponse<?> apiResp = gson.fromJson(jsonBody, ApiResponse.class);
        
        if (apiResp == null || !apiResp.success) {
            throw new Exception(apiResp != null ? apiResp.message : "Invalid API response");
        }
        
        String dataJson = gson.toJson(apiResp.data);
        return gson.fromJson(dataJson, new TypeToken<List<Karyawan>>() {}.getType());
    }

    private Karyawan parseSingleResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + extractErrorMessage(response.body()));
        }
        
        ApiResponse<?> apiResp = gson.fromJson(response.body(), ApiResponse.class);
        
        if (apiResp == null || !apiResp.success) {
            throw new Exception(apiResp != null ? apiResp.message : "Invalid API response");
        }
        
        String dataJson = gson.toJson(apiResp.data);
        return gson.fromJson(dataJson, Karyawan.class);
    }

    private void parseVoidResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + extractErrorMessage(response.body()));
        }
        
        ApiResponse<?> apiResp = gson.fromJson(response.body(), ApiResponse.class);
        
        if (apiResp == null || !apiResp.success) {
            throw new Exception(apiResp != null ? apiResp.message : "Invalid API response");
        }
    }

    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }

    private String extractErrorMessage(String body) {
        try {
            ApiResponse<?> resp = gson.fromJson(body, ApiResponse.class);
            return resp != null && resp.message != null ? resp.message : "Unknown server error";
        } catch (Exception e) {
            return "Server returned invalid response: " + 
                   (body != null ? body.substring(0, Math.min(100, body.length())) : "null");
        }
    }
}