package com.gatech.fabbadgetest.domain.usecases

abstract class UseCase {

    /**
     * Data passed to a request.
     */
    interface RequestValues

    /**
     * Data received from a request.
     */
    interface ResponseValue

    interface UseCaseCallback<R> {
        fun onSuccess(response: R)
        fun onError()
    }
}