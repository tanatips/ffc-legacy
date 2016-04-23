/*
 * Copyright (C) 2010 Family Folder Collector Project - NECTEC
 * 
 *	Person Class [version 1.0]
 *  November 09, 2010  
 *  
 *
 *	Create by  Piruin Panichphol [Blaze]
 *
 *---------------Version log---------------------------------
 *	[version 1.0b]
 *	November 21, 2010
 *	- add getCitizenID() method that call parseCitizenID for parse 'idcard' to really use pattern
 *  - fix findFather/Mother() method bug 
 */

package th.in.ffc.person.genogram.V1;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import th.in.ffc.provider.PersonProvider.Chronic;
import th.in.ffc.provider.PersonProvider.Death;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.DateTime.Date;

import java.util.ArrayList;
import java.util.List;

public class Person {
    // Constant Variable PERSON'S STATUS ON FAMILY
    // ROOT
    public static final int STATUS_LEADER_GRANDPARENT = -1;
    public static final int STATUS_MATE_GRANDPARENT = -2;
    // FIRST GENERATION
    public static final int STATUS_LEADER_PARENT = 1;
    public static final int STATUS_LEADER_PARENT_SIBLING = 3;
    public static final int STATUS_MATE_PARENT = 2;
    public static final int STATUS_MATE_PARENT_SIBLING = 4;
    // SECOND GENERATION
    public static final int STATUS_LEADER = 11;
    public static final int STATUS_LEADER_SIBLING = 13;
    public static final int STATUS_LEADER_SIBLING_MATE = 15;
    public static final int STATUS_LEADER_SIBLING_SAME_FATHER = 17;
    public static final int STATUS_LEADER_SIBLING_SAME_MOTHER = 19;
    public static final int STATUS_MATE = 12;
    public static final int STATUS_MATE_SIBLING = 14;
    public static final int STATUS_MATE_SIBLING_MATE = 16;
    public static final int STATUS_MATE_SIBLING_SAME_FATHER = 18;
    public static final int STATUS_MATE_SIBLING_SAME_MOTHER = 20;
    // THIRD GENERATION
    public static final int STATUS_LEADER_CHILD = 21;
    public static final int STATUS_LEADER_CHILD_MATE = 23;
    public static final int STATUS_LEADER_SIBLING_CHILD = 25;
    public static final int STATUS_LEADER_SIBLING_CHILD_MATE = 27;
    public static final int STATUS_MATE_CHILD = 22;
    public static final int STATUS_MATE_CHILD_MATE = 24;
    public static final int STATUS_MATE_SIBLING_CHILD = 26;
    public static final int STATUS_MATE_SIBLING_CHILD_MATE = 28;
    public static final int STATUS_COUPLE_CHILD = 29;
    public static final int STATUS_COUPLE_CHILD_MATE = 30;
    // FORTH GENERATION
    public static final int STATUS_LEADER_GRANDCHILD = 31;
    public static final int STATUS_LEADER_GRAND_CHILD_MATE = 33;
    public static final int STATUS_LEADER_SIBLING_GRANDCHILD = 37;
    public static final int STATUS_LEADER_SIBLING_GRANDCHILD_MATE = 39;
    public static final int STATUS_MATE_GRANDCHILD = -32;
    public static final int STATUS_MATE_GRAND_CHILD_MATE = 34;
    public static final int STATUS_MATE_SIBLING_GRANDCHILD = 38;
    public static final int STATUS_MATE_SIBLING_GRANDCHILD_MATE = 40;
    public static final int STATUS_COUPLE_GRANDCHILD = 35;
    public static final int STATUS_COUPLE_GRANDCHILD_MATE = 36;
    // FIFTH GENERATION
    public static final int STATUS_LEADER_GREAT_GRANDCHILD = 41;
    public static final int STATUS_LEADER_GREAT_GRANDCHILD_MATE = 43;
    public static final int STATUS_LEADER_SIBLING_GREAT_GRANDCHILD = 47;
    public static final int STATUS_LEADER_SIBLING_GREAT_GRANDCHILD_MATE = 49;
    public static final int STATUS_MATE_GREAT_GRANDCHILD = 42;
    public static final int STATUS_MATE_GREAT_GRANDCHILD_MATE = 44;
    public static final int STATUS_MATE_SIBLING_GREAT_GRANDCHILD = 48;
    public static final int STATUS_MATE_SIBLING_GREAT_GRANDCHILD_MATE = 50;
    public static final int STATUS_COUPLE_GREAT_GRANDCHILD = 45;
    public static final int STATUS_COUPLE_GREAT_GRANDCHILD_MATE = 46;
    // Unknown
    public static final int STATUS_UNKNOWN = 0;

    // about Family Attributes
    private Family mFamily;
    private int mStatusInFamily;
    private boolean mHouseOwner = false;

    private String mMessage;
    // Personal Attributes
    private boolean alive = true;
    private boolean chronic = false;
    private List<String> disease = new ArrayList<String>();
    ;
    private String pcucode;
    private int pid;
    private int hcode;
    private String idCard;
    private String fname;
    private String lname;
    private int sex;
    private String birthDay;
    private int age;

    private int familyNo;
    private String familyPosition;
    private String marryStatus;

    // Relation Attributes :
    // mate
    private int matePid;
    private String mateFname;
    private String mateLname;
    private String mateID;
    private int mateHcode;
    // father
    private int fatherPid;
    private String fatherFname;
    private String fatherID;
    // mother
    private int motherPid;
    private String motherFname;

    private String motherID;
    // control variables
    private boolean checkedForAlive = false;
    private boolean checkForChronic = false;
    private boolean foundMate = false;
    private boolean foundChildWithMate = false;
    private boolean foundChildWithOther = false;
    private boolean foundParent = false;
    private boolean foundMother = false;
    private boolean foundFather = false;
    private boolean foundSibling = false;
    private Context mContext;

    public Person(Context context, Cursor personCursor, Family family) {
        mContext = context;
        createPersonByCursor(personCursor);
        mStatusInFamily = STATUS_UNKNOWN;
        mFamily = family;

    }

    public Person(Context context, Cursor personCursor, Family family,
                  int personStatus) {
        mContext = context;
        createPersonByCursor(personCursor);
        mStatusInFamily = personStatus;
        mFamily = family;
    }

    public Person(Context context, Cursor personCursor, Family family,
                  int personStatus, boolean beHouseOwner) {
        mContext = context;
        createPersonByCursor(personCursor);
        mStatusInFamily = personStatus;
        mHouseOwner = beHouseOwner;
        mFamily = family;
    }

