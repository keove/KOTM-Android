package com.keove.mylibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class Dragoman extends Activity {

    public static final int DRAGO = 9990783;

    private enum KotmPrefs {
        KOTM_SELECTED_LANGUAGE,
        ONLINE_TRANSLATION_MAP
    }


    /**
     * Keys here must be same with the type names on json
     */
    private enum Element {
        text,
        placeholder,
        value,
        desc,
        validation
    }

    private static Context context;
    public static void setContext(Context context) {
        Dragoman.context = context;
    }

    public static void setLanguage(String lang) {
        setLanguage(context,lang);
    }

    public static String language() {
        return languageMap(context);
    }

    public static void setTranslationMap(String map) {
        setTranslationMap(context,map);
    }

    public static String map() {
        return languageMap(context);
    }

    public static void translate(Object object) {
        translate(context,object);
    }

    public static String translation(String tag) {
        return translation(context,tag);
    }

    public static String translation(Element element,String tag) {
        return translation(context,element,tag);
    }

    public static String validationString(View view) {
        return validationString(context,view);
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

    public static String languageMap(Context context) {
        return valueByContext(context.getApplicationContext(),KotmPrefs.ONLINE_TRANSLATION_MAP.name(),"");
    }

    public static String validationString(Context context, View object) {
        try {
            String string = (String)object.getTag(DRAGO);
            return string;
        }
        catch (Exception e) {
            return "";
        }
    }

    public static void translate(Context context,Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {

            try {
                if(field.isAnnotationPresent(Translate.class)) {

                    Translate notation = field.getAnnotation(Translate.class);
                    String tag = notation.value();
                    field.setAccessible(true);
                    

                    if(Button.class.isAssignableFrom(field.getType())) {
                        Button btn = (Button)field.get(object);
                        btn.setText(elementValue(context, Element.value,tag));
                        btn.setTag(DRAGO,elementValue(context,Element.validation,tag));
                    }
                    else if(EditText.class.isAssignableFrom(field.getType())) {
                        EditText et = (EditText)field.get(object);
                        et.setHint(elementValue(context, Element.value,tag));
                        et.setTag(DRAGO,elementValue(context,Element.validation,tag));
                    }
                    else if(TextView.class.isAssignableFrom(field.getType())) {
                        TextView tv = (TextView)field.get(object);
                        tv.setText(elementValue(context, Element.value,tag));
                        tv.setTag(DRAGO,elementValue(context,Element.validation,tag));
                    }
                    else if(String.class.isAssignableFrom(field.getType())) {
                        field.set(object,elementValue(context, Element.value,tag));
                    }
                    else if(ArrayList.class.isAssignableFrom(field.getType())) {
                        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
                        Class pklass = (Class) ptype.getActualTypeArguments()[0];
                        if(pklass.isAssignableFrom(String.class)) {
                            field.set(object,elementArrayValue(context, Element.value,tag,String.class));
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
        return elementValue(context, Element.value,tag);
    }

    public static String translation(Context context,Element element,String tag) {
        return elementValue(context,element,tag);
    }

    // endregion

    

    // region INTERNAL METHODS

    private static String elementValue(Context context, Element element, String tag) {
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

    private static <T extends Object> ArrayList<T> elementArrayValue(Context context, Element element, String tag, Class<T> klass) {

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








}

