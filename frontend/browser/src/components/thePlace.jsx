import * as React from 'react';
import {connect} from 'react-redux';
import {setColor} from '../actions';


const PIXEL_SIZE = 10;

function ThePlaceComponent({board, width, height}) {
    return (
        <div style={{width: width * PIXEL_SIZE}}>
            {board.map(t => (
                <Tile color={t.color} key={`${t.x}-${t.y}`} />
            ))}
        </div>
    );
}

function Tile({color}) {
    return (
        <div className='tile' style={{backgroundColor: color}}/>
    );
}

export const ThePlace = connect(
    (state, ownProps) => ({
        board: state.board,
        width: state.boardWidth,
        height: state.boardHeight
    }),
    (dispatch, ownProps) => ({
        onSetColor: (x, y, color) => dispatch(setColor(x, y, color))
    })
)(ThePlaceComponent);