    public static final String[] PROJECTION = new String[]{
            th.in.ffc.provider.PersonProvider.Person.PCUPERSONCODE,
            th.in.ffc.provider.PersonProvider.Person.PID,
            th.in.ffc.provider.PersonProvider.Person.HCODE,
            th.in.ffc.provider.PersonProvider.Person.CITIZEN_ID,
            th.in.ffc.provider.PersonProvider.Person.FIRST_NAME,
            th.in.ffc.provider.PersonProvider.Person.LAST_NAME,
            th.in.ffc.provider.PersonProvider.Person.SEX,
            th.in.ffc.provider.PersonProvider.Person.BIRTH,
            th.in.ffc.provider.PersonProvider.Person.FAMILY_NO,
            th.in.ffc.provider.PersonProvider.Person.FAMILY_POSITION,
            th.in.ffc.provider.PersonProvider.Person.MARRY_STATUS,
            th.in.ffc.provider.PersonProvider.Person.MATE,
            th.in.ffc.provider.PersonProvider.Person.MATE_ID,
            th.in.ffc.provider.PersonProvider.Person.FATHER,
            th.in.ffc.provider.PersonProvider.Person.FATHER_ID,
            th.in.ffc.provider.PersonProvider.Person.MOTHER,
            th.in.ffc.provider.PersonProvider.Person.MOTHER_ID,

    };

    private boolean createPersonByCursor(Cursor pc) {

        pcucode = pc.getString(pc.getColumnIndex("pcucodeperson"));
        pid = pc.getInt(pc.getColumnIndex("pid"));
        hcode = pc.getInt(pc.getColumnIndex("hcode"));
        fname = pc.getString(pc.getColumnIndex("fname"));
        lname = pc.getString(pc.getColumnIndex("lname"));
        birthDay = pc.getString(pc.getColumnIndex("birth"));
        sex = Integer.parseInt(pc.getString(pc.getColumnIndex("sex")));
        idCard = pc.getString(pc.getColumnIndex("idcard"));

        marryStatus = pc.getString(pc.getColumnIndex("marystatus"));

        familyNo = pc.getInt(pc.getColumnIndex("familyno"));
        familyPosition = pc.getString(pc.getColumnIndex("familyposition"));
        fatherFname = pc.getString(pc.getColumnIndex("father"));
        fatherID = pc.getString(pc.getColumnIndex("fatherid"));
        motherFname = pc.getString(pc.getColumnIndex("mother"));
        motherID = pc.getString(pc.getColumnIndex("motherid"));
        mateFname = pc.getString(pc.getColumnIndex("mate"));
        mateID = pc.getString(pc.getColumnIndex("mateid"));

        isChronic();

        Date birth = Date.newInstance(birthDay);
        Date system = Date.newInstance(DateTime.getCurrentDate());
        if (!isAlive()) {
            Uri deathUri = Uri.withAppendedPath(Death.CONTENT_URI, "" + pid);
            Cursor c = mContext.getContentResolver().query(deathUri,
                    new String[]{Death.DATE}, null, null,
                    Death.DEFAULT_SORTING);
            if (c.moveToFirst()) {
                String death = c.getString(0);
                Date deathDate = Date.newInstance(death);

                if (deathDate != null) {
                    AgeCalculator cal = new AgeCalculator(deathDate, birth);
                    Date age = cal.calulate();
                    this.age = age.year;
                } else {
                    this.age = 999;
                }
            }
        } else {
            AgeCalculator cal = new AgeCalculator(system, birth);
            Date age = cal.calulate();
            this.age = age.year;
        }

        return true;

    }

    public int childStatusInFamily() {
        int childStatusInFamily;
        switch (mStatusInFamily) {
            case STATUS_LEADER:
                childStatusInFamily = STATUS_LEADER_CHILD;
                break;
            case STATUS_LEADER_CHILD:
                childStatusInFamily = STATUS_LEADER_GRANDCHILD;
                break;
            case STATUS_LEADER_GRANDCHILD:
                childStatusInFamily = STATUS_LEADER_GREAT_GRANDCHILD;
                break;
            case STATUS_LEADER_SIBLING:
                childStatusInFamily = STATUS_LEADER_SIBLING_CHILD;
                break;
            case STATUS_LEADER_SIBLING_CHILD:
                childStatusInFamily = STATUS_LEADER_SIBLING_GRANDCHILD;
                break;
            case STATUS_LEADER_SIBLING_GRANDCHILD:
                childStatusInFamily = STATUS_LEADER_SIBLING_GREAT_GRANDCHILD;
                break;
            case STATUS_MATE:
                childStatusInFamily = STATUS_MATE_CHILD;
                break;
            case STATUS_MATE_CHILD:
                childStatusInFamily = STATUS_MATE_GRANDCHILD;
                break;
            case STATUS_MATE_GRANDCHILD:
                childStatusInFamily = STATUS_MATE_GREAT_GRANDCHILD;
                break;
            case STATUS_MATE_SIBLING:
                childStatusInFamily = STATUS_MATE_SIBLING_CHILD;
                break;
            case STATUS_MATE_SIBLING_CHILD:
                childStatusInFamily = STATUS_MATE_SIBLING_GRANDCHILD;
                break;
            case STATUS_MATE_SIBLING_GRANDCHILD:
                childStatusInFamily = STATUS_MATE_SIBLING_GREAT_GRANDCHILD;
                break;
            case STATUS_COUPLE_CHILD:
                childStatusInFamily = STATUS_COUPLE_GRANDCHILD;
                break;
            case STATUS_COUPLE_GRANDCHILD:
                childStatusInFamily = STATUS_COUPLE_GREAT_GRANDCHILD;
                break;
            default:
                childStatusInFamily = STATUS_UNKNOWN;
                break;
        }
        return childStatusInFamily;
    }

