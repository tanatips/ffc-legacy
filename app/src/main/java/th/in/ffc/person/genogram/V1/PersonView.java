/*
 * Copyright (C) 2010 Family Folder Collector Project - NECTEC
 * 
 *	PersonView Class [version 1.0]
 *  November 01, 2010  
 *  
 *	Create by  Piruin Panichphol [Blaze]
 *
 * Version Log -----------------------------------------------
 *  
 *  [version 2.0] by Piruin Panichphol
 * 	November 18, 2010
 * 	- can completely represent person by mode 1,2 and 3.
 *  - fix general bug of version 1.0
 *  
 */

package th.in.ffc.person.genogram.V1;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.View;
import th.in.ffc.R;

public class PersonView extends View {

    public static final int MODE_PICTURE = 1;
    public static final int MODE_SYMBOL = 2;
    public static final int MODE_SYMBOL2 = 3;
    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;
    public static final float AGE_SIZE = 0.30f;
    private Person mPerson;
    private Paint mPaint = new Paint();
    private static int selectMode;

    public static int HCode = -1;

    public PersonView(Context context, Person person) {
        super(context);
        SharedPreferences house = context.getSharedPreferences("house", 0);
        this.setFocusable(true);
        int hcode = house.getInt("hcode", -1);
        int symbolMode = house.getInt("SymbolMode", MODE_PICTURE);
        selectMode = symbolMode;

        mPerson = person;
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.FILL);

