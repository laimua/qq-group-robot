package simbot.example.persistence.domain;

public class KeyWordBan {
   private int rowId;
   private String keyWord;

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public String toString() {
        return "KeyWordBan{" +
                "rowId=" + rowId +
                ", keyWord='" + keyWord + '\'' +
                '}';
    }
}
