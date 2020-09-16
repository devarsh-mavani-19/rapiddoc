package com.shortcontent.imagetopdf;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);

    void onItemMoved(int fromPosition, int toPosition);
}