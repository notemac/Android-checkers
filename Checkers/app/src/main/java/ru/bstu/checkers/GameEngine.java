package ru.bstu.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.bstu.checkers.Item.ITEM_TYPE;

// https://habr.com/post/227911/ - Как я шашки писал

public class GameEngine {
   enum TURN {black, white};
   /** Item, на который кликнули */
   public static Item selectedItem = null;
   /** Надо бить? (Бить обязательно)*/
   public static boolean isJump = false;
    /** Есть куда ходить? (Если нечего бить)*/
    public static boolean isMove = false;
    /** Шашка выбрана, необходимо сделать ход/побить (т.е. клинуть второй раз)*/
    public static boolean isNeedSecondClick = false;
   /** Очередь хода*/
   public static ITEM_TYPE turn = ITEM_TYPE.white;
   /** Массив диагоналей (ways). В каждом way[i] хранятся шашки и пустые клетки, лежащие на этой диагонали*/
   public static ArrayList<Item>[] ways;
   /** Количество шашек одного цвета*/
   public static int DRAUGHTS_COUNT = 12;
   /** Массив из списков ходов. Т.е. куда может пойти шашка. Первый элемент списка
    * всегда шашка; все остальные элементы - клетки, куда может пойти эта шашка.*/
   public static ArrayList<LinkedList<Item>> moves;
   /** Список из списков с ходами, куда шашка может побить. Первый элемент списка всегда бьющая шашка;
    *  второй - шашка, которая под боем; все остальные элементы - клетки, где может оказаться бьющая шашка после битья.*/
   public static LinkedList<LinkedList<Item>> jumps;

