/*
 * Copyright (C) 2010 Family Folder Collector Project - NECTEC
 * 
 *	RelationLineView Class [version 1.0]
 *  November 09, 2010  
 *  
 *
 *	Create by  Piruin Panichphol [Blaze]
 */

package th.in.ffc.person.genogram.V1;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RelationLineView extends View {

    private static final int lineTheshold = 5;

    public static final int STATUS_SINGLE = 1;
    public static final int STATUS_MARRY = 2;
    public static final int STATUS_MATE_DIED = 3;
    public static final int STATUS_BROKE_UP = 4;
    public static final int STATUS_SUPARATE = 5;
    public static final int STATUS_TEMPLE_MAN = 6;
    public static final int STATUS_UNKNOWN = 9;
    public static final int STATUS_MARRY_NOT_RECORD = 10;

    public static final float PERSON_NAME_SIZE = 16f;
    public static final int PERSON_NAME_COLOR = 0xFF000000;

    private Paint mPaint;
    private Paint bPaint;

    private List<Coordinate> parentLine = new ArrayList<Coordinate>();
    private List<Coordinate> childLine = new ArrayList<Coordinate>();
    private List<Integer> marryLine = new ArrayList<Integer>();
    private List<String> name = new ArrayList<String>();
    private List<Coordinate> nameLocation = new ArrayList<Coordinate>();

    public RelationLineView(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(0x88000000);
        mPaint.setStrokeWidth(6);
        bPaint = new Paint();
        bPaint.setAntiAlias(true);
        bPaint.setStyle(Style.STROKE);
        bPaint.setColor(0xAA000000);
        bPaint.setStrokeWidth(6);
        // mPaint.setShadowLayer(1, -1, 1, 0x88333333);


    }

    public void addParentLine(Coordinate parent, Coordinate child) {
        parentLine.add(parent);
        parentLine.add(child);
        invalidate();
    }

    public void addParentLine(Coordinate p1, Coordinate p2, Coordinate child) {

        if ((p1 != null) && (p2 != null) && (child != null)) {
            if (p1.X < p2.X) {
                childLine.add(p1);
                childLine.add(p2);
            } else {
                childLine.add(p2);
                childLine.add(p1);
            }
            childLine.add(child);
            invalidate();
        }
    }

    public void addMarryLine(String marryStatus, int x1, int x2, int y) {
        if (marryStatus != null) {
            try {
                marryLine.add(Integer.parseInt(marryStatus.trim()));
            } catch (Exception ex) {
                if (marryStatus == "f") {
                    marryLine.add(STATUS_MARRY_NOT_RECORD);
                } else {
                    marryLine.add(STATUS_UNKNOWN);
                }
            }
        } else {
            marryLine.add(STATUS_UNKNOWN);
        }
        marryLine.add(x1);
        marryLine.add(x2);
        marryLine.add(y);
        invalidate();
    }

    public void addName(String name, Coordinate location) {
        this.name.add(name);
        this.nameLocation.add(location);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas.drawColor(Color.GRAY);
        // canvas.drawColor(Color.GRAY);
        drawParentLine(canvas);
        drawMarryLine(canvas);
        drawChildLine(canvas);
        drawPersonName(canvas);
    }

    private void drawChildLine(Canvas canvas) {
        if (!childLine.isEmpty()) {
            Path path = new Path();
            int hStrokeWidth = (int) mPaint.getStrokeWidth() / 2;
            List<Coordinate> line = childLine;
            for (int i = 0; i < line.size(); i = i + 3) {
                try {
                    //father
                    Coordinate f = line.get(i);
                    //mother
                    Coordinate m = line.get(i + 1);
                    //child
                    Coordinate c = line.get(i + 2);

                    float midY = f.Y + ((c.Y - f.Y) * 0.6f);
                    float midX = f.X + ((m.X - f.X) * 0.5f);

                    // Start Paint the path
                    path.moveTo(midX, f.Y + hStrokeWidth);
                    if (((c.X - midX) > -lineTheshold)
                            && ((c.X - midX) < lineTheshold)) {
                        path.lineTo(c.X, c.Y);
                    } else {
                        path.lineTo(midX, midY);
                        path.lineTo(c.X, midY);
                        path.lineTo(c.X, c.Y);
                    }
                } catch (Exception ex) {
                    continue;
                }
            }
            canvas.drawPath(path, mPaint);
        }

    }

    private void drawParentLine(Canvas canvas) {
        if (!parentLine.isEmpty()) {
            Path path = new Path();
            List<Coordinate> line = parentLine;
            for (int i = 0; i < line.size(); i = i + 2) {
                try {
                    Coordinate p = line.get(i);
                    Coordinate c = line.get(i + 1);
                    float midY = p.Y + ((c.Y - p.Y) * 0.5f);
                    path.moveTo(p.X, p.Y);
                    path.lineTo(p.X, midY);
                    path.lineTo(c.X, midY);
                    path.lineTo(c.X, c.Y);

                } catch (Exception ex) {
                    continue;
                }
            }
            canvas.drawPath(path, mPaint);
        }
    }

    private void drawMarryLine(Canvas canvas) {
        if (!marryLine.isEmpty()) {
            Paint paint = new Paint(mPaint);

            List<Integer> line = marryLine;
            for (int i = 0; i < line.size(); i = i + 4) {
                try {
                    Path path = new Path();
                    int x1, x2, y = line.get(i + 3);
                    if ((line.get(i + 1) - line.get(i + 2)) < 0) {
                        x1 = line.get(i + 1);
                        x2 = line.get(i + 2);
                    } else {
                        x1 = line.get(i + 2);
                        x2 = line.get(i + 1);
                    }
                    int marryStatus = line.get(i);
                    int dx = x2 - x1;
                    // draw marry line
                    path.moveTo(x1, y);
                    path.lineTo(x2, y);

                    switch (marryStatus) {
                        case STATUS_BROKE_UP:
                            // draw double slash
                            Path brokePath = new Path();
                            brokePath.moveTo(x1 + (dx * 0.35f), y + dx * 0.13f);
                            brokePath.lineTo(x1 + (dx * 0.55f), y - dx * 0.13f);
                            brokePath.moveTo(x1 + (dx * 0.45f), y + dx * 0.13f);
                            brokePath.lineTo(x1 + (dx * 0.65f), y - dx * 0.13f);
                            canvas.drawPath(brokePath, bPaint);
                            break;
                        case STATUS_SUPARATE:
                            // draw single slash
                            Path supPath = new Path();
                            supPath.moveTo(x1 + (dx * 0.40f), y + dx * 0.13f);
                            supPath.lineTo(x1 + (dx * 0.60f), y - dx * 0.13f);
                            canvas.drawPath(supPath, bPaint);
                            break;
                        case STATUS_UNKNOWN:
                        case STATUS_MARRY_NOT_RECORD:
                            //change marry line's effect --- --- --- --- --- ---
                            PathEffect effect = new DashPathEffect(new float[]{15,
                                    5, 15, 5}, 0);
                            paint.setPathEffect(effect);

                            break;
                    }
                    canvas.drawPath(path, paint);
                    paint.setPathEffect(null);

                } catch (Exception ex) {
                    continue;
                }
            }

        }
    }

    private void drawPersonName(Canvas canvas) {
        int index;
        int size;
        if ((size = name.size()) > 0) {
            Paint paint = new Paint();
            // paint.setStrokeWidth(0.5f);
            paint.setColor(PERSON_NAME_COLOR);
            paint.setAntiAlias(true);
            // paint.setFakeBoldText(true);
            // paint.setTypeface(null);
            paint.setTextAlign(Align.CENTER);
            paint.setTextSize(PERSON_NAME_SIZE);
            for (index = 0; index < size; index++) {
                Coordinate c = nameLocation.get(index);
                canvas.drawText(name.get(index), c.X, c.Y + 15f, paint);
            }
        }
    }
}
