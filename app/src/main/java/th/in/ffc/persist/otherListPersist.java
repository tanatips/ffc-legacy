package th.in.ffc.persist;

public class otherListPersist {

    String keyValue;
    String keyName;

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public otherListPersist(String keyValue, String keyName) {
        super();
        this.keyValue = keyValue;
        this.keyName = keyName;
    }

    public otherListPersist() {
        // TODO Auto-generated constructor stub
    }

    public String toString() {
        return keyValue + " " + keyName;
    }

}
