package com.example.art.design.model;

import lombok.Getter;

@Getter
public enum DecorationPebbles {

    NONE("/images/decoration-pebbles/none.png", "None"),
    CROWN_PINK("/images/decoration-pebbles/crown-pink.png", "Pink crown"),
    DIAMONDS("/images/decoration-pebbles/diamonds.png", "Diamonds"),
    FLOWERS_PINK("/images/decoration-pebbles/flowers-pink.png", "Pink flowers"),
    PEARLS_BLUE("/images/decoration-pebbles/pearls-blue.png", "Blue pearls"),
    PEARLS_COLORFUL("/images/decoration-pebbles/pearls-colorful.png", "Colorful pearls"),
    PEARLS_PINK("/images/decoration-pebbles/pearls-pink.png", "Pink pearls"),
    PEARLS_WHITE("/images/decoration-pebbles/pearls-white.png", "White pearls"),
    HEARTS("/images/decoration-pebbles/hearts.png", "Hearts"),
    PEARLS_PURPLE("/images/decoration-pebbles/pearls-purple.png", "Purple pearls"),
    ROSES_GREEN("/images/decoration-pebbles/roses-green.png", "Green roses"),
    ROSES_GOLD("/images/decoration-pebbles/roses-gold.png", "Gold roses");

    private final String imagePath;
    private final String imageName;


    DecorationPebbles(String imagePath,
                      String imageName) {
        this.imagePath = imagePath;
        this.imageName = imageName;
    }
}
