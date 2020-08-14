package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelperCallback  extends ItemTouchHelper.Callback{
    private final ItemTouchHelperAdapter mAdapter;
    private int dragFrom = -1, dragTo = -1;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        final int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();


        if(dragFrom == -1) {
            dragFrom =  fromPosition;
        }
        dragTo = toPosition;

//        mAdapter.onItemMove(fromPosition, toPosition);

        return true;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    //    @Override
//    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
//            itemViewHolder.onItemSelected();
//        }
//
//        super.onSelectedChanged(viewHolder, actionState);
//    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);


        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            mAdapter.onItemMoved(dragFrom, dragTo);
        }

        dragFrom = dragTo = -1;


    }

    //    @Override
//    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
//        itemViewHolder.onItemClear();
//    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


}
