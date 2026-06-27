package com.pagamento.simplificado.service;

import com.pagamento.simplificado.domain.User;
import com.pagamento.simplificado.dtos.NotificationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    private final RestTemplate restTemplate;

    NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        try {
            ResponseEntity<String> notificationResponse = restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);

            if (!(notificationResponse.getStatusCode() == HttpStatus.OK)){
                System.out.println("erro ao enviar notificacao");
                return;
            }

            System.out.println("Notificacao enviada");
        } catch (RestClientException exception) {
            System.out.println("erro ao enviar notificacao");
        }
    }
}
