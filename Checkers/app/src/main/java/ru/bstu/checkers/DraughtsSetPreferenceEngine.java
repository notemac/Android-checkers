package ru.bstu.checkers;
/* Пример создания кастомного списка с картинками в PreferencesActivity
 *  url: http://www.lucazanini.eu/en/2014/android/display-icon-preferences/ */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class DraughtsSetPreferenceEngine extends ListPreference {

    /**
     * Контейнер для хранения информации о внутренностях элемента кастомного списка. Содержит
     * переменную для отображения иконки iconImage:ImageView, чекбокса radioButton:RadioButton, индекс в списке position:int.
     * */
    private static class ViewHolder {
        protected ImageView iconImage;
        protected int position;
        protected RadioButton radioButton;
    }

    /**
     * Контейнер для хранения информации об иконке. Содержит
     * имя иконки name:String, имя файла иконки file:String, состояние isChecked:boolean.
     * */
    private static class IconItem {

        private String file;
        private boolean isChecked;
        private String name;

        public IconItem(CharSequence name, CharSequence file, boolean isChecked) {
            this(name.toString(), file.toString(), isChecked);
        }

        public IconItem(String name, String file, boolean isChecked) {
            this.name = name;
            this.file = file;
            this.isChecked = isChecked;
        }
    }

    /**Конструктор класса DraughtsSetPreferenceEngine. Инициализируем context:Context,
     * resources:Resources, preferences:SharedPreferences, defaultIconFile:String.
     * ПОЗИЦИЯ В ПОРЯДКЕ ВЫЗОВА МЕТОДОВ в рамках PreferencesActivity: №1
     * */
    public DraughtsSetPreferenceEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.resources = context.getResources();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.defaultIconFile = this.resources.getString(R.string.defaultDraughtsSet);
    }
    /**Дескриптор контекста приложения*/
    private Context context;
    /**Переменная типа CharSequence[] для хранения имен файлов иконок*/
    private CharSequence[] iconFiles;
    /**Переменная типа CharSequence[] для хранения имен иконок*/
    private CharSequence[] iconNames;
    /**Список объектов IconItem*/
    private List<IconItem> icons;
    /**Дескриптор доступа к настройкам приложения*/
    private SharedPreferences preferences;
    /**Дескриптор для доступа к ресурсам приложения*/
    private Resources resources;
    /**Перменная типа TextView для хранения краткого описания выбранного элемента списка*/
    private TextView summary;
    /**Переменная типа ImageView для установки новой иконки*/
    private ImageView icon;
    private String selectedIconFile, defaultIconFile;

    /**Ищем имя иконки (ключ entry) по имени файла иконки (значение entryValue).*/
    private String getIconName(String iconFile) {
        String[] entries = resources.getStringArray(R.array.iconNames);
        String[] values = resources.getStringArray(R.array.iconFiles);
        return entries[Arrays.asList(values).indexOf(iconFile)];
    }

    /**Настраиваем внешний вид draughts_set_preference.xml согласно текущим настройкам.
     * В данном методе устаналиваются все необходимые значения элементов кастомного списка.
     * ПОЗИЦИЯ В ПОРЯДКЕ ВЫЗОВА МЕТОДОВ в рамках PreferencesActivity: №2
     * */
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        //Извлекаем имя файла выбранной иконки
        selectedIconFile = preferences.getString(resources.getString(R.string.selectedDraughtsSet), defaultIconFile);
        //Обновляем выбранную иконку и ее краткое описание
        icon = (ImageView) view.findViewById(R.id.ii_dsp_iconImageSelected);
        updateIcon();
        summary = (TextView) view.findViewById(R.id.tv_dsp_summary);
        summary.setText(getIconName(selectedIconFile));
    }

    /**The class CustomListPreferenceAdapter handles the dialog box*/
    private class CustomListPreferenceAdapter extends ArrayAdapter<IconItem> {

        private Context context;
        private List<IconItem> icons;
        private int resource;

        /** Конструктор класса CustomListPreferenceAdapter.
         *  ПОЗИЦИЯ В ПОРЯДКЕ ВЫЗОВА МЕТОДОВ в рамках PreferencesActivity: №4*/
        public CustomListPreferenceAdapter(Context context, int resource,
                                           List<IconItem> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.icons = objects;
        }

        /** Метод getView вызвается для каждого элемента списка каждый раз при создании диалогово окна.
         *  ПОЗИЦИЯ В ПОРЯДКЕ ВЫЗОВА МЕТОДОВ в рамках PreferencesActivity: №5*/
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            /* Если диалоговое окно еще ни разу не открывалось с момента запуска приложения,
            *  т.е. текущий элемент списка covertView равен null, то инициализируем его
            *  разметкой R.layout.draughts_set_picker*/
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);
                holder = new ViewHolder();
                // holder.iconName = (TextView) convertView.findViewById(R.id.iconName);
                holder.iconImage = (ImageView) convertView.findViewById(R.id.iv_dsp_iconImage);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.rb_dsp_iconRadio);
                holder.position = position;
                // Помещаем во внутрь элемента списка информацию о внутренней структуре этого элемента
                // для дальнейшей его настройки
                convertView.setTag(holder);

            } else {
                // Если данный элемент списка уже существовал (т.е. диалоговое окно открывалось ранее),
                // то извлекаем внутреннюю структуру этого элемента
                holder = (ViewHolder) convertView.getTag();
            }
            // Настраиваем внешний вид текущего элемента списка перед показом диалогового окна
            int identifier = context.getResources().getIdentifier(
                    icons.get(position).file, "drawable",
                    context.getPackageName());
            holder.iconImage.setImageResource(identifier);
            holder.radioButton.setChecked(icons.get(position).isChecked);
            // Устаналиваем обработчик события onClick для текущего элемента списка
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    for (int i = 0; i < icons.size(); i++) {
                        // Запоминаем какой элемент списка выбран с помощью списка icons<ItemIcon>
                        if (i == holder.position)
                            icons.get(i).isChecked = true;
                        else
                            icons.get(i).isChecked = false;
                    }
                    // Закрываем диалоговое окно и попадаем в метод onDialogClosed
                    getDialog().dismiss();
                }
            });
            return convertView;
        }
    }

    /**The method onPrepareDialogBuilder is called before the opening of the dialog box, it defines
     * the adapter for the bulder (dialog box).
     * ПОЗИЦИЯ В ПОРЯДКЕ ВЫЗОВА МЕТОДОВ в рамках PreferencesActivity: №3
     * */
    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton(null, null);
        iconNames = getEntries();
        iconFiles = getEntryValues();

        if (iconNames == null || iconFiles == null
                || iconNames.length != iconFiles.length) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array "
                            + "and an entryValues array which are both the same length");
        }

        String selectedIconFile = preferences.getString( resources.getString(R.string.selectedDraughtsSet),  defaultIconFile);

        icons = new ArrayList<IconItem>();

        for (int i = 0; i < iconNames.length; i++) {
            boolean isSelected = selectedIconFile.equals(iconFiles[i]);
            icons.add(new IconItem(iconNames[i], iconFiles[i], isSelected));
        }

        CustomListPreferenceAdapter customListPreferenceAdapter = new CustomListPreferenceAdapter(
                context, R.layout.draughts_set_picker, icons);
        builder.setAdapter(customListPreferenceAdapter, null);
    }


    /**The method onDialogClosed is called after the dialog box is closed and it saves the choice of the user in the preferences.
     * Настраиваем внешний вид draughts_set_preference.xml согласно текущим настройкам.
     * ПОЗИЦИЯ В ПОРЯДКЕ ВЫЗОВА МЕТОДОВ в рамках PreferencesActivity: №6
     * */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (icons != null) {
            for (int i = 0; i < iconNames.length; i++) {
                IconItem item = icons.get(i);
                if (item.isChecked) {
                    Editor editor = preferences.edit();
                    if (item.file.equals(getEntryValues()[0]))
                    {
                        editor.putInt(resources.getString(R.string.idBlackDraught), R.drawable.black_draught);
                        editor.putInt(resources.getString(R.string.idWhiteDraught), R.drawable.white_draught);
                        editor.putInt(resources.getString(R.string.idBlackKing), R.drawable.black_king);
                        editor.putInt(resources.getString(R.string.idWhiteKing), R.drawable.white_king);
                    }
                    else
                    {
                        editor.putInt(resources.getString(R.string.idBlackDraught), R.drawable.black_draught2);
                        editor.putInt(resources.getString(R.string.idWhiteDraught), R.drawable.white_draught2);
                        editor.putInt(resources.getString(R.string.idBlackKing), R.drawable.black_king2);
                        editor.putInt(resources.getString(R.string.idWhiteKing), R.drawable.white_king2);
                    }
                    editor.putString(resources.getString(R.string.selectedDraughtsSet), item.file);
                    editor.commit();
                    //Обновляем выбранную иконку и ее краткое описание
                    selectedIconFile = item.file;
                    updateIcon();
                    summary.setText(item.name);
                    break;
                }
            }
        }
    }

    private void updateIcon() {
        int identifier = resources.getIdentifier(selectedIconFile, "drawable",
                context.getPackageName());
        icon.setImageResource(identifier);
        //icon.setTag(selectedIconFile);
    }
}
