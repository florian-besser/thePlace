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
        color: color
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
            .then(response => dispatch(setColorSuccess()));
    };
}

export function setColorSuccess() {
    return {
        type: ActionType.SET_COLOR_SUCCESS
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
 interface body {
     pixels: {x: number; y: number; color: string}[],
     xmaximum: number;
     ymaximum: number;
 }
 */
export function loadPlaceSuccess(body) {
    return {
        type: ActionType.LOAD_PLACE_SUCCESS,
        ...body
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

export function pixelUpdated(data) {
    return {
        type: ActionType.PIXEL_UPDATED,
        ...data
    };
}
