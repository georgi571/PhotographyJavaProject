package bg.reports.web.controllers;

import bg.reports.exception.ReportNotFoundException;
import bg.reports.report.service.impl.ReportServiceImpl;
import bg.reports.web.dto.*;
import bg.reports.web.filter.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
class ReportControllerAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportServiceImpl reportServiceImpl;

    @MockitoBean
    private JWTService jwtService;

    private UUID reportId;
    private UUID challengeId;
    private UUID pictureId;
    private UUID commentId;
    private UUID authorId;
    private UUID userId;
    private UUID reporterId;

    @BeforeEach
    void setUp() {
        reportId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        pictureId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        userId = UUID.randomUUID();
        reporterId = UUID.randomUUID();

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
    void getAllPictureReports_ShouldReturnList() throws Exception {
        List<PictureReportResponse> mockResponse = List.of(new PictureReportResponse());
        when(reportServiceImpl.getAllPictureReports()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/reports/pictures")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllCommentReports_ShouldReturnList() throws Exception {
        List<CommentReportResponse> mockResponse = List.of(new CommentReportResponse());
        when(reportServiceImpl.getAllCommentReports()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/reports/comments")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllUserReports_ShouldReturnList() throws Exception {
        List<UserReportResponse> mockResponse = List.of(new UserReportResponse());
        when(reportServiceImpl.getAllUserReports()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/reports/users")
                        .header("Authorization", "Bearer mock-valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void reportPicture_ShouldReturnCreatedResponse() throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testUser", null, AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        PictureReportRequest request = new PictureReportRequest();
        request.setChallengeId(challengeId);
        request.setPictureId(pictureId);
        request.setAuthorId(authorId);
        request.setReason("Test Report");

        PictureReportResponse response = new PictureReportResponse();
        response.setId(UUID.randomUUID());
        response.setChallengeId(challengeId);
        response.setPictureId(pictureId);
        response.setAuthorId(authorId);
        response.setReason("Test Report");
        response.setReportedBy(reporterId);

        when(reportServiceImpl.savePictureReport(any(), any()))
                .thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/reports/pictures")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("challengeId").value(challengeId.toString()))
                .andExpect(jsonPath("pictureId").value(pictureId.toString()))
                .andExpect(jsonPath("authorId").value(authorId.toString()))
                .andExpect(jsonPath("reason").value("Test Report"))
                .andExpect(jsonPath("reportedBy").value(reporterId.toString()));
    }

    @Test
    void reportPicture_ShouldFailValidation_WhenMissingFields() throws Exception {
        PictureReportRequest invalidRequest = new PictureReportRequest();
        invalidRequest.setChallengeId(null);
        invalidRequest.setPictureId(pictureId);
        invalidRequest.setAuthorId(authorId);
        invalidRequest.setReason("Test Report");

        mockMvc.perform(post("/api/v1/reports/pictures")
                        .header("Authorization", "Bearer mock-valid-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reportComment_ShouldReturnCreatedResponse() throws Exception {
        CommentReportRequest request = new CommentReportRequest();
        request.setChallengeId(challengeId);
        request.setPictureId(pictureId);
        request.setCommentId(commentId);
        request.setAuthorId(authorId);
        request.setReason("Test Report");

        CommentReportResponse response = new CommentReportResponse();
        response.setId(UUID.randomUUID());
        response.setChallengeId(challengeId);
        response.setPictureId(pictureId);
        response.setCommentId(commentId);
        response.setAuthorId(authorId);
        response.setReason("Test Report");
        response.setReportedBy(reporterId);

        when(reportServiceImpl.saveCommentReport(any(), any()))
                .thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/reports/comments")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("challengeId").value(challengeId.toString()))
                .andExpect(jsonPath("pictureId").value(pictureId.toString()))
                .andExpect(jsonPath("commentId").value(commentId.toString()))
                .andExpect(jsonPath("authorId").value(authorId.toString()))
                .andExpect(jsonPath("reason").value("Test Report"))
                .andExpect(jsonPath("reportedBy").value(reporterId.toString()));
    }

    @Test
    void reportComment_ShouldFailValidation_WhenMissingFields() throws Exception {
        CommentReportRequest invalidRequest = new CommentReportRequest();
        invalidRequest.setChallengeId(null);
        invalidRequest.setPictureId(pictureId);
        invalidRequest.setCommentId(commentId);
        invalidRequest.setAuthorId(authorId);
        invalidRequest.setReason("Test Report");

        mockMvc.perform(post("/api/v1/reports/comments")
                        .header("Authorization", "Bearer mock-valid-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reportUser_ShouldReturnCreatedResponse() throws Exception {
        UserReportRequest request = new UserReportRequest();
        request.setUserId(userId);
        request.setReason("Test User Report");

        UserReportResponse response = new UserReportResponse();
        response.setId(UUID.randomUUID());
        response.setUserId(userId);
        response.setReason("Test User Report");
        response.setReportedBy(reporterId);

        when(reportServiceImpl.saveUserReport(any(), any()))
                .thenReturn(response);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/reports/user")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("userId").isNotEmpty())
                .andExpect(jsonPath("reason").isNotEmpty())
                .andExpect(jsonPath("reportedBy").isNotEmpty());
    }

    @Test
    void reportUser_ShouldFailValidation_WhenMissingFields() throws Exception {
        UserReportRequest invalidRequest = new UserReportRequest();
        invalidRequest.setUserId(null);
        invalidRequest.setReason("Test User Report");

        mockMvc.perform(post("/api/v1/reports/user")
                        .header("Authorization", "Bearer mock-valid-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReport_ShouldReturnNoContent() throws Exception {
        doNothing().when(reportServiceImpl).deleteReport(any(UUID.class));

        mockMvc.perform(delete("/api/v1/reports/" + reportId)
                        .header("Authorization", "Bearer mock-valid-token")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReport_ShouldReturnNotFoundWhenReportDoesNotExist() throws Exception {
        doThrow(new ReportNotFoundException("Report with ID " + reportId + " not found"))
                .when(reportServiceImpl).deleteReport(reportId);

        mockMvc.perform(delete("/api/v1/reports/" + reportId)
                        .header("Authorization", "Bearer mock-valid-token")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("message").value("Report with ID " + reportId + " not found"));
    }
}
