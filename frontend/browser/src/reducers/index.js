import moment from "moment";
import uuid from "../lib/uuid";
import {combineReducers} from "redux";
import * as ActionTypes from "../actions/actionTypes";


const initialBoardState = {
    isFetching: false,
    colors: []
};

function board(state = initialBoardState, action) {
    switch (action.type) {
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

const initialColorSelectorState = {
    updatePending: false,
    setColorError: false,
    selectedPixel: {
        x: undefined,
        y: undefined
    },
    selectedColor: undefined,
    timeoutExpiry: undefined,
    timeoutLeft: undefined, // millis until timeout is over (used to trigger rerender)
    timeoutSeconds: 10
};

function colorSelector(state = initialColorSelectorState, action) {
    switch (action.type) {
        case ActionTypes.SELECT_PIXEL:
            return {
                ...state,
                selectedPixel: {
                    x: action.x,
                    y: action.y
                },
                selectedColor: action.color
            };
        case ActionTypes.SET_PICKER_COLOR:
            return {
                ...state,
                selectedColor: action.color
            };
        case ActionTypes.SET_COLOR_REQUESTED:
            return {
                ...state,
                updatePending: true,
                setColorError: false
            };
        case ActionTypes.SET_COLOR_SUCCESS:
            const timeoutExpiry = moment().add(10, 'seconds');
            return {
                ...state,
                updatePending: false,
                timeoutExpiry: timeoutExpiry,
                timeoutLeft: timeoutExpiry.diff(),
            };
        case ActionTypes.SET_COLOR_ERROR:
            return {
                ...state,
                updatePending: false,
                setColorError: true
            };
        case ActionTypes.UPDATE_TIMEOUT:
            return {
                ...state,
                timemoutLeft: Math.max(-10000, state.timeoutExpiry.diff())
            };
        case ActionTypes.LOAD_TIMEOUT_SUCCESS:
            return {
                ...state,
                timeoutSeconds: action.seconds
            };
        default:
            return state;
    }
}


const rootReducer = combineReducers({
    board,
    user,
    colorSelector
});

export default rootReducer;
