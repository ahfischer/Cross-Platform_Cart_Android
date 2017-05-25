package fischeranthony.com.a_fischer_android_usersanddata.objects;

import java.io.Serializable;

public class Item implements Serializable {
    String mItemName;
    String mItemType;
    int mQuantity;

    public Item(String mItemName, String mItemType, int mQuantity) {
        this.mItemName = mItemName;
        this.mItemType = mItemType;
        this.mQuantity = mQuantity;
    }

    public String getmItemName() {
        return mItemName;
    }

    public String getmItemType() {
        return mItemType;
    }

    public int getmQuantity() {
        return mQuantity;
    }
}
