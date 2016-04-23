/*
 * Copyright (C) 2010 Family Folder Collector Project - NECTEC
 * 
 *	FamilyTreePainter Class [version 1.0]
 *  November 09, 2010  
 *  
 *  Create by  Piruin Panichphol [Blaze]
 *
 *  
 *  Version Log ----------------------------------------------- 
 *  															
 *  [version 1.0b]  by Piruin Panichphol
 * 	November 09, 2010
 * 	- initialPaintingScale() แยกการใช้พื่นในการวาดผังเครือญาติ ระหว่างแบบแนวตั้ง กับแนวนอน 
 *  - drawPersonname() เพิ่มการ�th.in.ffc.person.genogram.V1าวสูงสุดขอชื่อที่จะแสดงทั้ง ชื่อและนานสกุล
 *  - findOrderToPaint() เพิ่มสถานะที่ใช้ในการวาดผังเครือญาติ
 *	
 */

package th.in.ffc.person.genogram.V1;

import android.content.Context;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class FamilyTreePainter {

    private Context mContext;
    private Family mFamily;
    private AbsoluteLayout mDrawingLayout;
    private RelationLineView mRelationLine;
    private int mDrawingAreaWidth;
    private int mDrawingAreaHeight;
    private int mDrawingAreaHalfWidth;

    private Map<Integer, Measure> mInfoToDraw;
    private Map<Integer, Coordinate> mPosition = new HashMap<Integer, Coordinate>(); // mapping

    private final static float USING_AREA_HEIGHT_LAND = 0.9f;
    private final static float USING_AREA_WIDTH_LAND = 0.9f;
    private final static float USING_AREA_HEIGHT_PORT = 0.8f;
    private final static float USING_AREA_WIDTH_PORT = 0.9f;

    private final static int MAX_FULLNAME = 5;
    private final static int MAX_FULLNAME_LENGHT = 20;
    private final static int MAX_SHOW_NAME = 7;

    private final static float SYMBOL_SPACE = 1.75f;
    private final static float SYMBOL_SIZE_ = 0.8f;
    private final static int MAX_SYMBOL_SIZE = 150;
    public static int SPACE_COUPLE = 100;
    public static int SPACE_SIBLING = 100;
    public static int SPACE_GENERATION = 100;

    public FamilyTreePainter(Context context, Family FamilyToPaint,
                             AbsoluteLayout drawingLayout, int drawingLayoutWidth,
                             int drawingLayoutHeight) {

        mContext = context;
        mFamily = FamilyToPaint;
        mDrawingLayout = drawingLayout;
        mDrawingAreaHeight = drawingLayoutHeight;
        mDrawingAreaWidth = drawingLayoutWidth;
        mDrawingAreaHalfWidth = mDrawingAreaWidth / 2;

        mRelationLine = new RelationLineView(mContext);
        mDrawingLayout.addView(mRelationLine, new AbsoluteLayout.LayoutParams(
                mDrawingAreaWidth, mDrawingAreaHeight, 0, 0));
        // mRelationLine.setBackgroundResource(R.drawable.icon);

    }

    public void setOnClickListenerToPersonView(OnClickListener onClickListener) {
        AbsoluteLayout ab = mDrawingLayout;
        for (int i = 1; i < ab.getChildCount(); i++) {
            try {
                PersonView pv = (PersonView) ab.getChildAt(i);
                pv.setOnClickListener(onClickListener);
            } catch (Exception ex) {
                continue;
            }
        }
    }

    public void startPainting() {
        initialPaintingScale();
        Log.d("Painting", mFamily.toString());
        for (int genearation : mInfoToDraw.keySet()) {
            paintTheTree(genearation, mInfoToDraw.get(genearation));
        }
    }

    private void paintTheTree(int generation, Measure m) {
        int[] orderBy;
        int symbolSize;
        int halfSize;
        int index;
        int y;
        int[] x;
        List<Person> family = mFamily.getAllMember();
        // Do it if family member > 0
        if (family != null && m != null) {
            // initailize local variable
            orderBy = findOrderForPaint(generation);
            symbolSize = m.iSymbolSize;
            halfSize = symbolSize / 2;
            y = m.iCenterVertical;
            x = m.iMarker;
            index = 0;
            System.err.println("paint at x=" + Arrays.toString(x) + " y=" + y
                    + " size=" + symbolSize);
            for (int status : orderBy) {
                for (Person p : family) {
                    Person q = p;
                    try {
                        // Find Person by Order and draw him/her
                        if (p.getStatusInFamily() == status
                                && !isAlreadyDraw(p.getPid())) {
                            // Adding PersonView to DrawingLayout
                            System.err.println("@painting p where pid="
                                    + p.getPid() + " at x=" + index);
                            mDrawingLayout.addView(new PersonView(mContext, p),
                                    new AbsoluteLayout.LayoutParams(symbolSize,
                                            symbolSize, x[index] - halfSize, y
                                            - halfSize));
                            // record it coordinate
                            mPosition.put(p.getPid(), new Coordinate(x[index], y));

                            drawPersoname(p, m, index, x, y, halfSize);
                            // next drawing target to next X
                            index++;

                            // has any Mate ?
                            if (p.isFoundMate()) {
                                q = mFamily.getPersonByIdCard(p.getMateID());
                                if (q != null) {
                                    if (q.getStatusInFamily() != Person.STATUS_UNKNOWN) {
                                        // Adding PersonView to DrawingLayout
                                        System.err
                                                .println("@painting q where pid="
                                                        + q.getPid() + " at index="
                                                        + index);
                                        mDrawingLayout.addView(new PersonView(
                                                        mContext, q),
                                                new AbsoluteLayout.LayoutParams(
                                                        symbolSize, symbolSize,
                                                        x[index] - halfSize, y
                                                        - halfSize));
                                        // record it coordinate
                                        mPosition.put(q.getPid(), new Coordinate(
                                                x[index], y));
                                        drawPersoname(q, m, index, x, y, halfSize);
                                        // draw Marry Line
                                        mRelationLine.addMarryLine(p
                                                        .getMarryStatus(), x[index - 1],
                                                x[index], y);
                                        // next drawing target to next X
                                        index++;
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("Painter", "paint p status=" + p.getStatusInFamily() + "; q status =" + q.getStatusInFamily());
                        ex.printStackTrace();
                        break;
                    }
                }
            }
            drawParentLine();

        }
    }

    private void drawParentLine() {
        // draw Parent line
        for (int pid : mPosition.keySet()) {
            try {
                System.err.println(" " + pid);
                Person p = mFamily.getPersonByPid(pid);
                if (p != null) {
                    if (p.isFoundParent()) {
                        mRelationLine.addParentLine(mPosition.get(p
                                .getFatherPID()), mPosition.get(p
                                .getMotherPID()), mPosition.get(p.getPid()));
                    } else {
                        if (p.isFoundFather()) {
                            mRelationLine
                                    .addParentLine(mPosition.get(p
                                            .getFatherPID()), mPosition.get(p
                                            .getPid()));
                        } else if (p.isFoundMother()) {
                            mRelationLine
                                    .addParentLine(mPosition.get(p
                                            .getMotherPID()), mPosition.get(p
                                            .getPid()));
                        } else {
                            // Do nothing
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }

    // private boolean isAlreadyDraw(String idCard) {
    // Coordinate c = mPosition.get(idCard);
    // if (c == null) {
    // return false;
    // } else {
    // return true;
    // }
    // }

    private void drawPersoname(Person p, Measure m, int index, int[] x, int y,
                               int halfSize) {
        if (mDrawingAreaWidth > mDrawingAreaHeight) {
            // Orentation Horizental
            if (m.iCountMember <= MAX_FULLNAME) {
                if ((m.iCountMember > MAX_FULLNAME - 1)
                        && (p.getFullName().length() >= MAX_FULLNAME_LENGHT))
                    mRelationLine.addName(p.getFirstName(), new Coordinate(
                            x[index], y + halfSize));
                else
                    mRelationLine.addName(p.getFirstName(), new Coordinate(
                            x[index], y + halfSize));

            } else if (m.iCountMember <= MAX_SHOW_NAME) {
                mRelationLine.addName(p.getFirstName(), new Coordinate(
                        x[index], y + halfSize));
            }
        } else {
            // Orentation Vertical
            if (m.iCountMember <= MAX_FULLNAME - 2) {
                if ((m.iCountMember != MAX_FULLNAME)
                        && (p.getFullName().length() <= MAX_FULLNAME_LENGHT))
                    mRelationLine.addName(p.getFirstName(), new Coordinate(
                            x[index], y + halfSize));
                else
                    mRelationLine.addName(p.getFirstName(), new Coordinate(
                            x[index], y + halfSize));
            } else if (m.iCountMember <= MAX_SHOW_NAME - 2) {
                mRelationLine.addName(p.getFirstName(), new Coordinate(
                        x[index], y + halfSize));
            }
        }
    }

    private boolean isAlreadyDraw(int pid) {
        Coordinate c = mPosition.get(pid);
        if (c == null) {
            return false;
        } else {
            return true;
        }
    }

    private int[] findOrderForPaint(int generation) {
        int[] orderBy;
        switch (generation) {
            case -1:
                orderBy = new int[]{Person.STATUS_LEADER_GRANDPARENT,
                        Person.STATUS_MATE_GRANDPARENT};
                break;
            case 0:
                orderBy = new int[]{Person.STATUS_UNKNOWN};
                break;
            case 1:
                orderBy = new int[]{Person.STATUS_LEADER_PARENT_SIBLING,
                        Person.STATUS_LEADER_PARENT, Person.STATUS_MATE_PARENT,
                        Person.STATUS_MATE_PARENT_SIBLING};
                break;
            case 2:
                orderBy = new int[]{Person.STATUS_LEADER_SIBLING,
                        Person.STATUS_LEADER, Person.STATUS_MATE,
                        Person.STATUS_MATE_SIBLING};
                break;
            case 3:
                orderBy = new int[]{Person.STATUS_LEADER_SIBLING_CHILD,
                        Person.STATUS_LEADER_CHILD, Person.STATUS_COUPLE_CHILD,
                        Person.STATUS_MATE_CHILD, Person.STATUS_MATE_SIBLING_CHILD};
                break;
            case 4:
                orderBy = new int[]{Person.STATUS_LEADER_SIBLING_GRANDCHILD,
                        Person.STATUS_LEADER_GRANDCHILD,
                        Person.STATUS_COUPLE_GRANDCHILD,
                        Person.STATUS_MATE_GRANDCHILD,
                        Person.STATUS_MATE_SIBLING_GRANDCHILD};
                break;
            case 5:
                orderBy = new int[]{
                        Person.STATUS_LEADER_SIBLING_GREAT_GRANDCHILD,
                        Person.STATUS_LEADER_GREAT_GRANDCHILD,
                        Person.STATUS_COUPLE_GREAT_GRANDCHILD,
                        Person.STATUS_MATE_GREAT_GRANDCHILD,
                        Person.STATUS_MATE_SIBLING_GREAT_GRANDCHILD};
                break;
            default:
                orderBy = null;
        }
        return orderBy;
    }

    private void initialPaintingScale() {
        float height;
        float width;
        if (mDrawingAreaWidth > mDrawingAreaHeight) {
            // land
            height = USING_AREA_HEIGHT_LAND;
            width = USING_AREA_WIDTH_LAND;
        } else {
            // port
            height = USING_AREA_HEIGHT_PORT;
            width = USING_AREA_WIDTH_PORT;
        }
        // มีกี่รุ่น?
        Map<Integer, Integer> generation = checkGeneration();
        int generationSpace = (int) ((mDrawingAreaHeight * height) / generation
                .size());
        int borderSpace = (int) (mDrawingAreaHeight * 0.05f);
        int mSymbolVisibleArea = (int) (mDrawingAreaWidth * width);

        // ขนาดหลักของสัญลักษณ์
        int mSymbolSize = (int) (generationSpace * SYMBOL_SIZE_);
        if (mSymbolSize > MAX_SYMBOL_SIZE) {
            mSymbolSize = MAX_SYMBOL_SIZE;
        }
        int mSymbolSpace = (int) (mSymbolSize * SYMBOL_SPACE);

        Map<Integer, Measure> forDraw = new HashMap<Integer, Measure>();
        // ขนาดสัญลักษณ์ของแต่ละรุ่นตามความหนาแน่น
        int index = 1;
        System.out.println("@initialPaintinScale key Size="
                + generation.keySet().size());

        for (int i = -1; i < 6; i++) {
            if (i != 0) {
                Integer count = generation.get(i);
                if (count != null) {
                    // System.err.println("count = "+count );
                    int space = mSymbolSpace;
                    int size = mSymbolSize;
                    int left;
                    int CenterVertical;

                    // find best symbol size
                    while ((space * count) > (mSymbolVisibleArea - size)) {
                        size *= 0.95f;
                        space = (int) (size * SYMBOL_SPACE);
                    }
                    // findCenterVertical
                    CenterVertical = ((index * generationSpace) + (borderSpace))
                            - (generationSpace / 2);
                    CenterVertical += ((mSymbolSize - size) / 2);

                    // find Leftest symbolPosition
                    if ((count % 2) == 0) {
                        left = (int) (mDrawingAreaHalfWidth - (space * ((count / 2) - 0.5)));
                    } else {
                        left = (mDrawingAreaHalfWidth - (space * ((count - 1) / 2)));
                    }
                    // marker each position to draw;
                    index++;
                    forDraw.put(i, new Measure(CenterVertical, size, space,
                            count, left));
                }
            }
        }

        System.err.println("forDraw size=" + forDraw.size());
        for (int x : forDraw.keySet()) {
            System.err.print(x + "generation : ");
            System.err.println(forDraw.get(x).toString());
        }
        mInfoToDraw = forDraw;
    }

    private Map<Integer, Integer> checkGeneration() {
        Map<Integer, Integer> genCount = new HashMap<Integer, Integer>();
        for (int i = -1; i <= 5; i++) {
            if (i == 0) {
                continue;
            } else {
                int count = mFamily.countGeneration(i);
                if (count > 0) {
                    System.err.println(i + "Generation has " + count
                            + " member");
                    genCount.put(i, count);
                }
            }
        }
        return genCount;
    }

    @Override
    public String toString() {
        return "FamilyTreePainter " + "  mDrawingAreaHeight="
                + mDrawingAreaHeight + ", mDrawingAreaWidth="
                + mDrawingAreaWidth;
    }

    private class Measure {

        public int iCenterVertical;
        public int iSymbolSize;
        public int iSymbolSpace;
        public int iCountMember;
        public int[] iMarker;

        public Measure(int centerVertical, int symbolSize, int symbolspace,
                       int countMember, int leftPosition) {
            iCenterVertical = centerVertical;
            iSymbolSize = symbolSize;
            iSymbolSpace = symbolspace;
            iCountMember = countMember;
            iMarker = new int[iCountMember];
            for (int i = 0; i < iCountMember; i++)
                iMarker[i] = leftPosition + (iSymbolSpace * (i));
        }

        @Override
        public String toString() {
            return "information [iCenterVertical=" + iCenterVertical
                    + ", iCountMember=" + iCountMember + ", iMarker="
                    + Arrays.toString(iMarker) + ", iSymbolSize=" + iSymbolSize
                    + ", iSymbolSpace=" + iSymbolSpace + "]";
        }

    }
}
