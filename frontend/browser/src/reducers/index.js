import * as moment from 'moment';
import uuid from "../lib/uuid";
import {combineReducers} from "redux";
import * as ActionTypes from "../actions/actionTypes";

const BOARD_WIDTH = 50;
const BOARD_HEIGHT = 50;

const initialBoardState = {
    isFetching: false,
    colors: [],  // generateRandomBoard(BOARD_WIDTH, BOARD_HEIGHT),
    xmaximum: BOARD_WIDTH,
    ymaximum: BOARD_HEIGHT,
    updatePending: false,
    selectedPixel: {
        x: undefined,
        y: undefined
    },
    pickerColor: undefined,
    timeoutExpiry: undefined, // moment when board can be updated again by this user
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
            // TODO: set timeout to trigger rerender
            return {
                ...state,
                updatePending: false,
                timeoutExpiry: moment().add(10, 'seconds')
            };
        case ActionTypes.PIXEL_UPDATED:
            const updatedPixels = [...state.colors];
            updatedPixels[action.y][action.x] = action.color;
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
                xmaximum: action.colors[0].length,
                ymaximum: action.colors.length,
                colors: action.colors
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
