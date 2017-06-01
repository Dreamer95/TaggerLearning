/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NLP;

/**
 *
 * @author Dreamer
 * 
 */
//class để lưu từng node trong bảng viterbi
public class Node {
    
    private String Tag;

   
    private String Word;
    private float Prob;
    private String prevWord;
    private String prevTag;

    public String getTag() {
        return Tag;
    }
    public void setTag(String Tag) {
        this.Tag = Tag;
    }
    public void setWord(String Word) {
        this.Word = Word;
    }

    public void setProb(float Prob) {
        this.Prob = Prob;
    }

    public void setPrevWord(String prevWord) {
        this.prevWord = prevWord;
    }

    public void setPrevTag(String prevTag) {
        this.prevTag = prevTag;
    }

    public String getWord() {
        return Word;
    }

    public float getProb() {
        return Prob;
    }

    public String getPrevWord() {
        return prevWord;
    }

    public String getPrevTag() {
        return prevTag;
    }
    
    
    public Node() {
        this.Tag = null;
        this.Word = null;
        this.Prob = 0f;
        this.prevTag = null;
        this.prevWord = null;
    }

    public Node(String Tag,String Word, float Prob, String prevWord, String prevTag) {
        this.Tag = Tag;
        this.Word = Word;
        this.Prob = Prob;
        this.prevWord = prevWord;
        this.prevTag = prevTag;
    }
    
     public Node(String Tag,String Word) {
        this.Tag = Tag;
        this.Word = Word;
        this.Prob = 0f;
        this.prevWord = null;
        this.prevTag = null;
    }
    
}
