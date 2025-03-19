package com.example.art;

import com.example.art.design.model.*;
import com.example.art.design.repository.DesignRepository;
import com.example.art.design.service.DesignService;
import com.example.art.history.service.HistoryService;
import com.example.art.user.model.User;
import com.example.art.web.dto.DesignDecorationRequest;
import com.example.art.web.dto.DesignFormRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DesignServiceUTest {

    @Mock
    private DesignRepository designRepository;

    @Mock
    private HistoryService historyService;

    @Captor
    private ArgumentCaptor<Design> designCaptor;

    @InjectMocks
    private DesignService designService;


    @Test
    void whenSaveForm_thenReturnSavedForm() {
        DesignFormRequest dto = new DesignFormRequest();
        dto.setForm(FormDesign.ALMOND);
        dto.setConstruction(ConstructionDesign.NO);

        assertNotNull(designService.saveForm(dto));
    }


    @Test
    void whenSaveDecoration_thenReturnSavedDecoration() {
        DesignDecorationRequest dto = new DesignDecorationRequest();
        dto.setColor("03");
        dto.setPebbles(DecorationPebbles.DIAMONDS);
        dto.setPicture(DecorationPicture.CAT);

        assertNotNull(designService.saveDecoration(dto));
    }


    @Test
    void whenGetLastDesignById_thenReturnLastDesign() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();

        Design design = Design.builder()
                .form(FormDesign.ALMOND)
                .construction(ConstructionDesign.NO)
                .color("03")
                .pebbles(DecorationPebbles.DIAMONDS)
                .picture(DecorationPicture.CAT)
                .user(user)
                .build();

        when(designRepository.findTopByUserIdOrderByCreatedAtDesc(userId)).thenReturn(design);

        assertNotNull(designService.getLastDesign(userId));
    }


    @Test
    void whenSaveAllInformationDesign_thenSavedDatabase() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();

        DesignFormRequest dtoForm = new DesignFormRequest();
        dtoForm.setForm(FormDesign.ALMOND);
        dtoForm.setConstruction(ConstructionDesign.NO);

        DesignDecorationRequest dtoDecoration = new DesignDecorationRequest();
        dtoDecoration.setColor("03");
        dtoDecoration.setPebbles(DecorationPebbles.DIAMONDS);
        dtoDecoration.setPicture(DecorationPicture.CAT);

        DesignFormRequest savedForm = new DesignFormRequest();
        savedForm.setForm(dtoForm.getForm());
        savedForm.setConstruction(dtoForm.getConstruction());

        DesignDecorationRequest savedDecoration = new DesignDecorationRequest();
        savedDecoration.setColor(dtoDecoration.getColor());
        savedDecoration.setPebbles(dtoDecoration.getPebbles());
        savedDecoration.setPicture(dtoDecoration.getPicture());

        Design design = Design.builder()
                .form(savedForm.getForm())
                .construction(savedForm.getConstruction())
                .color(savedDecoration.getColor())
                .pebbles(savedDecoration.getPebbles())
                .picture(savedDecoration.getPicture())
                .user(user)
                .build();


        designService.saveAllInformationDesign(dtoForm, dtoDecoration, user);

        verify(historyService, times(1)).saveInHistory(designCaptor.capture());
        verify(designRepository, times(1)).save(designCaptor.capture());

        Design capturedDesign = designCaptor.getValue();

        assertEquals(design.getForm(), capturedDesign.getForm());
        assertEquals(design.getConstruction(), capturedDesign.getConstruction());
        assertEquals(design.getColor(), capturedDesign.getColor());
        assertEquals(design.getPebbles(), capturedDesign.getPebbles());
        assertEquals(design.getPicture(), capturedDesign.getPicture());
        assertEquals(design.getUser(), capturedDesign.getUser());
    }
}



