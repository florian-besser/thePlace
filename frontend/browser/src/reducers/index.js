import uuid from '../lib/uuid';
import {generateRandomBoard} from '../lib/boardGenerator';
import {combineReducers} from 'redux';
import * as ActionTypes from '../actions/actionTypes';

const BOARD_WIDTH = 50;
const BOARD_HEIGHT = 50;

const initialBoardState = {
    isFetching: false,
    pixels: generateRandomBoard(BOARD_WIDTH, BOARD_HEIGHT),
    xmaximum: BOARD_WIDTH,
    ymaximum: BOARD_HEIGHT,
    updatePending: false,
    selectedPixel: {
        x: undefined,
        y: undefined
    },
    pickerColor: undefined
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
            return {
                ...state,
                updatePending: false
            };
        case ActionTypes.PIXEL_UPDATED:
            const indexToUpdate = action.y * state.xmaximum + action.x;
            const updatedPixels = [...state.pixels];
            updatedPixels[indexToUpdate] = action.color;
            return {
                ...state,
                pixels: updatedPixels
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
                xmaximum: action.xmaximum,
                ymaximum: action.ymaximum,
                pixels: action.pixels
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

function user(state = initialUserState, action) {
    return state;
}

const rootReducer = combineReducers({
    board,
    user
});

export default rootReducer;