    public int childWithMateStatusInFamily() {
        int childWithMateStatus;
        switch (mStatusInFamily) {
            case STATUS_LEADER:
            case STATUS_MATE:
                childWithMateStatus = STATUS_COUPLE_CHILD;
                break;

            case STATUS_LEADER_CHILD:
            case STATUS_LEADER_CHILD_MATE:
                childWithMateStatus = STATUS_LEADER_GRANDCHILD;
                break;
            case STATUS_LEADER_GRANDCHILD:
            case STATUS_LEADER_GRAND_CHILD_MATE:
                childWithMateStatus = STATUS_LEADER_GREAT_GRANDCHILD;
                break;
            case STATUS_LEADER_SIBLING:
            case STATUS_LEADER_SIBLING_MATE:
                childWithMateStatus = STATUS_LEADER_SIBLING_CHILD;
                break;
            case STATUS_LEADER_SIBLING_CHILD:
            case STATUS_LEADER_SIBLING_CHILD_MATE:
                childWithMateStatus = STATUS_LEADER_SIBLING_GRANDCHILD;
                break;
            case STATUS_LEADER_SIBLING_GRANDCHILD:
            case STATUS_LEADER_SIBLING_GRANDCHILD_MATE:
                childWithMateStatus = STATUS_LEADER_SIBLING_GREAT_GRANDCHILD;
                break;

            case STATUS_MATE_CHILD:
            case STATUS_MATE_CHILD_MATE:
                childWithMateStatus = STATUS_MATE_GRANDCHILD;
                break;
            case STATUS_MATE_GRANDCHILD:
            case STATUS_MATE_GRAND_CHILD_MATE:
                childWithMateStatus = STATUS_MATE_GREAT_GRANDCHILD;
                break;
            case STATUS_MATE_SIBLING:
            case STATUS_MATE_SIBLING_MATE:
                childWithMateStatus = STATUS_MATE_SIBLING_CHILD;
                break;
            case STATUS_MATE_SIBLING_CHILD:
            case STATUS_MATE_SIBLING_CHILD_MATE:
                childWithMateStatus = STATUS_MATE_SIBLING_GRANDCHILD;
                break;
            case STATUS_MATE_SIBLING_GRANDCHILD:
            case STATUS_MATE_SIBLING_GRANDCHILD_MATE:
                childWithMateStatus = STATUS_MATE_SIBLING_GREAT_GRANDCHILD;
                break;

            case STATUS_COUPLE_CHILD:
            case STATUS_COUPLE_CHILD_MATE:
                childWithMateStatus = STATUS_COUPLE_GRANDCHILD;
                break;
            case STATUS_COUPLE_GRANDCHILD:
            case STATUS_COUPLE_GRANDCHILD_MATE:
                childWithMateStatus = STATUS_COUPLE_GREAT_GRANDCHILD;
                break;

            default:
                childWithMateStatus = STATUS_UNKNOWN;
                break;
        }
        return childWithMateStatus;
    }

    public List<Person> findAllInHouse() {
        List<Person> people = new ArrayList<Person>();
        String sql = "hcode = " + hcode + " AND familyno = " + familyNo;
        Cursor cursor = mContext.getContentResolver().query(
                th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                PROJECTION, sql, null, "birth ASC");
        if (cursor.moveToFirst()) {
            do {
                people.add(new Person(mContext, cursor, mFamily));
            } while (cursor.moveToNext());
        }
        return people;
    }

    public List<Person> findChild() {
        // must use findChildWithMate before this
        List<Person> children = new ArrayList<Person>();
        if (!foundChildWithOther) {
            String sql = "select * from person where ";
            String sqlOderBy = "birth ASC";
            String sqlWhere = null;
            if (sex == 1) {
                if (idCard != null) {
                    // best case
                    sqlWhere = "( fatherid = \'" + idCard + "\')";
                }
                // second case
                if (fname != null && lname != null) {
                    String sqlWhere2 = "( father = \'" + fname
                            + "\' AND lname = \'" + lname + "\' )";
                    if (sqlWhere != null) {
                        sqlWhere += " OR ";
                        sqlWhere += sqlWhere2;
                    } else {
                        sqlWhere = sqlWhere2;
                    }
                }
            } else {
                if (idCard != null) {
                    sqlWhere = "( motherid = \'" + idCard + "\')";
                }
                if (fname != null && lname != null) {
                    String sqlWhere2 = "( mother = \'" + fname
                            + "\' AND lname = \'" + lname + "\' )";
                    if (sqlWhere != null) {
                        sqlWhere += " OR ";
                        sqlWhere += sqlWhere2;
                    } else {
                        sqlWhere = sqlWhere2;
                    }

                }
            }
            // worth case
            if (familyPosition != null) {
                // System.out.println("family position =" + familyPosition);
                String childPosition = childPosition();
                if (childPosition != null) {
                    // System.out.println("child position =" + childPosition);
                    if (sqlWhere != null) {
                        sqlWhere += " OR ( hcode = " + hcode
                                + " AND familyno = " + familyNo;
                        sqlWhere += " AND familyposition = \'" + childPosition
                                + "\' )";
                    } else {
                        sqlWhere = "( hcode = " + hcode + " AND familyno = "
                                + familyNo;
                        sqlWhere += " AND familyposition = \'" + childPosition
                                + "\' )";
                    }
                }
            }
            // if (foundMate) {
            // if (sex == 1) {
            // sqlWhere = "(" + sqlWhere + ") AND (motherid <> \'" + mateID
            // + "\')";
            // } else {
            // sqlWhere = "(" + sqlWhere + ") AND (fatherid <> \'" + mateID
            // + "\')";
            // }
            // }
            sql += sqlWhere;
            sql += sqlOderBy;
            // System.out.println(sql);
            Cursor cursor = mContext.getContentResolver().query(
                    th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                    PROJECTION, sqlWhere, null, sqlOderBy);
            if (cursor.moveToFirst()) {
                do {
                    if (!mFamily.isAlreadyBeFamilyMember(cursor.getString(8))) {
                        System.out.println("Found His/Hes ChildWithOther");
                        Person child = new Person(mContext, cursor, mFamily,
                                childStatusInFamily());
                        if (sex == 1) {
                            child.fatherID = this.idCard;
                            child.foundFather = true;
                            child.fatherFname = this.fname;
                            child.fatherPid = this.pid;

                        } else {
                            child.motherID = this.idCard;
                            child.foundMother = true;
                            child.motherFname = this.fname;
                            child.motherPid = this.pid;
                        }
                        child.foundParent = false;
                        children.add(child);
                    }

                } while (cursor.moveToNext());
                foundChildWithOther = true;
            } else {
                System.out.println("not Found His/Hes ChildWithOther");
                mMessage += "\nForm findChild()\n\t" + sql;
            }
        }
        return children;
    }

