package th.in.ffc.map.database;

import th.in.ffc.FamilyFolderCollector;

public class Database {

//	private String stringName;
//	private String stringPath;

    public Database() {
//		this.stringName = FinalValue.STRING_DATABASE_NAME;
//		this.stringPath = FinalValue.STRING_DATABASE_PATH;

    }

    public String getStringName() {
        return FamilyFolderCollector.DATABASE_DATA;
    }

    public String getStringPath() {
        return FamilyFolderCollector.PATH_PLAIN_DATABASE;
    }

}
