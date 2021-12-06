package Database;


//not used

public class Row {
//    private final Object[] values;

    private int id;
    private String name;
    private String contents;
    // Add/generate constructors, getters and setters.

    public Row(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String toString() {
        return String.format("Row[id = %d,\nname = %s,\ncontents = %s", id, name, contents);
    }


    //    public Row(Object[] values) {
//        this.values = values;
//    }
//
//    public int getSize() {
//        return values.length;
//    }
//
//    public Object getValue(int i) {
//        return values[i];
//    }
}

//public class Row {
//    private int id;
//    private String name;
//    private String contents;
//    // Add/generate constructors, getters and setters.