    public List<Person> findChildWithMate() {

        List<Person> ChildrenWithMate = new ArrayList<Person>();
        if (foundMate && !foundChildWithMate) {
            String sql = "select * from person where ";
            String sqlWhere = null;
            if (sex == 1) {
                // 1st case
                sqlWhere = "(( fatherid = \'" + idCard + "\' OR ( father = \'"
                        + fname + "\' AND ( lname = \'" + lname
                        + "\' OR hcode = " + hcode + ")))";
                sqlWhere += " AND  ( motherid = \'" + mateID
                        + "\' OR ( mother = \'" + mateFname
                        + "\' AND ( lname = \'" + mateLname + "\' OR hcode = "
                        + mateHcode + "))))";
                // 2rd case
                sqlWhere += " OR ( father = \'" + fname + "\' AND mother = \'"
                        + mateFname + "\' AND ( lname = \'" + lname
                        + "\' OR lname = \'" + mateLname + "\'))";

            } else {
                sqlWhere = "(( motherid = \'" + idCard + "\' OR ( mother = \'"
                        + fname + "\' AND ( lname = \'" + lname
                        + "\' OR hcode = " + hcode + ")))";
                sqlWhere += " AND ( fatherid = \'" + mateID
                        + "\' OR ( father = \'" + mateFname
                        + "\' AND ( lname = \'" + mateLname + "\' OR hcode = "
                        + mateHcode + "))))";
                sqlWhere += " OR ( mother = \'" + fname + "\' AND father = \'"
                        + mateFname + "\' AND ( lname = \'" + lname
                        + "\' OR lname = \'" + mateLname + "\'))";
            }
            if (familyPosition != null) {
                String childWithMatePosition = childWithMatePosition();
                if (childWithMatePosition != null) {
                    // sqlWhere = "(" + sqlWhere + ")";
                    // if in same house, same familyNo and right Position
                    sqlWhere += " OR (hcode = " + hcode + " AND familyno = "
                            + familyNo + " AND familyposition = \'"
                            + childWithMatePosition + "\' )";
                    // sqlWhere += "(" + sqlWhere + ")";
                }
            }
            // sqlWhere += "(" + sqlWhere + ")";
            sqlWhere += " AND ( birth > \'" + birthDay + "\' ) ";
            sql += sqlWhere.concat(" order by birth ASC");

            Cursor cursor = mContext.getContentResolver().query(
                    th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                    PROJECTION, sqlWhere, null, "birth ASC");
            if (cursor.moveToFirst()) {
                do {
                    Person child = new Person(mContext, cursor, mFamily,
                            childWithMateStatusInFamily());
                    System.out.println("Found His/Hes ChildWithMate");
                    if (sex == 1) {
                        child.fatherID = this.idCard;
                        child.fatherFname = this.fname;
                        child.fatherPid = this.pid;
                        child.motherID = this.mateID;
                        child.motherFname = this.mateFname;
                        child.motherPid = this.matePid;

                    } else {
                        child.fatherID = this.mateID;
                        child.fatherFname = this.mateFname;
                        child.fatherPid = this.matePid;
                        child.motherID = this.idCard;
                        child.motherFname = this.fname; // bug child.motherID
                        // @1/30/2012
                        child.motherPid = this.pid;
                    }
                    child.foundParent = true;
                    ChildrenWithMate.add(child);
                } while (cursor.moveToNext());

                this.foundChildWithMate = true;
                Person mate = mFamily.getPersonByIdCard(mateID);
                if (mate != null)
                    mate.foundChildWithMate = true;
            } else {
                // mMessage += "\nNot His/Hes ChildWithMate" + sql;
                System.out.println("Not His/Hes ChildWithMate" + sql);
            }
        }
        return ChildrenWithMate;
    }

    public Person findMate() {
        boolean haveMethodTofind = false;
        Person soulMate = null;
        if (!this.foundMate) {
            if (true) {
                String sql = "sex <> " + sex;
                if ((mateFname != null) || (mateID != null)) {
                    haveMethodTofind = true;
                    if (mateID != null) {
                        System.out.println(pid + " find mate by mateid!");
                        sql += " AND idcard = \'" + mateID + "\'";
                    } else {
                        System.out.println(pid
                                + " find mate by matename, lname OR hcode");
                        sql += " AND fname = \'" + this.mateFname + "\'";
                        sql += " AND (lname = \'" + lname + "\' OR hcode = "
                                + hcode + ")";
                    }
                } else if (familyPosition != null) {
                    haveMethodTofind = true;
                    String mateFamilyPosition = mateFamilyPosition();
                    if (mateFamilyPosition != null) {
                        System.out.println(getFullName()
                                + " find mate by familyPosition");
                        sql += " AND hcode = " + hcode + " AND familyno = "
                                + familyNo + " AND familyposition = \'"
                                + mateFamilyPosition + "\'";
                    } else {
                        System.out.println(pid
                                + " have no anyway to find his/her mate");
                        sql += " AND pid = -1";
                    }
                }
                if (haveMethodTofind) {
                    Cursor cursor = mContext
                            .getContentResolver()
                            .query(th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                                    PROJECTION, sql, null, null);
                    if (cursor.moveToFirst()) {
                        System.out.println(pid + " found his/her mate");
                        soulMate = new Person(mContext, cursor, mFamily,
                                mateStatusInFamily());
                        // set mate information
                        soulMate.matePid = this.pid;
                        soulMate.mateID = this.idCard;
                        soulMate.mateFname = this.fname;
                        soulMate.mateLname = this.lname;
                        soulMate.mateHcode = this.hcode;
                        soulMate.foundMate = true;
                        // set self information
                        foundMate = true;
                        matePid = soulMate.pid;
                        mateFname = soulMate.fname;
                        mateLname = soulMate.lname;
                        mateID = soulMate.idCard;
                        mateHcode = soulMate.hcode;

                        if (this.sex == 1) {
                            soulMate.marryStatus = this.marryStatus;
                        } else {
                            this.marryStatus = soulMate.marryStatus;
                        }
                    } else {
                        mMessage = sql;
                        System.out.println("not found " + pid + "\'s mate");
                    }
                }
            }
        } else {

        }
        return soulMate;
    }

