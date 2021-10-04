package com.saviway.saviway.others.api.rest;

/***
 * The interface to bind a repository to another component of application
 * @param <T>
 */
public interface OnFetchResponse<T> {
    void onFetched(T dto);
}
