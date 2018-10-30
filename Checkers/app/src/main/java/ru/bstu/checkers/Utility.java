package ru.bstu.checkers;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Utility {
    /**
     * Method that parses the JSON file and returns a JSONObject
     */
    public static JSONObject ParseJSONData(String fileName) {
        JSONObject JSONObj = null;
        try {
            //open the inputStream to the file
            InputStream inputStream = MyApplication.getInstance().getAssets().open(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            JSONObj = new JSONObject(new String(bytes, "UTF-8"));
        } catch (IOException ex) {
            //ex.printStackTrace();
            return null;
        } catch (JSONException ex) {
            //ex.printStackTrace();
            return null;
        }
        return JSONObj;
    }

    /**
     * Создаем ways для сохранения в БД
     */
    public static ArrayList<ru.bstu.checkers.roomdb.Item>[] CreateObjectForDatabaseInsert(ArrayList<Item>[] rawWays) {
        ArrayList<ru.bstu.checkers.roomdb.Item>[] ways = new ArrayList[Item.WAYS_COUNT];
        for (int i = 0; i < ways.length; ++i)
            ways[i] = new ArrayList<ru.bstu.checkers.roomdb.Item>(8);// Максимум 8 клеток/шашек на одной диагонали
        for (int i = 0; i < rawWays.length; ++i) {
            ArrayList<Item> items = rawWays[i];
            for (int j = 0; j < items.size(); ++j) {
                Item item = items.get(j);
                ArrayList<Integer> idx = item.GetWaysIdx();
                ways[i].add(new ru.bstu.checkers.roomdb.Item(item.id, item.type.ordinal(), item.isKing, idx));
            }
        }
        return ways;
    }

    /** Конвертируем объект в массив байтов */
    public static <T> byte[] ToByteArray(T object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            out.flush();
            bytes = baos.toByteArray();
        } catch (IOException ex) {
        } finally {
            try {
                if (null != baos) baos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return bytes;
    }

    /** Конвертируем массив байтов в объект*/
    public static Object FromByteArray(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object obj = null;
        try {
            in = new ObjectInputStream(bais);
            obj = in.readObject();
        } catch (Exception ex) {
        } finally {
            try {
                if (null != bais) bais.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return obj;
    }

}