    public List<Person> findParent() {
        List<Person> Parent = new ArrayList<Person>();
        if (!foundParent) {
            Person father = findFather();
            Person Mother = findMother();
            if (father != null) {
                Parent.add(father);
            }
            if (Mother != null) {
                Parent.add(Mother);
            }
            if (Parent.size() == 2) {
                father.foundChildWithMate = true;
                if (father.mateID == null || father.mateFname == null) {
                    father.mateID = Mother.idCard;
                    father.mateFname = Mother.fname;
                    father.matePid = Mother.pid;
                    if (father.marryStatus == null
                            || father.marryStatus.trim().length() < 1)
                        father.marryStatus = ""
                                + RelationLineView.STATUS_UNKNOWN;
                    father.foundMate = true;
                }
                Mother.foundChildWithMate = true;
                if (Mother.mateID == null || Mother.mateFname == null) {
                    Mother.mateID = father.idCard;
                    Mother.mateFname = father.fname;
                    Mother.matePid = father.pid;
                    // if(Mother.marryStatus == null ||
                    // Mother.marryStatus.trim().length() < 1)
                    Mother.marryStatus = "" + father.marryStatus;
                    Mother.foundMate = true;
                }
                foundParent = true;
            }
        }
        return Parent;
    }

    public List<Person> findSibling() {
        List<Person> sibling = new ArrayList<Person>();
        if (!foundSibling) {
            String sql = "select * from person where ";
            String sqlwhere = null;
            String sqltail = " and ( hcode = " + hcode + " and idcard <> \'"
                    + idCard + "\')";
            if ((fatherID != null) && (motherID != null)) {
                sqlwhere = " fatherid = \'" + this.fatherID
                        + "\' AND motherid = \'" + this.motherID + "\' ";
            }
            if ((fatherFname != null) && (motherFname != null)) {
                if (sqlwhere != null) {
                    sqlwhere = "((" + sqlwhere + ") OR";
                    sqlwhere += " ( father = \'" + fatherFname
                            + "\' AND mother = \'" + motherFname
                            + "\' AND ( lname = \'" + this.lname
                            + "\' OR hcode = " + hcode + " )))";
                } else {
                    sqlwhere = " ( father = \'" + fatherFname
                            + "\' AND mother = \'" + motherFname
                            + "\' AND ( lname = \'" + this.lname
                            + "\' OR hcode = " + hcode + " ))";
                }

            }
            if (sqlwhere == null) {
                sqlwhere = siblingPosition() + " AND hcode = " + hcode + " ";
            }
            if (sqlwhere != null) {
                sql += sqlwhere;
                sql += sqltail;
                Cursor cursor = mContext
                        .getContentResolver()
                        .query(th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                                PROJECTION, sqlwhere.concat(sqltail), null,
                                "birth ASC");
                if (cursor.moveToFirst()) {
                    do {
                        System.out.println(pid + " found his/her sibling "
                                + cursor.getInt(1));
                        Person s = new Person(mContext, cursor, mFamily,
                                siblingStatusInFamily());
                        s.foundSibling = true;
                        s.foundParent = foundParent;
                        s.foundMother = foundMother;
                        s.foundFather = foundFather;
                        s.fatherFname = fatherFname;
                        s.fatherID = fatherID;
                        s.fatherPid = fatherPid;
                        s.motherFname = motherFname;
                        s.motherID = motherID;
                        s.motherPid = motherPid;
                        sibling.add(s);
                        this.foundSibling = true;
                    } while (cursor.moveToNext());
                }
            }

        }
        return sibling;
    }

    public int getAge() {
        return age;
    }

    public String getBirthDate() {
        if (birthDay != null)
            return birthDay;
        return "ไม่ระบุ";
    }

    public String getCitizenID() {
        return parseCitizenID(this.idCard);
    }

    public String getDadName() {
        if (fatherFname == null)
            return "ไม่ระบุ";
        return fatherFname;
    }

    public List<String> getDisease() {
        if (chronic) {
            return disease;
        } else {
            return null;
        }
    }

    public Family getFamily() {
        return mFamily;
    }

    public String getFamilyPosition() {
        return familyPosition;
    }

    public int getFamilyNo() {
        return familyNo;
    }

    public String getFatherFirstName() {
        return fatherFname;
    }

    public String getFatherID() {
        return fatherID;
    }

    public int getFatherPID() {
        return fatherPid;
    }

    public String getFirstName() {
        return fname;
    }

    // Modified LUT prename for support 21files สนย 2555 //
    public String getFullName(Context context) {

        return fname + " " + lname;

    }

    public String getFullName() {
        return fname + " " + lname;
    }

    public int getHcode() {
        return hcode;
    }

    public String getIDcard() {
        return idCard;
    }

    public String getLastName() {
        return lname;
    }

    public String getMateName() {
        if (mateFname == null)
            return "ไม่ระบุ";
        return mateFname;
    }

    public String getMarryStatus() {
        return marryStatus;
    }

    public String getMateFirstName() {
        return mateFname;
    }