        setViewBackground(symbolMode, mPerson.getSex(), HCode);
        invalidate();
    }

    public Person getPerson() {
        return mPerson;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPerson.getStatusInFamily() == Person.STATUS_LEADER) {
            drawLeaderSign(canvas);
        }
        if (selectMode == MODE_PICTURE) {
            if (mPerson.isHouseOwner()) {
                drawHouseOwner(canvas);
            }
            if (!mPerson.isAlive()) {
                drawDeadSymbol(canvas);
            }
        }
        drawPersonAge(canvas);
    }

    private void drawLeaderSign(Canvas canvas) {
        int size = getHeight();
        if (selectMode == MODE_SYMBOL || selectMode == MODE_SYMBOL2) {
            int signSize = (int) (size * 0.7f);
            int origin = (size / 2) - (signSize / 2);
            Drawable dr = getResources()
                    .getDrawable(R.drawable.blaze_star_dark);
            dr.setBounds(origin, origin, origin + signSize, origin + signSize);
            dr.draw(canvas);
        } else if (selectMode == MODE_PICTURE) {
            Drawable dr = getResources().getDrawable(
                    R.drawable.pic_leader_hourse_icon);
            dr.setBounds(0, 0, size, size);
            dr.draw(canvas);

        }
    }

    private void drawDeadSymbol(Canvas canvas) {
        int size = getHeight();
        Drawable dr = getResources().getDrawable(R.drawable.pic_die_icon);
        dr.setBounds(0, 0, size, size);
        dr.draw(canvas);
    }

    private void drawHouseOwner(Canvas canvas) {
        int size = getHeight();
        Drawable dr = getResources().getDrawable(
                R.drawable.pic_house_owner_icon);
        dr.setBounds(0, 0, size, size);
        dr.draw(canvas);
    }

    private void drawPersonAge(Canvas canvas) {
        int size = getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setTextSize(size * AGE_SIZE);

        // paint.setTextSize(15.5f);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Align.LEFT);
        int age = mPerson.getAge();
        canvas.drawText((age > -1 ? age : "?") + "", size * 0.1f, size * 0.1f
                + (paint.getTextSize() / 2), paint);
    }

    private void setViewBackground(int mode, int sex, int hcode) {
        if (!mPerson.isAlive()) {
            setDeadPersonBackground(mode, sex, hcode);
        } else {
            if (mPerson.isChronic()) {
                setChronicPersonBackground(mode, sex, hcode);
            } else {
                setPersonBackground(mode, sex, hcode);
            }
        }

    }

    private void setDeadPersonBackground(int mode, int sex, int hcode) {
        switch (sex) {
            case SEX_MALE: {
//			if (mode == MODE_SYMBOL) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.sb1);
//					} else {
//						this.setBackgroundResource(R.drawable.sb2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.sb2);
//				}
//			} else if (mode == MODE_SYMBOL2) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.tb1);
//					} else {
//						this.setBackgroundResource(R.drawable.tb2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.sb2);
//				}
//			} else 
                if (mode == MODE_PICTURE) {
                    if (mPerson.isChronic()) {
                        setChronicPersonBackground(mode, sex, hcode);
                    } else {
                        setPersonBackground(mode, sex, hcode);
                    }
                }
                break;
            }
            case SEX_FEMALE: {
//			if (mode == MODE_SYMBOL || mode == MODE_SYMBOL2) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.ob1);
//					} else {
//						this.setBackgroundResource(R.drawable.ob2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.ob2);
//				}
//			} else
                if (mode == MODE_PICTURE) {
                    if (mPerson.isChronic()) {
                        setChronicPersonBackground(mode, sex, hcode);
                    } else {
                        setPersonBackground(mode, sex, hcode);
                    }
                }
                break;
            }
        }
    }

    private void setChronicPersonBackground(int mode, int sex, int hcode) {
        switch (sex) {
            case SEX_MALE: {
//			if (mode == MODE_SYMBOL) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.sy1);
//					} else {
//						this.setBackgroundResource(R.drawable.sy2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.sy2);
//				}
//			} else if (mode == MODE_SYMBOL2) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.ty1);
//					} else {
//						this.setBackgroundResource(R.drawable.ty2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.ty2);
//				}
//			} else 
                if (mode == MODE_PICTURE) {
                    int age = mPerson.getAge();
                    if (mPerson.getHcode() == hcode) {
                        if (age < 15)
                            this
                                    .setBackgroundResource(R.drawable.pic_boy_sick_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_young_man_sick_icon);
                        else if (age < 60)
                            this
                                    .setBackgroundResource(R.drawable.pic_man_sick_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_grand_father_sick_icon);
                    } else {
                        if (age < 15)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_boy_sick_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_young_man_sick);
                        else if (age < 60)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_man_sick_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_other_grand_father_sick);
                    }
                }
                break;
            }
            case SEX_FEMALE: {
//			if (mode == MODE_SYMBOL || mode == MODE_SYMBOL2) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.oy1);
//					} else {
//						this.setBackgroundResource(R.drawable.oy2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.oy2);
//				}
//			} else 
                if (mode == MODE_PICTURE) {
                    int age = mPerson.getAge();
                    if (mPerson.getHcode() == hcode) {
                        if (age < 15)
                            this
                                    .setBackgroundResource(R.drawable.pic_girl_sick_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_young_woman_sick_icon);
                        else if (age < 60)
                            this
                                    .setBackgroundResource(R.drawable.pic_woman_sick_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_grand_mother_sick);
                    } else {
                        if (age < 15)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_girl_sick_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_young_woman_sick);
                        else if (age < 60)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_woman_sick_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_other_grand_mother_sick);
                    }
                }
                break;
            }
        }
    }

    private void setPersonBackground(int mode, int sex, int hcode) {
        switch (sex) {
            case SEX_MALE: {
//			if (mode == MODE_SYMBOL) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.sg1);
//					} else {
//						this.setBackgroundResource(R.drawable.sg2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.sg2);
//				}
//			} else if (mode == MODE_SYMBOL2) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.tg1);
//					} else {
//						this.setBackgroundResource(R.drawable.tg2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.tg2);
//				}
//			} else
                if (mode == MODE_PICTURE) {
                    int age = mPerson.getAge();
                    if (mPerson.getHcode() == hcode) {
                        if (age < 15)
                            this.setBackgroundResource(R.drawable.pic_boy_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_young_man_icon);
                        else if (age < 60)
                            this.setBackgroundResource(R.drawable.pic_man_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_grand_father_icon);
                    } else {
                        if (age < 15)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_boy_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_young_man_icon);
                        else if (age < 60)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_man_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_other_grand_father_icon);
                    }
                }
                break;
            }
            case SEX_FEMALE: {
//			if (mode == MODE_SYMBOL || mode == MODE_SYMBOL2) {
//				if (hcode != -1) {
//					if (mPerson.getHcode() == hcode) {
//						this.setBackgroundResource(R.drawable.og1);
//					} else {
//						this.setBackgroundResource(R.drawable.og2);
//					}
//				} else {
//					this.setBackgroundResource(R.drawable.og2);
//				}
//			} else
                if (mode == MODE_PICTURE) {
                    int age = mPerson.getAge();
                    if (mPerson.getHcode() == hcode) {
                        if (age < 15)
                            this.setBackgroundResource(R.drawable.pic_girl_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_young_woman_icon);
                        else if (age < 60)
                            this.setBackgroundResource(R.drawable.pic_woman_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_grand_mother_icon);
                    } else {
                        if (age < 15)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_girl_icon);
                        else if (age < 26)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_young_woman_icon);
                        else if (age < 60)
                            this
                                    .setBackgroundResource(R.drawable.pic_other_woman_icon);
                        else
                            this
                                    .setBackgroundResource(R.drawable.pic_other_grand_mother_icon);
                    }
                }
                break;
            }
        }
    }

}
