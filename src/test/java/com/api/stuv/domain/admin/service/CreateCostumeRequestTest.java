package com.api.stuv.domain.admin.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateCostumeRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("커스튬 등록 성공 테스트")
    public void createCostumeRequest() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        MockMultipartFile request = new MockMultipartFile(
                "post",
                "",
                "application/json",
                """
                {
                    "costumeName": "바나모",
                    "point": 100
                }
                """.getBytes()
        );

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/admin/costume")
                .file(file)
                .file(request)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        resultActions.andExpect(status().isCreated());
    }
}
