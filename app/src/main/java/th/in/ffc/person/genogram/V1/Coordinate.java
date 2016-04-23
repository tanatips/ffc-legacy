/*
 * Copyright (C) 2010 Family Folder Collector Project - NECTEC
 * 
 *	Coordinate Class [version 1.0]
 *  November 09, 2010  
 *  
 *
 *	Create by  Piruin Panichphol [Blaze]
 */

package th.in.ffc.person.genogram.V1;

public class Coordinate {
    //store in pixel
    public int X;
    public int Y;

    public Coordinate(int X, int Y) {
        this.X = X;
        this.Y = Y;
    }

    public Coordinate(Coordinate origin) {
        this.X = origin.X;
        this.Y = origin.Y;
    }
}

	
