import * as React from 'react';
import ReactTimeout from 'react-timeout'
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {ChromePicker} from 'react-color';
import {setColor, selectPixel, setPickerColor, updateTimeout} from '../actions';


const PIXEL_SIZE = 10;

function ThePlaceComponent({board, selectPixel, setPickerColor, setColor, updateTimeout}) {
    const width = board.colors[0] ? board.colors[0].length : 0;
    const height = board.colors.length;
    const selectedPixel = board.selectedPixel;
    const tiles = [];

    board.colors.forEach((line, y) => {
        line.forEach((color, x) => {
            const isSelected = selectedPixel.x === x && selectedPixel.y === y;
            tiles.push(
                <Tile
                    color={color}
                    key={`${x}-${y}`}
                    selected={isSelected}
                    onSelect={() => selectPixel(x, y, color)}
                />
            );
        });
    });

    return (
        <div>
            <div className='place' style={{width: width * PIXEL_SIZE, height: height * PIXEL_SIZE}}>
                {tiles}
            </div>
            <ColorSelector
                x={selectedPixel.x}
                y={selectedPixel.y}
                color={board.pickerColor}
                setPickerColor={setPickerColor}
                onSetColor={setColor}
                timeoutExpiry={board.timeoutExpiry}
                updateTimeout={updateTimeout}
            />
        </div>
    );
}

function Tile({color, selected, onSelect}) {
    const classes = selected ? 'tile selected' : 'tile';
    return (
        <div
            className={classes}
            style={{backgroundColor: `${color}`}}
            onClick={onSelect}
        />
    );
}

function ColorSelector({x, y, color, setPickerColor, onSetColor, timeoutExpiry, updateTimeout}) {
    const hasSelectedPixel = !isNaN(x) && !isNaN(y);
    if (!hasSelectedPixel) {
        return (
            <div className='colorActions'>
                <h1>Select a Pixel</h1>
            </div>
        );
    }

    const disabled = timeoutExpiry && timeoutExpiry.isAfter();

    return (
        <div className='colorActions'>
            <p>Setting color for pixel {`${x}/${y}`} to {color}</p>
            {disabled && <p>Waiting for timeout...</p>}
            <TimeoutClock timeout={timeoutExpiry} updateTimeout={updateTimeout}/>
            {!disabled && (
                <div>
                    <ChromePicker disableAlpha onChangeComplete={setPickerColor} color={color}/>
                    <button onClick={() => onSetColor(x, y, color)}>Save</button>
                </div>
            )}
        </div>
    );
}

const TimeoutClock = ReactTimeout(({timeout, updateTimeout, setTimeout}) => {
    const diff = timeout ? timeout.diff() : 0;
    if (diff <= 0) {
        return null;
    }

    if (diff > 0) {
        setTimeout(updateTimeout, 1000);
    }

    return (
        <span>Wait for {Math.ceil(diff / 1000)} seconds</span>
    );
});

export const ThePlace = connect(
    (state, ownProps) => ({
        board: state.board
    }),
    dispatch => bindActionCreators({setColor, selectPixel, setPickerColor, updateTimeout}, dispatch)
)(ThePlaceComponent);
