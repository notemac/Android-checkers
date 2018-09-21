package ru.bstu.checkers;

import android.graphics.drawable.Drawable;
import android.view.Window;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import ru.bstu.checkers.Item.ITEM_TYPE;

/* Backup: https://github.com/notemac/Android-checkers.
   Основные идеи реализации алгоритма взяты отсюда: https://habr.com/post/227911/ - Как я шашки писал */

public class GameEngine {
    /**
     * Item, на который игрок кликнул первый раз
     */
    public static Item selectedFirstItem = null;
    /**
     * Надо бить? (Бить обязательно)
     */
    public static boolean isJump = false;
    /**
     * Есть куда ходить? (Если нечего бить)
     */
    public static boolean isMove = false;
    /**
     * Шашка выбрана, необходимо сделать ход/побить (т.е. клинуть второй раз)
     */
    public static boolean isNeedSecondClick = false;
    /**
     * Очередь хода
     */
    public static ITEM_TYPE turn = ITEM_TYPE.white;
    /**
     * Массив диагоналей (ways) представляет игровую доску.
     * В каждом way[i] хранятся шашки и пустые клетки, лежащие на этой диагонали.
     */
    public static ArrayList<Item>[] ways;
    /**
     * Массив из списков ходов. Т.е. куда может пойти шашка. Первый элемент списка
     * всегда шашка; все остальные элементы - клетки, куда может пойти эта шашка.
     */
    public static ArrayList<LinkedList<Item>> moves;
    /**
     * Список из списков с ходами, куда шашка может побить. Первый элемент списка всегда бьющая шашка;
     * второй - шашка, которая под боем; все остальные элементы - клетки, где может оказаться бьющая шашка после битья.
     * Т.о. если шашка может бить одновременно в разные стороны, то для нее будет создано несколько списков,
     * в которых первым элементом будет эта шашка.
     */
    public static LinkedList<LinkedList<Item>> jumps;

    /** Map-ключ это индекс диагонали, на которой находится дамка, для которой (диагонали) уже вызывался метод RemoveFakeJumps.
     *  Map-значение это индекс списка в jumps для этой диагонали и этой дамки
     */
    private static Map<Integer, Integer> removedFakeJumps;
    /**
     * Клетки, на которых шашки превращаются в дамки. kingSquares[0] - для черных, kingSquares[1] - для белых
     */
    public static int[][] kingSquares;

