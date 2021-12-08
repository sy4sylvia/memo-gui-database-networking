package Networking;

import java.io.Serializable;

public class MemoData implements Serializable {
    private String name;
    private String contents;

    public MemoData(String name) {
        this.name = name;
    }


    public MemoData(String name, String contents) {
        this.name = name;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "Memo title: " + this.name + "\nMemo Contents: \n" + this.contents;
    }
}
