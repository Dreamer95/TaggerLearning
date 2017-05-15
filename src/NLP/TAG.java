package NLP;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dreamer
 */
public class TAG {
    private String TagName;
    private float Value;
    public String getTagName() {
        return TagName;
    }

    public void setTagName(String TagName) {
        this.TagName = TagName;
    }

    public float getValue() {
        return Value;
    }

    public void setValue(float Value) {
        this.Value = Value;
    }
    public TAG(){
        this.TagName = null;
        this.Value = 0;
    }
    public TAG(String TagName){
        this.TagName = TagName;
        this.Value = 0;
    }
    public TAG(String TagName, float Value) {
        this.TagName = TagName;
        this.Value = Value;
    }
    
}