    public static void Init() {
        kingSquares = new int[2][4];
        kingSquares[0][0] = R.id.a1;
        kingSquares[0][1] = R.id.c1;
        kingSquares[0][2] = R.id.e1;
        kingSquares[0][3] = R.id.g1;
        kingSquares[1][0] = R.id.b8;
        kingSquares[1][1] = R.id.d8;
        kingSquares[1][2] = R.id.f8;
        kingSquares[1][3] = R.id.h8;
        final int DRAUGHTS_COUNT = 12;// Количество шашек одного цвета
        moves = new ArrayList<LinkedList<Item>>(DRAUGHTS_COUNT);
        jumps = new LinkedList<LinkedList<Item>>();
        Item[] items = new Item[DRAUGHTS_COUNT * 2 + 4 * 6 + 16];// 64 клетки
        items[0] = new Item(R.id.a8, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //A8
        items[1] = new Item(R.id.b8, ITEM_TYPE.black, false, true, false, true,
                false, false, false, false, false,
                false, false, false, false); //B8
        items[2] = new Item(R.id.c8, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //C8
        items[3] = new Item(R.id.d8, ITEM_TYPE.black, false, false, false, false,
                false, false, false, false, true,
                true, false, false, false); //D8
        items[4] = new Item(R.id.e8, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //E8
        items[5] = new Item(R.id.f8, ITEM_TYPE.black, false, false, false, false,
                false, false, true, true, false,
                false, false, false, false); //F8
        items[6] = new Item(R.id.g8, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //G8
        items[7] = new Item(R.id.h8, ITEM_TYPE.black, false, false, false, false,
                false, false, false, false, false,
                false, false, false, true); //H8

        items[8] = new Item(R.id.a7, ITEM_TYPE.black, true, false, false, true,
                false, false, false, false, false,
                false, false, false, false); //A7
        items[9] = new Item(R.id.b7, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //B7
        items[10] = new Item(R.id.c7, ITEM_TYPE.black, false, true, false, false,
                false, false, false, false, true,
                false, false, false, false); //C7
        items[11] = new Item(R.id.d7, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //D7
        items[12] = new Item(R.id.e7, ITEM_TYPE.black, false, false, false, false,
                false, false, false, true, false,
                true, false, false, false); //E7
        items[13] = new Item(R.id.f7, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //F7
        items[14] = new Item(R.id.g7, ITEM_TYPE.black, false, false, false, false,
                false, false, true, false, false,
                false, false, false, true); //G7
        items[15] = new Item(R.id.h7, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //H7

        items[16] = new Item(R.id.a6, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //A6
        items[17] = new Item(R.id.b6, ITEM_TYPE.black, true, false, false, false,
                false, false, false, false, true,
                false, false, false, false); //B6
        items[18] = new Item(R.id.c6, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //C6
        items[19] = new Item(R.id.d6, ITEM_TYPE.black, false, true, false, false,
                false, false, false, true, false,
                false, false, false, false); //D6
        items[20] = new Item(R.id.e6, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //E6
        items[21] = new Item(R.id.f6, ITEM_TYPE.black, false, false, false, false,
                false, false, false, false, false,
                true, false, false, true); //F6
        items[22] = new Item(R.id.g6, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //G6
        items[23] = new Item(R.id.h6, ITEM_TYPE.black, false, false, false, false,
                false, true, true, false, false,
                false, false, false, false); //H6

        items[24] = new Item(R.id.a5, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, true,
                false, true, false, false); //A5
        items[25] = new Item(R.id.b5, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //B5
        items[26] = new Item(R.id.c5, ITEM_TYPE.square, true, false, false, false,
                false, false, false, true, false,
                false, false, false, false); //C5
        items[27] = new Item(R.id.d5, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //D5
        items[28] = new Item(R.id.e5, ITEM_TYPE.square, false, true, false, false,
                false, false, false, false, false,
                false, false, false, true); //E5
        items[29] = new Item(R.id.f5, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //F5
        items[30] = new Item(R.id.g5, ITEM_TYPE.square, false, false, false, false,
                false, true, false, false, false,
                true, false, false, false); //G5
        items[31] = new Item(R.id.h5, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //H5

        items[32] = new Item(R.id.a4, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //A4
        items[33] = new Item(R.id.b4, ITEM_TYPE.square, false, false, false, false,
                false, false, false, true, false,
                false, true, false, false); //B4
        items[34] = new Item(R.id.c4, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //C4
        items[35] = new Item(R.id.d4, ITEM_TYPE.square, true, false, false, false,
                false, false, false, false, false,
                false, false, false, true); //D4
        items[36] = new Item(R.id.e4, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //E4
        items[37] = new Item(R.id.f4, ITEM_TYPE.square, false, true, false, false,
                false, true, false, false, false,
                false, false, false, false); //F4
        items[38] = new Item(R.id.g4, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //G4
        items[39] = new Item(R.id.h4, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                true, false, true, false); //H4

        items[40] = new Item(R.id.a3, ITEM_TYPE.white, false, false, false, false,
                true, false, false, true, false,
                false, false, false, false); //A3
        items[41] = new Item(R.id.b3, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //B3
        items[42] = new Item(R.id.c3, ITEM_TYPE.white, false, false, false, false,
                false, false, false, false, false,
                false, true, false, true); //C3
        items[43] = new Item(R.id.d3, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //D3
        items[44] = new Item(R.id.e3, ITEM_TYPE.white, true, false, false, false,
                false, true, false, false, false,
                false, false, false, false); //E3
        items[45] = new Item(R.id.f3, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //F3
        items[46] = new Item(R.id.g3, ITEM_TYPE.white, false, true, false, false,
                false, false, false, false, false,
                false, false, true, false); //G3
        items[47] = new Item(R.id.h3, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //H3

        items[48] = new Item(R.id.a2, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //A2
        items[49] = new Item(R.id.b2, ITEM_TYPE.white, false, false, false, false,
                true, false, false, false, false,
                false, false, false, true); //B2
        items[50] = new Item(R.id.c3, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //C2
        items[51] = new Item(R.id.d2, ITEM_TYPE.white, false, false, false, false,
                false, true, false, false, false,
                false, true, false, false); //D2
        items[52] = new Item(R.id.e2, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //E2
        items[53] = new Item(R.id.f2, ITEM_TYPE.white, true, false, false, false,
                false, false, false, false, false,
                false, false, true, false); //F2
        items[54] = new Item(R.id.g2, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //G2
        items[55] = new Item(R.id.h2, ITEM_TYPE.white, false, true, true, false,
                false, false, false, false, false,
                false, false, false, false); //H2

        items[56] = new Item(R.id.a1, ITEM_TYPE.white, false, false, false, false,
                false, false, false, false, false,
                false, false, false, true); //A1
        items[57] = new Item(R.id.b1, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //B1
        items[58] = new Item(R.id.c1, ITEM_TYPE.white, false, false, false, false,
                true, true, false, false, false,
                false, false, false, false); //C1
        items[59] = new Item(R.id.d1, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //D1
        items[60] = new Item(R.id.e1, ITEM_TYPE.white, false, false, false, false,
                false, false, false, false, false,
                false, true, true, false); //E1
        items[61] = new Item(R.id.f1, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //F1
        items[62] = new Item(R.id.g1, ITEM_TYPE.white, true, false, true, false,
                false, false, false, false, false,
                false, false, false, false); //G1
        items[63] = new Item(R.id.h1, ITEM_TYPE.square, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false); //H1

        ways = new ArrayList[Item.WAYS_COUNT];
        for (int i = 0; i < ways.length; ++i)
            ways[i] = new ArrayList<Item>(8);// Максимум 8 клеток/шашек на одной диагонали
        //Добавляем в массивы, являющиеся диагоналями, шашки и пустые клетки
        for (int i = items.length - 1; i >= 0; --i)
            for (int j = 0; j < Item.WAYS_COUNT; ++j)
                if (items[i].ways[j]) ways[j].add(items[i]);

        items = null;
    }

    /**
     * Добавить возможный ход-move для шашки-item
     */
    private static void AddMove(Item item, Item move) {
        for (int i = 0; i < moves.size(); ++i) {
            LinkedList<Item> items = moves.get(i);
            if (items.contains(item)) {
                items.add(move);
                return;
            }
        }
        LinkedList<Item> items = new LinkedList<Item>();
        items.add(item);
        items.add(move);
        moves.add(items);
    }

    /**
     * Добавить возможный ход-move для шашки-itemJump при взятии шашки-itemOff.
     * Метод возвращает индекс списка, в который сделали запись.
     */
    private static int AddJump(Item itemJump, Item itemOff, Item move) {
        for (int i = 0; i < jumps.size(); ++i) {
            LinkedList<Item> items = jumps.get(i);
            if (items.contains(itemJump) && items.contains(itemOff)) {
                items.add(move);
                return i;
            }
        }
        LinkedList<Item> items = new LinkedList<Item>();
        items.add(itemJump);
        items.add(itemOff);
        items.add(move);
        jumps.add(items);
        return jumps.size() - 1;
    }


    /**
     * Ищем ходы, куда шашка item может побить далее
     */
    private static void SearchForNextJumps(Item item)
    {
        // Ищем диагонали, на которых лежит шашка
        for (int i = 0; i < Item.WAYS_COUNT; ++i)
        {
            if (item.ways[i])
            {
                //Проходим по текущей диагонали
                int way_size = ways[i].size();
                int j = ways[i].indexOf(item); //Индекс шашки item на этой диагонали
                if (item.isKing) // ДАМКА
                {
                    int k = j;
                    Item king = item;
                    //Пропускаем все пустые клетки после дамки (ИДЕМ ВВЕРХ ПО ДИАГОНАЛИ)
                    for(++k; (k < way_size) && (ways[i].get(k).type == ITEM_TYPE.square); ++k);
                    // Встретили шашку?
                    if (k != way_size)
                    {
                        // Шашка соперника?
                        if (king.type != ways[i].get(k).type)
                        {
                            // Добавляем все ходы для битья
                            for (int q = k + 1; (q < way_size) && (ways[i].get(q).type == ITEM_TYPE.square); ++q)
                            {
                                AddJump(king, ways[i].get(k), ways[i].get(q));
                                isJump = true;
                            }
                        }
                    }

                    k = j;
                    //Пропускаем все пустые клетки до дамки (ИДЕМ ВНИЗ ПО ДИАГОНАЛИ)
                    for(--k; (k >= 0) && (ways[i].get(k).type == ITEM_TYPE.square); --k);
                    // Встретили шашку на пути?
                    if (k != -1)
                    {
                        // Шашка соперника?
                        if (king.type != ways[i].get(k).type)
                        {
                            // Добавляем все ходы для битья
                            for (int q = k - 1; (q != -1) && (ways[i].get(q).type == ITEM_TYPE.square); --q)
                            {
                                AddJump(king, ways[i].get(k), ways[i].get(q));
                                isJump = true;
                                isMove = false;
                            }
                        }
                    }
                    // Если есть куда бить дамкой
                    /*if (isJump) {
                        // TODO: попробовать вынести за цикл RemoveFakeJumps
                        RemoveFakeJumps(king);
                    }*/
                }
                else { // ОБЫЧНАЯ ШАШКА
                    // Ищем комбинацию «шашка(1) - шашка (2) - пустое поле» (движемся вверх по диагонали)
                    if (j + 1 != way_size)// На диагонали после текущей шашки(1) имеются еще шашки или клетки?
                    {
                        ITEM_TYPE nextItemType = ways[i].get(j + 1).type;
                        // Далее находится шашка(2)?
                        if ((item.type != nextItemType) && (nextItemType != ITEM_TYPE.square)) {
                            // После шашки(2) находится клетка?
                            if ((j + 2 != way_size) && (ways[i].get(j + 2).type == ITEM_TYPE.square)) {
                                AddJump(ways[i].get(j), ways[i].get(j + 1), ways[i].get(j + 2));
                                isJump = true;
                            }
                        }
                    }
                    // Ищем комбинацию «шашка(1) - шашка (2) - пустое поле» (движемся вниз по диагонали)
                    if (j - 1 >= 0)// На диагонали перед текущей шашкой(1) имеются еще шашки или клетки?
                    {
                        ITEM_TYPE prevItemType = ways[i].get(j - 1).type;
                        // Сзади находится шашка(2)?
                        if ((item.type != prevItemType) && (prevItemType != ITEM_TYPE.square)) {
                            // Перед шашкой(2) находится клетка?
                            if ((j - 2 >= 0) && (ways[i].get(j - 2).type == ITEM_TYPE.square)) {
                                AddJump(ways[i].get(j), ways[i].get(j - 1), ways[i].get(j - 2));
                                isJump = true;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Игрок кликнул на свою шашку? Если да, запомним ее как selectedItem
     */
    public static boolean CheckTurn(int id)
    {
        // Проходим по всем диагоналям
        for (int i = 0; i < ways.length; ++i)
        {
            int way_size = ways[i].size();
            // Проходим по всем шашкам и клеткам на диагонали
            for (int j = 0; j < way_size; ++j)
            {
                Item item = ways[i].get(j);
                // Пропускаем шашки соперника и пустые клетки
                if (item.type == turn)
                {
                    //Игрок кликнул на эту шашку?
                    if (item.id == id)
                    {
                        selectedFirstItem = item;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Выбранная шашка selectedFirstItem может бить?
     */
    public static boolean CheckJump() {
        for (int i = 0; i < jumps.size(); ++i) {
            if (jumps.get(i).get(0).id == selectedFirstItem.id)
                return true;
        }
        return false;
    }

    /**
     * Выбранная шашка selectedFirstItem может ходить?
     */
    public static boolean CheckMove() {
        for (int i = 0; i < moves.size(); ++i) {
            if (moves.get(i).get(0).id == selectedFirstItem.id)
                return true;
        }
        return false;
    }

    /**
     * Бьем выбранной шашкой selectedItem на клетку с идентификатором id.
     */
    public static boolean Jump(int id, Window window) {
        // Ищем выбранную шашку среди всех шашек, которые могут бить
        for (int i = 0; i < jumps.size(); ++i)
        {
            LinkedList<Item> items = jumps.get(i);
            if (items.get(0).id == selectedFirstItem.id)
            {
                // Проверяем все клетки, куда шашка может бить
                for (int j = 2; j < items.size(); ++j)
                {
                    Item toItem = items.get(j);
                    if (toItem.id == id) // Можем бить на клетку с идентификатором id?
                    {
                        // Перемещаем шашку на экране
                        ImageButton from = window.findViewById(selectedFirstItem.id);
                        ImageButton middle = window.findViewById(items.get(1).id);
                        ImageButton to = window.findViewById(id);
                        Drawable d = from.getDrawable();
                        from.setImageDrawable(to.getDrawable());
                        to.setImageDrawable(d);
                        middle.setImageDrawable(from.getDrawable());

                        //Превращаем в дамку простую шашку, если встали на дамочное поле
                        if (!selectedFirstItem.isKing) {
                            for (int k = 0; k < 4; ++k) {
                                if (kingSquares[turn.ordinal()][k] == toItem.id) {
                                    selectedFirstItem.isKing = true;
                                    break;
                                }
                            }
                        }

                        // Переинициализируем значения для передачи хода сопернику или для того чтобы самому продолжить бить
                        isJump = isMove = isNeedSecondClick = false;
                        toItem.isKing = selectedFirstItem.isKing;
                        selectedFirstItem.isKing = false;
                        items.get(1).isKing = false;
                        items.get(1).type = ITEM_TYPE.square;
                        toItem.type = selectedFirstItem.type;
                        selectedFirstItem.type = ITEM_TYPE.square;
                        moves.clear();
                        jumps.clear();
                        selectedFirstItem = null;
                        SearchForNextJumps(toItem);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Делаем ход выбранной шашкой selectedItem на клетку с идентификатором id.
     */
    public static boolean Move(int id, Window window)
    {
        // Ищем выбранную шашку среди всех шашек, которые могут ходить
        for (int i = 0; i < moves.size(); ++i)
        {
            LinkedList<Item> items = moves.get(i);
            if (items.get(0).id == selectedFirstItem.id)
            {
                // Проверяем все клетки, куда шашка может пойти
                for (int j = 1; j < items.size(); ++j)
                {
                    Item toItem = items.get(j);
                    if (toItem.id == id)// Можем ходить на клетку с идентификатором id?
                    {
                        // Перемещаем шашку на экране
                        ImageButton from = window.findViewById(selectedFirstItem.id);
                        ImageButton to = window.findViewById(id);
                        Drawable d = from.getDrawable();
                        from.setImageDrawable(to.getDrawable());
                        to.setImageDrawable(d);

                        //Превращаем в дамку простую шашку, если встали на дамочное поле
                        if (!selectedFirstItem.isKing) {
                            for (int k = 0; k < 4; ++k) {
                                if (kingSquares[turn.ordinal()][k] == toItem.id) {
                                    selectedFirstItem.isKing = true;
                                    break;
                                }
                            }
                        }

                        // Переинициализируем значения для передачи хода сопернику или для того чтобы самому продолжить бить
                        isJump = isMove = isNeedSecondClick = false;
                        toItem.isKing = selectedFirstItem.isKing;
                        selectedFirstItem.isKing = false;
                        toItem.type = selectedFirstItem.type;
                        selectedFirstItem.type = ITEM_TYPE.square;
                        moves.clear();
                        jumps.clear();
                        selectedFirstItem = null;
                        return true;
                    }
                }
                // Выходим, если игрок кликнул не туда
                return false;
            }
        }
        // Сюда никогда не попадем
        return false;
    }

    /**
     * Изменить очередь хода при необходимости
     */
    public static void NextTurn() {
        if (!isJump) // Если бить больше нечего, меняем очередь хода
            GameEngine.turn = (GameEngine.turn == Item.ITEM_TYPE.white ? Item.ITEM_TYPE.black : Item.ITEM_TYPE.white);
    }

    /**
     * Удаляет неверные ходы дамки после битья. Допустим, после битья дамка может оказаться
     * на двух разных клетках. С одной клетки она будет должна бить далее, а с другой нет.
     * Поэтому одну из клеток необходимо убрать из возможных ходов.
     * idx - индекс диагонали, на которой находится дамка.
     */
   /* static private void RemoveFakeJumps(Item king, int idx)
    {
        if (removedFakeJumps)
        LinkedList<Item> realJumps = new LinkedList<Item>();
        LinkedList<Item> undecidedJumps = new LinkedList<Item>();

        for (int i = 0; i < jumps.size(); ++i)
        {
            // Ищем нашу дамку
            if (jumps.get(i).get(0).id == king.id)
            {
                int w = 2;
                //Проходим по всем клеткам, куда дамка может побить, для текущей диагонали
                for (; w < jumps.get(i).size(); ++w)
                {
                    Item item = jumps.get(i).get(w);
                    for (int s = 0; s < Item.WAYS_COUNT; ++s) {
                        //Ищем диагонали, на которых лежит клетка
                        if (item.ways[s]) {
                            //Ищем, где находиться наша дамка, чтобы не идти в ее сторону
                            int res = ways[s].indexOf(king);
                            int way_size = ways[s].size();
                            int j = ways[s].indexOf(item); //Индекс item на этой диагонали
                            if ((res == -1) || (res < j)) {
                                int k = j;
                                //Пропускаем все пустые клетки после дамки (ИДЕМ ВВЕРХ ПО ДИАГОНАЛИ)
                                for(++k; (k < way_size) && (ways[i].get(k).type == ITEM_TYPE.square); ++k);
                                // Встретили шашку соперника?
                                if ((k != way_size) && (king.type != ways[i].get(k).type))
                                {
                                    // Дамка может бить еще дальше с этой позиции
                                    for (int q = k + 1; (q < way_size) && (ways[i].get(q).type == ITEM_TYPE.square); ++q) {
                                        if (!realJumps.contains(item)) realJumps.add(item);
                                        if (undecidedJumps.contains(item))
                                            undecidedJumps.remove(item);
                                        break;
                                    }
                                } // Дамка не может бить еще дальше с этой позиции
                                else {
                                    if (!realJumps.contains(item) && !undecidedJumps.contains(item))
                                        undecidedJumps.add(item);
                                }
                            }


                            // Встретили шашку?
                            if (k != way_size) {
                                // Шашка соперника?
                                if (king.type != ways[i].get(k).type) {
                                    // Добавляем все ходы для битья
                                    for (int q = k + 1; (q < way_size) && (ways[i].get(q).type == ITEM_TYPE.square); ++q) {
                                        AddJump(king, ways[i].get(k), ways[i].get(q));
                                        isJump = true;
                                        isMove = false;
                                    }
                                }
                                // Если бить не можем, т.е. таких ходов нет, или встретили нашу другую шашку,
                                // Тогда добавляем обычные ходы до встретившейся шашки (своей или соперника)
                                if (isJump == false) {
                                    for (int t = j + 1; t < k; ++t) {
                                        AddMove(king, ways[i].get(t));
                                        isMove = true;
                                    }
                                }
                            } // Иначе добавляем возможные ходы, только если не надо бить
                            else if (isJump == false) {
                                for (int t = j + 1; t < k; ++t) {
                                    AddMove(king, ways[i].get(t));
                                    isMove = true;
                                }
                            }

                            if ((res == -1) || (res > j)) {
                                int k = j;
                                //Пропускаем все пустые клетки до дамки
                                do {
                                    --k;
                                } while ((k >= 0) && (ways[i].get(k).type == ITEM_TYPE.square));
                                // Встретили шашку соперника?
                                if ((k != -1) && (king.type != ways[i].get(k).type)) {
                                    // Дамка сможет бить еще дальше с этой позиции
                                    for (int q = k - 1; (q != -1) && (ways[i].get(q).type == ITEM_TYPE.square); --q) {
                                        if (!realJumps.contains(item)) realJumps.add(item);
                                        if (undecidedJumps.contains(item))
                                            undecidedJumps.remove(item);
                                        break;
                                    }
                                } // Дамка не может бить еще дальше с этой позиции
                                else {
                                    if (!realJumps.contains(item) && !undecidedJumps.contains(item))
                                        undecidedJumps.add(item);
                                }
                            }
                        }
                    }
                }
                if (!realJumps.isEmpty()) {
                    for (int b = 0; b < undecidedJumps.size(); ++b)
                        jumps.get(i).remove(undecidedJumps.get(b));
                }
                realJumps.clear();
                undecidedJumps.clear();
            }
        }
    }*/

    /**
     * Ищем все возможные ходы (надо бить или ходить)
     */
    public static void SearchForAllMoves() {
        // Если игрок продолжает бить, то return. Т.к. ходы уже найдены с помощью вызова метода
        // SearchForNextJumps(Item item) для бьющей шашки item в конце метода Jump(int id, Window window)
        if (isJump) return;

        isJump = isMove = false;
        //Проходим по всем диагоналям
        for (int i = 0; i < ways.length; ++i)
        {
            //Проходим по текущей диагонали
            int way_size = ways[i].size();
            for (int j = 0; j < way_size; ++j)
            {
                if (ways[i].get(j).type == turn) //Ищем ходы только для шашек соответствующего цвета
                {
                    if (ways[i].get(j).isKing) // ДАМКА
                    {
                        int idx = -1; // индекс списка в jumps для этой дамки и диагонали, на которой она лежит
                        int k = j; // j - индекс дамки на диагонали
                        Item king = ways[i].get(j);
                        //Пропускаем все пустые клетки после дамки (ИДЕМ ВВЕРХ ПО ДИАГОНАЛИ)
                        for(++k; (k < way_size) && (ways[i].get(k).type == ITEM_TYPE.square); ++k);
                        // Встретили шашку?
                        if (k != way_size) {
                            // Шашка соперника?
                            if (king.type != ways[i].get(k).type) {
                                // Добавляем все ходы для битья
                                for (int q = k + 1; (q < way_size) && (ways[i].get(q).type == ITEM_TYPE.square); ++q) {
                                    idx = AddJump(king, ways[i].get(k), ways[i].get(q));
                                    isJump = true;
                                    isMove = false;
                                }
                            }
                            // Если бить не можем, т.е. таких ходов нет, или встретили нашу другую шашку,
                            // Тогда добавляем обычные ходы до встретившейся шашки (своей или соперника)
                            if (isJump == false) {
                                for (int t = j + 1; t < k; ++t) {
                                    AddMove(king, ways[i].get(t));
                                    isMove = true;
                                }
                            }
                        } // Иначе добавляем возможные ходы, только если не надо бить
                        else if (isJump == false) {
                            for (int t = j + 1; t < k; ++t) {
                                AddMove(king, ways[i].get(t));
                                isMove = true;
                            }
                        }

                        k = j;
                        //Пропускаем все пустые клетки до дамки (ИДЕМ ВНИЗ ПО ДИАГОНАЛИ)
                        for(--k; (k >= 0) && (ways[i].get(k).type == ITEM_TYPE.square); --k);
                        // Встретили шашку на пути?
                        if (k != -1) {
                            // Шашка соперника?
                            if (king.type != ways[i].get(k).type) {
                                // Добавляем все ходы для битья
                                for (int q = k - 1; (q != -1) && (ways[i].get(q).type == ITEM_TYPE.square); --q) {
                                    idx = AddJump(king, ways[i].get(k), ways[i].get(q));
                                    isJump = true;
                                    isMove = false;
                                }
                            }
                            // Если бить не можем, т.е. таких ходов нет, или встретили нашу другую шашку,
                            // Тогда добавляем обычные ходы до встретившейся шашки (своей или соперника)
                            if (isJump == false) {
                                for (int t = j - 1; t > k; --t) {
                                    AddMove(king, ways[i].get(t));
                                    isMove = true;
                                }
                            }
                        }
                        // Иначе дошли до конца диагонали? Добавляем возможные ходы
                        // до конца диагонали, только если не надо бить
                        else if (isJump == false) {
                            for (int t = j - 1; t > k; --t) {
                                AddMove(king, ways[i].get(t));
                                isMove = true;
                            }
                        }

                        // Если есть куда бить дамкой
                        /*if (isJump) {
                            // TODO: попробовать вынести за цикл RemoveFakeJumps
                            RemoveFakeJumps(king, i);
                        }*/
                    }
                    else { // Иначе ОБЫЧНАЯ ШАШКА
                        ITEM_TYPE itemType = ways[i].get(j).type;
                        // Ищем комбинацию «шашка(1) - шашка (2) - пустое поле» (движемся вверх по диагонали)
                        if (j + 1 != way_size)// На диагонали после текущей шашки(1) имеются еще шашки или клетки?
                        {
                            ITEM_TYPE nextItemType = ways[i].get(j + 1).type;
                            // Далее находится шашка(2)?
                            if ((itemType != nextItemType) && (nextItemType != ITEM_TYPE.square)) {
                                // После шашки(2) находится клетка?
                                if ((j + 2 != way_size) && (ways[i].get(j + 2).type == ITEM_TYPE.square)) {
                                    AddJump(ways[i].get(j), ways[i].get(j + 1), ways[i].get(j + 2));
                                    isJump = true;
                                    isMove = false;
                                }
                            }
                            // Иначе далее находится клетка и мы играем за белых? (Черные не могут ходить в обратную сторону)
                            else if ((nextItemType == ITEM_TYPE.square) && (itemType == ITEM_TYPE.white)) {
                                // Добавляем возможные ходы, только если не надо бить
                                if (isJump == false) {
                                    AddMove(ways[i].get(j), ways[i].get(j + 1));
                                    isMove = true;
                                }
                            }
                        }
                        // Ищем комбинацию «шашка(1) - шашка (2) - пустое поле» (движемся вниз по диагонали)
                        if (j - 1 >= 0)// На диагонали перед текущей шашкой(1) имеются еще шашки или клетки?
                        {
                            ITEM_TYPE prevItemType = ways[i].get(j - 1).type;
                            // Сзади находится шашка(2)?
                            if ((itemType != prevItemType) && (prevItemType != ITEM_TYPE.square)) {
                                // Перед шашкой(2) находится клетка?
                                if ((j - 2 >= 0) && (ways[i].get(j - 2).type == ITEM_TYPE.square)) {
                                    AddJump(ways[i].get(j), ways[i].get(j - 1), ways[i].get(j - 2));
                                    isJump = true;
                                    isMove = false;
                                }
                            }
                            // Иначе сзади находится клетка и мы играем за черных? (Белые не могут ходить в обратную сторону)
                            else if ((prevItemType == ITEM_TYPE.square) && (itemType == ITEM_TYPE.black)) {
                                // Добавляем возможные ходы, только если не надо бить
                                if (isJump == false) {
                                    AddMove(ways[i].get(j), ways[i].get(j - 1));
                                    isMove = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

