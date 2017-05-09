import fetch from 'isomorphic-fetch'
import parse from 'url-parse';
import * as ActionType from "./actionTypes";

export const API_HOST = parse(window.location).hostname + ':2222';
const REST_API = '//' + API_HOST + '/rest/thePlace';
const request_options = {};

/*
 action creators
 */

export function updateTimeout() {
    return {
        type: ActionType.UPDATE_TIMEOUT
    };
}

export const loadTimeout = () =>
    (dispatch) => {
        dispatch(requestTimeout());
        return fetch(REST_API + `/timeout`)
            .then(response => response.json())
            .then(seconds => dispatch(
                loadTimeoutSuccess(seconds))
            );
    };


export const requestTimeout = () => ({
    type: ActionType.REQUEST_TIMEOUT
});

export const loadTimeoutSuccess = (timeoutSeconds) => ({
    type: ActionType.LOAD_TIMEOUT_SUCCESS,
    seconds: timeoutSeconds
});


export function selectPixel(x, y, color) {
    return {
        type: ActionType.SELECT_PIXEL,
        x,
        y,
        color
    };
}

export function setPickerColor(color) {
    return {
        type: ActionType.SET_PICKER_COLOR,
        color: color.hex
    };
}

export function setColor(x, y, color) {
    return (dispatch, getState) => {
        dispatch(setColorRequested(x, y, color));
        const request = new Request(REST_API + `/place/${x}/${y}`, {
            method: 'put',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                user: getState().user.userId,
                color: color
            })
        });
        return fetch(request)
            .then(response => {
                if (response.status === 200) {
                    dispatch(setColorSuccess());
                    dispatch(pixelsUpdated([{
                        x, y, color
                    }]))
                } else {
                    dispatch(setColorError());
                }
            });
    };
}

export function setColorSuccess() {
    return {
        type: ActionType.SET_COLOR_SUCCESS
    };
}

export function setColorError() {
    return {
        type: ActionType.SET_COLOR_ERROR
    };
}

export function setColorRequested(x, y, color) {
    return {
        type: ActionType.SET_COLOR_REQUESTED,
        x,
        y,
        color
    };
}

export function requestPlace() {
    return {
        type: ActionType.REQUEST_PLACE
    };
}

export function loadPlaceError() {
    return {
        type: ActionType.LOAD_PLACE_ERROR
    };
}

/*
 type body = string[][]
 */
export function loadPlaceSuccess(body) {
    return {
        type: ActionType.LOAD_PLACE_SUCCESS,
        body: body
    };
}


export function loadPlace() {
    return dispatch => {
        dispatch(requestPlace());
        return fetch(REST_API + `/place`, request_options)
            .then(response => response.json())
            .then(json => {
                dispatch(loadPlaceSuccess(json));
            });
    }
}

export function pixelsUpdated(data) {
    return {
        type: ActionType.PIXEL_UPDATED,
        pixels: data
    };
}
