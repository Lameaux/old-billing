package com.euromoby.api.sms.rest;

import com.euromoby.api.sms.dto.SmsRequest;
import com.euromoby.api.sms.dto.SmsRequestStatus;
import com.euromoby.api.sms.service.SmsRequestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureRestDocs(
        outputDir = "target/snippets",
        uriHost = "euromoby.com",
        uriScheme = "https",
        uriPort = 443
)
public class SmsRequestControllerDocTest {
    private static final String MSISDN = "+420123456789";
    private static final String MESSAGE = "Please call me";

    @Autowired
    private MockMvc mockMvc;

    private final SmsRequest savedSmsRequest;

    public SmsRequestControllerDocTest() {
        savedSmsRequest = SmsRequest.builder()
                .id(UUID.randomUUID())
                .status(SmsRequestStatus.NEW)
                .msisdn(MSISDN)
                .message(MESSAGE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @MockBean
    private SmsRequestService smsRequestService;

    @Test
    public void createSmsRequest() throws Exception {
        Mockito.when(smsRequestService.save(Mockito.any(SmsRequest.class))).thenReturn(savedSmsRequest);

        mockMvc.perform(RestDocumentationRequestBuilders.post(SmsRequestController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("msisdn", MSISDN)
                .param("message", MESSAGE))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(document("sms-requests/create",
                        requestParameters(
                                parameterWithName("msisdn").description("Recipient's MSISDN"),
                                parameterWithName("message").description("Message text")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Unique identifier"),
                                fieldWithPath("status").description("Status of the request"),
                                fieldWithPath("msisdn").description("Recipient's MSISDN"),
                                fieldWithPath("message").description("Message text"),
                                fieldWithPath("createdAt").description("Time when request was created"),
                                fieldWithPath("updatedAt").description("Time when request was last updated"),
                                subsectionWithPath("links").description("Links to the resources")
                        )
                ));
    }

    @Test
    public void getSmsRequest() throws Exception {
        Mockito.when(smsRequestService.findById(savedSmsRequest.getId())).thenReturn(Optional.of(savedSmsRequest));

        mockMvc.perform(RestDocumentationRequestBuilders.get(SmsRequestController.BASE_URL + "/{id}", savedSmsRequest.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(document("sms-requests/get-by-id",
                        pathParameters(parameterWithName("id").description("Unique identifier of an SMS request")),
                        responseFields(
                                fieldWithPath("id").description("Unique identifier"),
                                fieldWithPath("status").description("Status of the request"),
                                fieldWithPath("msisdn").description("Recipient's MSISDN"),
                                fieldWithPath("message").description("Message text"),
                                fieldWithPath("createdAt").description("Time when request was created"),
                                fieldWithPath("updatedAt").description("Time when request was last updated"),
                                subsectionWithPath("links").description("Links to the resources")
                        )
                ));
    }

}
