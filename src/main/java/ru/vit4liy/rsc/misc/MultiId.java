package ru.vit4liy.rsc.misc;

public class MultiId<T> {
    private final T[] ids;

    public MultiId(T ... ids) {
        this.ids = ids;
    }

    public T getByIdx(int idx){
        return ids[idx];
    }
}
