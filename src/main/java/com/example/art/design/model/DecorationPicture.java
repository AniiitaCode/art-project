package com.example.art.design.model;

import lombok.Getter;

@Getter
public enum DecorationPicture {

    NONE("/images/decoration-pebbles/none.png", "None"),
    BEAR("/images/decoration-pictures/bear.png", "Bear"),
    BUTTERFLY("/images/decoration-pictures/butterfly.png", "Butterfly"),
    CAT("/images/decoration-pictures/cat.png", "Cat"),
    DOG("/images/decoration-pictures/dog.png", "Dog"),
    DUCK("/images/decoration-pictures/duck.png", "Duck"),
    FLOWER("/images/decoration-pictures/flower.png", "Flower"),
    MICKEY_MOUSE("/images/decoration-pictures/mickey.png", "Mickey Mouse"),
    RABBIT("/images/decoration-pictures/rabbit.png", "Rabbit"),
    RIBBON("/images/decoration-pictures/ribbon.png", "Ribbon"),
    ROSES("/images/decoration-pictures/roses.png", "Roses"),
    SANTA_CLAUS("/images/decoration-pictures/santa.png", "Santa Claus");

    private final String imagePath;
    private final String imageName;


    DecorationPicture(String imagePath,
                      String imageName) {
        this.imagePath = imagePath;
        this.imageName = imageName;
    }
}
