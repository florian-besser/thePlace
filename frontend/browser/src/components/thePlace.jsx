import * as React from 'react';
import {connect} from 'react-redux';
import {ChromePicker} from 'react-color';
import {setColor, selectPixel, setPickerColor} from '../actions';


const PIXEL_SIZE = 10;

function ThePlaceComponent({board, onSelectPixel, setPickerColor, onSetColor}) {
    const width = board.colors[0] ? board.colors[0].length : 0;
    const height = board.colors.length;
    const selectedPixel = board.selectedPixel;
    const tiles = [];

    board.colors.forEach((line, y) => {
        line.forEach((tile, x) => {
            const isSelected = selectedPixel.x === x && selectedPixel.y === y;
            tiles.push(
                <Tile
                    color={tile.color}
                    key={`${x}-${y}`}
                    selected={isSelected}
                    onSelect={() => onSelectPixel(x, y, tile.color)}
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
                onSetColor={onSetColor}
                disabled={board.timeoutExpiry && board.timeoutExpiry.isAfter()}
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

function ColorSelector({x, y, color, setPickerColor, onSetColor, disabled}) {
    const hasSelectedPixel = !isNaN(x) && !isNaN(y);
    if (!hasSelectedPixel) {
        return (
            <div className='colorActions'>
                <h1>Select a Pixel</h1>
            </div>
        );
    }

    return (
        <div className='colorActions'>
            <p>Setting color for pixel {`${x}/${y}`} to {color}</p>
            {disabled && <p>Waiting for timeout...</p>}
            <ChromePicker disableAlpha onChangeComplete={setPickerColor} color={color}/>
            <button onClick={() => onSetColor(x, y, color)}>Save</button>
        </div>
    );
}

export const ThePlace = connect(
    (state, ownProps) => ({
        board: state.board
    }),
    (dispatch, ownProps) => ({
        onSetColor: (x, y, color) => dispatch(setColor(x, y, color)),
        onSelectPixel: (x, y, color) => dispatch(selectPixel(x, y, color)),
        setPickerColor: (color) => dispatch(setPickerColor(color.hex))
    })
)(ThePlaceComponent);
