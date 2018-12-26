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
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class KOTM extends Activity{

    private enum KotmPrefs {
        KOTM_SELECTED_LANGUAGE,
        ONLINE_TRANSLATION_MAP
    }


    /**
     * Keys here must be same with the type names on json
     */
    private enum KotmElement {
        text,
        placeholder,
        value,
        desc
    }


    // region API METHODS
    public static void setLanguage(Context context,String lang) {
        setValueByContext(context,KotmPrefs.KOTM_SELECTED_LANGUAGE.name(),lang);
    }

    public static String language(Context context) {
        return valueByContext(context,KotmPrefs.KOTM_SELECTED_LANGUAGE.name(),"");
    }

    public static void setTranslationMap(Context context, String mapJson) {
        setValueByContext(context.getApplicationContext(), KotmPrefs.ONLINE_TRANSLATION_MAP.name(), mapJson);
        return;
    }

    public static void translate(Context context,Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {

            try {
                if(field.isAnnotationPresent(KOTMTag.class)) {

                    KOTMTag notation = field.getAnnotation(KOTMTag.class);
                    String tag = notation.value();
                    field.setAccessible(true);
                    

                    if(Button.class.isAssignableFrom(field.getType())) {
                        Button btn = (Button)field.get(object);
                        btn.setText(elementValue(context,KotmElement.text,tag));
                    }
                    else if(EditText.class.isAssignableFrom(field.getType())) {
                        EditText et = (EditText)field.get(object);
                        et.setHint(elementValue(context,KotmElement.placeholder,tag));
                    }
                    else if(TextView.class.isAssignableFrom(field.getType())) {
                        TextView tv = (TextView)field.get(object);
                        tv.setText(elementValue(context,KotmElement.text,tag));
                    }
                    else if(String.class.isAssignableFrom(field.getType())) {
                        field.set(object,elementValue(context,KotmElement.value,tag));
                    }
                    else if(ArrayList.class.isAssignableFrom(field.getType())) {
                        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
                        Class pklass = (Class) ptype.getActualTypeArguments()[0];
                        if(pklass.isAssignableFrom(String.class)) {
                            field.set(object,elementArrayValue(context,KotmElement.value,tag,String.class));
                        }
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String translation(Context context,String tag) {
        return elementValue(context,KotmElement.value,tag);
    }
    // endregion

    

    // region INTERNAL METHODS

    private static String elementValue(Context context,KotmElement element, String tag) {
        try {
            JSONObject map = new JSONObject(valueByContext(context,KotmPrefs.ONLINE_TRANSLATION_MAP.name(),""));
            JSONObject node = map.getJSONObject(tag);
            JSONObject elementNode = node.getJSONObject(element.name());
            String t = elementNode.getString(language(context));
            if(t == null) {t="";}
            return t;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private static <T extends Object> ArrayList<T> elementArrayValue(Context context,KotmElement element,String tag,Class<T> klass) {

        ArrayList<T> list = new ArrayList<>();
        try {
            JSONObject map = new JSONObject(valueByContext(context,KotmPrefs.ONLINE_TRANSLATION_MAP.name(),""));
            JSONObject node = map.getJSONObject(tag);
            JSONObject elementNode = node.getJSONObject(element.name());
            JSONArray valueArray = elementNode.getJSONArray(language(context));
            for (int i = 0; i < valueArray.length(); i++) {
                String listValue= valueArray.getString(i);
                // later here, if complex objects will be translatable, we ll have to implemet complex object translation mechanism
                T t = klass.cast(listValue);
                list.add(t);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private static void setValueByContext(Context context, String tag, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefs.edit().putString(tag, value).commit();
    }

    private static String valueByContext(Context context, String tag, String defaultvalue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return prefs.getString(tag, defaultvalue);
    }

    // endregion








    // region DEPRECATED METHODS

    @Deprecated
    public static String getTranslation(Context context, String text,String languageCode) {
        String json = valueByContext(context.getApplicationContext(), "translations", text);

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

    @Deprecated
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

    @Deprecated
    private static <T extends Object> ArrayList<T> getTextArrays(Context context, String annotation,String languageCode,Class<T> klass) {

        String json = valueByContext(context.getApplicationContext(), "translations", annotation);
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

    @Deprecated
    private static String getTextForTextsAndButtons(Context context, String annotation, String languageCode) {
        String json = valueByContext(context.getApplicationContext(), "translations", annotation);

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
    
    @Deprecated
    private static String getTextForStrings(Context context, String annotation,String languageCode) {
        String json = valueByContext(context.getApplicationContext(), "translations", annotation);

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

    @Deprecated
    private static String getTextForEditText(Context context, String annotation,String languageCode) {
        String json = valueByContext(context.getApplicationContext(), "translations", annotation);

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

    // endregion
}

