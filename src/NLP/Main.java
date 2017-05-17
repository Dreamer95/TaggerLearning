/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NLP;

import java.io.*;
import java.util.*;

/**
 *
 * @author Dreamer
 */
public class Main {

    static ArrayList<TAG> tagName;
    static ArrayList<String> tag;
    static Map<String, ArrayList<TAG>> Transition = new HashMap<String, ArrayList<TAG>>();
    static Map<String, ArrayList<TAG>> Emission = new HashMap<String, ArrayList<TAG>>();
    static Map<String, Float> TotalTable = new HashMap<>();// lưu tổng số đường đi của key

    public static void main(String[] args) {
        ReadFile();
        System.out.println("done");
    }

    public static void ReadFile() {
        String[] Tag = null;
        TAG temp;
        boolean Flag = false;
        tagName = new ArrayList<TAG>();
        tag = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("train.txt"));
            String line = br.readLine();
            while (line != null) {
                if ("****".equals(line) || Flag == true) {
                    if ("****".equals(line)) {
                        line = br.readLine();
                        Flag = true;
                    }

                    Tag = line.split("\\s");
                    updateTwoTable(Tag);
                    for (String w : Tag) {
                        System.out.print(w);
                        System.out.println();
                    }
                } else {
                    line = line.trim();
                    Tag = line.split("\\s");
                    // vòng lặp gắn tag cho list tag và list object tagName
                    for (String w : Tag) {
                        temp = new TAG(w);
                        tag.add(w);
                        tagName.add(temp);
                    }
                    //gọi các hàm khởi tạo
                    InitTransitionTable(tag, tagName);
                    InitEmissionTable(tag);
                }
                line = br.readLine();
            }

        } catch (IOException ex) {

        }
    }

    //hàm khởi tạo bảng Transition 
    static void InitTransitionTable(ArrayList<String> tag, ArrayList<TAG> tagName) {
        ArrayList<TAG> myTag;
        for (int i = 0; i < tag.size(); i++) {
            myTag = new ArrayList<TAG>();
            for (TAG t : tagName) {
                TAG temp = new TAG();
                temp.setTagName(t.getTagName());
                myTag.add(temp);
            }
            Transition.put((String) tag.get(i), myTag);
        }
    }
    
    //hàm khởi tạo bảng Emission và TotalTable
    static void InitEmissionTable(ArrayList<String> tag) {
        for (int i = 0; i < tag.size(); i++) {
            ArrayList<TAG> temp = new ArrayList<>();
            Emission.put((String) tag.get(i), temp);
            //khởi tạo với key = tag và value = 0
            TotalTable.put((String) tag.get(i), 0f);
        }
    }
    
    // hàm cập nhật giá trị của bảng khi đọc file text
    static void updateTwoTable(String[] Tag) {
        String name;
        String tag;
        int index;
        float value;
        Map.Entry<String, ArrayList<TAG>> me;
        ArrayList<TAG> resultTransition;
        ArrayList<TAG> resultEmission;
        float totalTag;
        String key = "S";
        for (String w : Tag) {
            index = w.indexOf('_');
            name = w.substring(0, index);
            tag = w.substring(index + 1);
            //lấy list value để gán giá trị
            resultTransition = Transition.get(key);
            resultEmission = Emission.get(tag);
            totalTag = TotalTable.get(key);
            //update bảng Transition
            for (TAG temp : resultTransition) {
                if (temp.getTagName().equals(tag)) {
                    value = temp.getValue();
                    temp.setValue(value + 1);
                    totalTag++; // tăng giá trị totalTag
                    TotalTable.put(key, totalTag);//update lại giá trị
                    key = tag;
                    break;
                }
            }
            //update bảng Emission
            //nếu bảng đang rỗng
            if (resultEmission.isEmpty()) {
                TAG newTag = new TAG(name, 1);
                resultEmission.add(newTag);
            } else {
                for (int i = 0; i < resultEmission.size(); i++) {
                    //kiểm tra xem name đã tồn tại trong tagName chưa,
                    //nếu rồi thì tăng value
                    if (resultEmission.get(i).getTagName().equalsIgnoreCase(name)) {
                        value = resultEmission.get(i).getValue();
                        resultEmission.get(i).setValue(value + 1);
                        break;
                    }
                    //ngược lại tạo TAG mới và add vào ArrayList
                    if (i == (resultEmission.size() - 1)) {
                        TAG newTag = new TAG(name, 1);
                        resultEmission.add(newTag);
                        break;
                    }
                }
            }
        }
    }
    
   static void smoothTransition(Map<String, ArrayList<TAG>> Transition,
            Map<String, Float> TotalTable,
            ArrayList<String> tag)
    {
        float total;
        ArrayList<TAG> result;
        for(String s : tag){
            total = TotalTable.get(s);
            total += 6; // smooth mẫu +6
            result = Transition.get(s);
            for(TAG t :result){
                if(t.getValue()==0){
                    //nếu giá trị = 0 thì smooth = 1/total
                    t.setValue(1/total);
                }else{
                    //ngược lại smooth = value+1/total
                    t.setValue((t.getValue()+1)/total);
                }
            }
        }
    }
}
