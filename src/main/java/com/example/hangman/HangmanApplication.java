package com.example.hangman;

import com.example.hangman.player.HangmanPlayer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
public class HangmanApplication {

    public static void main(String[] args) {
        SpringApplication.run(HangmanApplication.class, args);
        HangmanPlayer hangmanPlayer  =new HangmanPlayer();
        hangmanPlayer.runGame();
    }

}
