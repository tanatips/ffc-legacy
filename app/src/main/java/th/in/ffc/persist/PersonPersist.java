package th.in.ffc.persist;


public class PersonPersist {
    String pid;
    String fname;
    String lname;
    String prename;

    public PersonPersist() {
        // TODO Auto-generated constructor stub
    }

    public PersonPersist(String pid, String fname, String lname) {
        super();
        this.pid = pid;
        this.fname = fname;
        this.lname = lname;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getPrename() {
        return this.prename;
    }

    public String toString() {
        return fname + " " + lname;
    }
}
