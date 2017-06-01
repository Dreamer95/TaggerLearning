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
    static ArrayList<String> Words; // lưu những từ độc lập
    static Map<String, ArrayList<TAG>> Transition = new HashMap<String, ArrayList<TAG>>();
    static Map<String, ArrayList<TAG>> Emission = new HashMap<String, ArrayList<TAG>>();
    static Map<String, ArrayList<Node>> ViterbiTable = new HashMap<String, ArrayList<Node>>();
    static Map<String, Float> TotalTable = new HashMap<>();// lưu tổng số đường đi của key
    static String[] Query; // mảng lưu câu truy vấn
    

    public static void main(String[] args) {
        ReadFile();
        String query = "Nam thích học";
        Query = query.split(" ");
        String[] QueryFix = Query;
        for(int i = 0 ; i<QueryFix.length;i++){
            QueryFix[i] += "_"+i;
        }
        InitArrayWords(Emission,tag);
        smoothTransition(Transition,TotalTable,tag);
        smoothEmission(Emission,TotalTable,tag,Words);
        InitViterbiTable(QueryFix, tag);
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
    
    // hàm smooth bảng Transition, các tham số truyền vào để biết lấy dữ liệu từ những bảng nào
    // mặc dù chúng đag là biến toàn cục
   static void smoothTransition(Map<String, ArrayList<TAG>> Transition,
            Map<String, Float> TotalTable,
            ArrayList<String> tag)
    {
        float total;
        ArrayList<TAG> result;
        for(String s : tag){
            total = TotalTable.get(s); 
            total += (tag.size()-2); // smooth mẫu + số lượng tag có thể có,trừ đi 2 tag thừa khi đọc file
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
   
   static void smoothEmission(Map<String, ArrayList<TAG>> Emission,
            Map<String, Float> TotalTable,
            ArrayList<String> tag, ArrayList<String> Word){
        float total;
        int size = Words.size(); // số lượng từ độc lập trong tập train
        ArrayList<TAG> result;
        for(String s : tag){
            total = TotalTable.get(s); // lấy giá trị lưu ở TotalTable
            total += size; // + thêm tổng số từ độc lập nữa
            result = Emission.get(s);
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
   
   // Hàm tạo danh sách từ độc lập trong tập train
   static void InitArrayWords(Map<String, ArrayList<TAG>> Emission,ArrayList<String> tag){
       Words = new ArrayList<>();
       ArrayList<TAG> myTag = null;
       for(String t : tag){
           myTag = Emission.get(t);
           for(TAG i : myTag){
               if(!Words.contains(i.getTagName())){
                   Words.add(i.getTagName());
               }
           }
       }
   }
   
   // Hàm tạo bảng Viterbi
   static void InitViterbiTable(String[] query,ArrayList<String>tag){
       ArrayList<Node> myNode;
       for(String w : query){
          
       }
   }
   
   
}
