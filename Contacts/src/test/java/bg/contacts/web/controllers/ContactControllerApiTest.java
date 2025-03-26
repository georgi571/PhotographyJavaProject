package bg.contacts.web.controllers;

import bg.contacts.contact.service.ContactMessageService;
import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;
import bg.contacts.web.dto.ContactReplayRequest;
import bg.contacts.web.filter.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
class ContactControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactMessageService contactMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private ContactController contactController;

    @MockitoBean
    private JWTService jwtService;

    private UUID adminId;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();

        when(jwtService.validateToken(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("testUser");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testUser", null, AuthorityUtils.createAuthorityList(
                "ROLE_USER", "PERMISSION_deletePicture", "ROLE_ADMIN", "PERMISSION_deleteMessage",
                "PERMISSION_banUsers"
        ));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetContactsPage() throws Exception {
        mockMvc.perform(get("/api/v1/contacts"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllMessages() throws Exception {
        ContactMessageResponse messageResponse = new ContactMessageResponse();
        messageResponse.setName("Test User");
        messageResponse.setEmail("testuser@abv.bg");
        messageResponse.setMessage("New message");

        List<ContactMessageResponse> messageResponses = List.of(messageResponse);

        when(contactMessageService.getAllMessages()).thenReturn(messageResponses);

        mockMvc.perform(get("/api/v1/contacts/receive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("testuser@abv.bg"))
                .andExpect(jsonPath("$[0].message").value("New message"));

        verify(contactMessageService, times(1)).getAllMessages();
    }

    @Test
    void testReceiveMessage() throws Exception {
        ContactMessageRequest request = new ContactMessageRequest();
        request.setName("Test User");
        request.setEmail("testuser@abv.bg");
        request.setMessage("New message");

        ContactMessageResponse response = new ContactMessageResponse();
        response.setName("Test User");
        response.setEmail("testuser@abv.bg");
        response.setMessage("New message");

        when(contactMessageService.receiveContactMessage(any(ContactMessageRequest.class))).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/contacts/receive")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("testuser@abv.bg"))
                .andExpect(jsonPath("$.message").value("New message"));

        verify(contactMessageService, times(1)).receiveContactMessage(any(ContactMessageRequest.class));
    }

    @Test
    void testSendReply() throws Exception {
        ContactReplayRequest request = new ContactReplayRequest();
        request.setId(UUID.randomUUID());
        request.setAnswer("Test Reply");

        ContactMessageResponse response = new ContactMessageResponse();
        response.setMessage("Test Message");

        when(contactMessageService.sendAnswer(any(), any())).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = patch("/api/v1/contacts/reply")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Test Message"));

        verify(contactMessageService, times(1)).sendAnswer(any(), any());
    }

    @Test
    void testDeleteMessage() throws Exception {
        UUID messageId = UUID.randomUUID();

        ContactMessageResponse response = new ContactMessageResponse();
        response.setName("Test User");
        response.setEmail("testuser@abv.bg");
        response.setMessage("Message to be deleted");

        when(contactMessageService.deleteMessage(any(), any())).thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = delete("/api/v1/contacts/{id}", messageId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("testuser@abv.bg"))
                .andExpect(jsonPath("$.message").value("Message to be deleted"));

        verify(contactMessageService, times(1)).deleteMessage(any(), any());
    }

}