import {SET_COLOR} from "./actionTypes";

/*
    action creators
 */

export function setColor(x, y, color) {
    return {
        type: SET_COLOR,
        x,
        y,
        color
    };
}
