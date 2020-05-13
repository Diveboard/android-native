package com.diveboard.util.binding.recyclerViewBinder.adapter.binder;

public interface ItemBinder<T> {
    int getLayoutRes(T model);

    int getBindingVariable(T model);
}
