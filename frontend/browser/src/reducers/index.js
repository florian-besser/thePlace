import moment from 'moment';
import uuid from "../lib/uuid";
import {combineReducers} from "redux";
import * as ActionTypes from "../actions/actionTypes";


const initialBoardState = {
    isFetching: false,
    colors: [],
    updatePending: false,
    selectedPixel: {
        x: undefined,
        y: undefined
    },
    pickerColor: undefined,
    timeoutExpiry: undefined, // moment when board can be updated again by this user
    timeoutLeft: undefined, // millis until timeout is over (used to trigger rerender)
};

function board(state = initialBoardState, action) {
    switch (action.type) {
        case ActionTypes.SET_PICKER_COLOR:
            return {
                ...state,
                pickerColor: action.color
            };
        case ActionTypes.SET_COLOR_REQUESTED:
            return {
                ...state,
                updatePending: true
            };
        case ActionTypes.SET_COLOR_SUCCESS:
            const timeoutExpiry = moment().add(10, 'seconds');
            return {
                ...state,
                updatePending: false,
                timeoutExpiry: timeoutExpiry,
                timeoutLeft: timeoutExpiry.diff()
            };
        case ActionTypes.UPDATE_TIMEOUT:
            if (state.timeoutExpiry.isSameOrBefore()) {
                return state;
            }
            return {
                ...state,
                timemoutLeft: Math.max(0, state.timeoutExpiry.diff())
            };
        case ActionTypes.PIXEL_UPDATED:
            const updatedPixels = state.colors;
            for (let pixel of action.pixels) {
                updatedPixels[pixel.y][pixel.x] = pixel.color;
            }
            return {
                ...state,
                colors: updatedPixels
            };
        case ActionTypes.REQUEST_PLACE:
            return {
                ...state,
                isFetching: true
            };
        case ActionTypes.LOAD_PLACE_SUCCESS:
            return {
                ...state,
                isFetching: false,
                colors: action.body.colors
            };
        case ActionTypes.SELECT_PIXEL:
            return {
                ...state,
                selectedPixel: {
                    x: action.x,
                    y: action.y
                },
                pickerColor: action.color
            };
        default:
            return state;
    }
}


const initialUserState = {
    userId: uuid()
};

function user(state = initialUserState) {
    return state;
}

const rootReducer = combineReducers({
    board,
    user
});

export default rootReducer;
