package com.example.hangman.player;
import com.example.hangman.model.ServerResponse;
import com.example.hangman.server.HangmanServer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HangmanPlayer {

    private static HangmanServer server = new HangmanServer();
    private static List<String> ahoyLetters=null;
    private static List<String> words = new ArrayList<>();
    private  static  List<String> commonChars=new ArrayList<>();
    /**
     * This is the entry point of your Hangman Player
     * To start a new game call server.startNewGame()
     */


    public void runGame(){
        try {
            ServerResponse serverResponse = server.startNewGame();
            ahoyLetters = getAhoyLetters();
            words=fetchWordsFromDicationary();
            while(true){
                if (checkIfGameOver(serverResponse)) break;
                serverResponse = guessWord(serverResponse);
                analyzeServerResponse(serverResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ServerResponse guessWord(ServerResponse serverResponse) throws Exception {
        String guess = guessAhoy();
        if(guess!=null){
            return server.guess(serverResponse.getToken(),guess);
        }
        Integer indexOfFirstMissingChar= serverResponse.getHangman().indexOf("_");
        guess = getMostCommonCharByIndex(serverResponse,indexOfFirstMissingChar);
        serverResponse=server.guess(serverResponse.getToken(),guess);
        return serverResponse;
    }




    private String getMostCommonCharByIndex(ServerResponse serverResponse, Integer indexOfFirstMissingChar) {
        HashMap<String,Integer>  map = new HashMap<>();
        String regexPatten=serverResponse.getHangman().replace('_','.');
        for(String current:words){
            if(current.matches(regexPatten)){
                String currentChar = current.substring(indexOfFirstMissingChar,indexOfFirstMissingChar+1);
                Integer currentCounter = map.get(currentChar);
                if(currentCounter==null){
                    currentCounter=1;
                }
                map.put(current,currentCounter+1);
            }
        }
        List<String> commonChars = getCommonCharsFromMap(map);
        return commonChars.get(0);
    }

    private List<String> getCommonCharsFromMap(HashMap<String, Integer> map) {
        List<String> result = new ArrayList<>();
        Integer maxCounter = getMaxCounterFromMap(map);
        for(String current:map.keySet()){
            Integer currentCounter = map.get(current);
            if(currentCounter.equals(maxCounter)){
                result.add(current);
            }
        }
        return result;
    }


    private Integer getMaxCounterFromMap(HashMap<String, Integer> map){
        Integer maxCouner=0;
        for(String current:map.keySet()){
            Integer currentCounter = map.get(current);
            if(currentCounter>maxCouner){
                maxCouner=currentCounter;
            }
        }
        return maxCouner;
    }


    private String guessAhoy(){
        String guess = "";
        if(ahoyLetters.size()>0){
            guess=ahoyLetters.get(0);
            ahoyLetters.remove(guess);
            return guess;
        }
        return null;
    }


    private void analyzeServerResponse(ServerResponse serverResponse) {
        String hangman = serverResponse.getHangman();
        removeMisMatchWords(hangman);

    }

    private void removeMisMatchWords(String hangman) {
        List<String> removalList = new ArrayList<>();
        String regexPatten=hangman.replace('_','.');
        for(String current:words){
            if(!current.matches(regexPatten)){
                removalList.add(current);
            }
        }
        removeWords(removalList);
    }

    private void removeWords(List<String> removalList){
        for(String current:removalList){
            words.remove(current);
        }
    }


    private List<String> fetchWordsFromDicationary() throws IOException {
        File file = new File("src\\main\\resources\\dictionary.txt");
        List<String> words = fileToListString(file);
        return words;
    }



    private List<String> fileToListString(File file) throws IOException {
        List<String> list = new ArrayList<>();
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }



    public boolean checkIfGameOver(ServerResponse serverResponse){
        if(serverResponse.isGameEnded()){
            System.out.println("THE GAME IS OVER GOOD BYE ");
            System.out.println("Server Respose is: " + serverResponse.toString());
            return true;
        }
        return false;
    }


    private List<String> getAhoyLetters() {
        List<String> ahoyLetters = new ArrayList<>();
        ahoyLetters.add("a");
        ahoyLetters.add("e");
        ahoyLetters.add("i");
        ahoyLetters.add("o");
        ahoyLetters.add("u");
        return ahoyLetters;
    }

    public static void main(String[] args) throws Exception {
        HangmanPlayer hangmanPlayer = new HangmanPlayer();
        hangmanPlayer.runGame();

    }
}
