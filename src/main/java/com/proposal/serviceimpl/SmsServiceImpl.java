package com.proposal.serviceimpl;

import com.proposal.service.SmsService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsServiceImpl implements SmsService {

    private static final String SMS_URL = "https://govtsms.odisha.gov.in/api/api.php";
    private static final String ACTION = "sendOTPSMS";
    private static final String SOURCE = "ODIGOV";
    private static final String DEPARTMENT_ID = "D047010";
    private static final String TEMPLATE_ID = "1007663666376547511";
    private static final String TEMPLATE_TEXT = "OTP for password change is %s. Do not share it with anyone. PanchayatiRaj-Samikshya, PR&DW Dept, GoO";

    private final RestTemplate restTemplate;

    public SmsServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean sendOtpSms(String phoneNumber, String otp) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            String smsContent = String.format(TEMPLATE_TEXT, otp);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("action", ACTION);
            body.add("source", SOURCE);
            body.add("department_id", DEPARTMENT_ID);
            body.add("template_id", TEMPLATE_ID);
            body.add("sms_content", smsContent);
            body.add("phonenumber", phoneNumber);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(SMS_URL, requestEntity, String.class);

            System.out.println("SMS API Response for phone " + phoneNumber + ": " + response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Failed to send OTP SMS to " + phoneNumber + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
