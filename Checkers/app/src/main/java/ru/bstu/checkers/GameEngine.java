package ru.bstu.checkers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.LinkedList;


import ru.bstu.checkers.Item.ITEM_TYPE;

/* Backup: https://github.com/notemac/Android-checkers.
   Основные идеи алгоритма взяты отсюда: https://habr.com/post/227911/ - Как я шашки писал

   НЕРЕАЛИЗОВАНО:
 * Избегаем ситуации "побить шашку дважды". Рассмотрим позицию:
 * белые простые: d2, e3, d4, f4, g3; черная дамка: h4.
 * Дамка черных после битья белых шашек должна оказаться на одной из клеток e5:h8,
 * т.е. она не может бить далее на g3 и h2.
 */

// TODO: 9/26/2018 Fix bug: шашка превращается в дамку и продолжает бить по этой же диагонали назад
// TODO: 9/27/2018 Code review: пересмотреть весь код, связанный с BackMove(). В некоторых местах можно оптимизировать.
// TODO: 9/27/2018 ??? Сравнивать ways у Item через ways1.equals(ways2), т.е. ways никогда не изменяются ???
// TODO: 9/27/2018 ??? Использовать для ways у Item массив из 2 Integer (номера диагоналей) ???

public class GameEngine {

    private class PrevMove {
        private ArrayList<Item> items;
        private ArrayList<Integer> imageId;
        private ITEM_TYPE turn;
        public PrevMove() {
            items = new ArrayList<>(3);
            imageId = new ArrayList<>(3);
        }
        public void SetTurn(ITEM_TYPE turn) { this.turn = turn; }
        public ITEM_TYPE GetTurn() { return this.turn; }
        public void Add(Item from, int fromImageId, Item to, int toImageId)
        {
            items.clear();
            items.add(from);
            items.add(to);
            imageId.clear();
            imageId.add(fromImageId);
            imageId.add(toImageId);
        }
        public void Add(Item from, int fromImageId, Item middle, int middleImageId, Item to, int toImageId)
        {
            items.clear();
            items.add(from);
            items.add(middle);
            items.add(to);
            imageId.clear();
            imageId.add(fromImageId);
            imageId.add(middleImageId);
            imageId.add(toImageId);
        }
        public boolean Exist() { return (!items.isEmpty() && !imageId.isEmpty()); }
        public void Clear()
        {
            items.clear();
            imageId.clear();
        }
    }

    private PrevMove prevMove;
    /**
     * Item, на который игрок кликнул первый раз
     */
    private Item selectedFirstItem = null;
    private Item selectedSecondItem = null;
    /**
     * Надо бить? (Бить обязательно)
     */
    public boolean isJump = false;
    /**
     * Есть куда ходить? (Если нечего бить)
     */
    public boolean isMove = false;
    /**
     * Шашка выбрана, необходимо сделать ход/побить (т.е. клинуть второй раз)
     */
    public boolean isNeedSecondClick = false;
    /**
     * Очередь хода
     */
    public ITEM_TYPE turn = ITEM_TYPE.white;
    /**
     * Массив диагоналей (ways) представляет игровую доску.
     * В каждом way[i] хранятся шашки и пустые клетки, лежащие на этой диагонали.
     */
    public ArrayList<Item>[] ways;

    /**
     * Массив из списков ходов. Т.е. куда может пойти шашка. Первый элемент списка
     * всегда шашка; все остальные элементы - клетки, куда может пойти эта шашка.
     */
    public ArrayList<LinkedList<Item>> moves;
    /**
     * Список из списков с ходами, куда шашка может побить. Первый элемент списка всегда бьющая шашка;
     * второй - шашка, которая под боем; все остальные элементы - клетки, где может оказаться бьющая шашка после битья.
     * Т.о. если шашка может бить одновременно в разные стороны, то для нее будет создано несколько списков,
     * в которых первым элементом будет эта шашка.
     */
    public LinkedList<LinkedList<Item>> jumps;

    /**
     * Клетки, на которых шашки превращаются в дамки. kingSquares[0] - для черных, kingSquares[1] - для белых
     */
    public int[][] kingSquares;

