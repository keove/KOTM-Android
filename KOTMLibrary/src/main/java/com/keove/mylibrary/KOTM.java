package com.keove.mylibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class KOTM extends Activity{

    public static void translate(Object object, String languageCode, Context context) {


        ArrayList<String> fieldNames= new ArrayList<>();

        Field[] fields= object.getClass().getDeclaredFields();
        ArrayList<String> annotationList= new ArrayList<>();

        for(Field f: fields) {
            fieldNames.add(f.getType().getSimpleName());
            try {
                KOTMTag notation = f.getAnnotation(KOTMTag.class);
                if (notation != null){
                    annotationList.add(notation.value());
                } else {
                    annotationList.add("");
                }
            }
            catch (Exception ex) {
                ex.getStackTrace();
            }
        }

        for (int i=0; i<=fieldNames.size()-1; i++) {

            Field field = fields[i];
            Class k = field.getType().getClass();

            try {

                if(k.isInstance(ArrayList.class)) {

                    ArrayList<String> stringArrayList= (ArrayList<String>) fields[i].get(object);
                    ArrayList<String> textArray = getTextArrays(context,annotationList.get(i),languageCode,String.class);

                    for (int a=0; a<=textArray.size()-1; a++) {
                        stringArrayList.add(textArray.get(a));
                    }

                } else if (k.isInstance(String.class) && !k.isInstance(ArrayList.class)) {

                    String textForStrings= getTextForStrings(context,annotationList.get(i),languageCode);
                    field.set(object,textForStrings);

                } else if (k.isInstance(Button.class)) {

                    String textForTextsAndButtons = getTextForTextsAndButtons(context, annotationList.get(i), languageCode);
                    Button fieldButton = (Button) fields[i].get(object);
                    fieldButton.setText(textForTextsAndButtons);

                } else if (k.isInstance(EditText.class)) {

                    EditText fieldEditText = (EditText) fields[i].get(object);
                    String textForEditText= getTextForEditText(context,annotationList.get(i),languageCode);
                    fieldEditText.setHint(textForEditText);

                } else if (k.isInstance(TextView.class)) {

                    TextView fieldTextView = (TextView) fields[i].get(object);
                    String textForTextsAndButtons= getTextForTextsAndButtons(context,annotationList.get(i),languageCode);
                    fieldTextView.setText(textForTextsAndButtons);

                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private static String getTextForTextsAndButtons(Context context, String annotation, String languageCode) {
        String json = GetValueByContext(context.getApplicationContext(), "translations", annotation);

        try {
            JSONObject mainJsonObject = new JSONObject(json);
            JSONObject translationNodeObject= mainJsonObject.getJSONObject(annotation);
            JSONObject textObject= translationNodeObject.getJSONObject("text");
            String t= textObject.getString(languageCode);

            if(t!=null && t.length() > 0) {
                return t;
            } else {
                return "";
            }
        }
        catch (Exception e) {
            return annotation;
        }
    }

    private static String getTextForStrings(Context context, String annotation,String languageCode) {
        String json = GetValueByContext(context.getApplicationContext(), "translations", annotation);

        try {
            JSONObject mainJsonObject = new JSONObject(json);
            JSONObject translationNodeObject= mainJsonObject.getJSONObject(annotation);
            JSONObject valueObject= translationNodeObject.getJSONObject("value");
            String t= valueObject.getString(languageCode);

            if(t!=null && t.length() > 0) {
                return t;
            } else {
                return "";
            }
        }
        catch (Exception e) {
            return annotation;
        }
    }

    private static String getTextForEditText(Context context, String annotation,String languageCode) {
        String json = GetValueByContext(context.getApplicationContext(), "translations", annotation);

        try {
            JSONObject mainJsonObject = new JSONObject(json);
            JSONObject translationNodeObject= mainJsonObject.getJSONObject(annotation);
            JSONObject placeholderObject= translationNodeObject.getJSONObject("placeholder");
            String t= placeholderObject.getString(languageCode);

            if(t!=null && t.length() > 0) {
                return t;
            } else {
                return "";
            }
        }
        catch (Exception e) {
            return annotation;
        }
    }

    private static <T extends Object> ArrayList<T> getTextArrays(Context context, String annotation,String languageCode,Class<T> klass) {

        String json = GetValueByContext(context.getApplicationContext(), "translations", annotation);
        ArrayList<T> list = new ArrayList<>();

        try {

            JSONObject mainJsonObject = new JSONObject(json);
            JSONObject translationNodeObject= mainJsonObject.getJSONObject(annotation);
            JSONObject valueObject= translationNodeObject.getJSONObject("value");
            JSONArray valueObjectJSONArray= valueObject.getJSONArray(languageCode);

            for (int i = 0; i < valueObjectJSONArray.length(); i++) {
                String listValues= valueObjectJSONArray.getString(i);
                T t = klass.cast(listValues);
                list.add(t);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public static void setTranslation(Context context, String response) {
        SetValueByContext(context.getApplicationContext(), "translations", response);
        return;
    }

    public static String getTranslation(Context context, String text,String languageCode) {
        String json = GetValueByContext(context.getApplicationContext(), "translations", text);

        try {
            JSONObject mainJsonObject = new JSONObject(json);
            JSONObject textJsonObject = mainJsonObject.getJSONObject(text);
            JSONObject valueObject= textJsonObject.getJSONObject("value");
            String t = valueObject.getString(languageCode);

            if(t!=null && t.length() > 0) {
                return t;
            }
            else return text;

        }
        catch (Exception e) {
            return text;
        }
    }

    private static void SetValueByContext(Context ctx, String tag, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().putString(tag, value).commit();
    }

    private static String GetValueByContext(Context ctx, String tag, String defaultvalue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(tag, defaultvalue);
    }
}