   public static void Init() {
        moves = new ArrayList<LinkedList<Item>>(DRAUGHTS_COUNT);
        jumps = new LinkedList<LinkedList<Item>>();
        Item[] items = new Item[DRAUGHTS_COUNT*2 + 4*6 + 16];// 64 клетки
        items[0] = new Item(R.id.a8, ITEM_TYPE.square, false, false,false, false,
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

        items[8] = new Item(R.id.a7, ITEM_TYPE.black, true, false,false, true,
                false, false, false, false, false,
                false, false, false, false); //A7
        items[9] = new Item(R.id.b7, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //B7
        items[10] = new Item(R.id.c7, ITEM_TYPE.black, false, true,false, false,
                false, false, false, false, true,
                false, false, false, false); //C7
        items[11] = new Item(R.id.d7, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //D7
        items[12] = new Item(R.id.e7, ITEM_TYPE.black, false, false,false, false,
                false, false, false, true, false,
                true, false, false, false); //E7
        items[13] = new Item(R.id.f7, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //F7
        items[14] = new Item(R.id.g7, ITEM_TYPE.black, false, false,false, false,
                false, false, true, false, false,
                false, false, false, true); //G7
        items[15] = new Item(R.id.h7, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //H7

        items[16] = new Item(R.id.a6, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //A6
        items[17] = new Item(R.id.b6, ITEM_TYPE.black, true, false,false, false,
                false, false, false, false, true,
                false, false, false, false); //B6
        items[18] = new Item(R.id.c6, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //C6
        items[19] = new Item(R.id.d6, ITEM_TYPE.black, false, true,false, false,
                false, false, false, true, false,
                false, false, false, false); //D6
        items[20] = new Item(R.id.e6, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //E6
        items[21] = new Item(R.id.f6, ITEM_TYPE.black, false, false,false, false,
                false, false, false, false, false,
                true, false, false, true); //F6
        items[22] = new Item(R.id.g6, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //G6
        items[23] = new Item(R.id.h6, ITEM_TYPE.black, false, false,false, false,
                false, true, true, false, false,
                false, false, false, false); //H6

        items[24] = new Item(R.id.a5, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, true,
                false, true, false, false); //A5
        items[25] = new Item(R.id.b5, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //B5
        items[26] = new Item(R.id.c5, ITEM_TYPE.square, true, false,false, false,
                false, false, false, true, false,
                false, false, false, false); //C5
        items[27] = new Item(R.id.d5, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //D5
        items[28] = new Item(R.id.e5, ITEM_TYPE.square, false, true,false, false,
                false, false, false, false, false,
                false, false, false, true); //E5
        items[29] = new Item(R.id.f5, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //F5
        items[30] = new Item(R.id.g5, ITEM_TYPE.square, false, false,false, false,
                false, true, false, false, false,
                true, false, false, false); //G5
        items[31] = new Item(R.id.h5, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //H5

        items[32] = new Item(R.id.a4, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //A4
        items[33] = new Item(R.id.b4, ITEM_TYPE.square, false, false,false, false,
                false, false, false, true, false,
                false, true, false, false); //B4
        items[34] = new Item(R.id.c4, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //C4
        items[35] = new Item(R.id.d4, ITEM_TYPE.square, true, false,false, false,
                false, false, false, false, false,
                false, false, false, true); //D4
        items[36] = new Item(R.id.e4, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //E4
        items[37] = new Item(R.id.f4, ITEM_TYPE.square, false, true,false, false,
                false, true, false, false, false,
                false, false, false, false); //F4
        items[38] = new Item(R.id.g4, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //G4
        items[39] = new Item(R.id.h4, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                true, false, true, false); //H4

        items[40] = new Item(R.id.a3, ITEM_TYPE.white, false, false,false, false,
                true, false, false, true, false,
                false, false, false, false); //A3
        items[41] = new Item(R.id.b3, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //B3
        items[42] = new Item(R.id.c3, ITEM_TYPE.white, false, false,false, false,
                false, false, false, false, false,
                false, true, false, true); //C3
        items[43] = new Item(R.id.d3, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //D3
        items[44] = new Item(R.id.e3, ITEM_TYPE.white, true, false,false, false,
                false, true, false, false, false,
                false, false, false, false); //E3
        items[45] = new Item(R.id.f3, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //F3
        items[46] = new Item(R.id.g3, ITEM_TYPE.white, false, true,false, false,
                false, false, false, false, false,
                false, false, true, false); //G3
        items[47] = new Item(R.id.h3, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //H3

        items[48] = new Item(R.id.a2, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //A2
        items[49] = new Item(R.id.b2, ITEM_TYPE.white, false, false,false, false,
                true, false, false, false, false,
                false, false, false, true); //B2
        items[50] = new Item(R.id.c3, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //C2
        items[51] = new Item(R.id.d2, ITEM_TYPE.white, false, false,false, false,
                false, true, false, false, false,
                false, true, false, false); //D2
        items[52] = new Item(R.id.e2, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //E2
        items[53] = new Item(R.id.f2, ITEM_TYPE.white, true, false,false, false,
                false, false, false, false, false,
                false, false, true, false); //F2
        items[54] = new Item(R.id.g2, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //G2
        items[55] = new Item(R.id.h2, ITEM_TYPE.white, false, true,true, false,
                false, false, false, false, false,
                false, false, false, false); //H2

        items[56] = new Item(R.id.a1, ITEM_TYPE.white, false, false,false, false,
                false, false, false, false, false,
                false, false, false, true); //A1
        items[57] = new Item(R.id.b1, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //B1
        items[58] = new Item(R.id.c1, ITEM_TYPE.white, false, false,false, false,
                true, true, false, false, false,
                false, false, false, false); //C1
        items[59] = new Item(R.id.d1, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //D1
        items[60] = new Item(R.id.e1, ITEM_TYPE.white, false, false,false, false,
                false, false, false, false, false,
                false, true, true, false); //E1
        items[61] = new Item(R.id.f1, ITEM_TYPE.square, false, false,false, false,
                false, false, false, false, false,
                false, false, false, false); //F1
        items[62] = new Item(R.id.g1, ITEM_TYPE.white, true, false,true, false,
                false, false, false, false, false,
                false, false, false, false); //G1
        items[63] = new Item(R.id.h1, ITEM_TYPE.square, false, false,false, false,
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

   /** Добавить возможный ход-move для шашки-item */
   private static void AddMove(Item item, Item move)
   {
       for (int i = 0; i < moves.size(); ++i)
       {
           LinkedList<Item> items = moves.get(i);
           if (items.contains(item))
           {
               items.add(move);
               return;
           }
       }
       LinkedList<Item> items = new LinkedList<Item>();
       items.add(item); items.add(move);
       moves.add(items);
   }

   /** Добавить возможный ход-move для шашки-itemJump после взятия шашки-itemOff */
   private static void AddJump(Item itemJump, Item itemOff, Item move)
   {
        for (int i = 0; i < jumps.size(); ++i)
        {
            LinkedList<Item> items = jumps.get(i);
            if (items.contains(itemJump) && items.contains(itemOff))
            {
                items.add(move);
                return;
            }
        }
        LinkedList<Item> items = new LinkedList<Item>();
        items.add(itemJump); items.add(itemOff); items.add(move);
        jumps.add(items);
    }

   /**Проверяем, надо ли бить*/
   public static void SearchForMoves()
   {
       isJump = isMove = false;
       for(int i = 0; i < ways.length; ++i)
       {
           //Проходим по текущей диагонали
           int way_size = ways[i].size();
           for(int j = 0; j < way_size; ++j)
           {
               ITEM_TYPE itemType = ways[i].get(j).type;
                //Если цвет БЕЛОЙ шашки(1) совпадает с очередью хода
                if ((itemType == turn) && (itemType == ITEM_TYPE.white))
                {
                    // Ищем комбинацию «шашка(1) - шашка (2) - пустое поле»
                    if (j+1 != way_size)// На диагонали после текущей шашки(1) имеются еще шашки или клетки?
                    {
                        ITEM_TYPE nextItemType = ways[i].get(j+1).type;
                        // Далее находится шашка(2)?
                        if ((itemType != nextItemType) && (nextItemType != ITEM_TYPE.square))
                        {
                            // После шашки(2) находится клетка?
                            if ((j+2 != way_size) && (ways[i].get(j+2).type == ITEM_TYPE.square))
                            {
                                AddJump(ways[i].get(j), ways[i].get(j+1), ways[i].get(j+2));
                                isJump = true; isMove = false;
                            }
                        }
                        // Иначе далее находится клетка?
                        else if (nextItemType == ITEM_TYPE.square)
                        {
                            // Добавляем возможные ходы, только если не надо бить
                            if (isJump == false) {
                                AddMove(ways[i].get(j), ways[i].get(j + 1));
                                isMove = true;
                            }
                        }
                    }
                    // Ищем комбинацию «пустое поле — шашка(2) — шашка (1)»
                    else if (j-1 >= 0)// На диагонали перед текущей шашкой(1) имеются еще шашки или клетки?
                    {
                        ITEM_TYPE prevItemType = ways[i].get(j-1).type;
                        // Сзади находится шашка(2)?
                        if ((itemType != prevItemType) && (prevItemType != ITEM_TYPE.square))
                        {
                            // Перед шашкой(2) находится клетка?
                            if ((j-2 >= 0) && (ways[i].get(j-2).type == ITEM_TYPE.square))
                            {
                                AddJump(ways[i].get(j), ways[i].get(j-1), ways[i].get(j-2));
                                isJump = true; isMove = false;
                            }
                        }
                        // Иначе сзади находится клетка?
                        else if (prevItemType == ITEM_TYPE.square)
                        {
                            //TODO: только дамки могут ходить назад для белых
                            //AddMove(ways[i].get(j), ways[i].get(j-1));
                        }
                    }
                }
                //TODO: else { //Если цвет ЧЕРНОЙ шашки(1) совпадает с очередью хода}
           }
       }
   }

    /** Игрок кликнул на свою шашку? Если да, запомним ее как selectedItem*/
    public static boolean CheckTurn(int id)
    {
        for (int i = 0; i < ways.length; ++i)
        {
            int way_size = ways[i].size();
            for (int j = 0; j < way_size; ++j)
            {
                Item item = ways[i].get(j);
                if (item.id == id)
                {
                    if (item.type == turn)
                    {
                        selectedItem = item;
                        return true;
                    }
                    else return false;
                }
            }
        }
        // Сюда никогда не попадем
        return false;
    }

    /** Выбранная шашка selectedItem может бить?*/
    public static boolean CheckJump()
    {
        for (int i = 0; i < jumps.size(); ++i)
        {
            if (jumps.get(i).get(0).id == selectedItem.id)
                return true;
        }
        return false;
    }

    /** Выбранная шашка selectedItem может ходить?*/
    public static boolean CheckMove()
    {
        for (int i = 0; i < moves.size(); ++i)
        {
            if (moves.get(i).get(0).id == selectedItem.id)
                return true;
        }
        return false;
    }

    /** Можем сделать ход выбранной шашкой selectedItem на клетку с идентификатором id?*/
    public static boolean Move(int id)
    {
        for (int i = 0; i < moves.size(); ++i)
        {
            LinkedList<Item> items = moves.get(i);
            if (items.get(0).id == selectedItem.id)
            {
                for (int j = 1; j < items.size(); ++j)
                {
                    if (items.get(j).id == id)
                        return true;
                }
                return false;
            }
        }
        // Сюда никогда не попадем
        return false;
    }
}