    /** Constructor for creating from DB*/
    public void Init(int turn, ArrayList<Item>[] ways) {
        prevMove = new PrevMove();
        selectedFirstItem = selectedSecondItem = null;
        isJump = isMove = isNeedSecondClick = false;
        this.turn = ITEM_TYPE.values()[turn];

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
        this.ways = ways;

        if (this.turn == Item.ITEM_TYPE.white) {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.VISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.INVISIBLE);
        } else {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.VISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.INVISIBLE);
        }
    }

    public void Init() {
        prevMove = new PrevMove();
        selectedFirstItem = selectedSecondItem = null;
        isJump = isMove = isNeedSecondClick = false;
        turn = ITEM_TYPE.white;

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

        if (turn == Item.ITEM_TYPE.white) {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.VISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.INVISIBLE);
        } else {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.VISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Добавить возможный ход-move для шашки-item
     */
    private void AddMove(Item item, Item move) {
        for (int i = 0; i < moves.size(); ++i) {
            LinkedList<Item> items = moves.get(i);
            if (items.get(0).id == item.id) {
                items.add(move);
                return;
            }
            /*if (items.contains(item)) {
                items.add(move);
                return;
            }*/
        }
        LinkedList<Item> items = new LinkedList<Item>();
        items.add(item);
        items.add(move);
        moves.add(items);
    }

    /**
     * Добавить возможный ход-move для шашки-itemJump при взятии шашки-itemOff.
     * Метод возвращает индекс списка в jumps, в который сделали запись.
     */
    private int AddJump(Item itemJump, Item itemOff, Item move) {
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
    public boolean SearchForNextJump() {
        // Ищем диагонали, на которых лежит шашка
        for (int i = 0; i < Item.WAYS_COUNT; ++i) {
            if (selectedSecondItem.ways[i]) {
                //Проходим по текущей диагонали
                int way_size = ways[i].size();
                int j = ways[i].indexOf(selectedSecondItem); //Индекс шашки item на этой диагонали
                if (selectedSecondItem.isKing) // ДАМКА
                {
                    int k = j;
                    Item king = selectedSecondItem;
                    int listJumpId = -1; // индекс списка в jumps для этой дамки и диагонали, на которой дамка лежит
                    //Пропускаем все пустые клетки после дамки (ИДЕМ ВВЕРХ ПО ДИАГОНАЛИ)
                    for (++k; (k < way_size) && (ways[i].get(k).type == ITEM_TYPE.square); ++k) ;
                    // Встретили шашку?
                    if (k != way_size) {
                        // Шашка соперника?
                        if (king.type != ways[i].get(k).type) {
                            // Добавляем все ходы для битья
                            for (int q = k + 1; (q < way_size) && (ways[i].get(q).type == ITEM_TYPE.square); ++q) {
                                listJumpId = AddJump(king, ways[i].get(k), ways[i].get(q));
                                isJump = true;
                            }
                            if (listJumpId != -1)
                                RemoveFakeJumps(listJumpId);
                        }
                    }

                    listJumpId = -1;
                    k = j;
                    //Пропускаем все пустые клетки до дамки (ИДЕМ ВНИЗ ПО ДИАГОНАЛИ)
                    for (--k; (k >= 0) && (ways[i].get(k).type == ITEM_TYPE.square); --k) ;
                    // Встретили шашку на пути?
                    if (k != -1) {
                        // Шашка соперника?
                        if (king.type != ways[i].get(k).type) {
                            // Добавляем все ходы для битья
                            for (int q = k - 1; (q != -1) && (ways[i].get(q).type == ITEM_TYPE.square); --q) {
                                listJumpId = AddJump(king, ways[i].get(k), ways[i].get(q));
                                isJump = true;
                            }
                            if (listJumpId != -1)
                                RemoveFakeJumps(listJumpId);
                        }
                    }
                } else { // ОБЫЧНАЯ ШАШКА
                    // Ищем комбинацию «шашка(1) - шашка (2) - пустое поле» (движемся вверх по диагонали)
                    if (j + 1 != way_size)// На диагонали после текущей шашки(1) имеются еще шашки или клетки?
                    {
                        ITEM_TYPE nextItemType = ways[i].get(j + 1).type;
                        // Далее находится шашка(2)?
                        if ((selectedSecondItem.type != nextItemType) && (nextItemType != ITEM_TYPE.square)) {
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
                        if ((selectedSecondItem.type != prevItemType) && (prevItemType != ITEM_TYPE.square)) {
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
        selectedSecondItem = null;
        return isJump;
    }

    /**
     * Игрок кликнул на свою шашку? Если да, запомним ее как selectedItem
     */
    public boolean CheckTurn(int id) {
        // Проходим по всем диагоналям
        for (int i = 0; i < ways.length; ++i) {
            int way_size = ways[i].size();
            // Проходим по всем шашкам и клеткам на диагонали
            for (int j = 0; j < way_size; ++j) {
                Item item = ways[i].get(j);
                // Пропускаем шашки соперника и пустые клетки
                if (item.type == turn) {
                    //Игрок кликнул на эту шашку?
                    if (item.id == id) {
                        selectedFirstItem = item;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Подсвечивает возможные ходы
     */
    public void HighlightMoves() {
        MyApplication.getCurrentActivity().findViewById(selectedFirstItem.id).setBackgroundResource(R.color.colorRed);
        if (isJump) {
            for (int i = 0; i < jumps.size(); ++i) {
                LinkedList<Item> items = jumps.get(i);
                if (items.get(0).id == selectedFirstItem.id) {
                    for (int j = 2; j < items.size(); ++j) {
                        MyApplication.getCurrentActivity().findViewById(items.get(j).id).setBackgroundResource(R.color.colorGreen);
                    }
                }
            }
        } else { //isMove
            for (int i = 0; i < moves.size(); ++i) {
                LinkedList<Item> items = moves.get(i);
                if (items.get(0).id == selectedFirstItem.id) {
                    for (int j = 1; j < items.size(); ++j) {
                        MyApplication.getCurrentActivity().findViewById(items.get(j).id).setBackgroundResource(R.color.colorGreen);
                    }
                }
            }
        }
    }

    private void RemoveHighlighting()
    {
        MyApplication.getCurrentActivity().findViewById(selectedFirstItem.id).setBackgroundResource(R.drawable.black_square);
        if (isJump) {
            for (int i = 0; i < jumps.size(); ++i) {
                LinkedList<Item> items = jumps.get(i);
                if (items.get(0).id == selectedFirstItem.id) {
                    for (int j = 2; j < items.size(); ++j) {
                        MyApplication.getCurrentActivity().findViewById(items.get(j).id).setBackgroundResource(R.drawable.black_square);
                    }
                }
            }
        } else { //isMove
            for (int i = 0; i < moves.size(); ++i) {
                LinkedList<Item> items = moves.get(i);
                if (items.get(0).id == selectedFirstItem.id) {
                    for (int j = 1; j < items.size(); ++j) {
                        MyApplication.getCurrentActivity().findViewById(items.get(j).id).setBackgroundResource(R.drawable.black_square);
                    }
                }
            }
        }
    }

    public void PrepareForNextMove(boolean isRepick)
    {
        RemoveHighlighting();
        if (!isRepick) {
            isJump = isMove = false;
            moves.clear();
            jumps.clear();
        }
        isNeedSecondClick = false;
        selectedFirstItem = null;
    }

    public void PrepareForBackMove()
    {
        if (selectedFirstItem != null) RemoveHighlighting();
        isJump = isMove = false;
        moves.clear();
        jumps.clear();
        isNeedSecondClick = false;
        selectedFirstItem = null;
    }

    /**
     * Выбранная шашка selectedFirstItem может бить?
     */
    public boolean CheckJump() {
        for (int i = 0; i < jumps.size(); ++i) {
            if (jumps.get(i).get(0).id == selectedFirstItem.id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Выбранная шашка selectedFirstItem может ходить?
     */
    public boolean CheckMove() {
        for (int i = 0; i < moves.size(); ++i) {
            if (moves.get(i).get(0).id == selectedFirstItem.id)
                return true;
        }
        return false;
    }

    /**
     * Бьем выбранной шашкой selectedItem на клетку с идентификатором id.
     */
    public boolean Jump(int id) {
        // Ищем выбранную шашку среди всех шашек, которые могут бить
        for (int i = 0; i < jumps.size(); ++i) {
            LinkedList<Item> items = jumps.get(i);
            if (items.get(0).id == selectedFirstItem.id) {
                // Проверяем все клетки, куда шашка может бить
                for (int j = 2; j < items.size(); ++j) {
                    Item toItem = items.get(j);
                    if (toItem.id == id) // Можем бить на клетку с идентификатором id?
                    {
                        selectedSecondItem = toItem;

                        ImageButton from = MyApplication.getCurrentActivity().findViewById(selectedFirstItem.id);
                        ImageButton middle = MyApplication.getCurrentActivity().findViewById(items.get(1).id);
                        ImageButton to = MyApplication.getCurrentActivity().findViewById(id);
                        int fromTag = (int)(from.getTag());
                        int middleTag = (int)(middle.getTag());
                        int toTag = (int)(to.getTag());
                        prevMove.Add(selectedFirstItem.Clone(), fromTag,
                                items.get(1).Clone(), middleTag,  toItem.Clone(), toTag);
                        prevMove.SetTurn(turn);

                        //Превращаем в дамку простую шашку, если встали на дамочное поле
                        if (!selectedFirstItem.isKing) {
                            for (int k = 0; k < 4; ++k) {
                                if (kingSquares[turn.ordinal()][k] == toItem.id) {
                                    selectedFirstItem.isKing = true;
                                    break;
                                }
                            }
                        }

                        toItem.isKing = selectedFirstItem.isKing;
                        selectedFirstItem.isKing = false;
                        items.get(1).isKing = false;
                        items.get(1).type = ITEM_TYPE.square;
                        toItem.type = selectedFirstItem.type;
                        selectedFirstItem.type = ITEM_TYPE.square;


                        from.setImageResource(R.drawable.black_square);
                        from.setTag(R.drawable.black_square);


                        if (toItem.isKing) {
                            Context context = MyApplication.getCurrentActivity().getApplicationContext();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            if (toItem.type == ITEM_TYPE.black)
                            {
                                int imageId = preferences.getInt(context.getResources()
                                        .getString(R.string.idBlackKing), R.drawable.black_king);
                                to.setImageResource(imageId);
                                to.setTag(imageId);
                            }
                            else {
                                int imageId = preferences.getInt(context.getResources()
                                        .getString(R.string.idWhiteKing), R.drawable.white_king);
                                to.setImageResource(imageId);
                                to.setTag(imageId);
                            }
                        } else {
                            to.setImageResource(fromTag);
                            to.setTag(fromTag);
                        }

                        middle.setImageResource(R.drawable.black_square);
                        middle.setTag(R.drawable.black_square);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean BackMoveExist() { return prevMove.Exist(); }
    public void BackMove()
    {
        turn = prevMove.turn;
        if (turn == Item.ITEM_TYPE.white) {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.INVISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.VISIBLE);
        } else {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.INVISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.VISIBLE);
        }
        for(int i = 0; i < prevMove.items.size(); ++i)
        {
            for(int j = 0; j < Item.WAYS_COUNT; ++j)
            {
                for (int k = 0; k < ways[j].size(); ++k)
                {
                    Item item = prevMove.items.get(i);
                    if (item.ways.equals(ways[j].get(k).ways))
                    {
                        ImageButton ib = MyApplication.getCurrentActivity().findViewById(item.id);
                        ib.setImageResource(prevMove.imageId.get(i));
                        ib.setTag(prevMove.imageId.get(i));
                        ways[j].set(k, item);
                        break;
                    }
                }
            }
        }
        prevMove.Clear();
    }


    public void UpdateDraughtsSetForBackMove()
    {
        for(int i = 0; i < prevMove.items.size(); ++i)
        {
            Resources resources = MyApplication.getCurrentActivity().getApplicationContext().getResources();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getCurrentActivity().getApplicationContext());
            int idBlackDraught = preferences.getInt(resources.getString(R.string.idBlackDraught), R.drawable.black_draught);
            int idWhiteDraught = preferences.getInt(resources.getString(R.string.idWhiteDraught), R.drawable.white_draught);
            int idBlackKing = preferences.getInt(resources.getString(R.string.idBlackKing), R.drawable.black_king);
            int idWhiteKing = preferences.getInt(resources.getString(R.string.idWhiteKing), R.drawable.white_king);
            Item item = prevMove.items.get(i);
            if (item.type == ITEM_TYPE.black)
            {
                if (item.isKing)
                    prevMove.imageId.set(i, idBlackKing);
                else
                    prevMove.imageId.set(i, idBlackDraught);
            }
            else if (item.type == ITEM_TYPE.white)
            {
                if (item.isKing)
                    prevMove.imageId.set(i, idWhiteKing);
                else
                    prevMove.imageId.set(i, idWhiteDraught);
            }
        }
    }

    /**
     * Делаем ход выбранной шашкой selectedItem на клетку с идентификатором id.
     */
    public boolean Move(int id) {
        // Ищем выбранную шашку среди всех шашек, которые могут ходить
        for (int i = 0; i < moves.size(); ++i) {
            LinkedList<Item> items = moves.get(i);
            if (items.get(0).id == selectedFirstItem.id) {
                // Проверяем все клетки, куда шашка может пойти
                for (int j = 1; j < items.size(); ++j) {
                    Item toItem = items.get(j);
                    if (toItem.id == id)// Можем ходить на клетку с идентификатором id?
                    {
                        ImageButton from = MyApplication.getCurrentActivity().findViewById(selectedFirstItem.id);
                        ImageButton to = MyApplication.getCurrentActivity().findViewById(toItem.id);

                        int fromTag = (int)(from.getTag());
                        int toTag = (int)(to.getTag());
                        prevMove.Add(selectedFirstItem.Clone(), fromTag, toItem.Clone(), toTag);
                        prevMove.SetTurn(turn);

                        //Превращаем в дамку простую шашку, если встали на дамочное поле
                        if (!selectedFirstItem.isKing) {
                            for (int k = 0; k < 4; ++k) {
                                if (kingSquares[turn.ordinal()][k] == toItem.id) {
                                    selectedFirstItem.isKing = true;
                                    break;
                                }
                            }
                        }

                        toItem.isKing = selectedFirstItem.isKing;
                        selectedFirstItem.isKing = false;
                        toItem.type = selectedFirstItem.type;
                        selectedFirstItem.type = ITEM_TYPE.square;

                        from.setImageResource(R.drawable.black_square);
                        from.setTag(R.drawable.black_square);

                        if (toItem.isKing) {
                            Context context = MyApplication.getCurrentActivity().getApplicationContext();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            if (toItem.type == ITEM_TYPE.black) {
                                int imageId = preferences.getInt(context.getResources()
                                        .getString(R.string.idBlackKing), R.drawable.black_king);
                                to.setImageResource(imageId);
                                to.setTag(imageId);
                            }
                            else {
                                int imageId = preferences.getInt(context.getResources()
                                        .getString(R.string.idWhiteKing), R.drawable.white_king);
                                to.setImageResource(imageId);
                                to.setTag(imageId);
                            }
                        } else {
                            to.setImageResource(fromTag);
                            to.setTag(fromTag);
                        }
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
     * Изменить очередь хода
     */
    public void NextTurn() {
        if (turn == Item.ITEM_TYPE.white) {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.INVISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.VISIBLE);
            turn = Item.ITEM_TYPE.black;
        } else {
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer2).setVisibility(View.INVISIBLE);
            MyApplication.getCurrentActivity().findViewById(R.id.iv_timer1).setVisibility(View.VISIBLE);
            turn = ITEM_TYPE.white;
        }
    }

    private boolean RemoveFakeJumpsUp(Item king, int idxItem, int s, Item item, LinkedList<Item> ongoingJumps, LinkedList<Item> undecidedJumps) {
        int way_size = ways[s].size();
        boolean ongoing = false;
        int k = idxItem;
        //Пропускаем все пустые клетки после item (ИДЕМ ВВЕРХ ПО ДИАГОНАЛИ)
        for (++k; (k < way_size) && (ways[s].get(k).type == ITEM_TYPE.square); ++k) ;
        if (k != way_size) // Встретили шашку?
        {
            // Встретили шашку соперника?
            if (king.type != ways[s].get(k).type) {
                // Проверяем, может ли item (т.е. возможно будущая наша дамка) бить с этой позиции
                for (int q = k + 1; (q < way_size) && (ways[s].get(q).type == ITEM_TYPE.square); ++q) {
                    ongoing = true;
                    ongoingJumps.add(item);
                    undecidedJumps.remove(item);
                    break;
                }
                if (ongoing) return true; //может
                else if (!undecidedJumps.contains(item)) undecidedJumps.add(item); //не может
            } //Встретили нашу другую шашку
            else if (!undecidedJumps.contains(item)) undecidedJumps.add(item);
        } //Не встретили шашку
        else if (!undecidedJumps.contains(item)) undecidedJumps.add(item);
        return false;
    }

    private boolean RemoveFakeJumpsDown(Item king, int idxItem, int s, Item item, LinkedList<Item> ongoingJumps, LinkedList<Item> undecidedJumps) {
        boolean ongoing = false;
        int k = idxItem;
        //Пропускаем все пустые клетки до item (ИДЕМ ВНИЗ ПО ДИАГОНАЛИ)
        for (--k; (k >= 0) && (ways[s].get(k).type == ITEM_TYPE.square); --k) ;
        if (k != -1) // Встретили шашку на пути?
        {
            // Встретили шашку соперника?
            if (king.type != ways[s].get(k).type) {
                // Проверяем, может ли item (т.е. возможно будущая наша дамка) бить с этой позиции
                for (int q = k - 1; (q != -1) && (ways[s].get(q).type == ITEM_TYPE.square); --q) {
                    ongoing = true;
                    ongoingJumps.add(item);
                    undecidedJumps.remove(item);
                    break;
                }
                if (ongoing) return true; //может
                else if (!undecidedJumps.contains(item)) undecidedJumps.add(item); //не может
            } //Встретили нашу другую шашку
            else if (!undecidedJumps.contains(item)) undecidedJumps.add(item);
        } //Не встретили шашку
        else if (!undecidedJumps.contains(item)) undecidedJumps.add(item);
        return false;
    }

    /**
     * Удаляет неверные ходы для дамок после битья. Допустим, после битья дамка может оказаться
     * на двух разных клетках. С одной клетки она будет должна бить далее, а с другой нет.
     * Поэтому одну из клеток необходимо убрать из возможных ходов.
     */
    private void RemoveFakeJumps(int listJumpId) {
        LinkedList<Item> ongoingJumps = new LinkedList<Item>();
        LinkedList<Item> undecidedJumps = new LinkedList<Item>();

        //Проходим по всем клеткам в списке, куда дамка может побить, для текущей диагонали
        LinkedList<Item> items = jumps.get(listJumpId);
        for (int i = 2; i < items.size(); ++i) {
            Item king = items.get(0); //Запоминаем дамку
            Item item = items.get(i); //Запоминаем текущую клетку
            for (int s = 0; s < Item.WAYS_COUNT; ++s) {
                if (item.ways[s]) //Ищем диагонали, на которых лежит item
                {
                    int idxItem = ways[s].indexOf(item); //Индекс item на этой диагонали
                    if (king.ways[s])//Дамка лежит на этой же диагонали?
                    {
                        //Ищем, где находиться дамка, чтобы не идти в ее сторону
                        int idxKing = ways[s].indexOf(king); //Индекс king на этой диагонали
                        if (idxKing < idxItem) {
                            if (RemoveFakeJumpsUp(king, idxItem, s, item, ongoingJumps, undecidedJumps))
                                break;
                        }//idxKing > idxItem
                        else if (RemoveFakeJumpsDown(king, idxItem, s, item, ongoingJumps, undecidedJumps))
                            break;

                    }//Дамка НЕ лежит на этой же диагонали
                    else {
                        if (RemoveFakeJumpsUp(king, idxItem, s, item, ongoingJumps, undecidedJumps))
                            break;
                        if (RemoveFakeJumpsDown(king, idxItem, s, item, ongoingJumps, undecidedJumps))
                            break;
                    }
                }
            }
        }
        // Если есть ходы, после которых дамка будет бить дальше, то удаляем все остальные
        if (!ongoingJumps.isEmpty()) {
            for (int i = 0; i < undecidedJumps.size(); ++i)
                items.remove(undecidedJumps.get(i));
        }
    }

    /**
     * Ищем все возможные ходы (надо бить или ходить)
     */
    public void SearchForAllMoves() {
        isJump = isMove = false;
        //Проходим по всем диагоналям
        for (int i = 0; i < ways.length; ++i) {
            //Проходим по текущей диагонали
            int way_size = ways[i].size();
            for (int j = 0; j < way_size; ++j) {
                if (ways[i].get(j).type == turn) //Ищем ходы только для шашек соответствующего цвета
                {
                    if (ways[i].get(j).isKing) // ДАМКА
                    {
                        int listJumpId = -1; // индекс списка в jumps для этой дамки и диагонали, на которой дамка лежит
                        int k = j; // j - индекс дамки на диагонали
                        Item king = ways[i].get(j);
                        //Пропускаем все пустые клетки после дамки (ИДЕМ ВВЕРХ ПО ДИАГОНАЛИ)
                        for (++k; (k < way_size) && (ways[i].get(k).type == ITEM_TYPE.square); ++k)
                            ;
                        // Встретили шашку?
                        if (k != way_size) {
                            // Шашка соперника?
                            if (king.type != ways[i].get(k).type) {
                                // Добавляем все ходы для битья
                                for (int q = k + 1; (q < way_size) && (ways[i].get(q).type == ITEM_TYPE.square); ++q) {
                                    listJumpId = AddJump(king, ways[i].get(k), ways[i].get(q));
                                    isJump = true;
                                    isMove = false;
                                }
                                if (listJumpId != -1)
                                    RemoveFakeJumps(listJumpId);
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

                        listJumpId = -1;
                        k = j;
                        //Пропускаем все пустые клетки до дамки (ИДЕМ ВНИЗ ПО ДИАГОНАЛИ)
                        for (--k; (k >= 0) && (ways[i].get(k).type == ITEM_TYPE.square); --k) ;
                        // Встретили шашку на пути?
                        if (k != -1) {
                            // Шашка соперника?
                            if (king.type != ways[i].get(k).type) {
                                // Добавляем все ходы для битья
                                for (int q = k - 1; (q != -1) && (ways[i].get(q).type == ITEM_TYPE.square); --q) {
                                    listJumpId = AddJump(king, ways[i].get(k), ways[i].get(q));
                                    isJump = true;
                                    isMove = false;
                                }
                                if (listJumpId != -1)
                                    RemoveFakeJumps(listJumpId);
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
                    } else { // Иначе ОБЫЧНАЯ ШАШКА
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


    /** Загрузить выбранный набор шашек*/
    public void LoadDraughtsSet()
    {
        Resources resources = MyApplication.getCurrentActivity().getApplicationContext().getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getCurrentActivity().getApplicationContext());
        int idBlackDraught = preferences.getInt(resources.getString(R.string.idBlackDraught), R.drawable.black_draught);
        int idWhiteDraught = preferences.getInt(resources.getString(R.string.idWhiteDraught), R.drawable.white_draught);
        int idBlackKing = preferences.getInt(resources.getString(R.string.idBlackKing), R.drawable.black_king);
        int idWhiteKing = preferences.getInt(resources.getString(R.string.idWhiteKing), R.drawable.white_king);
        for(int i = 0; i < ways.length; ++i)
        {
            ArrayList<Item> way = ways[i];
            for(int j = 0; j < way.size(); ++j) {
                Item item = way.get(j);
                ImageButton ib = MyApplication.getCurrentActivity().findViewById(item.id);
                if (item.type == Item.ITEM_TYPE.black)
                {
                    if(item.isKing)
                    {
                        ib.setImageResource(idBlackKing);
                        ib.setTag(idBlackKing);
                    }
                    else {
                        ib.setImageResource(idBlackDraught);
                        ib.setTag(idBlackDraught);
                    }
                }
                else if (item.type == Item.ITEM_TYPE.white) {
                    if (item.isKing) {
                        ib.setImageResource(idWhiteKing);
                        ib.setTag(idWhiteKing);
                    }
                    else {
                        ib.setImageResource(idWhiteDraught);
                        ib.setTag(idWhiteDraught);
                    }
                }
                else {
                    ib.setImageResource(R.drawable.black_square);
                    ib.setTag(R.drawable.black_square);
                }
            }
        }
    }

    /** Игрок решил сделать ход/побить другой шашкой или кликнул второй раз не туда.
     * id - идентификатор шашки/клетки, куда кликнул игрок.*/
    public void RepickMove(int id)
    {
        PrepareForNextMove(true);
        if (CheckTurn(id)) {
            if (isMove) {
                if (CheckMove()) {
                    HighlightMoves();
                    isNeedSecondClick = true;
                }
            } else if (isJump) {
                if (CheckJump()) {
                    HighlightMoves();
                    isNeedSecondClick = true;
                }
            }
        }
    }

    public boolean GameOver()
    {
        return (!isJump && !isMove);
    }
}


