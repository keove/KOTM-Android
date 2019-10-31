package com.keove.keovelangadvance;



import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.keove.mylibrary.Dragoman;
import com.keove.mylibrary.Translate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Translate("test_prj_tv") AppCompatTextView textView;

    @Translate("test_prj_et") AppCompatEditText editText;

    @Translate("test_prj_btn") AppCompatButton button;

    AppCompatTextView tv2, tv3;

    @Translate("test_prj_string") String text = null;

    @Translate("test_prj_array") ArrayList<String> texts = null;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);
        editText = findViewById(R.id.et);
        textView = findViewById(R.id.tv);

        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        // test
        String json = "{\"alan_1\": {\"type\": \"Text\",\"desc\": \"%27%C3%BC12\",\"text\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"},\"value\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"},\"placeholder\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"}},\"alan_2\": {\"type\": \"Value\",\"desc\": \"%27%C3%BC12\",\"text\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"},\"value\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"},\"placeholder\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"}},\"alan_3\": {\"type\": \"Placeholder\",\"desc\": \"%27%C3%BC12\",\"text\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"},\"value\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"},\"placeholder\": {\"TR\": \"%27%C3%BC12\",\"EN\": \"%27%C3%BC12\"}},\"alan_4\": {\"type\": \"List\",\"desc\": \"%27ü12\",\"value\": {\"TR\": [\"%27%C3%BC12\",\"%27%C3%BC12\",\"%27%C3%BC12\",\"%27%C3%BC12\"],\"EN\": [\"%27%C3%BC12\",\"%27%C3%BC12\",\"%27%C3%BC12\",\"%27%C3%BC12\"]}},\"test_prj_btn\": {\"type\": \"Text\",\"desc\": \"\",\"text\": {\"TR\": \"Tuş\",\"EN\": \"Button\"},\"value\": {\"TR\": \"\",\"EN\": \"\"},\"placeholder\": {\"TR\": \"\",\"EN\": \"\"}},\"test_prj_tv\": {\"type\": \"Text\",\"desc\": \"\",\"text\": {\"TR\": \"yazı alanı\",\"EN\": \"text field\"},\"value\": {\"TR\": \"\",\"EN\": \"\"},\"placeholder\": {\"TR\": \"\",\"EN\": \"\"}},\"test_prj_et\": {\"type\": \"Text\",\"desc\": \"\",\"text\": {\"TR\": \"\",\"EN\": \"\"},\"value\": {\"TR\": \"\",\"EN\": \"\"},\"placeholder\": {\"TR\": \"bir şey yaz\",\"EN\": \"say sth\"}},\"test_prj_string\": {\"type\": \"Value\",\"desc\": \"\",\"text\": {\"TR\": \"\",\"EN\": \"\"},\"value\": {\"TR\": \"string tr\",\"EN\": \"string en\"},\"placeholder\": {\"TR\": \"\",\"EN\": \"\"}},\"test_prj_array\": {\"type\": \"List\",\"desc\": \"\",\"value\": {\"TR\": [\"str1tr\",\"str2tr\",\"str3tr\"],\"EN\": [\"str1en\",\"str2en\",\"str3en\"]}}}";
        Dragoman.setLanguage(this, "TR");
        Dragoman.setTranslationMap(this, json);
        Dragoman.translate(this, this);

        tv2.setText(text);

        String arrText = "";
        for (String str : texts) {
            arrText += str + ",";
        }

        tv3.setText(arrText);

        //Dragoman.translate(this,"TR",this);

    }
}
