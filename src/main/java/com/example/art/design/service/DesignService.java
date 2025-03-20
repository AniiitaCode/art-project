package com.example.art.design.service;

import com.example.art.design.model.Design;
import com.example.art.design.repository.DesignRepository;
import com.example.art.user.model.User;
import com.example.art.web.dto.DesignDecorationRequest;
import com.example.art.web.dto.DesignFormRequest;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DesignService {

    @Getter
    private DesignFormRequest savedForm;
    private final DesignRepository designRepository;

    public DesignService(DesignRepository designRepository) {
        this.designRepository = designRepository;
    }

    public DesignFormRequest saveForm(DesignFormRequest designFormRequest) {
        this.savedForm = designFormRequest;
        return designFormRequest;
    }

    public DesignDecorationRequest saveDecoration(DesignDecorationRequest designDecorationRequest) {
        return designDecorationRequest;
    }


   public void saveAllInformationDesign(DesignFormRequest designFormRequest,
                                        DesignDecorationRequest designDecorationRequest,
                                        User user) {

       DesignFormRequest savedForm = saveForm(designFormRequest);
       DesignDecorationRequest savedDecoration = saveDecoration(designDecorationRequest);

       Design design = Design.builder()
               .form(savedForm.getForm())
               .construction(savedForm.getConstruction())
               .color(savedDecoration.getColor())
               .pebbles(savedDecoration.getPebbles())
               .picture(savedDecoration.getPicture())
               .createdAt(LocalDateTime.now())
               .user(user)
               .build();

       designRepository.save(design);
   }

    public Design getLastDesign(UUID id) {
        return designRepository.findTopByUserIdOrderByCreatedAtDesc(id);
    }
}
