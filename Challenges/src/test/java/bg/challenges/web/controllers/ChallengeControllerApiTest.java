package bg.challenges.web.controllers;

import bg.challenges.challenge.model.ChallengeActivity;
import bg.challenges.challenge.model.ChallengeType;
import bg.challenges.challenge.service.ChallengeService;
import bg.challenges.challenge.service.CommentService;
import bg.challenges.challenge.service.PictureService;
import bg.challenges.web.dto.*;
import bg.challenges.web.filter.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChallengeController.class)
class ChallengeControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChallengeService challengeService;

    @MockitoBean
    private PictureService pictureService;

    @MockitoBean
    private CommentService commentService;

    @InjectMocks
    private ChallengeController challengeController;

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
                "ROLE_USER", "PERMISSION_deletePicture", "ROLE_ADMIN", "PERMISSION_deleteMessage"
        ));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetChallengesPage() throws Exception {
        mockMvc.perform(get("/api/v1/challenges"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllChallenges() throws Exception {
        ChallengeResponse challengeResponse = new ChallengeResponse();
        challengeResponse.setDetails("Test Details");
        challengeResponse.setId(UUID.randomUUID());
        challengeResponse.setType(ChallengeType.DAILY.name());
        challengeResponse.setTitle("Test Title");
        challengeResponse.setEndAt(LocalDateTime.now());
        challengeResponse.setStartAt(LocalDateTime.now());
        challengeResponse.setActivity(ChallengeActivity.ACTIVE.name());
        challengeResponse.setDescription("Test Description");

        List<ChallengeResponse> challengeResponseList = List.of(challengeResponse);

        when(challengeService.getAllChallenges()).thenReturn(challengeResponseList);;

        mockMvc.perform(get("/api/v1/challenges/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].details").isNotEmpty())
                .andExpect(jsonPath("$[0].type").isNotEmpty())
                .andExpect(jsonPath("$[0].endAt").isNotEmpty())
                .andExpect(jsonPath("$[0].startAt").isNotEmpty())
                .andExpect(jsonPath("$[0].startAt").isNotEmpty())
                .andExpect(jsonPath("$[0].description").isNotEmpty());

        verify(challengeService, times(1)).getAllChallenges();
    }

    @Test
    void testGetChallengeDetails() throws Exception {
        UUID challengeId = UUID.randomUUID();
        ChallengeDetailsResponse challengeDetailsResponse = new ChallengeDetailsResponse();
        challengeDetailsResponse.setDetails("Test Details");
        challengeDetailsResponse.setId(UUID.randomUUID());
        challengeDetailsResponse.setType(ChallengeType.DAILY.name());
        challengeDetailsResponse.setTitle("Test Title");
        challengeDetailsResponse.setEndAt(LocalDateTime.now());
        challengeDetailsResponse.setStartAt(LocalDateTime.now());
        challengeDetailsResponse.setActivity(ChallengeActivity.ACTIVE.name());
        challengeDetailsResponse.setDescription("Test Description");

        when(challengeService.getChallengeDetails(any(), any())).thenReturn(challengeDetailsResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/challenges/{id}", challengeId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.details").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.endAt").isNotEmpty())
                .andExpect(jsonPath("$.startAt").isNotEmpty())
                .andExpect(jsonPath("$.startAt").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty());

        verify(challengeService, times(1)).getChallengeDetails(any(), any());
    }

    @Test
    void testUploadPicture() throws Exception {
        UUID challengeId = UUID.randomUUID();

        PictureResponse pictureResponse = new PictureResponse();
        pictureResponse.setCaption("Caption");
        pictureResponse.setStory("Story");
        pictureResponse.setId(UUID.randomUUID());

        when(challengeService.savePictureForChallenge(any(), any(), any(), any(), any()))
                .thenReturn(pictureResponse);

        MockMultipartFile file = new MockMultipartFile("file", "dummyfile.txt", "text/plain", "dummyfile".getBytes());

        MockHttpServletRequestBuilder sendRequest = multipart("/api/v1/challenges/{challengeId}/pictures", challengeId)
                .file(file)
                .param("caption", "Caption")
                .param("story", "Story")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caption").isNotEmpty())
                .andExpect(jsonPath("$.story").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty());

        verify(challengeService, times(1)).savePictureForChallenge(any(), any(), any(), any(), any());
    }

    @Test
    void testToggleLikePicture() throws Exception {
        UUID pictureId = UUID.randomUUID();

        PictureToggleResponse pictureToggleResponse = new PictureToggleResponse();
        pictureToggleResponse.setLiked(true);

        when(pictureService.toggleLikePicture(any(), any()))
                .thenReturn(pictureToggleResponse);

        MockHttpServletRequestBuilder sendRequest = patch("/api/v1/challenges/pictures/{pictureId}/toggle-like", pictureId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true));

        verify(pictureService, times(1)).toggleLikePicture(any(), any());
    }

    @Test
    void testAddComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("Test Reply");

        UUID pictureId = UUID.randomUUID();

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setText("Great picture!");
        commentResponse.setId(UUID.randomUUID());

        when(pictureService.addComment(any(), any(), any()))
                .thenReturn(commentResponse);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/challenges/pictures/{pictureId}/comments", pictureId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty());

        verify(pictureService, times(1)).addComment(any(), any(), any());
    }

    @Test
    void testDeletePicture() throws Exception {
        UUID pictureId = UUID.randomUUID();

        doNothing().when(pictureService).deletePicture(any(), any(), any());

        MockHttpServletRequestBuilder sendRequest = delete("/api/v1/challenges/pictures/{pictureId}", pictureId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isNoContent());

        verify(pictureService, times(1)).deletePicture(any(), any(), any());
    }

    @Test
    void testDeleteComment() throws Exception {
        UUID commentId = UUID.randomUUID();

        doNothing().when(commentService).deleteComment(any(), any(), any());

        MockHttpServletRequestBuilder sendRequest = delete("/api/v1/challenges/comments/{commentId}", commentId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(any(), any(), any());
    }

    @Test
    void testCreateChallenge() throws Exception {
        CreateChallengeRequest request = new CreateChallengeRequest();
        request.setTitle("Test Challenge");
        request.setDescription("Test Description");
        request.setDetails("Test Details");
        request.setType("DAILY");

        ChallengeResponse challengeResponse = new ChallengeResponse();
        challengeResponse.setTitle("Test Challenge");
        challengeResponse.setDescription("Test Description");
        challengeResponse.setDetails("Test Details");
        challengeResponse.setStartAt(LocalDateTime.now());
        challengeResponse.setEndAt(LocalDateTime.now().plusDays(5));
        challengeResponse.setType("DAILY");
        challengeResponse.setActivity("ACTIVE");
        challengeResponse.setId(UUID.randomUUID());

        when(challengeService.createChallenge(any()))
                .thenReturn(challengeResponse);

        MockHttpServletRequestBuilder sendRequest = post("/api/v1/challenges/create-challenge")
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.details").isNotEmpty())
                .andExpect(jsonPath("$.startAt").isNotEmpty())
                .andExpect(jsonPath("$.endAt").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty());

        verify(challengeService, times(1)).createChallenge(any());
    }

    @Test
    void testEditChallenge() throws Exception {
        UUID challengeId = UUID.randomUUID();

        EditChallengeRequest request = new EditChallengeRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setDetails("Updated Details");
        request.setType("ADMIN");

        ChallengeResponse challengeResponse = new ChallengeResponse();
        challengeResponse.setId(challengeId);
        challengeResponse.setTitle("Updated Title");
        challengeResponse.setDescription("Updated Description");
        challengeResponse.setDetails("Updated Details");
        challengeResponse.setStartAt(LocalDateTime.now());
        challengeResponse.setEndAt(LocalDateTime.now().plusDays(7));
        challengeResponse.setType("ADMIN");

        when(challengeService.editChallenge(any(), any()))
                .thenReturn(challengeResponse);

        MockHttpServletRequestBuilder sendRequest = put("/api/v1/challenges/{id}", challengeId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request));

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.details").isNotEmpty())
                .andExpect(jsonPath("$.startAt").isNotEmpty())
                .andExpect(jsonPath("$.endAt").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty());

        verify(challengeService, times(1)).editChallenge(any(), any());
    }

    @Test
    void testDeleteChallenge() throws Exception {
        UUID challengeId = UUID.randomUUID();

        doNothing().when(challengeService).deleteChallenge(any());

        MockHttpServletRequestBuilder sendRequest = delete("/api/v1/challenges/{id}", challengeId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isNoContent());

        verify(challengeService, times(1)).deleteChallenge(any());
    }

    @Test
    void testGetReportedPicture() throws Exception {
        UUID pictureId = UUID.randomUUID();

        PictureReportResponse pictureReportResponse = new PictureReportResponse();
        pictureReportResponse.setId(UUID.randomUUID());
        pictureReportResponse.setImageUrl("http://example.com/picture.jpg");
        pictureReportResponse.setChallengeId(UUID.randomUUID());
        pictureReportResponse.setAuthorId(UUID.randomUUID());

        when(pictureService.getReportedPicture(any())).thenReturn(pictureReportResponse);

        mockMvc.perform(get("/api/v1/challenges/reported/picture/{id}", pictureId)
                        .header("Authorization", "Bearer mock-valid-token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.imageUrl").isNotEmpty())
                .andExpect(jsonPath("$.challengeId").isNotEmpty())
                .andExpect(jsonPath("$.authorId").isNotEmpty());

        verify(pictureService, times(1)).getReportedPicture(any());
    }

    @Test
    void testGetReportedComment() throws Exception {
        UUID commentId = UUID.randomUUID();

        CommentReportResponse commentReportResponse = new CommentReportResponse();
        commentReportResponse.setId(UUID.randomUUID());
        commentReportResponse.setText("This is a reported comment.");
        commentReportResponse.setImageUrl("http://example.com/comment.jpg");
        commentReportResponse.setChallengeId(UUID.randomUUID());
        commentReportResponse.setAuthorId(UUID.randomUUID());

        when(commentService.getReportedComment(any())).thenReturn(commentReportResponse);

        MockHttpServletRequestBuilder sendRequest = get("/api/v1/challenges/reported/comment/{id}", commentId)
                .header("Authorization", "Bearer mock-valid-token")
                .with(csrf());

        mockMvc.perform(sendRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.text").isNotEmpty())
                .andExpect(jsonPath("$.imageUrl").isNotEmpty())
                .andExpect(jsonPath("$.authorId").isNotEmpty());

        verify(commentService, times(1)).getReportedComment(any());
    }

    @Test
    void testGetAllPictureForUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();

        PictureResponse pictureResponse = new PictureResponse();
        pictureResponse.setId(pictureId);
        pictureResponse.setImageUrl("http://example.com/picture.jpg");
        pictureResponse.setAuthorId(userId);
        pictureResponse.setCaption("Test Caption");
        pictureResponse.setStory("Test Story");
        pictureResponse.setLikes(10);
        pictureResponse.setLiked(true);
        pictureResponse.setComments(Collections.emptyList());

        List<PictureResponse> pictureResponses = Collections.singletonList(pictureResponse);

        when(pictureService.getAllPicturesByUser(any(), any())).thenReturn(pictureResponses);

        mockMvc.perform(get("/api/v1/challenges/pictures/all/{id}", userId)
                        .header("Authorization", "Bearer mock-valid-token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].imageUrl").isNotEmpty())
                .andExpect(jsonPath("$[0].authorId").isNotEmpty())
                .andExpect(jsonPath("$[0].caption").isNotEmpty())
                .andExpect(jsonPath("$[0].story").isNotEmpty())
                .andExpect(jsonPath("$[0].likes").isNotEmpty())
                .andExpect(jsonPath("$[0].liked").isNotEmpty())
                .andExpect(jsonPath("$[0].comments").isEmpty());

        verify(pictureService, times(1)).getAllPicturesByUser(any(), any());
    }

}