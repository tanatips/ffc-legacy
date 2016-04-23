/* ***********************************************************************
 *                                                                 _ _ _
 *                                                               ( _ _  |
 *                                                           _ _ _ _  | |
 *                                                          (_ _ _  | |_|
 *  _     _   _ _ _ _     _ _ _   _ _ _ _ _   _ _ _ _     _ _ _   | | 
 * |  \  | | |  _ _ _|   /  _ _| |_ _   _ _| |  _ _ _|   /  _ _|  | |
 * | | \ | | | |_ _ _   /  /         | |     | |_ _ _   /  /      |_|
 * | |\ \| | |  _ _ _| (  (          | |     |  _ _ _| (  (    
 * | | \ | | | |_ _ _   \  \_ _      | |     | |_ _ _   \  \_ _ 
 * |_|  \__| |_ _ _ _|   \_ _ _|     |_|     |_ _ _ _|   \_ _ _| 
 *  a member of NSTDA, @Thailand
 *  
 * ***********************************************************************
 *
 *
 * FFC-Plus Project
 *
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */

package th.in.ffc.person;

import android.os.Bundle;
import th.in.ffc.app.form.ViewFormFragment;
import th.in.ffc.provider.CodeProvider.District;
import th.in.ffc.provider.CodeProvider.Province;
import th.in.ffc.provider.CodeProvider.Subdistrict;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.PersonColumns;

/**
 * add description here! please
 *
 * @author piruin panichphol
 * @version 1.2
 * @since Family Folder Collector 2.0
 */
public class PersonFragment extends ViewFormFragment {

    public static final String EXTRA_PID = Person.PID;
    public static final String EXTRA_HCODE = Person.HCODE;
    public static final String EXTRA_CITIZEN_ID = Person.PID;
    public static final String EXTRA_PROVINCE = Province.PROVCODE;
    public static final String EXTRA_DISTRICT = District.DISTCODE;
    public static final String EXTRA_SUBDISTRICT = Subdistrict.SUBDISTCODE;

    public String getPID() {
        Bundle args = getArguments();
        if (args != null)
            return args.getString(PersonColumns._PID);
        return null;
    }

    public String getPcucodePerson() {
        Bundle args = getArguments();
        if (args != null)
            return args.getString(PersonColumns._PCUCODEPERSON);
        return null;
    }


}
