package com.saviway.saviway.others.api.rest;

/***
 * The interface to bind a repository to another component of application
 * @param <T>
 */
public interface OnFetchResponse2<T,U> {
    void onFetched(T t , U u);
}
