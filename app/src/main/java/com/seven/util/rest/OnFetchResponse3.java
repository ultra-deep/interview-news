package com.saviway.saviway.others.api.rest;

/***
 * The interface to bind a repository to another component of application
 * @param <T>
 */
public interface OnFetchResponse3<T,U,V> {
    void onFetched(T t , U u, V v);
}
