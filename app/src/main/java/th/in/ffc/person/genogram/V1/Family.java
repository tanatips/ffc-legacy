/*
 * Copyright (C) 2010 Family Folder Collector Project - NECTEC
 * 
 *	Family Class [version 1.0]
 *  November 09, 2010  
 *  
 *
 *	Create by  Piruin Panichphol [Blaze]
 *
 * ---------------Version log---------------------------------
 *   
 */

package th.in.ffc.person.genogram.V1;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import th.in.ffc.provider.HouseProvider.House;

import java.util.ArrayList;
import java.util.List;

public class Family {

    private boolean mFoundHouseOwner = false;
    private int mHCode;
    //	private String mHPCUcode;
    private int mHouseOwnerPid;
    private int mFamilyNo;
    private boolean mFoundRefPerson = false;
    private String mLog;
    private List<Person> mMember = new ArrayList<Person>();
    private String mMessage;
    private Context mContext;

    public Family(Context context, int hcode, int familyno) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mHCode = hcode;
        mFamilyNo = familyno;
        findHouseOwnerPid();
        mMessage = null;
        mLog = null;
//		mHPCUcode = null;
    }

    public Family(Context context, int hcode, String pcucode, int familyno) {
        this(context, hcode, familyno);
//		mHPCUcode = pcucode;
    }

    public int countGeneration(int generation) {
        int count = 0;
        int maxRange = 0;
        int minRange = 0;
        boolean inRange = true;
        List<Person> m = mMember;
        switch (generation) {
            case -1:
                maxRange = -1;
                minRange = -10;
                break;
            case 0:
                // minRange = 0;
                // maxRange = 0;
                break;
            case 1:
                maxRange = 10;
                minRange = 1;
                break;
            case 2:
                maxRange = 20;
                minRange = 11;
                break;
            case 3:
                maxRange = 30;
                minRange = 21;
                break;
            case 4:
                maxRange = 40;
                minRange = 31;
                break;
            case 5:
                maxRange = 50;
                minRange = 41;
                break;
            default:
                inRange = false;
                break;
        }
        if (inRange) {
            for (Person p : m) {
                int position = p.getStatusInFamily();
                if (position >= minRange && position <= maxRange)
                    count++;
            }
        }
        return count;
    }

    public void findFamilyMember() {
        if (findReferencePerson()) {
            int member = mMember.size();
            for (int i = 0; i < member; i++) {
                Person m = mMember.get(i);
                if (m != null) {
                    int mPosition = m.getStatusInFamily();
                    System.err.println(m.getPid() + " start scan");
                    switch (mPosition) {
                        case Person.STATUS_LEADER:
                        case Person.STATUS_MATE:
                        case Person.STATUS_LEADER_PARENT:
                        case Person.STATUS_MATE_PARENT:
                            addPerson(m.findParent());
                        case Person.STATUS_LEADER_GRANDPARENT:
                        case Person.STATUS_MATE_GRANDPARENT:
                            addPerson(m.findSibling());
                        case Person.STATUS_COUPLE_CHILD:
                        case Person.STATUS_COUPLE_GRANDCHILD:
                        case Person.STATUS_COUPLE_GREAT_GRANDCHILD:
                        case Person.STATUS_LEADER_CHILD:
                        case Person.STATUS_LEADER_GRANDCHILD:
                        case Person.STATUS_LEADER_SIBLING_CHILD:
                        case Person.STATUS_LEADER_SIBLING:
                        case Person.STATUS_MATE_CHILD:
                        case Person.STATUS_MATE_GRANDCHILD:
                        case Person.STATUS_MATE_SIBLING_CHILD:
                        case Person.STATUS_MATE_SIBLING:
                            addPerson(m.findMate());
                            addPerson(m.findChildWithMate());
                            addPerson(m.findChild());
                            System.err.println(m.getPid() + " stop scan");
                    }
                    member = mMember.size();
                    System.out.println("i =" + i + ", member size =" + member);
                }
            }
            Person p = mMember.get(0);
            addPerson(p.findAllInHouse());
            setHouseOwner();
        }
    }

    public List<Person> getUnknownPerson() {
        List<Person> u = new ArrayList<Person>();
        for (Person p : mMember) {
            if (p.getHcode() == this.mHCode
                    && p.getStatusInFamily() == Person.STATUS_UNKNOWN) {
                u.add(p);
            }
        }
        return u;

    }

    public List<Person> getDefindedPerson() {
        List<Person> u = new ArrayList<Person>();
        for (Person p : mMember) {
            if (p.getHcode() == this.mHCode
                    && p.getStatusInFamily() != Person.STATUS_UNKNOWN) {
                u.add(p);
            }
        }
        return u;
    }

    private boolean addPerson(Person person) {
        if (person != null) {
            try {
                if (!isAlreadyBeFamilyMember(person.getIDcard())) {
                    mMember.add(person);
                    return true;
                } else
                    return false;
            } catch (Exception ex) {
                if (!isAlreadyBeFamilyMember(person.getPid())) {
                    mMember.add(person);
                    return true;
                } else
                    return false;
            }
        } else
            return false;
    }

    private boolean addPerson(List<Person> person) {
        boolean isAdd = false;
        if (!person.isEmpty()) {
            for (Person p : person) {
                try {
                    if (!isAlreadyBeFamilyMember(p.getIDcard())) {
                        mMember.add(p);
                        isAdd = true;
                    }
                } catch (Exception ex) {
                    if (!isAlreadyBeFamilyMember(p.getPid())) {
                        mMember.add(p);
                        isAdd = true;
                    }
                }
            }
            return isAdd;
        } else
            return false;
    }

    public List<Person> getAllMember() {
        if (mMember.size() > 0)
            return mMember;
        else
            return null;
    }

    public Person[] getAllMemberAsArray() {
        Person[] p = new Person[mMember.size()];
        int index = 0;
        for (Person m : mMember) {
            p[index] = m;
            index++;
        }
        return p;
    }

    public Person getPersonByPid(int pid) {
        for (Person m : mMember) {
            if (m.getPid() == pid) {
                return m;
            }
        }
        return null;
    }

    public Person getPersonByIdCard(String idCard) {
        for (Person m : mMember) {
            if (m.getIDcard().equalsIgnoreCase(idCard)) {
                return m;
            }
        }
        return null;
    }

    public Person getPersonAt(int location) {
        if (mMember.size() > 0) {
            if (location >= 0 && location < mMember.size()) {
                return mMember.get(location);
            }
        }
        return null;
    }

    public boolean isAlreadyBeFamilyMember(String idCard) {
        for (Person m : mMember) {
            if (m.getIDcard().equalsIgnoreCase(idCard)) {
                // System.err.println(idCard + " == " + m.getIDcard());
                return true;
            }
        }
        return false;
    }

    public boolean isAlreadyBeFamilyMember(int pid) {
        for (Person m : mMember) {
            if (m.getPid() == pid) {
                // System.err.println(idCard + " == " + m.getIDcard());
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String toReturn = "This Family in House : " + mHCode
                + "\nPerson in House = " + countPersonInHouse()
                + "\nHouse Owner ID : " + mHouseOwnerPid + " Family NO."
                + mFamilyNo;

        if (!mMember.isEmpty()) {
            toReturn += "\nFamily Member List :";
            for (Person p : mMember) {
                toReturn += "\n  " + p.getPid() + " " + p.getFullName() + " "
                        + p.getStatusInFamily();
            }
        }
        if (mLog != null) {
            toReturn += "\nLog : " + mLog;
        }
        if (mMessage != null) {
            toReturn += "\nMessage : \n" + mMessage;
        }
        return toReturn;
    }

    private int countPersonInHouse() {
        Cursor cursor = mContext.getContentResolver().query(th.in.ffc.provider.
                        PersonProvider.Person.CONTENT_URI, new String[]{BaseColumns._COUNT},
                "hcode = " + mHCode, null, "pid");
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        else
            return 0;
    }

    private void findHouseOwnerPid() {
        String sql = "hcode = " + mHCode;
//		if (mHPCUcode != null) {
//			sql += " AND pcucode = \'" + mHPCUcode + "\'";
//		}
        Cursor cursor = mContext.getContentResolver().query(House.CONTENT_URI,
                new String[]{House.PID,}, sql, null, House.DEFAULT_SORTING);
        if (cursor.moveToFirst())
            mHouseOwnerPid = cursor.getInt(0);
        else
            mHouseOwnerPid = -1;

    }

    private boolean findReferencePerson() {
        Log.d("FAmily", "familyno=" + mFamilyNo);
        if (!mFoundRefPerson) {
            String[] sqlForFindLeader = new String[3];
            sqlForFindLeader[0] = "familyposition = \'1\' AND hcode = "
                    + mHCode + " AND  familyno = " + mFamilyNo + " ";
            sqlForFindLeader[1] = "familyposition = \'2\' AND hcode = "
                    + mHCode + " AND familyno = " + mFamilyNo + " ";
            sqlForFindLeader[2] = "hcode = "
                    + mHCode + " AND familyno = " + mFamilyNo;

//			if (mHPCUcode != null) {
//				sqlForFindLeader[0] += "AND pcucodeperson = \'" + mHPCUcode
//						+ "\' ";
//				sqlForFindLeader[1] += "AND pcucodeperson = \'" + mHPCUcode
//						+ "\' ";
//				String strTemp = sqlForFindLeader[2];
//				sqlForFindLeader[2] = strTemp
//						+ " OR pcucodeperson = \'"
//						+ mHPCUcode
//						+ "\' ";
//			}

            for (int i = 0; i < 3; i++) {
                Cursor cursor = mContext.getContentResolver().query(th.in.ffc.provider.PersonProvider.Person.CONTENT_URI,
                        Person.PROJECTION, sqlForFindLeader[i], null, "birth ASC");
                if (cursor.moveToFirst()) {
                    Person refPerson;
                    if (cursor.getInt(1) == mHouseOwnerPid) { // ถ้าเป็นเจ้าของบ้าน
                        refPerson = new Person(mContext, cursor, this,
                                Person.STATUS_LEADER, true);
                        mFoundHouseOwner = true;
                    } else {
                        refPerson = new Person(mContext, cursor, this,
                                Person.STATUS_LEADER);
                    }
                    mFoundRefPerson = true;
                    mMember.add(refPerson);
                    return true;
                } else {
                    cursor.close();
                    Log.e("family", "family not found");
                }
            }
        }
        return false;
    }

    public boolean isFoundHouseOwner() {
        return mFoundHouseOwner;
    }

    private void setHouseOwner() {
        System.out.println("setting house owner");
        if (mHouseOwnerPid > -1) {
            int ownerID = mHouseOwnerPid;
            for (final Person m : mMember) {
                if (m.getPid() == ownerID) {
                    m.setToBeHouseOwner();
                    mFoundHouseOwner = true;
                    break;
                }
            }
        }
    }

}