    public String getMateID() {
        return mateID;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getMonName() {
        if (TextUtils.isEmpty(motherFname))
            return "ไม่ระบุ";
        return motherFname;
    }

    public String getMotherFirstName() {
        return motherFname;
    }

    public String getMotherID() {
        return motherID;
    }

    public int getMotherPID() {
        return motherPid;
    }

    public String getPCUCode() {
        return pcucode;
    }

    public int getPid() {
        return pid;
    }

    public int getSex() {
        return sex;
    }

    public int getStatusInFamily() {
        return mStatusInFamily;
    }

    public boolean isAlive() {
        if (!checkedForAlive) {
            alive = checkForAlive();
        }
        return alive;
    }

    public boolean isChronic() {
        if (!checkForChronic) {
            chronic = checkForChronic();
            return chronic;
        } else {
            return chronic;
        }
    }

    public boolean isFoundChildwithMate() {
        return this.foundChildWithMate;
    }

    public boolean isFoundChildWithOther() {
        return this.foundChildWithOther;
    }

    public boolean isFoundFather() {
        return this.foundFather;
    }

    public boolean isFoundMate() {
        return this.foundMate;
    }

    public boolean isFoundMother() {
        return this.foundMother;
    }

    public boolean isFoundParent() {
        return this.foundParent;
    }

    public boolean isFoundSibling() {
        return this.foundSibling;
    }

    public boolean isHouseOwner() {
        return this.mHouseOwner;
    }

    public int isInGeneration() {
        if ((mStatusInFamily > 0) && (mStatusInFamily <= 10)) {
            return 1;
        } else if ((mStatusInFamily > 10) && (mStatusInFamily <= 20)) {
            return 2;
        } else if ((mStatusInFamily > 20) && (mStatusInFamily <= 30)) {
            return 3;
        } else if ((mStatusInFamily > 30) && (mStatusInFamily <= 40)) {
            return 4;
        } else if ((mStatusInFamily > 40) && (mStatusInFamily <= 50)) {
            return 5;
        } else if ((mStatusInFamily > -10) && (mStatusInFamily <= -1)) {
            return 1;
        } else
            return 0;

    }

    public int mateStatusInFamily() {
        int mateStatus;
        switch (mStatusInFamily) {
            case STATUS_LEADER:
                mateStatus = STATUS_MATE;
                break;
            case STATUS_LEADER_PARENT:
                mateStatus = STATUS_LEADER_PARENT;
                break;
            case STATUS_LEADER_SIBLING:
                mateStatus = STATUS_LEADER_SIBLING_MATE;
                break;
            case STATUS_LEADER_SIBLING_MATE:
                mateStatus = STATUS_LEADER_SIBLING;
                break;
            case STATUS_LEADER_CHILD:
                mateStatus = STATUS_LEADER_CHILD_MATE;
                break;
            case STATUS_LEADER_GRANDCHILD:
                mateStatus = STATUS_LEADER_GRAND_CHILD_MATE;
                break;
            case STATUS_LEADER_SIBLING_CHILD:
                mateStatus = STATUS_LEADER_SIBLING_CHILD_MATE;
                break;
            case STATUS_LEADER_SIBLING_GRANDCHILD:
                mateStatus = STATUS_LEADER_SIBLING_GRANDCHILD_MATE;
                break;
            case STATUS_LEADER_SIBLING_GREAT_GRANDCHILD:
                mateStatus = STATUS_LEADER_SIBLING_GREAT_GRANDCHILD_MATE;
                break;
            case STATUS_MATE:
                mateStatus = STATUS_LEADER;
                break;
            case STATUS_MATE_PARENT:
                mateStatus = STATUS_MATE_PARENT;
                break;
            case STATUS_MATE_CHILD:
                mateStatus = STATUS_MATE_CHILD_MATE;
                break;
            case STATUS_MATE_GRANDCHILD:
                mateStatus = STATUS_MATE_GRAND_CHILD_MATE;
                break;
            case STATUS_MATE_SIBLING:
                mateStatus = STATUS_LEADER_SIBLING_MATE;
                break;
            case STATUS_MATE_SIBLING_MATE:
                mateStatus = STATUS_MATE_SIBLING;
                break;
            case STATUS_MATE_SIBLING_CHILD:
                mateStatus = STATUS_MATE_SIBLING_CHILD_MATE;
                break;
            case STATUS_MATE_SIBLING_GRANDCHILD:
                mateStatus = STATUS_MATE_SIBLING_GRANDCHILD_MATE;
                break;
            case STATUS_MATE_SIBLING_GREAT_GRANDCHILD:
                mateStatus = STATUS_MATE_SIBLING_GREAT_GRANDCHILD_MATE;
                break;
            case STATUS_COUPLE_CHILD:
                mateStatus = STATUS_COUPLE_CHILD_MATE;
                break;
            case STATUS_COUPLE_GRANDCHILD:
                mateStatus = STATUS_COUPLE_GRANDCHILD_MATE;
                break;
            case STATUS_COUPLE_GREAT_GRANDCHILD:
                mateStatus = STATUS_COUPLE_GREAT_GRANDCHILD_MATE;
                break;
            case STATUS_COUPLE_CHILD_MATE:
                mateStatus = STATUS_COUPLE_CHILD;
                break;
            case STATUS_COUPLE_GRANDCHILD_MATE:
                mateStatus = STATUS_COUPLE_GRANDCHILD;
                break;
            case STATUS_COUPLE_GREAT_GRANDCHILD_MATE:
                mateStatus = STATUS_COUPLE_GREAT_GRANDCHILD;
                break;
            default:
                mateStatus = STATUS_UNKNOWN;
                break;
        }
        return mateStatus;
    }

    public int parentStatusInFamily() {

        int parentStatus;
        switch (mStatusInFamily) {
            case STATUS_LEADER:
                parentStatus = STATUS_LEADER_PARENT;
                break;
            case STATUS_LEADER_SIBLING:
                parentStatus = STATUS_LEADER_PARENT;
                break;
            case STATUS_MATE:
                parentStatus = STATUS_MATE_PARENT;
                break;
            case STATUS_MATE_SIBLING:
                parentStatus = STATUS_MATE_PARENT;
                break;
            default:
                parentStatus = STATUS_UNKNOWN;
                break;
        }
        return parentStatus;
    }

    public void setFirstName(String fname) {
        this.fname = fname;
    }

    public void setLastName(String lname) {
        this.lname = lname;
    }

    public void setIDcard(String idcard) {
        this.idCard = idcard;
    }

    public void setFatherName(String name) {
        this.fatherFname = name;
    }

    public void setFatherID(String idCard) {
        this.fatherID = idCard;
    }

    public void setFoundChildWithMate(boolean found) {
        // TODO Auto-generated method stub
        this.foundChildWithMate = found;
    }

    public void setFoundMate(Boolean found) {
        this.foundMate = found;
    }

    public void setMateID(String idCard) {
        this.idCard = idCard;
    }

    public void setMateName(String name) {
        this.mateFname = name;
    }

    public void setMotherID(String idCard) {
        this.motherID = idCard;
    }

    public void setMotherName(String name) {
        this.motherFname = name;
    }

    public void setToBeHouseOwner() {
        this.mHouseOwner = true;
    }

    public void setMarryStatue(String marry) {
        this.marryStatus = marry;
    }

    public void setSex(int sex) {
        this.sex = (sex > 0 && sex < 3) ? sex : this.sex;
    }

    public int siblingStatusInFamily() {
        int siblingStatus;
        if (mStatusInFamily == STATUS_LEADER)
            siblingStatus = STATUS_LEADER_SIBLING;
        else if (mStatusInFamily == STATUS_MATE)
            siblingStatus = STATUS_MATE_SIBLING;
        else if (mStatusInFamily == STATUS_COUPLE_CHILD)
            siblingStatus = STATUS_COUPLE_CHILD;
        else if (mStatusInFamily == STATUS_COUPLE_CHILD)
            siblingStatus = STATUS_COUPLE_CHILD;
        else if (mStatusInFamily == STATUS_LEADER_PARENT)
            siblingStatus = STATUS_LEADER_PARENT_SIBLING;
        else if (mStatusInFamily == STATUS_MATE_PARENT)
            siblingStatus = STATUS_MATE_PARENT_SIBLING;
        else
            siblingStatus = STATUS_UNKNOWN;
        return siblingStatus;
    }

    @Override
    public String toString() {

        return "Pid=" + pid + "\t\tPosition=" + mStatusInFamily
                + "\nfound Parent" + foundParent + "\nfoundFather="
                + foundFather + "\nFatherID=" + fatherID + "\tfatherPID="
                + fatherPid + "\nfoundMother=" + foundMother + "\nMotherID="
                + motherID + "\tmotherPID=" + motherPid + "\nfoundMate="
                + foundMate + "\nMate ID=" + mateID + "\tMatePID=" + matePid
                + "\nfoundSiblig=" + foundSibling + "\nfoundChild="
                + foundChildWithOther + "\nfoundChildWithMate="
                + foundChildWithMate;
    }

    private boolean checkForAlive() {

        // Cursor cursor = JhcisDatabaseAdapter
        // .rawQuery("select * from person where pid = ( select pid from persondeath where pid = "
        // + pid + " )");
        Cursor cursor = mContext.getContentResolver().query(Death.CONTENT_URI,
                new String[]{Death.CAUSE}, "pid=" + pid, null,
                Death.DEFAULT_SORTING);
        checkedForAlive = true;
        if (cursor.moveToFirst()) {
            System.out.println(pid + " is Death");
            return false;
        } else {
            System.out.println(pid + " is Alive!");
            return true;
        }
    }

    private boolean checkForChronic() {

        // Cursor cursor = JhcisDatabaseAdapter
        // .rawQuery("select diseasenamethai, diseasename "
        // + "from personchronic, cdisease where (pid = " + pid
        // + ") AND (personchronic.chroniccode"
        // + " = cdisease.diseasecode )");
        Cursor cursor = mContext.getContentResolver().query(
                Chronic.CONTENT_URI, new String[]{Chronic.CODE},
                "pid=" + pid, null, Chronic.DEFAULT_SORTING);
        checkForChronic = true;
        if (cursor.moveToFirst()) {
            do {
                // Check For Thai Diseasename
                String diseasename = cursor.getString(0);
                if (!TextUtils.isEmpty(diseasename)) {
                    disease.add(diseasename);
                } else {
                    // Check For English Diseasename
                    diseasename = cursor.getString(1);
                    if (!TextUtils.isEmpty(diseasename)) {
                        disease.add(diseasename);
                    } else {
                        disease.add("ไม่ระบุชนิดของโรค");
                    }
                }
            } while (cursor.moveToNext());
            return true;
        } else {
            return false;
        }

    }

    private String childPosition() {
        String childPosition;
        // leader child
        if (familyPosition.equalsIgnoreCase("1"))
            if (foundMate) {
                childPosition = "4";
            } else {
                childPosition = "3";
            }
            // Mate child
        else if (familyPosition.equalsIgnoreCase("2"))
            childPosition = "5";
            // child of childWithMate
            // else if (familyPosition.equalsIgnoreCase("3"))
            // childPosition = "จ";
            // child of leader child
            // else if (familyPosition.equalsIgnoreCase("4"))
            // childPosition = "ฉ";
            // child of mate child
            // else if (familyPosition.equalsIgnoreCase("5"))
            // childPosition = "ช";
        else
            childPosition = null;
        return childPosition;
    }

    private String childWithMatePosition() {
        String childWithMatePosition;
        if (familyPosition.equalsIgnoreCase("1")
                || familyPosition.equalsIgnoreCase("2"))
            childWithMatePosition = "3";
        else if (familyPosition.equalsIgnoreCase("3"))
            childWithMatePosition = "จ";
        else if (familyPosition.equalsIgnoreCase("6")
                || familyPosition.equalsIgnoreCase("7"))
            childWithMatePosition = "1";
        else if (familyPosition.equalsIgnoreCase("8")
                || familyPosition.equalsIgnoreCase("9"))
            childWithMatePosition = "2";
        else if (familyPosition.equalsIgnoreCase("i")
                || familyPosition.equalsIgnoreCase("j"))
            childWithMatePosition = "6";
        else if (familyPosition.equalsIgnoreCase("k")
                || familyPosition.equalsIgnoreCase("l"))
            childWithMatePosition = "7";
        else if (familyPosition.equalsIgnoreCase("m")
                || familyPosition.equalsIgnoreCase("n"))
            childWithMatePosition = "8";
        else if (familyPosition.equalsIgnoreCase("o")
                || familyPosition.equalsIgnoreCase("p"))
            childWithMatePosition = "9";
        else
            childWithMatePosition = null;
        return childWithMatePosition;
    }

    private String fatherFamilyPosition() {

        String fatherPosition;
        String position = familyPosition;
        if (position.equalsIgnoreCase("1"))
            fatherPosition = "6";
        else if (position.equalsIgnoreCase("2"))
            fatherPosition = "8";
        else if (position.equalsIgnoreCase("3"))
            fatherPosition = "1";
        else if (position.equalsIgnoreCase("7"))
            fatherPosition = "k";
        else if (position.equalsIgnoreCase("6"))
            fatherPosition = "i";
        else if (position.equalsIgnoreCase("8"))
            fatherPosition = "m";
        else if (position.equalsIgnoreCase("9"))
            fatherPosition = "o";
        else
            fatherPosition = null;
        return fatherPosition;
    }

    private Person findFather() {
        Person father = null;
        if (!foundFather) {
            String sql = "select * from person where ";
            String sqlWhere = null;
            String sqlTails = " AND ( birth < \'" + birthDay
                    + "\' AND sex = 1 )";
            // find by motherID or mother name
            if (fatherID != null || fatherFname != null) {
                if (fatherID != null) {
                    sqlWhere = " idcard = \'" + fatherID + "\' ";
                } else {
                    sqlWhere = " fname = \'" + fatherFname
                            + "\' AND ( lname = \'" + lname + "\' OR hcode = "
                            + hcode + " )";
                }
            } else if (familyPosition != null) {
                String fatherPosition = fatherFamilyPosition();
                if (fatherPosition != null) {
                    sqlWhere = " familyposition = \'" + fatherPosition
                            + "\' AND hcode = " + hcode + " AND familyno = "
                            + familyNo;
                }
            }
            if (sqlWhere != null) {
                sql += sqlWhere;
                sql += sqlTails;
                Cursor cursor = mContext.getContentResolver().query(
                        th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                        PROJECTION, sqlWhere.concat(sqlTails), null, null);
                if (cursor.moveToFirst()) {
                    father = new Person(mContext, cursor, mFamily,
                            parentStatusInFamily());
                    System.out.println(pid + " was found his/her Father pid "
                            + father.pid);
                    fatherPid = father.pid;
                    fatherFname = father.fname;
                    fatherID = father.idCard;
                    foundFather = true;
                }
            }
        }
        return father;

    }

    private Person findMother() {
        Person mother = null;
        if (!foundMother) {
            System.err.println("Start find mother " + this.toString());
            String sql = "select * from person where ";
            String sqlWhere = null;
            String sqlTails = " AND ( birth < \'" + birthDay
                    + "\' AND sex = 2 )";
            // find by motherID or mother name

            if (motherID != null || motherFname != null) {
                if (motherID != null) {
                    System.err.println("find mother by ID " + motherID);
                    sqlWhere = " idcard = \'" + motherID + "\' ";
                } else {
                    sqlWhere = " fname = \'" + motherFname
                            + "\' AND ( lname = \'" + lname + "\' OR hcode = "
                            + hcode + " )";
                }
            } else if (familyPosition != null) {
                String motherPosition = motherFamilyPosition();
                if (motherPosition != null) {
                    sqlWhere = " familyposition = \'" + motherPosition
                            + "\' AND hcode = " + hcode + " AND familyno = "
                            + familyNo;
                }
            }
            if (sqlWhere != null) {
                sql += sqlWhere;
                sql += sqlTails;
                System.err.println("sql mother is " + sql);
                Cursor cursor = mContext.getContentResolver().query(
                        th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                        PROJECTION, sqlWhere.concat(sqlTails), null, null);
                if (cursor.moveToFirst()) {
                    mother = new Person(mContext, cursor, mFamily,
                            parentStatusInFamily());
                    System.out.println(pid + " was found his/her Mother pid "
                            + mother.pid);
                    motherPid = mother.pid;
                    motherFname = mother.fname;
                    motherID = mother.idCard;
                    foundMother = true;
                }
            }
        }
        return mother;
    }

    private String mateFamilyPosition() {
        String mateFamilyPostion;
        // หัวหน้าครอบครัว กับ
        // คู๋สมรส
        System.out.println(pid
                + "find mate's familyposition by his/her familypositon ="
                + familyPosition);
        System.out.println(familyPosition + " = \"1\" "
                + (familyPosition.equalsIgnoreCase("1")));
        if (familyPosition.equalsIgnoreCase("1"))
            mateFamilyPostion = "2";
        else if (familyPosition.equalsIgnoreCase("2"))
            mateFamilyPostion = "1";
            // พ่อแม่หัวหน้าครอบครัว
        else if (familyPosition.equalsIgnoreCase("6"))
            mateFamilyPostion = "7";
        else if (familyPosition.equalsIgnoreCase("7"))
            mateFamilyPostion = "6";
            // ปู่ ย่า หัวหน้าครอบครัว
        else if (familyPosition.equalsIgnoreCase("i"))
            mateFamilyPostion = "j";
        else if (familyPosition.equalsIgnoreCase("j"))
            mateFamilyPostion = "i";
            // ตา ยาย หัวหน้าครอบครัว
        else if (familyPosition.equalsIgnoreCase("k"))
            mateFamilyPostion = "l";
        else if (familyPosition.equalsIgnoreCase("l"))
            mateFamilyPostion = "k";

            // พ่อแม่คู่สมรส
        else if (familyPosition.equalsIgnoreCase("8"))
            mateFamilyPostion = "9";
        else if (familyPosition.equalsIgnoreCase("9"))
            mateFamilyPostion = "8";
            // ปู่ ย่า คู่สมรส
        else if (familyPosition.equalsIgnoreCase("m"))
            mateFamilyPostion = "n";
        else if (familyPosition.equalsIgnoreCase("n"))
            mateFamilyPostion = "m";
            // ตา ยาย คู่สมรส
        else if (familyPosition.equalsIgnoreCase("o"))
            mateFamilyPostion = "p";
        else if (familyPosition.equalsIgnoreCase("p"))
            mateFamilyPostion = "o";

            // บุตร บุตรสะใภ้ บุตรเขย
            // else if ((familyPosition.equalsIgnoreCase("3")) && (sex == 1))
            // mateFamilyPostion = "ฑ";
            // else if ((familyPosition.equalsIgnoreCase("3")) && (sex == 2))
            // mateFamilyPostion = "ฒ";
            // อื่น ๆ
        else
            mateFamilyPostion = null;
        System.out.println(pid + " found mate's familyposition with "
                + mateFamilyPostion);
        return mateFamilyPostion;
    }

    private String motherFamilyPosition() {
        String motherPosition;
        String position = familyPosition;
        if (position.equalsIgnoreCase("1"))
            motherPosition = "7";
        else if (position.equalsIgnoreCase("2"))
            motherPosition = "9";
        else if (position.equalsIgnoreCase("3"))
            motherPosition = "2";
        else if (position.equalsIgnoreCase("7"))
            motherPosition = "l";
        else if (position.equalsIgnoreCase("6"))
            motherPosition = "j";
        else if (position.equalsIgnoreCase("8"))
            motherPosition = "n";
        else if (position.equalsIgnoreCase("9"))
            motherPosition = "p";
        else
            motherPosition = null;
        return motherPosition;
    }

    private String siblingPosition() {
        if (familyPosition == null)
            return null;

        String siblingPosition;
        if (familyPosition.equalsIgnoreCase("1"))
            siblingPosition = " (familyposition = \'ก\' OR familyposition = \'ข\')";
        else if (familyPosition.equalsIgnoreCase("2"))
            siblingPosition = " (familyposition = \'ค\' OR familyposition = \'ง\')";
        else
            siblingPosition = null;
        return siblingPosition;
    }

    // -------------- sub fungtion ,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    private String toBE(String year) {
        int i = Integer.parseInt(year);
        i += 543;
        return Integer.toString(i);
    }

    public static String parseCitizenID(String idcard) {
        String CitizenID;
        if (idcard != null) {
            if (idcard.length() >= 13) {
                idcard.trim();
                try {
                    CitizenID = "" + idcard.charAt(0);
                    CitizenID += "-" + idcard.substring(1, 5);
                    CitizenID += "-" + idcard.substring(5, 10);
                    CitizenID += "-" + idcard.substring(10, 12);
                    CitizenID += "-" + idcard.charAt(12);
                    return CitizenID;
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                    Log.e("personParse", "idCard error while parsing");
                    return null;
                }
            }
        }
        Log.d("personParse", "idCard lenght is less than 13");
        return null;

    }

    public String getPersonPic() {
        return pcucode + "" + pid + ".jpg";
    }

}
