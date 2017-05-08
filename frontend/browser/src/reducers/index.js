import uuid from '../lib/uuid';
import {generateRandomBoard} from '../lib/boardGenerator';
import * as ActionTypes from '../actions/actionTypes';

const BOARD_WIDTH = 50;
const BOARD_HEIGHT = 50;

const initialState = {
    userId: uuid(),
    boardWidth: BOARD_WIDTH,
    boardHeight: BOARD_HEIGHT,
    board: generateRandomBoard(BOARD_WIDTH, BOARD_HEIGHT)
};

export function thePlace(state = initialState, action) {
    switch(action.type) {
        case ActionTypes.SET_COLOR:
            const indexToUpdate = action.y * state.boardWidth + action.x;
            const updatedBoard = [...state.board];
            updatedBoard[indexToUpdate] = action.color;
            return {
                ...state,
                board: updatedBoard
            };
        default:
            return state;
    }
}
